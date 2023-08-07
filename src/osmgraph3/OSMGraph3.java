/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package osmgraph3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

class Member {

    String type;
    String role;
    long ref;

    public String toString() {
        return String.format("member %s %s %s", type, ref, role);
    }

}

class Relation extends ArrayList<Member> implements TagsObject {

    Tags tags;
    long id;

    @Override
    public void put(String key, Object value) {
        if (tags == null) {
            tags = new Tags();
        }
        tags.put(key, value);
    }

    @Override
    public Object get(String key) {
        return tags == null || !tags.containsKey(key) ? null : tags.get(key);
    }

    @Override
    public Set<String> keySet() {
        return tags == null ? new HashSet<>() : tags.keySet();
    }

    public String toString() {
        return String.format("relation %d : %d", id, size());
    }
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

interface TagsObject {

    public void put(String key, Object value);

    public Object get(String key);

    public Set<String> keySet();

}

class Node implements TagsObject {

    Tags tags;
    long id;
    double lon;
    double lat;

    public Node(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "node  %d : %8.6f %8.6f", id, lon, lat);
    }

    @Override
    public Set<String> keySet() {
        return (tags == null) ? new HashSet<>() : tags.keySet();
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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof Node) {
            Node other = (Node) obj;
            return lon == other.lon && lat == other.lat;
        }
        return false;
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

class Way extends ArrayList<Node> implements TagsObject {

    CharSequence write(OutputStreamWriter writer) {
        String result = "";

        for (Node node : this) {
            result += "'ref' : " + node.id + "\n";
        }

        return result;
    }

    class EdgeIterator implements Iterator<Edge> {

        int index = 0;

        @Override
        public boolean hasNext() {
            return ++index < size();
        }

        @Override
        public Edge next() {
            return new Edge(get(index - 1), get(index));
        }

    }

    long id;

    Tags tags;

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

    Iterable<Edge> edges() {
        return new Iterable<Edge>() {
            @Override
            public Iterator<Edge> iterator() {
                return new EdgeIterator();
            }
        };
    }

    Node first() {
        return isEmpty() ? null : get(0);
    }

    Node last() {
        return isEmpty() ? null : get(size() - 1);
    }

    public String toString() {
        return String.format(Locale.US, "way %d : %d nodes ", id, size());
    }
}

class GraphRenderer {

    Graph graph;

    public GraphRenderer(Graph graph) {
        this.graph = graph;
    }

    public void render(Graphics g, boolean selected) {
        g.setColor(graph.color);

        int xoffset = -(int) (graph.minlon * graph.zoom);
        int yoffset = -(int) (graph.minlat * graph.zoom);

        for (Node node : graph.nodes) {
            Rectangle r = graph.nodeBound(node);
            g.drawRect(xoffset + r.x, yoffset + r.y, r.width, r.height);

        }

        Rectangle r;

        for (Way way : graph.ways) {

            g.setColor(Color.LIGHT_GRAY);
            r = graph.wayBound(way);
            g.drawRect(xoffset + r.x, yoffset + r.y, r.width, r.height);

            for (Edge edge : way.edges()) {
                g.setColor(graph.color);
                int x1 = xoffset + (int) (edge.node1.lon * graph.zoom);
                int y1 = yoffset + (int) (edge.node1.lat * graph.zoom);
                int x2 = xoffset + (int) (edge.node2.lon * graph.zoom);
                int y2 = yoffset + (int) (edge.node2.lat * graph.zoom);
                g.drawLine(x1, y1, x2, y2);

                r = graph.nodeBound(edge.center());
                g.drawLine(xoffset + r.x, yoffset + r.y, xoffset + r.x + r.width, yoffset + r.y + r.height);
                g.drawLine(xoffset + r.x, yoffset + r.y + r.height, xoffset + r.x + r.width, yoffset + r.y);
            }

            if (way.size() > 2) {
                g.setColor(Color.LIGHT_GRAY);
                Node center = graph.wayCenter(way);
                r = graph.nodeBound(center);
                g.drawLine(xoffset + r.x - 3, yoffset + r.y - 3, xoffset + r.x + 3, yoffset + r.y + 3);
                g.drawLine(xoffset + r.x - 3, yoffset + r.y + 3, xoffset + r.x + 3, yoffset + r.y - 3);
            }

        }
    }
}

class Graph implements TagsObject {

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

    double zoom = 100.0;
    double minlon;
    double minlat;
    double maxlon;
    double maxlat;
    int xOffset = 0;
    int yOffset = 0;

    Color color = Color.ORANGE;

    protected NodeList nodeList;
    protected WayList wayList;
    protected RelationList relationList;

    Tags tags;
    List<Node> nodes = new ArrayList<>();
    List<Way> ways = new ArrayList<>();
    List<Relation> relations = new ArrayList<>();

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

    public void paint(Graphics g, boolean selected) {
        renderer.render(g, selected);
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

    public Node add(Point p) {
        return add(node(p));
    }

    /**
     * Создание нового нода
     *
     * @param point точка броузера
     * @return Новый нод
     */
    Node node(Point point) {

//        double lonoffset = minlat
        double lon = minlon + point.x / zoom;
        double lat = minlat + point.y / zoom;
        return new Node(lon, lat);
    }

    Node nodeAt(Point point) {
        for (Node node : nodes) {
            Rectangle r = nodeBound(node);
            if (r.contains(point)) {
                return node;
            }
        }
        return null;
    }

    Rectangle nodeBound(Node node) {
        int x = (int) ((node.lon) * zoom);
        int y = (int) ((node.lat) * zoom);
        return new Rectangle(x - 3, y - 3, 6, 6);
    }

    Node wayCenter(Way way) {
        double min_lon = Double.MAX_VALUE;
        double min_lat = Double.MAX_VALUE;
        double max_lon = Double.MIN_VALUE;
        double max_lat = Double.MIN_VALUE;

        for (Node node : way) {
            min_lon = Math.min(min_lon, node.lon);
            min_lat = Math.min(min_lat, node.lat);
            max_lon = Math.max(max_lon, node.lon);
            max_lat = Math.max(max_lat, node.lat);
        }
        return new Node(min_lon + (max_lon - min_lon) / 2, min_lat + (max_lat - min_lat) / 2);
    }

    Rectangle wayBound(Way way) {
        double min_lon = Double.MAX_VALUE;
        double min_lat = Double.MAX_VALUE;
        double max_lon = Double.MIN_VALUE;
        double max_lat = Double.MIN_VALUE;

        for (Node node : way) {
            min_lon = Math.min(min_lon, node.lon);
            min_lat = Math.min(min_lat, node.lat);
            max_lon = Math.max(max_lon, node.lon);
            max_lat = Math.max(max_lat, node.lat);
        }
        int x = (int) (min_lon * zoom);
        int y = (int) (min_lat * zoom);
        int w = (int) ((max_lon - min_lon) * zoom);
        int h = (int) ((max_lat - min_lat) * zoom);

        return new Rectangle(x, y, w, h);
    }

    void read(InputStream in) {

        /*
        {'raph':{
        tag :{'k':'color','v',#FF4466},
            'node':[
                {'lon':1.22,'lat'},
                {},
            ],
            'way':[],
        }}
         */
        color = Color.BLUE;

        add(new Node(1, 1));
        add(new Node(2, 1));
        add(new Node(2, 2));
        add(new Node(1, 2));
        add(new Node(1, 1));

        Way way = new Way();
        way.add(new Node(3, 1));
        way.add(new Node(4, 1));
        way.add(new Node(4, 2));
        way.add(new Node(3, 2));
        way.add(new Node(3, 1));
        way.put("color", Color.PINK);
        way.put("type", "border");
        add(way);
        Node center = wayCenter(way);
        center.put("type", "center");
        add(center);

    }

    void write(OutputStream out) {
    }

}

class CommandManager extends ArrayList<Action> {

    public interface CommandListener {

        public void doCommand(String command);
    }

    CommandListener commandListener;

    public CommandManager(CommandListener commandListener) {
        this.commandListener = commandListener;
    }

    public CommandManager(CommandListener commandListener, String... commands) {
        this(commandListener);
        Action a;
        for (String command : commands) {

            if (command != null) {
                a = new AbstractAction(command) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        doCommand(e.getActionCommand());
                    }
                };
            } else {
                a = null;
            }
            add(a);
        }

    }

    public void doCommand(String command) {
        commandListener.doCommand(command);
    }

    public JComponent commandBar() {
        CommandBar commandBar = new CommandBar();
        commandBar.removeAll();
        for (Action a : this) {
            if (a != null) {
                commandBar.add(new JButton(a));
            } else {
                commandBar.add(new JLabel("|"));
            }
        }
        return commandBar;
    }

    public JMenu menu() {
        JMenu menu = new JMenu("File");
        for (Action a : this) {
            if (a == null) {
                menu.addSeparator();
            } else {
                menu.add(a);
            }
        }
        return menu;
    }

}

//######################  I N T E R F A C E #######################

class GraphListener extends MouseAdapter {

    Browser browser;

    public GraphListener(Browser browser) {
        this.browser = browser;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Node node = browser.nodeAt(e.getPoint());
        if (node!=null){
            browser.onClickNode(node);
        }
    }

}


class BrowserMouseAdapter extends MouseAdapter {

    public static final int MODE0 = 0;
    public static final int MODE1 = 1;
    public static final int MODE2 = 2;

    Graph graph;
    int mode = MODE2;

    Way way;
    Way way1;
    Node node1, node2;

    public void setMode(int mode) {
        this.mode = mode;
    }

    public BrowserMouseAdapter(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (node1 != null) {
            Node node = graph.node(e.getPoint());
            node1.lon = node.lon;
            node1.lat = node.lat;
            graph.change();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (node1 != null) {
            node1 = null;
        }
        if (way1 != null) {
            way1 = null;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

        int xoffset = (int) (graph.minlon * graph.zoom);
        int yoffset = (int) (graph.minlat * graph.zoom);
        Point p = new Point(xoffset + e.getX(), yoffset + e.getY());
//        System.out.println(p2);

//        Point p = new Point(e.getPoint());
        Node node = graph.nodeAt(p);

        switch (mode) {
            case MODE0:
                for (Way w : graph.ways) {
                    if (graph.nodeBound(graph.wayCenter(w)).contains(p)) {
                        way1 = w;
                        System.out.println("" + way1);
                        return;
                    }
                }
                node1 = node;
                break;
            case MODE1:
                if (node != null) {
                    graph.remove(node);
                } else {
                    graph.add(graph.node(e.getPoint()));
                }
                break;
            case MODE2:
                if (node == null) {

                    // isEdgeCenter
                    for (Way w : graph.ways) {
                        for (Edge edge : w.edges()) {
                            Rectangle r = graph.nodeBound(edge.center());
                            if (r.contains(p /*e.getPoint()*/)) {
                                graph.add(edge.center());
                                w.add(w.indexOf(edge.node2), edge.center());
                                return;
                            }
                        }
                    }

//                    node = graph.node(e.getPoint());
                    node = graph.add(e.getPoint());
                }

                if (way != null && node == way.last()) {
                    way = null;
                    break;
                }

                if (way != null && node == way.first()) {
                    way.add(node);
                    way = null;
                    graph.change();
                    break;
                }

                if (way == null) {
                    way = new Way();
                    graph.add(way);
                    graph.change();
                }

                if (node != way.last()) {
                    way.add(node);
                    graph.change();
                }
                break;
        }
    }

//    @Override
//    public void mouseReleased(MouseEvent e) {
//        Node node = graph.nodeAt(e.getPoint());
//        switch(mode){
//            case MODE2:
//                if (node == null){
//                    node =graph.createNode(e.getPoint());
////                }
////                if (node1!=null && node!=node){
//                    if (way == null){
//                        way = new Way();
//                        way.add(node1);
//                        graph.add(way);
//                    }
//                    way.add(node);                    
//                }
//                break;
//        }
//    }
}

class Browser extends JComponent implements ChangeListener {

    int mode;

    double zoom = 100.0;

    Graph graph;

    Iterable<Graph> graphList;

    WayList wayList = new WayList();

    NodeList nodeList = new NodeList();

    RelationList relationList = new RelationList();

    BrowserMouseAdapter mouseAdapter;

    @Override
    public void stateChanged(ChangeEvent e) {
        repaint();
    }

    public void setGraph(Graph graph) {

        if (graph != null) {
//            graph.zoom = getWidth() / (graph.maxlon - graph.minlon);
            zoom = getHeight() / (graph.maxlat - graph.minlat);
            graph.zoom = zoom;

            nodeList.clear();
            for (Node node : graph.nodes) {
                nodeList.add(node);
            }
            graph.nodeList = nodeList;

            wayList.clear();
            for (Way way : graph.ways) {
                wayList.add(way);
            }
            graph.wayList = wayList;

            relationList.clear();
            for (Relation r : graph.relations) {
                relationList.add(r);
            }
            graph.relationList = relationList;

            graph.addChangeListener(this);
            if (mouseAdapter != null) {
                removeMouseListener(mouseAdapter);
                removeMouseMotionListener(mouseAdapter);
            }
            mouseAdapter = new BrowserMouseAdapter(graph);
            setMode(BrowserMouseAdapter.MODE0);
            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
        } else {
            if (this.graph != null) {
                removeMouseListener(mouseAdapter);
                removeMouseMotionListener(mouseAdapter);
                mouseAdapter = null;
            }
            nodeList.clear();
            wayList.clear();
            relationList.clear();
        }
        this.graph = graph;
        repaint();

    }

    public Browser() {
        setPreferredSize(new Dimension(400, 400));
        addMouseListener(mouseAdapter);
    }

    public Browser(Iterable<Graph> graphList) {
        this();
        this.graphList = graphList;
    }

    @Override
    public void paint(Graphics g) {
        if (graphList != null) {
            for (Graph gr : graphList) {
                gr.paint(g, gr == graph);

            }
        }
    }

    public double getZoom() {
        return zoom;
    }

    void zoom_in() {
        setZoom(zoom * 2.0);
    }

    void zoom_out() {
        setZoom(zoom * 0.5);
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
        for (Graph graph : graphList) {
            graph.zoom = zoom;
        }
        repaint();
        System.out.println("zoom" + zoom);
    }

    void setMode(int mode) {
        this.mode = mode;
        if (mouseAdapter != null) {
            mouseAdapter.mode = mode;
        }
    }
    
    //--------------------------------------------------------------------------
    
    Node nodeAt(Point p){
        if (graph!= null){
            int xoffset = (int) (graph.minlon * graph.zoom);
            int yoffset = (int) (graph.minlat * graph.zoom);
            Point p1 = new Point(xoffset + p.x, yoffset + p.y);
            return graph.nodeAt(p1);
            
        }
        return null;
    }

    void onClickNode(Node node) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}

class SideBar extends Container {

    public SideBar(JComponent... comp) {
        setLayout(new GridLayout(-1, 1, 1, 1));
        setPreferredSize(new Dimension(200, 400));
        for (JComponent c : comp) {
            add(c);
        }
    }

}

class CommandBar extends JComponent {

    public CommandBar() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new JButton("button"));
    }

}

class StatausBar extends Container {

    JLabel label = new JLabel("StatusBar");

    public StatausBar() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(label);
    }

}

// #############################################################################
interface GraphElement {
}

class GraphEvent extends EventObject {

    public static final int CHANGE = 0;
    public static final int ADD = 1;
    public static final int REMOVE = 2;

    GraphElement element;
    int eventType;

    public GraphEvent(Object source) {
        super(source);
    }

    public GraphEvent(Object source, GraphElement element, int eventType) {
        super(source);
        this.eventType = eventType;
        this.element = element;
    }

}

interface GrpahChangeListener extends ChangeListener {

    public void add(GraphEvent e);

    public void remove(GraphEvent e);
}

class WayList extends JList<Way> {

    JComponent view() {
        return new JScrollPane(this);
    }

    DefaultListModel<Way> model = new DefaultListModel<>();

    public WayList() {
        setModel(model);
    }

    public void add(Way way) {
        model.addElement(way);
    }

    public void remove(Way way) {
        model.removeElement(way);
    }

    void clear() {
        model.removeAllElements();
    }

}

class NodeList extends JList<Node> {

    JComponent view() {
        return new JScrollPane(this);
    }

    DefaultListModel<Node> model = new DefaultListModel<>();

    public NodeList() {
        setModel(model);
    }

    public void add(Node node) {
        model.addElement(node);
    }

    public void remove(Node node) {
        model.removeElement(node);
    }

    public void clear() {
        model.removeAllElements();
    }

}

class GraphList extends JList<Graph> implements Iterable<Graph> {

    DefaultListModel<Graph> model = new DefaultListModel<>();

    JComponent view() {
        return new JScrollPane(this);
    }

    public GraphList() {
        setModel(model);
    }

    public void add(Graph graph) {
        model.addElement(graph);
        setSelectedIndex(model.indexOf(graph));
    }

    public void remove(Graph graph) {
        model.removeElement(graph);
    }

    @Override
    public Iterator<Graph> iterator() {

        return new Iterator<Graph>() {
            int index = -1;

            @Override
            public boolean hasNext() {
                return ++index < model.size();
            }

            @Override
            public Graph next() {
                return model.getElementAt(index);
            }
        };
    }

}

class RelationList extends JList<Relation> {

    DefaultListModel<Relation> model = new DefaultListModel<>();

    public RelationList() {
        setModel(model);
    }

    public void add(Relation relation) {
        model.addElement(relation);
    }

    public void remove(Relation relation) {
        model.removeElement(relation);
    }

    public JComponent view() {
        return new JScrollPane(this);
    }

    public void clear() {
        model.removeAllElements();
    }
}

class TagEditor extends JComponent implements CommandManager.CommandListener {

    public static final String ADD = "add";
    public static final String DELETE = "delete";
    public static final String POST = "post";

    @Override
    public void doCommand(String command) {
        switch (command) {
            case ADD:
                model.addRow(new Object[]{String.format("key%d", model.getRowCount()), "value"});
                break;
            case DELETE:
                if (table.getSelectedRowCount() > 0) {
                    model.removeRow(table.getSelectedRow());
                }
                break;
            case POST:
                for (int i = 0; i < model.getRowCount(); i++) {
                    String key = (String) model.getValueAt(i, 0);
                    Object value = model.getValueAt(i, 1);
                    tags.put(key, value);
                }

                break;
        }
    }

    CommandManager commandManager = new CommandManager(this, ADD, DELETE, POST);

    TagsObject tags;

    DefaultTableModel model = new DefaultTableModel(0, 2);

    JTable table = new JTable(model);

    public TagEditor() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JScrollPane(table));
    }

    public void setTags(TagsObject tags) {
        this.tags = tags;

        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
        if (tags != null) {
            for (String key : tags.keySet()) {
                model.addRow(new Object[]{key, tags.get(key)});
            }
        }
    }

    public JComponent view() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table));
        panel.add(commandManager.commandBar(), BorderLayout.PAGE_START);
        return panel;
    }

}

class App extends Container implements CommandManager.CommandListener {

    String appName = "OsmGraph3";

    public static final String ADD = "add";
    public static final String EDIT = "edit";
    public static final String DELETE = "delete";
    public static final String CLEAR = "clear";
    public static final String READ = "read";
    public static final String WRITE = "write";
    public static final String MODE1 = "mode1";
    public static final String MODE2 = "mode2";
    public static final String MODE0 = "mode0";
    public static final String ZOOM_IN = "zoom_in";
    public static final String ZOOM_OUT = "zoom_out";

    Color[] colors = {Color.BLUE, Color.CYAN, Color.MAGENTA, Color.YELLOW};
    int colorIndex = 0;

    Graph graph;

    @Override
    public void doCommand(String command) {
        try {
            switch (command) {
                case ZOOM_IN:
                    browser.zoom_in();
                    break;
                case ZOOM_OUT:
                    browser.zoom_out();
                    break;
                case MODE0:
                    browser.setMode(BrowserMouseAdapter.MODE0);
                    break;
                case MODE1:
                    browser.setMode(BrowserMouseAdapter.MODE1);
                    break;
                case MODE2:
                    browser.setMode(BrowserMouseAdapter.MODE2);
                    break;
                case CLEAR:
                    browser.graph.clear();
                    break;
                case ADD:
                    graph = new Graph(Color.BLUE);
                    graph.minlon = .0;
                    graph.minlat = .0;
                    graph.maxlon = 3.0;
                    graph.maxlat = 3.0;
                    graph.add(new Node(1.0, 1.0));
                    graph.add(new Node(2.0, 1.0));
                    graph.add(new Node(2.0, 2.0));
                    graph.add(new Node(1.0, 2.0));
                    graphList.add(graph);
                    break;

                case EDIT:
                    break;
                case DELETE:
                    graphList.remove(browser.graph);
                    break;
                case READ:

                    OSMParser parser = new OSMParser(new File("C:\\Users\\viljinsky\\Desktop", "test.osm"));
                    graph = new Graph();
                    graph.nodes = parser.nodes;
                    graph.ways = parser.ways;
                    graph.relations = parser.relations;
                    graph.maxlon = parser.maxlon;
                    graph.maxlat = parser.maxlat;
                    graph.minlon = parser.minlon;
                    graph.minlat = parser.minlat;
                    graphList.add(graph);
                    break;
                case WRITE:
                    
                      try (OutputStream out = new OutputStream() {
                        @Override
                        public void write(int b) throws IOException {
                            System.out.write(b);
                        }
                    };) {
//                        for(Graph graph:graphList.){
//                        }  
                }

                break;
                default:
                    throw new UnsupportedOperationException("command \"" + command + "\" - unsupported yet");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(getParent(), e.getMessage(), appName, JOptionPane.ERROR_MESSAGE);
        }
    }

    CommandManager commandManager = new CommandManager(this, ADD, EDIT, DELETE, null, ZOOM_IN, ZOOM_OUT, null, MODE0, MODE1, MODE2, null, CLEAR, null, READ, WRITE);

    GraphList graphList = new GraphList();
    
    Browser browser = new Browser(graphList){
        @Override
        void onClickNode(Node node) {
            nodeList.setSelectedValue(node, true);


            wayList.setSelectedIndex(-1);
            for(Way way: graph.ways){
                if (way.contains(node)){
                    wayList.setSelectedValue(way, true);
                }
            }
            
            relationList.setSelectedIndex(-1);
            for(Relation relation:graph.relations){
                for(Member m:relation){
                    if(m.ref == node.id){
                        relationList.setSelectedValue(relation, true);
                    }
                }
            }
        }
        
    };
    NodeList nodeList = browser.nodeList;
    WayList wayList = browser.wayList;
    RelationList relationList = browser.relationList;

    TagEditor tagEditor = new TagEditor();

    SideBar sideBar = new SideBar(graphList.view(), wayList.view(), nodeList.view(), relationList.view(), tagEditor.view());

    JComponent commandBar = commandManager.commandBar();

    StatausBar statausBar = new StatausBar();

    ListSelectionListener tagsListener = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            JList list = (JList) e.getSource();
            TagsObject tags = (TagsObject) list.getSelectedValue();
            tagEditor.setTags(tags);
        }
    };

    public App() {

        graphList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                GraphList l = (GraphList) e.getSource();
                Graph graph = l.getSelectedValue();
                System.out.println(graph);
                browser.setGraph(graph);
            }
        });

        graphList.addListSelectionListener(tagsListener);
        nodeList.addListSelectionListener(tagsListener);
        relationList.addListSelectionListener(tagsListener);
        wayList.addListSelectionListener(tagsListener);

        setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, browser, sideBar);
        splitPane.setResizeWeight(1.0);

        add(splitPane);
        add(commandBar, BorderLayout.PAGE_START);
        add(statausBar, BorderLayout.PAGE_END);

        browser.addMouseListener(new GraphListener(browser));

    }

    public void execute() {

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(commandManager.menu());

        JFrame frame = new JFrame(appName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        frame.setJMenuBar(menuBar);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        new App().execute();
    }
}

/**
 *
 * @author viljinsky
 */
//public class OSMGraph3 {
//
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String[] args) {
//        new App().execute();
//    }
//
//}
