/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.controls;

import osmgraph3.CommandManager;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import osmgraph3.graph.GraphElement;

public class TagValues extends JComponent implements CommandManager.CommandListener {

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

    GraphElement tags;

    DefaultTableModel model = new DefaultTableModel(0, 2);

    JTable table = new JTable(model);

    public TagValues() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(new JScrollPane(table));
    }

    public void setValues(GraphElement tags) {
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

