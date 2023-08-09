/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.controls;

import java.util.Iterator;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import osmgraph3.graph.Graph;

/**
 *
 * @author viljinsky
 */
public class GraphList extends JList<Graph> implements Iterable<Graph> {
    
    DefaultListModel<Graph> model = new DefaultListModel<>();

    public JComponent view() {
        return new JScrollPane(this);
    }

    public GraphList() {
        setModel(model);
    }

    public void add(Graph graph) {
        model.addElement(graph);
        setSelectedIndex(model.indexOf(graph));
    }

    public void remove(Graph graph) {
        model.removeElement(graph);
    }

    @Override
    public Iterator<Graph> iterator() {
        return new Iterator<Graph>() {
            int index = -1;

            @Override
            public boolean hasNext() {
                return ++index < model.size();
            }

            @Override
            public Graph next() {
                return model.getElementAt(index);
            }
        };
    }
    
}
