/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.controls;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import osmgraph3.graph.Node;

/**
 *
 * @author viljinsky
 */
public class NodeList extends JList<Node> {
    
    public JComponent view() {
        return new JScrollPane(this);
    }
    DefaultListModel<Node> model = new DefaultListModel<>();

    public NodeList() {
        setModel(model);
    }

    public void add(Node node) {
        model.addElement(node);
    }

    public void remove(Node node) {
        model.removeElement(node);
    }

    public void clear() {
        model.removeAllElements();
    }
    
}
