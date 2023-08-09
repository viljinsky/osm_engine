package osmgraph3.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import osmgraph3.Browser;
import osmgraph3.OSMParser;
import osmgraph3.controls.CommandManager;
import osmgraph3.controls.CommandManager.CommandListener;
import osmgraph3.controls.StatusBar;
import osmgraph3.graph.Graph;
import osmgraph3.graph.Node;

class GraphMamager implements CommandListener {

    public static final String CREATE = "add";
    public static final String READ = "read";
    public static final String WRITE = "write";
    public static final String DELETE = "delete";
    public static final String NORTH = "^";
    public static final String SOUTH = "V";
    public static final String WEST = "<";
    public static final String EAST = ">";
    public static final String ZOOM_IN = "+";
    public static final String ZOOM_OUT = "-";
    public static final String RESET = "Rst";

    CommandManager commandManager = new CommandManager(this, CREATE, READ, WRITE, DELETE, null, NORTH, SOUTH, WEST, EAST,null,ZOOM_IN,ZOOM_OUT,null,RESET);

    Browser browser;
    ArrayList<Graph> list;

    @Override
    public void doCommand(String command) {
        Graph graph;
        double dlon=.0,dlat=.0;
        try {
            switch (command) {
                case CREATE:
                    graph = new Graph(Color.BLACK);
                    //        graph.add(1.0, 1.0);
                    //        graph.add(2.0, 2.0);
                    //        graph.add(3.0, 3.0);
                    //        graph.add(4.0, 4.0);
                    //        graph.add(5.0, 5.0);
                    //        graph.add(6.0, 6.0);
                    //
                    //        Way way = new Way();
                    graph.add(1.0, 4.5);
                    graph.add(2.0, 5.5);
                    graph.add(3.1, 4.1);
                    graph.add(4.0, 5.5);
                    graph.add(4.0, 5.5);
                    graph.add(4.4, 5.5);
                    graph.add(4.8, 5.5);
                    //        graph.add(way);

                    browser.setBound(1.0, 1.0, 6.0, 6.0);
                    browser.zoom = browser.getPreferredSize().width / 5.0;
                    list.clear();
                    list.add(graph);
                    browser.setGraph(graph);

                    break;
                case READ:
                    OSMParser parser = new OSMParser(new File("C:\\Users\\viljinsky\\Desktop", "test.osm"));
                    graph = new Graph();
                    graph.nodes = parser.nodes;
                    graph.ways = parser.ways;
                    graph.relations = parser.relations;
                    browser.setBound(parser.bound());
                    browser.zoom = browser.getWidth() / (parser.maxlon - parser.minlon);
                    //                    browser.zoom = browser.getHeight()/(parser.maxlat - parser.minlat);
                    list.clear();
                    list.add(graph);
                    browser.setGraph(graph);
                    browser.reset();
                    break;
                    
                case ZOOM_IN:
                    browser.zoom_in();
                    break;
                case ZOOM_OUT:
                    browser.zoom_out();
                    break;
                case NORTH:
                    browser.move_noth();
                    break;
                case SOUTH:
                    browser.move_south();
                    break;
                case EAST:
                    browser.move_eact();
                    break;
                case WEST:
                    browser.move_west();
                    break;
                case RESET:
                    browser.reset();
                    break;
                default:
                    throw new AssertionError();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GraphMamager(Browser browser, ArrayList list) {
        this.browser = browser;
        this.list = list;

    }

    public JComponent commandBar() {
        return commandManager.commandBar();
    }

    public JMenu menu() {
        return commandManager.menu();
    }

}

class DefaultMouseAdapter extends MouseAdapter {

    Browser browser;

    public DefaultMouseAdapter(Browser browser) {
        this.browser = browser;
        browser.addMouseListener(this);
        browser.addMouseMotionListener(this);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Graph graph = browser.graph;
        if (graph == null) {
            return;
        }
        for (Node node : graph.nodes) {
            Rectangle r = browser.nodeBound(node);
            if (r.contains(e.getPoint())) {
                browser.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return;
            }
        }
        browser.setCursor(new Cursor(Cursor.DEFAULT_CURSOR) {
        });

    }

    @Override
    public void mousePressed(MouseEvent e) {
        Node center;
        Rectangle r;
        Graph graph = browser.graph;
        if (graph == null) {
            return;
        }

        int xoffset = (int) (browser.minlon * browser.zoom);
        int yoffset = (int) (browser.minlat * browser.zoom);

        Node n = new Node((e.getX() + xoffset) / browser.zoom, (e.getY() + yoffset) / browser.zoom);
        graph.add(n);

//        for (Way way : graph.ways) {
//            center = way.center();
//            center.lon -=browser.minlon;
//            center.lat -=browser.minlat;
//            r = browser.nodeBound(center);
//            if (r.contains(e.getPoint())){
//                System.err.println(way);
//            }
//            for (Edge edge : way.edges()) {
//                center = edge.center();
//                center.lat -= browser.maxlat;
//                center.lon -= browser.minlon;
//
//                r = browser.nodeBound(center);
//                if (r.contains(e.getPoint())) {
//                    System.out.println(edge);
//                }
//            }
//        }
    }

}

/**
 *
 * @author viljinsky
 */
public class Test1 extends JPanel {

    StatusBar statusBar = new StatusBar();

    ArrayList<Graph> list = new ArrayList<>();

    Browser browser = new Browser(list);

    GraphMamager graphMamager = new GraphMamager(browser, list);

    DefaultMouseAdapter mouseAdapter = new DefaultMouseAdapter(browser);

    public Test1() {
        super(new BorderLayout());
        add(browser);
        add(graphMamager.commandBar(), BorderLayout.PAGE_START);
        add(statusBar, BorderLayout.PAGE_END);

//        Graph graph = new Graph(Color.BLACK);
////        graph.add(1.0, 1.0);
////        graph.add(2.0, 2.0);
////        graph.add(3.0, 3.0);
////        graph.add(4.0, 4.0);
////        graph.add(5.0, 5.0);
////        graph.add(6.0, 6.0);
////
////        Way way = new Way();
//        graph.add(1.0, 4.5);
//        graph.add(2.0, 5.5);
//        graph.add(3.1, 4.1);
//        graph.add(4.0, 5.5);
//        graph.add(4.0, 5.5);
//        graph.add(4.4, 5.5);
//        graph.add(4.8, 5.5);
////        graph.add(way);
//
//        browser.setBound(1.0, 1.0, 6.0, 6.0);
//        browser.zoom = browser.getPreferredSize().width / 5.0;
//        list.add(graph);
//        browser.setGraph(graph);
    }

    public static void main(String[] args) {
        new Test1().execute();
    }

    private void execute() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        frame.pack();
        frame.setLocationRelativeTo(getParent());
        frame.setVisible(true);

    }

}
