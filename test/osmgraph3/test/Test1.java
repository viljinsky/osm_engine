package osmgraph3.test;

import osmgraph3.controls.GraphManager;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import osmgraph3.Browser;
import osmgraph3.controls.StatusBar;
import osmgraph3.graph.Edge;
import osmgraph3.graph.Graph;
import osmgraph3.graph.Node;
import osmgraph3.graph.Way;

class DefaultMouseAdapter extends MouseAdapter {

    Browser browser;

    Node start;

    public DefaultMouseAdapter(Browser browser) {
        this.browser = browser;
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        Graph graph = browser.graph;

        if (graph == null) {
            return;
        }

        if (start != null) {
            Node node = browser.nodeAt(e.getPoint());

            if (node == null) {

                node = browser.node(e.getPoint());
                if (start != null) {
                    Way way = new Way(start, node);
                    graph.add(way);
                }

            } else if (node != null && !node.equals(start)) {

                Way way = new Way(start, node);
                graph.add(way);
            }
            start = null;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Graph graph = browser.graph;
        if (graph == null) {
            return;
        }

        for (Way way : graph.ways) {

            if (browser.wayRectangle(way).contains(e.getPoint())) {
                System.out.println("way cenner " + way);
            }

            for (Edge edge : way.edges()) {
                Node center = edge.center();
                if (browser.nodeRectangle(center).contains(e.getPoint())) {
                    browser.graph.add(center);
                    way.add(way.indexOf(edge.node2), center);
                    System.out.println("edge click :" + edge + " " + way);
                    return;
                }
            }
        }

        start = browser.nodeAt(e.getPoint());
        if (start != null) {
            System.out.println("nodeClick : " + start);
        } else {
            start = browser.node(e.getPoint());
            browser.graph.add(start);
            return;
        }

    }

}

/**
 *
 * @author viljinsky
 */
public class Test1 extends JPanel {

    StatusBar statusBar = new StatusBar();

    ArrayList<Graph> list = new ArrayList<>();

    Browser browser = new Browser();

    GraphManager graphMamager = new GraphManager(browser, list);

    public Test1() {
        super(new BorderLayout());
        add(browser);
        add(graphMamager.commandBar(), BorderLayout.PAGE_START);
        add(statusBar, BorderLayout.PAGE_END);
        MouseListener l = new DefaultMouseAdapter(browser);
        browser.addMouseListener(l);
        browser.addChangeListener(e -> {
            statusBar.setStatusText(browser.statusText());
        });
        browser.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                statusBar.setStatusText(browser.statusText(e.getPoint()));
            }

        });
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
