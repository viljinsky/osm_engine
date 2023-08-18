package osmgraph3.controls;

import osmgraph3.CommandManager;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.util.EventObject;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import osmgraph3.OSMParser;
import osmgraph3.graph.Graph;

/**
 *
 * @author viljinsky
 */
public class FileManager implements CommandManager.CommandListener {

    Component parent = null;

    public static class FileManagerEvent extends EventObject {

        Graph graph;
        File file;

        public FileManagerEvent(Object source) {
            super(source);
        }

        public FileManagerEvent(Object source, Graph graph) {
            super(source);
            this.graph = graph;
        }

        public FileManagerEvent(Object source, File file) {
            super(source);
            this.file = file;
        }

        public FileManagerEvent(Object source, Graph graph, File file) {
            super(source);
            this.graph = graph;
            this.file = file;
        }

        public Graph getGraph() {
            return graph;
        }

        public File getFile() {
            return file;
        }

    }

    public interface FileManagerListener {

        public void onGraphNew(FileManagerEvent e);

        public void onGraphOpen(FileManagerEvent e);

        public void onGraphSave(FileManagerEvent e);
    }

    FileManagerListener listener;

    public static final String OPEN = "open";
    public static final String SAVE = "save";
    public static final String NEW = "new";
    public static final String SAVE_AS = "save_as";
    public static final String EXIT = "exit";

    public FileManager(FileManagerListener listener) {
        this.listener = listener;
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
            JOptionPane.showMessageDialog(parent, e.getMessage());
        }
    }

    public JComponent commandBar() {
        return commandManager.commandBar();
    }

    public JMenu menu() {
        return commandManager.menu("File");
    }
    File source;
    String userDir = System.getProperty("user.home");
    File dir = new File(userDir, "/osm");

    public void onFileOpen(Graph graph) {
    }

    public void open() throws Exception {
        JFileChooser fileChooser = new JFileChooser(dir);
        fileChooser.setSelectedFile(source);
        int ret_val = fileChooser.showOpenDialog(parent);
        if (ret_val == JFileChooser.APPROVE_OPTION) {
            open(fileChooser.getSelectedFile());
        }
    }

    public File lastOpennedFile() {
        File file = new File(System.getProperty("user.home") + "/osm", "test.osm");
        return file.exists() ? file : null;
    }

    public void open(File file) throws Exception {
        OSMParser p = new OSMParser(file);
        Graph graph = new Graph();
        graph.nodes = p.nodes;
        graph.ways = p.ways;
        graph.relations = p.relations;
        listener.onGraphOpen(new FileManagerEvent(this, graph, file));
        source = file;
        dir = file.getParentFile();
    }

    public void save() throws Exception {
        JFileChooser fileChooser = new JFileChooser(dir);
        fileChooser.setSelectedFile(source);
        int ret_val = fileChooser.showSaveDialog(parent);
        if (ret_val == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (file.exists() && !(JOptionPane.showConfirmDialog(parent, "file exists.Replace") == JOptionPane.YES_OPTION)) {
                throw new RuntimeException("file exists");
            }
            file = fileChooser.getSelectedFile();
            try (FileOutputStream out = new FileOutputStream(file)) {
                listener.onGraphSave(new FileManagerEvent(this, file));
//                browser.graph.write(out);
                source = file;
            }
        }
    }

    public void save_as() {
    }


    public void create() {
        listener.onGraphNew(new FileManagerEvent(this));
    }

}
