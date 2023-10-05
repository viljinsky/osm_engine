package osmgraph3.test;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import osmgraph3.controls.Browser;
import osmgraph3.Base;
import osmgraph3.CommandManager;
import osmgraph3.controls.GraphAdapter;
import osmgraph3.controls.GraphElementList;
import osmgraph3.controls.GraphManager;
import osmgraph3.controls.GraphRenderer;
import osmgraph3.SideBar;
import osmgraph3.controls.TagValues;
import osmgraph3.graph.Graph;
import osmgraph3.graph.Member;
import osmgraph3.graph.Node;
import osmgraph3.graph.Relation;
import osmgraph3.graph.Way;

class TmpWindow extends JPanel {
    
    class GraphRenderer2 extends GraphRenderer{
        
        @Override
        public void render(Browser browser,Graph graph, Graphics g, boolean selected) {
        }
        
        
    }

    Browser browser = new Browser();

    JFrame frame;

    public TmpWindow() {
        setLayout(new BorderLayout());
        add(browser);
        new GraphAdapter();
    }

    public void showInFrame(Graph graph) {
        if (frame == null) {
            frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setContentPane(this);
            frame.pack();
        }
        frame.setVisible(true);
        setGraph(graph);
    }

    void setGraph(Graph graph) {
        browser.setGraph(graph);
        browser.reset();
    }

}

/**
 *
 * @author viljinsky
 */
public class Test2 extends Base implements CommandManager.CommandListener {

    Graph graphFromRelation(Graph source, Relation relation) {
        Graph graph = new Graph();

        for (Member m : relation) {
            switch (m.type) {
                case "way":
                    Way w = source.wayById(m.ref);
                    if (w != null) {
                        Way w2 = new Way();
                        for (Node n : w) {
                            if (source.nodeById(n.id) == null) {
                                continue;
                            }
                            w2.add(n);
                        }
                        graph.add(w2);
                    }
                    break;
                case "node":
//                            System.out.println("->node.id " + m.ref + " " + (g.nodeById(m.ref) == null ? "not found" : "OK"));
                    Node node = source.nodeById(m.ref);
                    if (node != null) {
                        graph.add(node);
                    }
                    break;
                case "relation":
//                            System.out.println("->relation.id " + m.ref + " " + (g.relationById(m.ref) == null ? "not found" : "OK"));
                    break;
            }
        }

        return graph;
    }

    TmpWindow tmpWindow = new TmpWindow();

    public static final String CMD1 = "cmd1";
    public static final String CMD2 = "cmd2";
    public static final String CMD3 = "cmd3";
    public static final String CMD4 = "cmd4";

    class Adapter3 extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            nodes.clearSelection();
            ways.clearSelection();
            Node node = browser.nodeAt(e.getPoint());
            if (node != null) {
                nodes.setSelectedValue(node, true);
                return;
            }
            for (Way way : browser.graph.ways) {

                if (browser.nodeRectangle(way.center()).contains(e.getPoint())) {
                    ways.setSelectedValue(way, true);
                    return;
                }
            }
            System.out.println("not found");
        }

    }

    Adapter3 adapter = new Adapter3();

    TagValues tagList = new TagValues();
    GraphElementList relations = new GraphElementList(tagList);
    GraphElementList nodes = new GraphElementList(tagList);
    GraphElementList ways = new GraphElementList(tagList);

    @Override
    public void doCommand(String command) {
        try {
            switch (command) {
                case CMD1:
                    break;
                case CMD2:
                    break;
                case CMD3:
                case CMD4:
                    showMessage(command);
                    break;
                default:
                    throw new UnsupportedOperationException("unsupported yet");
            }
        } catch (Exception e) {
            showMessage(e);
        }
    }

    String[] commands = {CMD1, CMD2, CMD3, null, CMD4};

    CommandManager commmandManager = new CommandManager(this, commands);

    @Override
    public void windowOpened(WindowEvent e) {

        try{
        
        Graph graph = new GraphManager.Graph4();
        relations.setValues(graph.relations);
        nodes.setValues(graph.nodes);
        ways.setValues(graph.ways);
        browser.setGraph(graph);
        browser.reset();
        browser.addMouseListener(adapter);
//        adapter.setBrowser(browser);
//        browser.setAdapter(adapter);
        ways.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Graph g = browser.graph;
                Way way = (Way) ways.getSelectedValue();
                System.out.println(way);
                for (Node node : way) {
                    System.out.println(node + " " + (g.nodeById(node.id) == null ? "not found" : "OK"));
//                    System.out.println("->"+node.toString()+" "+ g.nodeById(node.id)== null? "not found":"OK");
                }
            }

        });
        relations.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                Relation rel = (Relation) relations.getSelectedValue();
                Graph graph = graphFromRelation(browser.graph, rel);
//                tmpWindow.setGraph(graph);

                tmpWindow.showInFrame(browser.graph);
            }

        });
        } catch (Exception h){
            h.printStackTrace();
        }

    }

    Browser browser = new Browser();

    public Test2() {
        super();
        add(browser);
        new GraphAdapter();
        addMenu(commmandManager.menu("Menu"));
        add(commmandManager.commandBar(), BorderLayout.PAGE_START);

        SideBar sideBar = new SideBar(nodes.view(), ways.view(), relations.view(), tagList.view());
        add(sideBar, BorderLayout.EAST);

    }

    public static void main(String[] args) {
        new Test2().execute();

    }

}
