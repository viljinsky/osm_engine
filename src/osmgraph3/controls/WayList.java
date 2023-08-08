/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.controls;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import osmgraph3.graph.Way;

/**
 *
 * @author viljinsky
 */
public class WayList extends JList<Way> {
    
    public JComponent view() {
        return new JScrollPane(this);
    }
    DefaultListModel<Way> model = new DefaultListModel<>();

    public WayList() {
        setModel(model);
    }

    public void add(Way way) {
        model.addElement(way);
    }

    public void remove(Way way) {
        model.removeElement(way);
    }

    public void clear() {
        model.removeAllElements();
    }
    
}
