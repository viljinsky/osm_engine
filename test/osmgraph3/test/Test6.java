/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.test;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import osmgraph3.Browser;
import osmgraph3.controls.Base;
import osmgraph3.controls.FileManager;
import osmgraph3.controls.GraphAdapter;
import osmgraph3.controls.SideBar;
import osmgraph3.controls.StatusBar;
import osmgraph3.controls.TagValues;
import osmgraph3.graph.Graph;
import osmgraph3.graph.GraphElement;
import osmgraph3.graph.Node;
import osmgraph3.graph.Relation;
import osmgraph3.graph.Way;

class GraphFilter extends Graph {

    public GraphFilter(Graph source, String tagName) {
        for (Node node : source.nodes) {
            if (node.containsKey(tagName)) {
                add(node);
            }
        }
        for (Way way : source.ways) {
            if (way.containsKey(tagName)) {
                add(way);
            }
        }
        for (Relation relation : source.relations) {
            if (relation.containsKey(tagName)) {
                add(relation);
            }
        }

    }

}

class ElementListModel extends DefaultListModel<GraphElement> {

    public ElementListModel(Graph source,String key) {

        for (Node node : source.nodes) {
            if(node.containsKey(key)){
                addElement(node);
            }
        }
        for (Way way : source.ways) {
            if(way.containsKey(key)){
                addElement(way);
            }
        }
        for (Relation r : source.relations) {
            if(r.containsKey(key)){
            addElement(r);
            }
        }

    }

}

class TagListModel extends DefaultListModel {

    public TagListModel(Graph source) {
        HashSet<String> set = new HashSet<>();
        for (Node node : source.nodes) {
            for (String key : node.keySet()) {
                set.add(key);
            }
        }
        for (Way way : source.ways) {
            for (String key : way.keySet()) {
                set.add(key);
            }
        }

        for (Relation r : source.relations) {
            for (String key : r.keySet()) {
                set.add(key);
            }
        }
        ArrayList<String> sorted = new ArrayList<>(set);
        Collections.sort(sorted);
        for (String k : sorted) {
            addElement(k);
        }

    }

}

/**
 *
 * @author viljinsky
 */
public class Test6 extends Base implements ListSelectionListener, FileManager.FileManagerListener {

    JList<String> tagList = new JList<>();
    JList<GraphElement> elements = new JList<>();
    TagValues tagValues = new TagValues();
    Graph graph;
    FileManager fileManager = new FileManager(this);
    Browser browser = new Browser();
    StatusBar statusBar = new StatusBar();

    @Override
    public void onGraphNew(FileManager.FileManagerEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void onGraphOpen(FileManager.FileManagerEvent e) {

        graph = e.getGraph();
        tagList.setModel(new TagListModel(graph));
        tagList.setSelectedValue("building", true);
        setStatusText(e.getFile().getName());
    }

    @Override
    public void onGraphSave(FileManager.FileManagerEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Graph filter(Graph source, String key) {

        Graph result = new GraphFilter(source, key);
        
        DefaultListModel<GraphElement> m = new ElementListModel(result,key);
        for(int i=0;i<m.size();i++){
            if (!m.get(i).containsKey(key)){
                System.err.println( m.get(i));
            }
        }

        elements.setModel(m);

        return result;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            Graph g = filter(graph, tagList.getSelectedValue());
            browser.setGraph(g);
            browser.reset();
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

        File file = new File(System.getProperty("user.home") + "/osm", "test.osm");
        try {
            fileManager.open(file);
        } catch (Exception h) {
            System.err.println(h.getMessage());
        }
    }

    void setStatusText(String text) {
        statusBar.setStatusText(text);
    }

    public Test6() {
        add(statusBar, BorderLayout.PAGE_END);
        addMenu(fileManager.menu());
        add(browser);
        SideBar sideBar = new SideBar(new JScrollPane(tagList), new JScrollPane(elements), tagValues.view());
        add(sideBar, BorderLayout.EAST);
        tagList.addListSelectionListener(this);
        GraphAdapter ga = new GraphAdapter(browser);
        elements.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                GraphElement el = elements.getSelectedValue();
                tagValues.setValues(el);
            }
        });

    }

    public static void main(String[] args) {
        new Test6().execute();
    }

}
