/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.controls;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import osmgraph3.graph.Relation;

/**
 *
 * @author viljinsky
 */
public class RelationList extends JList<Relation> {
    
    DefaultListModel<Relation> model = new DefaultListModel<>();

    public RelationList() {
        setModel(model);
    }

    public void add(Relation relation) {
        model.addElement(relation);
    }

    public void remove(Relation relation) {
        model.removeElement(relation);
    }

    public JComponent view() {
        return new JScrollPane(this);
    }

    public void clear() {
        model.removeAllElements();
    }
    
}
