/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import osmgraph3.graph.Edge;
import osmgraph3.graph.Graph;
import osmgraph3.graph.Node;
import osmgraph3.graph.Way;

/**
 *
 * @author viljinsky
 */
public class BrowserMouseAdapter extends MouseAdapter {

    public static final int MODE0 = 0;
    public static final int MODE1 = 1;
    public static final int MODE2 = 2;

    Graph graph;
    Browser browser;

    int mode = MODE2;

    Way way;
    Way way1;
    Node node1;
    Node node2;

    public void setMode(int mode) {
        this.mode = mode;
    }

    public BrowserMouseAdapter(Browser browser, Graph graph) {
        this.browser = browser;
        this.graph = graph;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (node1 != null) {
            Node node = browser.node(e.getPoint());
            node1.lon = node.lon;
            node1.lat = node.lat;
            graph.change();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (node1 != null) {
            node1 = null;
        }
        if (way1 != null) {
            way1 = null;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

        int xoffset = -(int) (browser.minlon * browser.zoom);
        int yoffset = -(int) (browser.minlat * browser.zoom);

        Point p = new Point(xoffset + e.getX(), yoffset + e.getY());
        Node node = browser.nodeAt(e.getPoint());

        switch (mode) {
            case MODE0:
                for (Way w : graph.ways) {
                    Node cenetr = browser.wayCenter(w);
                    Rectangle r = browser.nodeBound(cenetr);
//                    r.x+=xoffset;
//                    r.y+=yoffset;
                    if (r.contains(e.getPoint())) {
                        way1 = w;
                        System.out.println("" + way1);
                        return;
                    }
                }
                node1 = node;
                break;
            case MODE1:
                if (node != null) {
                    graph.remove(node);
                } else {
                    graph.add(browser.node(e.getPoint()));
                }
                break;
            case MODE2:
                if (node == null) {

                    // isEdgeCenter
                    for (Way w : graph.ways) {
                        for (Edge edge : w.edges()) {
                            Rectangle r = browser.nodeBound(edge.center());
                            if (r.contains(p)) {
                                graph.add(edge.center());
                                w.add(w.indexOf(edge.node2), edge.center());
                                return;
                            }
                        }
                    }
                    node = graph.add(browser.node(e.getPoint()));
                }

                if (way != null && node == way.last()) {
                    way = null;
                    break;
                }

                if (way != null && way.size()>1 && node == way.first()) {
                    way.close();//add(node);
                    way = null;
                    graph.change();
                    break;
                }

                if (way == null) {
                    way = new Way();
                    graph.add(way);
                    graph.change();
                }

                if (node != way.last()) {
                    way.add(node);
                    graph.change();
                }
                break;
        }
    }
}


