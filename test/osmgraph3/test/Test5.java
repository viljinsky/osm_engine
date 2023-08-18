/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.test;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import osmgraph3.Base;
import osmgraph3.CommandManager;
import osmgraph3.SideBar;
import osmgraph3.controls.Browser;
import osmgraph3.controls.GraphElementList;
import osmgraph3.controls.GraphManager;
import osmgraph3.controls.TagValues;
import osmgraph3.graph.Edge;
import osmgraph3.graph.Graph;
import osmgraph3.graph.Node;
import osmgraph3.graph.Way;

class DefaultGraphAdapter extends MouseAdapter {

    Browser browser;

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
        browser.addMouseListener(this);
        browser.addMouseMotionListener(this);
        browser.addMouseWheelListener(this);
    }

    public void removeBrowser() {
        if (browser != null) {
            browser.removeMouseListener(this);
            browser.removeMouseMotionListener(this);
            browser.removeMouseWheelListener(this);
            browser = null;
        }
    }

    public void nodeAdd(Node node) {
    }

    public void nodeRemove(Node node) {
    }

    public void wayAdd(Way way) {
    }

    public void wayRemove(Way way) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() > 0) {
            browser.zoom_in();
        } else {
            browser.zoom_out();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        start = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        start = null;
    }

    
    Point start;
    @Override
    public void mouseDragged(MouseEvent e) {
        if (start != null && e.getPoint() != start) {
            int dx = start.x - e.getX();
            int dy = - start.y + e.getY();
            browser.move(dx / browser.zoom, dy / browser.zoom);
        }
        start = e.getPoint();        
    }

}

class GraphAdapter0 extends DefaultGraphAdapter {


}

class GraphAdapter1 extends DefaultGraphAdapter {

    Way way;

    @Override
    public void mousePressed(MouseEvent e) {
        Node node = browser.nodeAt(e.getPoint());
        if (node == null) {
            node = browser.node(e.getPoint());
            browser.graph.add(node);
            nodeAdd(node);
        } else {

            if (way != null && way.last() == node) {
                wayAdd(way);
                way = null;
                return;

            }
            if (way != null && way.size() > 1 && way.first() == node) {
                way.close();
                way = null;
                browser.repaint();
                return;
            }
        }

        if (way == null) {
            way = new Way(node);
            browser.graph.add(way);
            wayAdd(way);
        } else {
            way.add(node);
        }

        browser.repaint();
    }

}

class GraphAdapter2 extends DefaultGraphAdapter {

    Node selectedNode;

    Way selectedWay;

    Edge selectedEdge;

    public void nodeAdd(Node node) {
    }

//    public void click(MouseEvent e) {
//        Node node = browser.node(e.getPoint());
//        browser.graph.add(node);
//        browser.repaint();
//
//        nodeAdd(node);
//
//        System.out.println("click");
//    }
    public void nodeClick(Node node) {
        selectedNode = node;
        System.out.println("pressed " + node);
    }

    public void edgeClick(Edge edge) {
        selectedEdge = edge;
        System.out.println("pressed " + edge);
    }

    public void wayClick(Way way) {
        selectedWay = way;
        System.out.println("pressed " + way);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if (selectedNode != null) {
            Node node = browser.node(e.getPoint());
            selectedNode.lon = node.lon;
            selectedNode.lat = node.lat;
            browser.repaint();
        }

        if (selectedEdge != null) {
            Node center = selectedEdge.center();
            Node node = browser.node(e.getPoint());
            double dlon = node.lon - center.lon;
            double dlat = node.lat - center.lat;
            selectedEdge.node1.lon += dlon;
            selectedEdge.node1.lat += dlat;
            selectedEdge.node2.lon += dlon;
            selectedEdge.node2.lat += dlat;
            browser.repaint();

        }

        if (selectedWay != null) {
            Node center = selectedWay.center();
            Node node = browser.node(e.getPoint());
            double dlon = node.lon - center.lon;
            double dlat = node.lat - center.lat;
            int size = selectedWay.isClosed() ? selectedWay.size() - 1 : selectedWay.size();
            for (int i = 0; i < size; i++) {
                Node n = selectedWay.get(i);
                n.lon += dlon;
                n.lat += dlat;

            }
            browser.repaint();
        }
    }

//    @Override
//    public void mouseDragged(MouseEvent e) {
//        if (selectedNode != null) {
//            System.out.println("drag " + selectedNode);
//        }
//        if (selectedEdge != null) {
//            System.out.println("drag " + selectedEdge);
//        }
//        if (selectedWay != null) {
//            System.out.println("drag " + selectedWay);
//        }
//    }
    @Override
    public void mousePressed(MouseEvent e) {

        selectedEdge = null;
        selectedWay = null;
        selectedNode = null;

        Graph graph = browser.graph;
        if (graph == null) {
            return;
        }
        for (Node node : graph.nodes) {
            if (browser.nodeRectangle(node).contains(e.getPoint())) {
                nodeClick(node);
                return;
            }
        }

        for (Way way : graph.ways) {
            if (browser.nodeRectangle(way.center()).contains(e.getPoint())) {
                wayClick(way);
                return;
            }
            for (Edge edge : way.edges()) {
                Node center = edge.center();
                if (browser.nodeRectangle(center).contains(e.getPoint())) {

                    browser.graph.add(center);
                    if (edge.node2 == way.first()) {
                        way.add(way.size() - 1, center);
                    } else {
                        way.add(way.indexOf(edge.node2), center);
                    }
                    nodeAdd(center);
                    nodeClick(center);//Click(edge);
                    browser.repaint();
                    return;
                }
            }
        }
//        click(e);
    }

}

/**
 *
 * @author viljinsky
 */
public class Test5 extends Base implements CommandManager.CommandListener {

    DefaultGraphAdapter adapter;

    public static final String ADAPTER0 = "adatpter0";
    public static final String ADAPTER1 = "adapter1";
    public static final String ADAPTER2 = "adapter2";

    void setAdapter(DefaultGraphAdapter adapter) {
        if (this.adapter != null) {
            this.adapter.removeBrowser();
        }
        this.adapter = adapter;
        this.adapter.setBrowser(browser);
    }

    @Override
    public void doCommand(String command) {
        switch (command) {
            case ADAPTER0:
                setAdapter(adapter0);
                break;
            case ADAPTER1:
                setAdapter(adapter1);
                break;
            case ADAPTER2:
                setAdapter(adapter2);
                break;
        }
    }

    CommandManager commandManager = new CommandManager(this, ADAPTER0, ADAPTER1, ADAPTER2);

    Browser browser = new Browser();
    GraphElementList nodes = new GraphElementList();
    GraphElementList ways = new GraphElementList();
    TagValues tagValues = new TagValues();
    SideBar sideBar = new SideBar(nodes.view(), ways.view(), tagValues.view());

    DefaultGraphAdapter adapter0 = new GraphAdapter0();
    DefaultGraphAdapter adapter2 = new GraphAdapter2() {
        @Override
        public void wayRemove(Way way) {
            ways.remove(way);
        }

        @Override
        public void wayAdd(Way way) {
            ways.add(way);
        }

        @Override
        public void nodeRemove(Node node) {
            nodes.remove(node);
        }

        @Override
        public void nodeAdd(Node node) {
            nodes.add(node);
        }
    };

    DefaultGraphAdapter adapter1 = new GraphAdapter1() {

        @Override
        public void wayRemove(Way way) {
            ways.remove(way);
        }

        @Override
        public void wayAdd(Way way) {
            ways.add(way);
        }

        @Override
        public void nodeRemove(Node node) {
            nodes.remove(node);
        }

        @Override
        public void nodeAdd(Node node) {
            nodes.add(node);
        }

    };

    @Override
    public void windowOpened(WindowEvent e) {
        Graph graph = new GraphManager.Graph1();
        browser.setGraph(graph);
        nodes.setValues(graph.nodes);
        ways.setValues(graph.ways);
        browser.reset();
        setAdapter(adapter0);
    }

    public Test5() {
        add(browser);
        add(sideBar, BorderLayout.EAST);
        add(commandManager.commandBar(), BorderLayout.PAGE_START);
//        adapter.setBrowser(browser);

    }

    public static void main(String[] args) {
        new Test5().execute();
    }

}
