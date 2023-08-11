/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.controls;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JMenu;
import osmgraph3.Browser;
import osmgraph3.OSMParser;
import osmgraph3.graph.Graph;
import osmgraph3.graph.Way;

/**
 *
 * @author viljinsky
 */
public class GraphManager implements CommandManager.CommandListener {

    public static class Graph1 extends Graph {

        public Graph1() {
            super(Color.BLUE);
            add(3.0, 3.0);
            add(4.0, 3.0);
            add(4.0, 4.0);
            add(3.0, 4.0);
            Way way = new Way();
            way.add(5.0, 3.0);
            way.add(6.0, 3.0);
            way.add(6.0, 4.0);
            way.add(5.0, 4.0);
            way.close();
            add(way);
            way = new Way();
            way.add(5.0, 4.5);
            way.add(6.0, 4.5);
            add(way);

        }

    }

    public static class Graph2 extends Graph {

        public Graph2() {
            super(Color.RED);
            OSMParser parser = new OSMParser(new File("C:\\Users\\viljinsky\\Desktop", "test.osm"));
            nodes = parser.nodes;
            ways = parser.ways;
            relations = parser.relations;
        }

    }

    public static class Graph3 extends Graph {

        public Graph3() {
            super(Color.RED);
            add(1.0, 1.0);
            add(2.0, 2.0);
            add(3.0, 3.0);
            add(4.0, 4.0);
            add(5.0, 5.0);
            add(6.0, 6.0);
            //
            Way way = new Way();
            way.add(1.0, 4.5);
            way.add(2.0, 5.5);
            way.add(3.1, 4.1);
            way.add(4.0, 5.5);
            way.add(4.0, 5.5);
            way.add(4.4, 5.5);
            way.add(4.8, 5.5);
            add(way);

        }

    }

    public static class Graph4 extends Graph {

        public Graph4() {
//        OSMParser parser = new OSMParser(new File("C:\\Users\\viljinsky\\Desktop", "map (2).osm"));
            OSMParser parser = new OSMParser(new File("C:\\Users\\viljinsky\\Desktop", "test.osm"));
            nodes = parser.nodes;
            ways = parser.ways;
            relations = parser.relations;
        }
    }

    public static final String CREATE = "add";
    public static final String READ = "read";
    public static final String WRITE = "write";
    public static final String CLEAR = "delete";
    public static final String NORTH = "^";
    public static final String SOUTH = "V";
    public static final String WEST = "<";
    public static final String EAST = ">";
    public static final String ZOOM_IN = "+";
    public static final String ZOOM_OUT = "-";
    public static final String RESET = "Rst";
    CommandManager commandManager = new CommandManager(this, CREATE, READ, WRITE, CLEAR, null, NORTH, SOUTH, WEST, EAST, null, ZOOM_IN, ZOOM_OUT, null, RESET);
    Browser browser;
    ArrayList<Graph> list;

    @Override
    public void doCommand(String command) {
        Graph graph;
        double dlon = .0;
        double dlat = .0;
        try {
            switch (command) {
                case CLEAR:
                    browser.graph.clear();
                    break;
                case CREATE:
                    graph = new Graph3();
                    list.clear();
                    list.add(graph);
                    browser.setGraph(graph);
                    browser.reset();
                    break;
                case READ:
                    graph = new Graph4();
                    list.clear();
                    list.add(graph);
                    browser.setGraph(graph);
                    browser.reset();
                    break;
                case WRITE:
                    try (OutputStream out = new OutputStream() {
                        @Override
                        public void write(int b) throws IOException {
                            System.out.write(b);
                        }
                    }) {
                    browser.graph.write(out);
                }
                break;
                case ZOOM_IN:
                    browser.zoom_in();
                    break;
                case ZOOM_OUT:
                    browser.zoom_out();
                    break;
                case NORTH:
                    browser.move_noth();
                    break;
                case SOUTH:
                    browser.move_south();
                    break;
                case EAST:
                    browser.move_east();
                    break;
                case WEST:
                    browser.move_west();
                    break;
                case RESET:
                    browser.reset();
                    break;
                default:
                    throw new AssertionError();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GraphManager(Browser browser, ArrayList list) {
        this.browser = browser;
        this.list = list;
    }

    public JComponent commandBar() {
        return commandManager.commandBar();
    }

    public JMenu menu() {
        return commandManager.menu();
    }

}
