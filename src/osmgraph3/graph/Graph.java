package osmgraph3.graph;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import osmgraph3.OSMWriter;

/**
 *
 * @author viljinsky
 */
public class Graph implements GraphElement {
    
    public static final String NODE = "node";
    public static final String WAY = "way";
    public static final String RELATION = "relation";

    public Tags tags;
    public List<Node> nodes = new ArrayList<>();
    public List<Way> ways = new ArrayList<>();
    public List<Relation> relations = new ArrayList<>();
    

    @Override
    public Node center() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void put(String key, Object value) {
        if (tags == null) {
            tags = new Tags();
        }
        tags.put(key, value);
    }

    @Override
    public Object get(String key) {
        if (tags == null || !tags.containsKey(key)) {
            return null;
        }
        return tags.get(key);
    }

    @Override
    public Set<String> keySet() {
        return tags == null ? new HashSet<>() : tags.keySet();
    }


    public Node nodeById(Long id) {
        for (Node node : nodes) {
            if (node.id.longValue() == id.longValue()) {
                return node;
            }
        }
        return null;
    }

    public Way wayById(Long id) {
        for (Way way : ways) {
            
            if (way.id.longValue() == id.longValue()) {
                return way;
            }

        }
        return null;
    }
    
    public Relation relationById(Long id){
        for(Relation r:relations){
            if (r.id.longValue() == id.longValue()){
                return r;
            }
        }
        return null;
    }

    public Graph() {
    }


    public void remove(Node node) {
        for(Way way:ways){
            if (way.contains(node)){
                way.remove(node);
                if (way.size()<2){
                    ways.remove(way);
                }
            }
        }
        nodes.remove(node);
        change();
    }
    
    public void remove(Way way){
        for(Node node:way){
            nodes.remove(node);
        }
        ways.remove(way);
        change();
    }
    
    public void remove(Relation relation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "graph";
    }

    ArrayList<ChangeListener> listeners = new ArrayList<>();

    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    public void change() {
        for (ChangeListener listener : listeners) {
            listener.stateChanged(new ChangeEvent(this));
        }
    }

    public void clear() {
        relations.clear();
        ways.clear();
        nodes.clear();
        change();
    }

    //------------------------- w a y s ---------------------------------------- 
    Long last_way = -1L;

    public Way add(Way way) {
        way.id = ++last_way;
        way.put("key", "tag");
        way.put("key", way.id);
        for (Node node : way) {
            if (!nodes.contains(node)) {
                node.id = ++last_node;
                nodes.add(node);
            }
        }
        ways.add(way);
        change();
        return way;

    }
    
    Long last_relation = -1L;
    
    public Relation add(Relation relation){
        relation.id = ++last_relation;
        relations.add(relation);
        return relation;
    }

    //------------------------- n o d e s   ------------------------------------
    Long last_node = -1L;

    public Node add(double lon,double lat){
        return add(new Node(lon,lat));
    }
    
    public Node add(Node node) {
        node.id = ++last_node;
        node.put("key", "value");
        nodes.add(node);
        change();
        return node;
    }

public void write(OutputStream out) throws Exception {
    
    OSMWriter.write(this, out);

//        try (OutputStreamWriter writer = new OutputStreamWriter(out, "utf-8");) {
//
//            writer.write("<?xml version='1.0' encoding='UTF-8'?>\n<osm version='0.6' generator='JOSM'>\n");
//
//            for (Node node : nodes) {
//                writer.write(String.format(Locale.US,"<node id='%d' lon='%8.6f' lat = '%8.6f' visible='true' version='30'/>\n", node.id, node.lon, node.lat));
//            }
//            for (Way way : ways) {
//                writer.write(String.format("<way id='%d' visible='true' version='30'>\n", way.id));
//                for (Node node : way) {
//                    writer.write(String.format("\t<nd ref = '%d' />\n", node.id));
//                }
//                if (way.tags != null) {
//                    for (String k : way.keySet()) {
//                        writer.write(String.format("\t<tag k='%s' v='%s'/>\n", k, way.get(k)));
//                    }
//                }
//                writer.write("</way>\n");
//            }
//            
//            for(Relation relation:relations){
//                writer.write(String.format("<relation id='%d' visible='true' version='30'>\n", relation.id));
//                for(Member m: relation){
//                    writer.write(String.format("\t<member ref='%d' type='%s' role='%s'/>\n", m.ref,m.type,m.role));
//                }
//                writer.write("</relation>\n");
//            }
//
//            writer.write("</osm>\n");
//        }
    }


}

