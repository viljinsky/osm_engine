/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.controls;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;

/**
 *
 * @author viljinsky
 */
public class CommandManager extends ArrayList<Action> {
    
    public interface CommandListener {

        public void doCommand(String command);
    }
    CommandListener commandListener;

    public CommandManager(CommandListener commandListener) {
        this.commandListener = commandListener;
    }

    public CommandManager(CommandListener commandListener, String... commands) {
        this(commandListener);
        Action a;
        for (String command : commands) {
            if (command != null) {
                a = new AbstractAction(command) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        doCommand(e.getActionCommand());
                    }
                };
            } else {
                a = null;
            }
            add(a);
        }
    }

    public void doCommand(String command) {
        commandListener.doCommand(command);
    }

    public JComponent commandBar() {
       // StatusBar commandBar = new StatusBar();
       CommandBar commandBar = new CommandBar();
       commandBar.removeAll();
        for (Action a : this) {
            if (a != null) {
                commandBar.add(new JButton(a));
            } else {
                commandBar.add(new JLabel("|"));
            }
        }
        return commandBar;
    }

    public JMenu menu() {
        JMenu menu = new JMenu("File");
        for (Action a : this) {
            if (a == null) {
                menu.addSeparator();
            } else {
                menu.add(a);
            }
        }
        return menu;
    }
    
}
