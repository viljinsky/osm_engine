/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.controls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import osmgraph3.graph.Node;
import osmgraph3.graph.Way;

/**
 *
 * @author viljinsky
 */
public class GraphAdapter2 extends MouseAdapter {
    
    Browser browser;
    Node start = null;

    public GraphAdapter2(Browser browser) {
        this.browser = browser;
        browser.setAdapter(this);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Node node = browser.nodeAt(e.getPoint());
        if (node == null) {
            node = browser.node(e.getPoint());
            if (start != null && !start.equals(node)) {
                browser.graph.add(new Way(start, node));
            }
            start = null;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Node node = browser.nodeAt(e.getPoint());
        if (node == null) {
            node = browser.node(e.getPoint());
            browser.graph.add(node);
        }
        start = node;
    }
    
}
