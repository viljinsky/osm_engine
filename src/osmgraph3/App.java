package osmgraph3;

import osmgraph3.controls.TagValues;
import osmgraph3.graph.Way;
import osmgraph3.graph.Node;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import osmgraph3.controls.Base;
import osmgraph3.controls.CommandManager;
import osmgraph3.controls.FileManager;
import osmgraph3.controls.GraphElementList;
import osmgraph3.controls.GraphAdapter;
import osmgraph3.controls.SideBar;
import osmgraph3.controls.StatusBar;
import osmgraph3.graph.Graph;
import osmgraph3.graph.GraphElement;

/**
 *
 * @author viljinsky
 */
class App extends Base implements CommandManager.CommandListener, FileManager.FileManagerListener, ChangeListener {
    
    public static final String ADD = "add";
    public static final String EDIT = "edit";
    public static final String DELETE = "delete";
    public static final String CLEAR = "clear";
    public static final String READ = "read";
    public static final String WEST = "<";
    private static final String EAST = ">";
    public static final String NOTH = "V";
    public static final String SOUTH = "^";
    public static final String ZOOM_IN = "+";
    public static final String ZOOM_OUT = "-";
    public static final String RESET = "Rst";
    
    CommandManager commandManager = new CommandManager(this, ADD, EDIT, DELETE, null, ZOOM_IN, ZOOM_OUT, null, WEST, NOTH, EAST, SOUTH, null, RESET, CLEAR);
    Browser browser = new Browser();
    TagValues tagEditor = new TagValues();
    GraphElementList nodeList = new GraphElementList(tagEditor);
    GraphElementList wayList = new GraphElementList(tagEditor);
    GraphElementList relationList = new GraphElementList(tagEditor);
    
    SideBar sideBar = new SideBar(wayList.view(), nodeList.view(), relationList.view(), tagEditor.view());
    JComponent commandBar = commandManager.commandBar();
    StatusBar statusBar = new StatusBar();
    
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
        throw new UnsupportedOperationException("Not supported yet.");
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
                default:
                    showErrorMessage("command \"" + command + "\" - unsupported yet");
            }
        } catch (Exception e) {
            showMessage(e.getMessage());
        }
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        statusBar.setStatusText(browser.statusText());
        
    }
    
    FileManager fileManager = new FileManager(this);
    
    MouseListener listListener = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            GraphElementList list = (GraphElementList) e.getSource();
            GraphElement ge = (GraphElement) list.getSelectedValue();
            browser.setCenter(ge.center());
        }
        
    };
    
    @Override
    public void windowOpened(WindowEvent e) {
        try {
            String home = System.getProperty("user.home");
            fileManager.open(new File(home+"/osm", "test.osm"));
        } catch (Exception h) {
            showErrorMessage(h.getMessage());
        }
    }
    
    public App() {
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, browser, sideBar);
        splitPane.setResizeWeight(1.0);
        add(splitPane);
        add(commandBar, BorderLayout.PAGE_START);
        add(statusBar, BorderLayout.PAGE_END);
        addMenu(fileManager.menu());
        
        nodeList.addMouseListener(listListener);
        wayList.addMouseListener(listListener);
        
        browser.addChangeListener(this);
        
        GraphAdapter ma = new GraphAdapter(browser) {
            @Override
            public void over(GraphElement e) {
                statusBar.setStatusText(e.toString());
            }
            
            @Override
            public void click(GraphElement e) {
                nodeList.clearSelection();
                wayList.clearSelection();
                relationList.clearSelection();
                
                if (e instanceof Node) {
                    nodeList.setSelectedValue(e, true);
                }
                if (e instanceof Way) {
                    wayList.setSelectedValue(e, true);
                }
            }
            
        };
    }
    
    public static void main(String[] args) {
        new App().execute();
    }
    
}
