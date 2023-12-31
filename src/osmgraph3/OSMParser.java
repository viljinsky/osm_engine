
package osmgraph3;

import osmgraph3.graph.Tags;
import osmgraph3.graph.Way;
import osmgraph3.graph.Relation;
import osmgraph3.graph.Member;
import osmgraph3.graph.Node;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import osmgraph3.graph.Graph;

public class OSMParser extends DefaultHandler {

    public List<Relation> relations = new ArrayList<>();
    public List<Node> nodes = new ArrayList<>();
    public List<Way> ways = new ArrayList<>();

    Node node;
    Way way;
    Relation relation;
    
    public double minlon, minlat, maxlon, maxlat;
    
    public double[] bound(){
        return new double[]{minlon,minlat,maxlon,maxlat};
    }

    Node nodeById(long id) {        
//        return nodes.stream().filter(nd->{return nd.id == id;}).findFirst().orElse(null);
       for (Node node : nodes) {
            if (node.id == id) {
                return node;
            }
       }
       return null;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equals(Graph.NODE)) {

            if (node != null) {
                nodes.add(node);
                node = null;
            }
        }

        if (qName.equals(Graph.WAY)) {
            if (way != null) {
                ways.add(way);
                way = null;
            }
        }

        if (qName.equals(Graph.RELATION)) {
            if (relation != null) {
                relations.add(relation);
                relation = null;
            }
        }

    }

    
    String decodeValue(String value){
        return value.replaceAll("&amp;", "&").replaceAll("&apos;", "'").replaceAll("&qt;", ">").replaceAll("&lt;", "<");
    }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (qName.equals("bounds")) {
            minlat = Double.parseDouble(attributes.getValue("minlat"));
            maxlat = Double.parseDouble(attributes.getValue("maxlat"));
            minlon = Double.parseDouble(attributes.getValue("minlon"));
            maxlon = Double.parseDouble(attributes.getValue("maxlon"));
        }

        if (qName.equals("node")) {
            double lon = Double.parseDouble(attributes.getValue("lon"));
            double lat = Double.parseDouble(attributes.getValue("lat"));
            node = new Node(lon, lat);
            node.id = Long.parseLong(attributes.getValue("id"));
        }

        if (qName.equals("way")) {
            way = new Way();
            way.id = Long.parseLong(attributes.getValue("id"));
        }

        if (qName.equals("nd")) {
            if (way != null) {
                Node ref = nodeById(Long.parseLong(attributes.getValue("ref")));
                way.add(ref);
            }
        }

        if (qName.equals("tag")) {
            if (node != null) {
                node.put(attributes.getValue("k"), decodeValue( attributes.getValue("v")));
            }
            if (way != null) {
                way.put(attributes.getValue("k"), decodeValue(attributes.getValue("v")));
            }
            if(relation!=null){
                relation.put(attributes.getValue("k"),decodeValue(attributes.getValue("v")));
            }
        }

        if (qName.equals("relation")) {
            relation = new Relation();
            relation.id = Long.parseLong(attributes.getValue("id"));
        }

        if (qName.equals("member")) {
            String type = attributes.getValue("type");
            String role = attributes.getValue("role");
            Long ref = Long.parseLong(attributes.getValue("ref"));
            Member member = new Member(ref,type,role);
            relation.add(member);
        }

    }

    void tagToJSON(Writer writer, Tags tags) throws Exception {
        writer.write(",\n\t\"tag\" : [\n");
        int c = 0;
        for (String k : tags.keySet()) {
            if (c++ > 0) {
                writer.write(",\n");
            }
            writer.write("\t\t{ \"k\" : \"" + k + "\", \"v\" : \"" + ((String) tags.get(k)).replaceAll("\"", "'") + "\"}");
        }
        writer.write("\t\n\t]\n");
    }

    void nodesToJSON(Writer writer) throws Exception {
        writer.write("\"nodes\" : [\n");
        int c2 = 0;
        for (Node node : nodes) {
            if (c2++ > 0) {
                writer.write(",\n");
            }
            writer.write(String.format(Locale.US, "{\"id\" : %d, \"lon\" : %8.6f, \"lat\" : %8.6f", node.id, node.lon, node.lat));
            if (node.tags != null) {
                tagToJSON(writer, node.tags);
            }
            writer.write("}");
        }
        writer.write("]");      // nodes close

    }

    void waysToJson(Writer writer) throws Exception {
        writer.write("\"way\" : [\n");
        int c4 = 0;
        for (Way way : ways) {
            if (c4++ > 0) {
                writer.write(",\n");
            }
            writer.write("{\t\"id\" : \"" + way.id + "\",\n");
            writer.write("\t\"ref\" : [");
            int c3 = 0;
            for (Node node : way) {
                if (c3++ > 0) {
                    writer.write(",");
                }
                writer.write(String.valueOf(node.id));
            }
            writer.write("\n\t]");

            if (way.tags != null) {
                tagToJSON(writer, way.tags);
            }
            writer.write("}");
        }
        writer.write("]");

    }

    void relationToJSON(Writer writer) throws Exception {

        writer.write("\"relation\" : [\n"); 
        int c6 = 0;
        for (Relation r : relations) {
            if (c6++ > 0) {
                writer.write(",\n");
            }
            writer.write("{\t\"id\" : " + r.id + ",\n");
            //----------
            writer.write("\t\"member\" : [\n");     // member start
            int c5 = 0;
            for (Member m : r) {
                if (c5++ > 0) {
                    writer.write(",\n");
                }
                writer.write(String.format("\t\t{\"type\": \"%s\", \"ref\":%d, \"role\":\"%s\"}", m.type, m.ref, m.role));
            }
            writer.write("\n\t]\n");                // member stop
            //----------
            
            if (r.tags!=null){
                tagToJSON(writer, r.tags);
            }
            
            writer.write("}");
        }
        writer.write("]\n");

    }

    void toJSON(OutputStream out) throws Exception{

        try (OutputStreamWriter writer = new OutputStreamWriter(out, "utf-8");) {

            writer.write("{\n");
            writer.write(String.format(Locale.US, "\"minlon\" : %f ,\n", maxlon));
            writer.write(String.format(Locale.US, "\"maxlon\" : %f ,\n", minlon));
            writer.write(String.format(Locale.US, "\"maxlat\" : %f ,\n", maxlat));
            writer.write(String.format(Locale.US, "\"minlat\" : %f ,\n", minlat));
            nodesToJSON(writer);
            writer.write(",\n");
            waysToJson(writer);
            writer.write(",\n");
            relationToJSON(writer);
            writer.write("}");
        }

    }

    public OSMParser(File source) throws Exception {
        long t = System.currentTimeMillis();
        System.out.print("start....");
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        saxParser.parse(source, this);
        System.out.println("OK ("+(System.currentTimeMillis()-t)+"ms)");
        
    }
    
    public static void main(String[] args) throws Exception {        
        File source = new File(System.getProperty("user.home")+"/osm", "test.osm");
        OSMParser handelr = new OSMParser(source);        
        
        try(OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                System.out.write(b);
            }
        }){
            handelr.toJSON(out);
        }
    }

}

