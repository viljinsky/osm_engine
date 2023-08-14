package osmgraph3;

import osmgraph3.controls.TagList;
import osmgraph3.graph.Way;
import osmgraph3.graph.Node;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import osmgraph3.controls.CommandManager;
import osmgraph3.controls.FileManager;
import osmgraph3.controls.GraphElementList;
import osmgraph3.controls.GraphAdapter;
import osmgraph3.controls.GraphManager;
import osmgraph3.controls.SideBar;
import osmgraph3.controls.StatusBar;
import osmgraph3.graph.Graph;
import osmgraph3.graph.GraphElement;

/**
 *
 * @author viljinsky
 */
class App extends Container implements CommandManager.CommandListener, FileManager.FileManagerListener, ChangeListener {

    @Override
    public void onGraphOpen(FileManager.FileManagerEvent e) {
        Graph graph = e.getGraph();
        browser.setGraph(graph);
        nodeList.setValues(graph.nodes);
        wayList.setValues(graph.ways);
        relationList.setValues(graph.relations);
        browser.reset();
    }

    @Override
    public void onGraphSave(FileManager.FileManagerEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onGraphNew(FileManager.FileManagerEvent e) {
        Graph graph = new Graph();
        browser.setGraph(graph);
        nodeList.setValues(graph.nodes);
        wayList.setValues(graph.ways);
        relationList.setValues(graph.relations);
        browser.reset();
        
    }

    
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
//                case ADD:
//                    browser.setGraph(new GraphManager.Graph1());
//                    nodeList.setList(browser.graph.nodes);
//                    wayList.setList(browser.graph.ways);
//                    relationList.setList(browser.graph.relations);
////                    graphList.add(new GraphManager.Graph1());
//                    browser.reset();
//                    break;
                case EDIT:
                    break;
//                case DELETE:
//                    graphList.remove(browser.graph);
//                    break;
//                case READ:
//                    browser.setGraph(new GraphManager.Graph2());
//                    nodeList.setList(browser.graph.nodes);
//                    wayList.setList(browser.graph.ways);
//                    relationList.setList(browser.graph.relations);
//                    
//                    browser.reset();
//                    break;
//                case WRITE:
//                    try (OutputStream out = new OutputStream() {
//                        @Override
//                        public void write(int b) throws IOException {
//                            System.out.write(b);
//                        }
//                    }) {
//                    browser.graph.write(out);
//                }
//                break;
                default:
                    throw new UnsupportedOperationException("command \"" + command + "\" - unsupported yet");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(getParent(), e.getMessage(), appName, JOptionPane.ERROR_MESSAGE);
        }
    }
    CommandManager commandManager = new CommandManager(this, ADD, EDIT, DELETE, null, ZOOM_IN, ZOOM_OUT, null, WEST, NOTH, EAST, SOUTH, null, RESET, CLEAR);
//    GraphList graphList = new GraphList();
    Browser browser = new Browser();

    TagList tagEditor = new TagList();
    GraphElementList nodeList = new GraphElementList(tagEditor);
    GraphElementList wayList = new GraphElementList(tagEditor);
    GraphElementList relationList = new GraphElementList(tagEditor);

    SideBar sideBar = new SideBar(wayList.view(), nodeList.view(), relationList.view(), tagEditor.view());
    JComponent commandBar = commandManager.commandBar();
    StatusBar statusBar = new StatusBar();

    @Override
    public void stateChanged(ChangeEvent e) {
        statusBar.setStatusText(browser.statusText());

    }

    FileManager fileManager = new FileManager(this);
//    {
//        @Override
//        public void onFileOpen(Graph graph) {
//            browser.setGraph(graph);
//            nodeList.setValues(graph.nodes);
//            wayList.setValues(graph.ways);
//            relationList.setValues(graph.relations);
//            browser.reset();           
//        }
//        
//    };

    public App() {
//        nodeList.addListSelectionListener(tagsListener);
//        relationList.addListSelectionListener(tagsListener);
//        wayList.addListSelectionListener(tagsListener);

        MouseListener listListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                GraphElementList list = (GraphElementList) e.getSource();
                GraphElement ge = (GraphElement) list.getSelectedValue();
                browser.setCenter(ge.center());
            }

        };

        nodeList.addMouseListener(listListener);
        wayList.addMouseListener(listListener);

        setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, browser, sideBar);
        splitPane.setResizeWeight(1.0);
        add(splitPane);
        add(commandBar, BorderLayout.PAGE_START);
        add(statusBar, BorderLayout.PAGE_END);
        browser.addChangeListener(this);

        GraphAdapter ma = new GraphAdapter(browser) {
            @Override
            public void over(GraphElement e) {
                statusBar.setStatusText(e.toString());
            }

            @Override
            public void click(GraphElement e) {
                if (e instanceof Node) {
                    nodeList.setSelectedValue(e, true);
                }
                if (e instanceof Way) {
                    wayList.setSelectedValue(e, true);
                }
            }

        };
//        browser.addMouseListener(ma);
//        browser.addMouseMotionListener(ma);
//        browser.addMouseWheelListener(ma);

    }

    public void execute() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(commandManager.menu());
        menuBar.add(fileManager.menu());
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
