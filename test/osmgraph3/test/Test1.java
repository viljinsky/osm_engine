package osmgraph3.test;

import java.awt.BorderLayout;
import java.awt.Color;
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
import osmgraph3.graph.Way;

class Graph3 extends Graph {

    public Graph3() {
        super(Color.RED);
        add(1.0, 1.0);
        add(2.0, 2.0);
        add(3.0, 3.0);
        add(4.0, 4.0);
        add(5.0, 5.0);
        add(6.0, 6.0);
        //
        Way way = new Way();
        way.add(1.0, 4.5);
        way.add(2.0, 5.5);
        way.add(3.1, 4.1);
        way.add(4.0, 5.5);
        way.add(4.0, 5.5);
        way.add(4.4, 5.5);
        way.add(4.8, 5.5);
        add(way);

    }

}

class Graph4 extends Graph {

    public Graph4() {
        OSMParser parser = new OSMParser(new File("C:\\Users\\viljinsky\\Desktop", "test.osm"));
        nodes = parser.nodes;
        ways = parser.ways;
        relations = parser.relations;
    }
}

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

    CommandManager commandManager = new CommandManager(this, CREATE, READ, WRITE, DELETE, null, NORTH, SOUTH, WEST, EAST, null, ZOOM_IN, ZOOM_OUT, null, RESET);

    Browser browser;
    ArrayList<Graph> list;

    @Override
    public void doCommand(String command) {
        Graph graph;
        double dlon = .0, dlat = .0;
        try {
            switch (command) {
                case CREATE:
                    graph = new Graph3();
                    list.clear();
                    list.add(graph);
                    browser.setGraph(graph);
                    browser.reset();

                    break;
                case READ:
                    graph = new Graph4();
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
                    browser.move_east();
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

/**
 *
 * @author viljinsky
 */
public class Test1 extends JPanel {

    StatusBar statusBar = new StatusBar();

    ArrayList<Graph> list = new ArrayList<>();

    Browser browser = new Browser(list);

    GraphMamager graphMamager = new GraphMamager(browser, list);

    public Test1() {
        super(new BorderLayout());
        add(browser);
        add(graphMamager.commandBar(), BorderLayout.PAGE_START);
        add(statusBar, BorderLayout.PAGE_END);
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
