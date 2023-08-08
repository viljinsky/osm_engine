/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3;

import osmgraph3.graph.Graph;
import osmgraph3.graph.TagsObject;
import osmgraph3.graph.Node;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Iterator;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

class GraphListener extends MouseAdapter {

    Browser browser;

    public GraphListener(Browser browser) {
        this.browser = browser;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Node node = browser.nodeAt(e.getPoint());
        if (node != null) {
            browser.onClickNode(node);
        }
    }

}

interface GraphElement {
}

class GraphEvent extends EventObject {

    public static final int CHANGE = 0;
    public static final int ADD = 1;
    public static final int REMOVE = 2;

    GraphElement element;
    int eventType;

    public GraphEvent(Object source) {
        super(source);
    }

    public GraphEvent(Object source, GraphElement element, int eventType) {
        super(source);
        this.eventType = eventType;
        this.element = element;
    }

}

interface GrpahChangeListener extends ChangeListener {

    public void add(GraphEvent e);

    public void remove(GraphEvent e);
}







class GraphList extends JList<Graph> implements Iterable<Graph> {

    DefaultListModel<Graph> model = new DefaultListModel<>();

    JComponent view() {
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


class TagEditor extends JComponent implements CommandManager.CommandListener {

    public static final String ADD = "add";
    public static final String DELETE = "delete";
    public static final String POST = "post";

    @Override
    public void doCommand(String command) {
        switch (command) {
            case ADD:
                model.addRow(new Object[]{String.format("key%d", model.getRowCount()), "value"});
                break;
            case DELETE:
                if (table.getSelectedRowCount() > 0) {
                    model.removeRow(table.getSelectedRow());
                }
                break;
            case POST:
                for (int i = 0; i < model.getRowCount(); i++) {
                    String key = (String) model.getValueAt(i, 0);
                    Object value = model.getValueAt(i, 1);
                    tags.put(key, value);
                }

                break;
        }
    }

    CommandManager commandManager = new CommandManager(this, ADD, DELETE, POST);

    TagsObject tags;

    DefaultTableModel model = new DefaultTableModel(0, 2);

    JTable table = new JTable(model);

    public TagEditor() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JScrollPane(table));
    }

    public void setTags(TagsObject tags) {
        this.tags = tags;

        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
        if (tags != null) {
            for (String key : tags.keySet()) {
                model.addRow(new Object[]{key, tags.get(key)});
            }
        }
    }

    public JComponent view() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table));
        panel.add(commandManager.commandBar(), BorderLayout.PAGE_START);
        return panel;
    }

}

