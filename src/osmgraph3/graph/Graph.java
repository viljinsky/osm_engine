package osmgraph3.graph;

import java.awt.Color;
import java.awt.Graphics;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import osmgraph3.Browser;
import osmgraph3.controls.GraphRenderer;
import osmgraph3.controls.NodeList;
import osmgraph3.controls.RelationList;
import osmgraph3.controls.WayList;






public class Graph implements TagsObject {

    private GraphRenderer renderer = new GraphRenderer(this);
    
    public void draw(Browser browser,Graphics g){
        renderer.render(browser, g, true);
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

    public Color color = Color.ORANGE;

    public  NodeList nodeList;
    public WayList wayList;
    public RelationList relationList;

    public Tags tags;
    public List<Node> nodes = new ArrayList<>();
    public List<Way> ways = new ArrayList<>();
    public List<Relation> relations = new ArrayList<>();

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

    public void clear() {
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
                nodes.add(node);
            }
        }
        ways.add(way);
        if (wayList != null) {
            wayList.add(way);
        }
        change();

    }

    //------------------------- n o d e s   ------------------------------------
    int last_node = -1;

    public Node add(double lon,double lat){
        return add(new Node(lon,lat));
    }
    
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

public void write(OutputStream out) throws Exception {

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

