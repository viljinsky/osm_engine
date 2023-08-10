package osmgraph3;

import osmgraph3.controls.TagList;
import osmgraph3.graph.Graph;
import osmgraph3.graph.Way;
import osmgraph3.graph.Relation;
import osmgraph3.graph.Member;
import osmgraph3.graph.Node;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import osmgraph3.controls.CommandManager;
import osmgraph3.controls.GraphList;
import osmgraph3.controls.NodeList;
import osmgraph3.controls.RelationList;
import osmgraph3.controls.SideBar;
import osmgraph3.controls.StatusBar;
import osmgraph3.controls.WayList;
import osmgraph3.graph.Edge;
import osmgraph3.graph.GraphElement;

class Graph1 extends Graph {

    public Graph1() {
        super(Color.BLUE);
        add(3.0, 3.0);
        add(4.0, 3.0);
        add(4.0, 4.0);
        add(3.0, 4.0);
        Way way = new Way();
        way.add(5.0, 3.0);
        way.add(6.0, 3.0);
        way.add(6.0, 4.0);
        way.add(5.0, 4.0);
        way.close();
        add(way);
        way = new Way();
        way.add(5.0, 4.5);
        way.add(6.0, 4.5);
        add(way);

    }

}

class Graph2 extends Graph {

    public Graph2() {
        super(Color.RED);
        OSMParser parser = new OSMParser(new File("C:\\Users\\viljinsky\\Desktop", "test.osm"));
        nodes = parser.nodes;
        ways = parser.ways;
        relations = parser.relations;
    }

}

/**
 *
 * @author viljinsky
 */
class App extends Container implements CommandManager.CommandListener, ChangeListener {

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
    public static final String WEST = "<";
    private static final String EAST = ">";
    public static final String NOTH = "V";
    public static final String SOUTH = "^";
    public static final String ZOOM_IN = "+";
    public static final String ZOOM_OUT = "-";
    public static final String RESET = "Rst";
    Graph graph;

    @Override
    public void doCommand(String command) {
        try {
            switch (command) {
                case RESET:
                    browser.reset();
                    break;
                case ZOOM_IN:
                    browser.zoom_in();
                    break;
                case ZOOM_OUT:
                    browser.zoom_out();
                    break;
                case WEST:
                    browser.move_west();
                    break;

                case EAST:
                    browser.move_east();
                    break;
                case NOTH:
                    browser.move_noth();
                    break;
                case SOUTH:
                    browser.move_south();
                    break;
                case CLEAR:
                    browser.graph.clear();
                    break;
                case ADD:
                    graphList.add(new Graph1());
                    browser.reset();
                    break;
                case EDIT:
                    break;
                case DELETE:
                    graphList.remove(browser.graph);
                    break;
                case READ:
                    graphList.add(new Graph2());
                    browser.reset();
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
    CommandManager commandManager = new CommandManager(this, ADD, EDIT, DELETE, null, ZOOM_IN, ZOOM_OUT, null, WEST, NOTH, EAST, SOUTH, null, RESET, CLEAR, null, READ, WRITE);
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
    TagList tagEditor = new TagList();
    SideBar sideBar = new SideBar(graphList.view(), wayList.view(), nodeList.view(), relationList.view(), tagEditor.view());
    JComponent commandBar = commandManager.commandBar();
    StatusBar statusBar = new StatusBar();
    ListSelectionListener tagsListener = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            JList list = (JList) e.getSource();
            GraphElement tags = (GraphElement) list.getSelectedValue();
            tagEditor.setTags(tags);
        }
    };

    @Override
    public void stateChanged(ChangeEvent e) {
        statusBar.setStatusText(browser.statusText());

    }

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
        nodeList.addListSelectionListener(e->{
            Node node = nodeList.getSelectedValue();
            if (node!=null){
                browser.setCenter(node);
            }
        });
        relationList.addListSelectionListener(tagsListener);
        wayList.addListSelectionListener(tagsListener);
        setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, browser, sideBar);
        splitPane.setResizeWeight(1.0);
        add(splitPane);
        add(commandBar, BorderLayout.PAGE_START);
        add(statusBar, BorderLayout.PAGE_END);
//        browser.addMouseListener(new GraphListener(browser));
        browser.addChangeListener(this);
        browser.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                statusBar.setStatusText(browser.statusText() + " " + String.format("x : %d y : %d lon : %.3f lat : %.3f", e.getX(), e.getY(), browser.xToLon(e.getX()), browser.yToLat(e.getY())));

                Graph g = browser.graph;
                if (g != null) {
                    for (Node node : g.nodes) {
                        int x = browser.lonToX(node.lon);
                        int y = browser.latToY(node.lat);
                        Rectangle R = new Rectangle(x - 3, y - 3, 6, 6);
                        if (R.contains(e.getPoint())) {
                            statusBar.setStatusText(node.toString());
                        }
                    }
                    for(Way way : g.ways){
                        for(Edge edge:way.edges()){
                            Node c = edge.center();
                            int x = browser.lonToX(c.lon);
                            int y = browser.latToY(c.lat);
                            Rectangle R = new Rectangle(x - 3, y - 3, 6, 6);
                            if (R.contains(e.getPoint())) {
                                statusBar.setStatusText(way.toString()+" "+edge.toString());
                            }
                        }
                    }
                }
            }

        });
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
