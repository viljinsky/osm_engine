/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.test;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import osmgraph3.Browser;
import osmgraph3.OSMParser;
import osmgraph3.controls.CommandManager;
import osmgraph3.graph.Graph;

/**
 *
 * @author viljinsky
 */
public class FileManager implements CommandManager.CommandListener {
    
    Browser browser;
    public static final String OPEN = "open";
    public static final String SAVE = "save";
    public static final String NEW = "new";
    public static final String SAVE_AS = "save_as";
    public static final String EXIT = "exit";

    public FileManager(Browser browser) {
        this.browser = browser;
    }
    CommandManager commandManager = new CommandManager(this, NEW, OPEN, SAVE, null, EXIT);

    @Override
    public void doCommand(String command) {
        try {
            switch (command) {
                case OPEN:
                    open();
                    break;
                case NEW:
                    create();
                    break;
                case SAVE:
                    save();
                    break;
                case SAVE_AS:
                    save_as();
                    break;
                case EXIT:
                default:
                    throw new UnsupportedOperationException("unsupported command" + command);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(browser, e.getMessage());
        }
    }

    public JComponent commandBar() {
        return commandManager.commandBar();
    }

    public JMenu menu() {
        return commandManager.menu();
    }
    File source;
    String userDir = System.getProperty("user.home");
    File dir = new File(userDir, "/Desktop");

    public void open() throws Exception {
        JFileChooser fileChooser = new JFileChooser(dir);
        fileChooser.setSelectedFile(source);
        int ret_val = fileChooser.showOpenDialog(browser);
        if (ret_val == JFileChooser.APPROVE_OPTION) {
            OSMParser p = new OSMParser(fileChooser.getSelectedFile());
            Graph graph = new Graph(Color.PINK);
            graph.nodes = p.nodes;
            graph.ways = p.ways;
            graph.relations = p.relations;
            browser.setGraph(graph);
            browser.reset();
            source = fileChooser.getSelectedFile();
            dir = fileChooser.getCurrentDirectory();
        }
    }

    public void save() throws Exception {
        JFileChooser fileChooser = new JFileChooser(dir);
        fileChooser.setSelectedFile(source);
        int ret_val = fileChooser.showSaveDialog(browser);
        if (ret_val == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file.exists() && !(JOptionPane.showConfirmDialog(browser, "file exists.Replace") == JOptionPane.YES_OPTION)) {
                throw new RuntimeException("file exists");
            }
            try (FileOutputStream out = new FileOutputStream(fileChooser.getSelectedFile())) {
                browser.graph.write(out);
                source = file;
            }
        }
    }

    public void save_as() {
    }

    public void create() {
        source = null;
        browser.setGraph(new Graph());
        browser.reset();
    }
    
}
