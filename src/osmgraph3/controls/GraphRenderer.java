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

        int xoffset = -(int) (browser.minlon * browser.zoom);
        int yoffset = -(int) (browser.minlat * browser.zoom);

        for (Node node : graph.nodes) {
            Rectangle r = browser.nodeBound(node);
            g.drawRect(xoffset + r.x, yoffset + r.y, r.width, r.height);

        }

        Rectangle r;

        for (Way way : graph.ways) {

            g.setColor(Color.LIGHT_GRAY);
            r = browser.wayBound(way);
//            if (r.width<5 && r.height<5) continue;
            g.drawRect(xoffset + r.x, yoffset + r.y, r.width, r.height);

            for (Edge edge : way.edges()) {
                g.setColor(graph.color);
                int x1 = xoffset + (int) (edge.node1.lon * browser.zoom);
                int y1 = yoffset + (int) (edge.node1.lat * browser.zoom);
                int x2 = xoffset + (int) (edge.node2.lon * browser.zoom);
                int y2 = yoffset + (int) (edge.node2.lat * browser.zoom);
                g.drawLine(x1, y1, x2, y2);

                r = browser.nodeBound(edge.center());
                g.drawLine(xoffset + r.x, yoffset + r.y, xoffset + r.x + r.width, yoffset + r.y + r.height);
                g.drawLine(xoffset + r.x, yoffset + r.y + r.height, xoffset + r.x + r.width, yoffset + r.y);
            }

            if (way.size() > 2) {
                g.setColor(Color.LIGHT_GRAY);
                Node center = way.center();//browser.wayCenter(way);
                r = browser.nodeBound(center);
                g.drawLine(xoffset + r.x - 3, yoffset + r.y - 3, xoffset + r.x + 3, yoffset + r.y + 3);
                g.drawLine(xoffset + r.x - 3, yoffset + r.y + 3, xoffset + r.x + 3, yoffset + r.y - 3);
            }

        }
    }
}


