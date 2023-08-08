package osmgraph3;

import java.awt.Color;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import osmgraph3.controls.NodeList;
import osmgraph3.controls.RelationList;
import osmgraph3.controls.WayList;

interface TagsObject {

    public void put(String key, Object value);

    public Object get(String key);

    public Set<String> keySet();

}

class Tags extends HashMap<String, Object> {

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(",\n", "\n", "\n");
        for (String key : keySet()) {
            String str = ("{'tag' : 'k' : " + key + " 'v' : '" + get(key).toString() + "'}");
            sj.add(str);
        }
        return sj.toString();
    }
}


class Edge {

    Node node1;
    Node node2;

    public Edge(Node node1, Node node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    Node center() {
        return new Node(node1.lon + (node2.lon - node1.lon) / 2, node1.lat + (node2.lat - node1.lat) / 2);
    }
}


class Member {

    String type;
    String role;
    long ref;

    public String toString() {
        return String.format("member %s %s %s", type, ref, role);
    }

}

public class Graph implements TagsObject {

    GraphRenderer renderer = new GraphRenderer(this);

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

    Color color = Color.ORANGE;

    protected NodeList nodeList;
    protected WayList wayList;
    protected RelationList relationList;

    Tags tags;
    List<Node> nodes = new ArrayList<>();
    List<Way> ways = new ArrayList<>();
    List<Relation> relations = new ArrayList<>();

    public Node nodeById(long id) {
        for (Node node : nodes) {
            if (Long.compare(id, node.id) == 0) {
                return node;
            }
        }
        return null;
    }

    public Way wayById(long id) {
        for (Way way : ways) {
            if (way.id == id) {
                return way;
            }

        }
        return null;
    }

    public Graph() {
    }

    public Graph(Color color) {
        this();
        this.color = color;
    }

    public void remove(Node node) {
        nodes.remove(node);
        nodeList.remove(node);
        change();
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

    void clear() {
        relations.clear();
        relationList.clear();

        ways.clear();
        wayList.clear();

        nodes.clear();
        nodeList.clear();

        change();
    }

    //------------------------- w a y s ---------------------------------------- 
    int last_way;

    public void add(Way way) {
        way.id = ++last_way;
        way.put("key", "tag");
        way.put("key", way.id);
        for (Node node : way) {
            if (!nodes.contains(node)) {
                node.id = ++last_node;
            }
            nodes.add(node);
        }
        ways.add(way);
        if (wayList != null) {
            wayList.add(way);
        }
        change();

    }

    //------------------------- n o d e s   ------------------------------------
    int last_node = -1;

    public Node add(Node node) {
        node.id = ++last_node;
        node.put("key", "value");
        nodes.add(node);
        if (nodeList != null) {
            nodeList.add(node);
        }
        change();
        return node;
    }

//    public Node add(Point p) {
//        return add(node(p));
//    }
//    Node nodeAt(Point point) {
//        for (Node node : nodes) {
//            Rectangle r = nodeBound(node);
//            if (r.contains(point)) {
//                return node;
//            }
//        }
//        return null;
//    }
//    void read(InputStream in) {
//
//        /*
//        {'raph':{
//        tag :{'k':'color','v',#FF4466},
//            'node':[
//                {'lon':1.22,'lat'},
//                {},
//            ],
//            'way':[],
//        }}
//         */
//        color = Color.BLUE;
//
//        add(new Node(1, 1));
//        add(new Node(2, 1));
//        add(new Node(2, 2));
//        add(new Node(1, 2));
//        add(new Node(1, 1));
//
//        Way way = new Way();
//        way.add(new Node(3, 1));
//        way.add(new Node(4, 1));
//        way.add(new Node(4, 2));
//        way.add(new Node(3, 2));
//        way.add(new Node(3, 1));
//        way.put("color", Color.PINK);
//        way.put("type", "border");
//        add(way);
//        Node center = wayCenter(way);
//        center.put("type", "center");
//        add(center);
//
//    }
    void write(OutputStream out) throws Exception {

        try (OutputStreamWriter writer = new OutputStreamWriter(out, "utf-8");) {

            writer.write("<osm>\n");

            for (Node node : nodes) {
                writer.write(String.format("<node id='%d', lon='%8.6f', lat = '%8.6f' />\n", node.id, node.lon, node.lat));
            }
            for (Way way : ways) {
                writer.write(String.format("<way id='%d'>\n", way.id));
                for (Node node : way) {
                    writer.write(String.format("\t<nd ref = '%d' />\n", node.id));
                }
                if (way.tags != null) {
                    for (String k : way.keySet()) {
                        writer.write(String.format("\t<tag k='%s', v='%s'/>\n", k, way.get(k)));
                    }
                }
                writer.write("</way>\n");
            }

            writer.write("</osm>\n");
        }
    }

    Relation relationById(long id) {
        for (Relation r : relations) {
            if (r.id == id) {
                return r;
            }
        }
        return null;
    }

}

