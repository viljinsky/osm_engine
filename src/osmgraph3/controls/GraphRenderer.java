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

    Color lineColor = Color.BLUE;
    Color nodeColor = Color.RED;
    Color boundColor = Color.LIGHT_GRAY;
    Color nameColor = Color.GRAY;

    public void render(Browser browser,Graph graph, Graphics g, boolean selected) {

        Rectangle r;
        
        for (Node node : graph.nodes) {
            r = browser.nodeRectangle(node);
            g.setColor(nodeColor);
            g.drawRect(r.x, r.y, r.width, r.height);
            if (node.containsKey("name")){
                g.setColor(nameColor);
                g.drawString((String)node.get("name"), r.x, r.y - 8);
            }
        }


        for (Way way : graph.ways) {


            g.setColor(nodeColor);
            for (Node node : way) {
                r = browser.nodeRectangle(node);
                g.drawRect(r.x, r.y, r.width, r.height);
            }

            g.setColor(boundColor);
            r = browser.wayRectangle(way);
            g.drawRect(r.x, r.y, r.width, r.height);

            for (Edge edge : way.edges()) {
                g.setColor(lineColor);
                int x1 = browser.lonToX(edge.node1.lon);
                int y1 = browser.latToY(edge.node1.lat);
                int x2 = browser.lonToX(edge.node2.lon);
                int y2 = browser.latToY(edge.node2.lat);
                g.drawLine(x1, y1, x2, y2);

                r = browser.nodeRectangle(edge.center());
                g.setColor(nodeColor);
                g.drawLine(r.x, r.y, r.x + r.width, r.y + r.height);
                g.drawLine(r.x, r.y + r.height, r.x + r.width, r.y);
            }

            if (way.size() > 2) {
                g.setColor(nodeColor);
                Node center = way.center();
                r = browser.nodeRectangle(center);
                g.drawLine(r.x - 3, r.y - 3, r.x + 3, r.y + 3);
                g.drawLine(r.x - 3, r.y + 3, r.x + 3, r.y - 3);
                if (way.containsKey("name")){
                    g.setColor(Color.RED);
                    g.drawString((String)way.get("name"), r.x, r.y-7);
                }
            }

        }

    }
}
