package osmgraph3.test;

import osmgraph3.controls.GraphElementList;
import osmgraph3.controls.GraphManager;
import osmgraph3.controls.GraphViewAdapter;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import osmgraph3.Browser;
import osmgraph3.controls.SideBar;
import osmgraph3.controls.StatusBar;
import osmgraph3.controls.TagList;
import osmgraph3.graph.Graph;
import osmgraph3.graph.GraphElement;
import osmgraph3.graph.Node;
import osmgraph3.graph.Way;


/**
 *
 * @author viljinsky
 */
public class Test2 extends Container {

    String appName = "Test2";
    JFrame frame;

    List<Graph> graphList = new ArrayList<>();
    Browser browser = new Browser();
    StatusBar statusBar = new StatusBar();
    GraphElementList nodeList = new GraphElementList();
    GraphElementList wayList = new GraphElementList();
    GraphElementList relationList = new GraphElementList();

    TagList tagList = new TagList();

    SideBar sideBar = new SideBar(nodeList.view(), wayList.view(), relationList.view(), tagList.view());

    public static void main(String[] args) {
        new Test2().execute();
    }

    public Test2() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800, 600));
        add(sideBar, BorderLayout.EAST);
        add(browser);
        add(statusBar, BorderLayout.PAGE_END);
        browser.addChangeListener(e -> {
            statusBar.setStatusText(browser.statusText());
        });
    }

    private void execute() {
        frame = new JFrame(appName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        frame.pack();
        frame.setLocationRelativeTo(getParent());
        frame.setVisible(true);

        graphList.add(new GraphManager.Graph4());
        browser.setGraph(graphList.get(0));
        browser.reset();
        nodeList.setList(browser.graph.nodes);
        wayList.setList(browser.graph.ways);
        relationList.setList(browser.graph.relations);
        
        ListSelectionListener listener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                GraphElementList list = (GraphElementList)e.getSource();
                GraphElement element = (GraphElement) list.getSelectedValue();
                if (element != null) {
                    tagList.setTags(element);
                }
            }
            }
        };

        nodeList.addListSelectionListener(listener);
        wayList.addListSelectionListener(listener);
        relationList.addListSelectionListener(listener);
        
        nodeList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                browser.setCenter((Node) nodeList.getSelectedValue());
            }

        });

        
        wayList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                browser.setCenter(((Way) wayList.getSelectedValue()).center());
            }
        });

        MouseAdapter ma = new GraphViewAdapter(browser) {
            @Override
            public void over(GraphElement e) {
                statusBar.setStatusText(e.toString());
            }

            @Override
            public void click(GraphElement e) {
                if (e instanceof Node) {
                    Node node = (Node) e;
                    nodeList.setSelectedValue(node, true);
                }
                if (e instanceof Way){
                    Way way = (Way)e;
                    wayList.setSelectedValue(way, true);
                }
            }

        };
        browser.addMouseListener(ma);
        browser.addMouseMotionListener(ma);
        browser.addMouseWheelListener(ma);
    }

}
