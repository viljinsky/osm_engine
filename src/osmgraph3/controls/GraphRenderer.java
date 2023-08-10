/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.controls;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import osmgraph3.Browser;
import osmgraph3.graph.Edge;
import osmgraph3.graph.Graph;
import osmgraph3.graph.Node;
import osmgraph3.graph.Way;

/**
 *
 * @author viljinsky
 */
public class GraphRenderer {

    Graph graph;

    public GraphRenderer(Graph graph) {
        this.graph = graph;
    }

    public void render(Browser browser, Graphics g, boolean selected) {

        g.setColor(graph.color);

        for (Node node : graph.nodes) {
            Rectangle r = browser.nodeBound(node);
            g.drawRect(r.x,r.y, r.width, r.height);
        }

        Rectangle r;

        for (Way way : graph.ways) {

            g.setColor(Color.LIGHT_GRAY);
            r = browser.wayBound(way);
            g.drawRect(r.x, r.y, r.width, r.height);

            for (Edge edge : way.edges()) {
                g.setColor(graph.color);
                int x1 = browser.lonToX(edge.node1.lon) ;
                int y1 = browser.latToY(edge.node1.lat);
                int x2 = browser.lonToX(edge.node2.lon);
                int y2 = browser.latToY(edge.node2.lat);
                g.drawLine(x1, y1, x2, y2);

                r = browser.nodeBound(edge.center());
                g.drawLine(r.x, r.y, r.x + r.width, r.y + r.height);
                g.drawLine(r.x, r.y + r.height, r.x + r.width, r.y);
            }

            if (way.size() > 2) {
                g.setColor(Color.LIGHT_GRAY);
                Node center = way.center();
                r = browser.nodeBound(center);
                g.drawLine(r.x - 3, r.y - 3, r.x + 3, r.y + 3);
                g.drawLine(r.x - 3, r.y + 3, r.x + 3, r.y - 3);
            }

        }
        
        // drow center
        int w = browser.getWidth()/2;
        int h = browser.getHeight()/2;
        g.setColor(Color.BLUE);
        g.drawOval(w, h, 6, 6);
    }
}


