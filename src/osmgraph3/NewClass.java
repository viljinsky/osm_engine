/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class MyHandler extends DefaultHandler {

    List<Node> nodes = new ArrayList<>();
    List<Way> ways = new ArrayList<>();

    Node node;
    Way way;
    double minlon, minlat, maxlon, maxlat;

    Node nodeById(long id) {
        for (Node node : nodes) {
            if (node.id == id) {
                return node;
            }
        }
        return null;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
//        System.out.println("end " +qName);

        if (qName.equals("node")) {

            if (node != null) {
                nodes.add(node);
                node = null;
            }
        }

        if (qName.equals("way")) {
            if (way != null) {
                ways.add(way);
                way = null;
            }
        }

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (qName.equals("bounds")) {
            minlat = Double.valueOf(attributes.getValue("minlat"));
            maxlat = Double.valueOf(attributes.getValue("maxlat"));
            minlon = Double.valueOf(attributes.getValue("minlon"));
            maxlon = Double.valueOf(attributes.getValue("maxlon"));
        }

        if (qName.equals("node")) {
            double lon = Double.valueOf(attributes.getValue("lon"));
            double lat = Double.valueOf(attributes.getValue("lat"));
            node = new Node(lon, lat);
            try {
                node.id = Integer.valueOf(attributes.getValue("id"));
            } catch (NumberFormatException e) {
                //System.err.println("start node : "+e.getMessage());
            }
        }

        if (qName.equals("way")) {
            way = new Way();
            way.id = Integer.valueOf(attributes.getValue("id"));
        }

        if (qName.equals("nd")) {
            if (way != null) {
                try {
                    Node ref = nodeById(Integer.valueOf(attributes.getValue("ref")));
                    way.add(ref);
                } catch (Exception e) {
                    //   System.err.println("start way :  " + e.getMessage());
                }
            }
        }

        if (qName.equals("tag")) {
            if (node != null) {
                node.put(attributes.getValue("k"), attributes.getValue("v"));
            }
            if (way != null) {
                way.put(attributes.getValue("k"), attributes.getValue("v"));
            }
        }

    }

}

class XMLParserSAX {

    static void toJSON(OutputStream out, Iterable<Node> nodes, Iterable<Way> ways) {

        try (OutputStreamWriter writer = new OutputStreamWriter(out, "utf-8");) {

            writer.write("{ \"graph\" : {\n");

            //   writer.write(String.format("minlon : %f",handler.));
            writer.write("\"node\" : [\n");
            int c2 = 0;
            for (Node node : nodes) {
                if (c2++ > 0) {
                    writer.write(",\n");
                }
                writer.write(String.format(Locale.US, "{\"id\" : %d, \"lon\" : %8.6f, \"lat\" : %8.6f", node.id, node.lon, node.lat));
                if (node.tags != null) {
                    writer.write(",\n\t\"tag\" : [\n");
                    int c = 0;
                    for (String k : node.keySet()) {
                        if (c++ > 0) {
                            writer.write(",\n");
                        }
                        writer.write("\t\t{ \"k\" : \"" + k + "\", \"v\" : \"" + ((String) node.get(k)).replaceAll("\"", "'") + "\"}");
                    }
                    writer.write("\t\n\t]\n");

                }
                writer.write("}");
            }
            writer.write("],\n");

            writer.write("\"way\": [\n");
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
                writer.write("]");

                if (way.tags != null) {
                    writer.write(",\n\t\"tag\" : [\n");
                    int c = 0;
                    for (String k : way.keySet()) {
                        if (c++ > 0) {
                            writer.write(",\n");
                        }
                        writer.write("\t\t{ \"" + k + "\": \"" + ((String) way.get(k)).replaceAll("\"", "'") + "\"}");
                    }
                    writer.write("\t]\n");
                }
                writer.write("}");
            }
            writer.write("]\n");

            writer.write("}}\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = saxParserFactory.newSAXParser();
            MyHandler handler = new MyHandler();
            saxParser.parse(new File("C:\\Users\\viljinsky\\Desktop", "test.osm"), handler);

            File file = new File("test22.json");
            try (FileOutputStream out = new FileOutputStream(file);) {
                toJSON(out, handler.nodes, handler.ways);
                System.out.println("file has bean saved");
            }

//            try(OutputStream out = new OutputStream() {
//                @Override
//                public void write(int b) throws IOException {
//                    System.out.write(b);
//                }
//            }){
//            
//            toJSON(out,handler.nodes,handler.ways);
//
//            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

}

/**
 *
 * @author viljinsky
 */
public class NewClass {

}
