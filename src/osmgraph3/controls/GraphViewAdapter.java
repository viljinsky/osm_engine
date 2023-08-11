/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.controls;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import osmgraph3.Browser;
import osmgraph3.graph.Edge;
import osmgraph3.graph.Graph;
import osmgraph3.graph.GraphElement;
import osmgraph3.graph.Node;
import osmgraph3.graph.Way;

/**
 *
 * @author viljinsky
 */
public class GraphViewAdapter extends MouseAdapter {
    
    Point start;
    Browser browser;

    public void over(GraphElement e) {
    }
    
    public void click(GraphElement e){
    }

    public GraphViewAdapter(Browser Browser) {
        this.browser = Browser;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        start = e.getPoint();
        Graph graph = browser.graph;
        if (graph != null) {
            for (Node node : graph.nodes) {
                if (browser.nodeRectangle(node).contains(e.getPoint())) {
                    click(node);
                }
            }
            for (Way way : graph.ways) {
                if (browser.nodeRectangle(way.center()).contains(e.getPoint())) {
                    click(way);
                }
                for (Edge edge : way.edges()) {
                    if (browser.nodeRectangle(edge.center()).contains(e.getPoint())) {
                        click(edge);
                    }
                }
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Graph graph = browser.graph;
        if (graph != null) {
            for (Node node : graph.nodes) {
                if (browser.nodeRectangle(node).contains(e.getPoint())) {
                    over(node);
                }
            }
            for (Way way : graph.ways) {
                if (browser.nodeRectangle(way.center()).contains(e.getPoint())) {
                    over(way);
                }
                for (Edge edge : way.edges()) {
                    if (browser.nodeRectangle(edge.center()).contains(e.getPoint())) {
                        over(edge);
                    }
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.isControlDown()) return ;
        if (e.getPoint() != start) {
            int dx = start.x - e.getX();
            int dy = start.y - e.getY();
            browser.move(dx / browser.zoom, dy / browser.zoom);
        }
        start = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        start = null;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            browser.zoom_out();
        } else {
            browser.zoom_in();
        }
    }
    
}
