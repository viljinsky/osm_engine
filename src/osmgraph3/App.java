package osmgraph3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;




/**
 *
 * @author viljinsky
 */
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
                    browser.minlon = 2.0;
                    browser.minlat = 2.0;
                    browser.maxlon = 5.0;
                    browser.maxlat = 5.0;
                    //                    browser.zoom = browser.getWidth()/(browser.maxlon - browser.minlon);
                    browser.zoom = browser.getHeight() / (browser.maxlat - browser.minlat);
                    graph.add(new Node(3.0, 3.0));
                    graph.add(new Node(4.0, 3.0));
                    graph.add(new Node(4.0, 4.0));
                    graph.add(new Node(3.0, 4.0));
                    Way way = new Way();
                    way.add(new Node(5.0, 3.0));
                    way.add(new Node(6.0, 3.0));
                    way.add(new Node(6.0, 4.0));
                    way.add(new Node(5.0, 4.0));
//                    way.add(new Node(5.0, 3.0));
                    way.close();
                    graph.add(way);
                    way = new Way();
                    way.add(new Node(5.0, 4.5));
                    way.add(new Node(6.0, 4.5));
                    graph.add(way);
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
                    browser.maxlon = parser.maxlon;
                    browser.maxlat = parser.maxlat;
                    browser.minlon = parser.minlon;
                    browser.minlat = parser.minlat;
                    browser.zoom = browser.getWidth() / (parser.maxlon - parser.minlon);
                    //                    browser.zoom = browser.getHeight()/(parser.maxlat - parser.minlat);
                    graphList.add(graph);
                    break;
                case WRITE:
                    try (OutputStream out = new OutputStream() {
                        @Override
                        public void write(int b) throws IOException {
                            System.out.write(b);
                        }
                    }) {
                        browser.graph.write(out);
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
    Browser browser = new Browser(graphList) {
        @Override
        void onClickNode(Node node) {
            nodeList.setSelectedValue(node, true);
            wayList.setSelectedIndex(-1);
            for (Way way : graph.ways) {
                if (way.contains(node)) {
                    wayList.setSelectedValue(way, true);
                }
            }
            relationList.setSelectedIndex(-1);
            for (Relation relation : graph.relations) {
                for (Member m : relation) {
                    if (m.ref == node.id) {
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
        //        wayList.addListSelectionListener(e -> {
        //            if (!e.getValueIsAdjusting()) {
        //                Way way = wayList.getSelectedValue();
        //                if (way != null) {
        //                    Graph graph = browser.graph;
        //                    Graph g = new Graph(Color.RED);
        //                    for (Node node : way) {
        //                        g.add(node);
        //                    }
        //                    graphList.add(g);
        //                }
        //            }
        //        });
        //
        //        relationList.addListSelectionListener(e -> {
        //            if (!e.getValueIsAdjusting()) {
        //                Relation r = relationList.getSelectedValue();
        //                if (r != null) {
        //                    Graph graph = browser.graph;
        //                    Graph g = new Graph(Color.BLACK);
        //                    for (Member m : r) {
        //                        System.out.println(m);
        //                        switch (m.type) {
        //                            case "node":
        //                                Node node = graph.nodeById(m.ref);
        //                                if (node == null) {
        //                                    System.out.println(" node not found");
        //                                }
        //                                g.nodes.add(node);
        //                                break;
        //                            case "way":
        //                                Way way = graph.wayById(m.ref);
        //                                if (way == null) {
        //                                    System.out.println(" way not found");
        //                                }
        //                                g.ways.add(way);
        //                                break;
        //                            case "relation":
        //                                g.relations.add(graph.relationById(m.ref));
        //                                break;
        //                        }
        //                    }
        ////                    try{
        ////                    g.write(new OutputStream() {
        ////                        @Override
        ////                        public void write(int b) throws IOException {
        ////                            System.out.write(b);
        ////                        }
        ////                    });
        ////                    } catch (Exception h){
        ////                        h.printStackTrace();
        ////                    }
        //                }
        //            }
        //        });
        //
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
