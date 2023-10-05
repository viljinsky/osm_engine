/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.test;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import osmgraph3.Base;
import osmgraph3.CommandManager;
import osmgraph3.SideBar;
import osmgraph3.StatusBar;
import osmgraph3.controls.Browser;
import osmgraph3.controls.BuildingRenderer;
import osmgraph3.controls.FileManager;
import osmgraph3.controls.GraphElementList;
import osmgraph3.controls.GraphManager;
import osmgraph3.controls.GraphRenderer;
import osmgraph3.controls.TagValues;
import osmgraph3.graph.Edge;
import osmgraph3.graph.Graph;
import osmgraph3.graph.GraphElement;
import osmgraph3.graph.Node;
import osmgraph3.graph.Relation;
import osmgraph3.graph.Way;

class DefaultGraphAdapter extends MouseAdapter {

    Browser browser;

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
        browser.addMouseListener(this);
        browser.addMouseMotionListener(this);
        browser.addMouseWheelListener(this);
    }

    public void removeBrowser() {
        if (browser != null) {
            browser.removeMouseListener(this);
            browser.removeMouseMotionListener(this);
            browser.removeMouseWheelListener(this);
            browser = null;
        }
    }

    public void nodeAdd(Node node) {
    }

    public void nodeRemove(Node node) {
    }

    public void wayAdd(Way way) {
    }

    public void wayRemove(Way way) {
    }
    
    public void nodeClick(Node node){
    }
    
    public void wayClick(Way way){
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() > 0) {
            browser.zoom_in();
        } else {
            browser.zoom_out();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        start = e.getPoint();
        for(Node node:browser.graph.nodes){
            if (browser.nodeRectangle(node).contains(e.getPoint())){
                nodeClick(node);
                return;
            }
        }
        for(Way way:browser.graph.ways){
            if (browser.nodeRectangle(way.center()).contains(e.getPoint())){
                wayClick(way);
                return;
            }
            for(Edge edge:way.edges() ){
                if (browser.nodeRectangle(edge.center()).contains(e.getPoint())){
                    wayClick(way);
                    return;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        start = null;
    }

    
    Point start;
    @Override
    public void mouseDragged(MouseEvent e) {
        if (start != null && e.getPoint() != start) {
            int dx = start.x - e.getX();
            int dy = - start.y + e.getY();
            browser.move(dx / browser.zoom, dy / browser.zoom);
        }
        start = e.getPoint();        
    }

}

// просмотр
class GraphAdapter0 extends DefaultGraphAdapter {

}

// росовние путей
class GraphAdapter1 extends DefaultGraphAdapter {

    Way way;

    @Override
    public void mousePressed(MouseEvent e) {
        Node node = browser.nodeAt(e.getPoint());
        if (node == null) {
            node = browser.node(e.getPoint());
            browser.graph.add(node);
            nodeAdd(node);
        } else {

            if (way != null && way.last() == node) {
                wayAdd(way);
                way = null;
                return;

            }
            if (way != null && way.size() > 1 && way.first() == node) {
                way.close();
                way = null;
                browser.repaint();
                return;
            }
        }

        if (way == null) {
            way = new Way(node);
            browser.graph.add(way);
            wayAdd(way);
        } else {
            way.add(node);
        }

        browser.repaint();
    }

}
// Перемечение путей точе и рёбр
class GraphAdapter2 extends DefaultGraphAdapter {

    Node selectedNode;

    Way selectedWay;

    Edge selectedEdge;

    public void nodeAdd(Node node) {
    }

    public void nodeClick(Node node) {
        selectedNode = node;
        System.out.println("pressed " + node);
    }

    public void edgeClick(Edge edge) {
        selectedEdge = edge;
        System.out.println("pressed " + edge);
    }

    public void wayClick(Way way) {
        selectedWay = way;
        System.out.println("pressed " + way);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if (selectedNode != null) {
            Node node = browser.node(e.getPoint());
            selectedNode.lon = node.lon;
            selectedNode.lat = node.lat;
            browser.repaint();
        }

        if (selectedEdge != null) {
            Node center = selectedEdge.center();
            Node node = browser.node(e.getPoint());
            double dlon = node.lon - center.lon;
            double dlat = node.lat - center.lat;
            selectedEdge.node1.lon += dlon;
            selectedEdge.node1.lat += dlat;
            selectedEdge.node2.lon += dlon;
            selectedEdge.node2.lat += dlat;
            browser.repaint();

        }

        if (selectedWay != null) {
            Node center = selectedWay.center();
            Node node = browser.node(e.getPoint());
            double dlon = node.lon - center.lon;
            double dlat = node.lat - center.lat;
            int size = selectedWay.isClosed() ? selectedWay.size() - 1 : selectedWay.size();
            for (int i = 0; i < size; i++) {
                Node n = selectedWay.get(i);
                n.lon += dlon;
                n.lat += dlat;

            }
            browser.repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

        selectedEdge = null;
        selectedWay = null;
        selectedNode = null;

        Graph graph = browser.graph;
        if (graph == null) {
            return;
        }
        for (Node node : graph.nodes) {
            if (browser.nodeRectangle(node).contains(e.getPoint())) {
                nodeClick(node);
                return;
            }
        }

        for (Way way : graph.ways) {
            if (browser.nodeRectangle(way.center()).contains(e.getPoint())) {
                wayClick(way);
                return;
            }
            for (Edge edge : way.edges()) {
                Node center = edge.center();
                if (browser.nodeRectangle(center).contains(e.getPoint())) {

                    browser.graph.add(center);
                    if (edge.node2 == way.first()) {
                        way.add(way.size() - 1, center);
                    } else {
                        way.add(way.indexOf(edge.node2), center);
                    }
                    nodeAdd(center);
                    nodeClick(center);//Click(edge);
                    browser.repaint();
                    return;
                }
            }
        }
//        click(e);
    }

}

class GraphAdapter3 extends DefaultGraphAdapter{

    @Override
    public void mousePressed(MouseEvent e) {
//        super.mousePressed(e); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        Node node = browser.nodeAt(e.getPoint());
        if (node == null){
            node = browser.node(e.getPoint());
        }
        browser.graph.add(node);
        nodeAdd(node);
    }
    
}

/**
 *
 * @author viljinsky
 */
public class Test5 extends Base implements 
        CommandManager.CommandListener,
        FileManager.FileManagerListener ,
        ListSelectionListener
{

    @Override
    public void valueChanged(ListSelectionEvent e) {
        GraphElementList list = (GraphElementList)e.getSource();
        GraphElement element = (GraphElement)list.getSelectedValue();
        if (element!=null){
            tagValues.setValues(element);
        }
    }
    
    

    @Override
    public void onGraphNew(FileManager.FileManagerEvent e) {
        
        Graph graph = new Graph();
        browser.setGraph(graph);
        nodes.setValues(graph.nodes);
        ways.setValues(graph.ways);
        relations.setValues(graph.relations);
        
        
        browser.reset();
        
        
    }

    GraphElementList relations = new GraphElementList();
    
    @Override
    public void onGraphOpen(FileManager.FileManagerEvent e) {
        Graph graph = e.getGraph();
        browser.setGraph(graph);
        nodes.setValues(graph.nodes);
        ways.setValues(graph.ways);
        relations.setValues(graph.relations);
        
        
        browser.reset();
    }

    @Override
    public void onGraphSave(FileManager.FileManagerEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
    FileManager fileManager = new FileManager(this);
    
    StatusBar statusBar = new StatusBar();
    
    public void setStatusText(String text){
        statusBar.setStatusText(text);
    }

    DefaultGraphAdapter adapter;
    
    GraphRenderer renderer0 = new GraphRenderer();
    
    GraphRenderer renderer1 = new BuildingRenderer();
    
    GraphRenderer renderer = renderer0;

    public static final String ADAPTER0 = "adatpter0";
    public static final String ADAPTER1 = "adapter1";
    public static final String ADAPTER2 = "adapter2";
    public static final String ADAPTER3 = "adapter3";
    
    public static final String RENDERER0  = "renderer0";
    public static final String RENDERER1 = "renderer1";

    void setAdapter(DefaultGraphAdapter adapter) {
        if (this.adapter != null) {
            this.adapter.removeBrowser();
        }
        this.adapter = adapter;
        this.adapter.setBrowser(browser);
    }

    @Override
    public void doCommand(String command) {
        switch (command) {
            case RENDERER0:
                browser.setDefaultRenderer(renderer0);
                break;
            case RENDERER1:
                browser.setDefaultRenderer(renderer1);
                break;
            case ADAPTER0:
                setAdapter(adapter0);
                break;
            case ADAPTER1:
                setAdapter(adapter1);
                break;
            case ADAPTER2:
                setAdapter(adapter2);
                break;
            case ADAPTER3:
                setAdapter(adapter3);
                break;
        }
    }

    CommandManager commandManager = new CommandManager(this, ADAPTER0, ADAPTER1, ADAPTER2,ADAPTER3,null,RENDERER0,RENDERER1);

    Browser browser = new Browser();
    GraphElementList nodes = new GraphElementList();
    GraphElementList ways = new GraphElementList();
    TagValues tagValues = new TagValues();
    SideBar sideBar = new SideBar(nodes.view(), ways.view(),relations.view(), tagValues.view());

    DefaultGraphAdapter adapter0 = new GraphAdapter0(){
        @Override
        public void wayClick(Way way) {
            ways.setSelectedValue(way, true);
        }

        @Override
        public void nodeClick(Node node) {
            nodes.setSelectedValue(node, true);
        }
        
    };
    
    DefaultGraphAdapter adapter3 = new GraphAdapter3(){
        @Override
        public void nodeAdd(Node node) {
            nodes.add(node);
        }
        
    };
    
    DefaultGraphAdapter adapter2 = new GraphAdapter2() {
        @Override
        public void wayRemove(Way way) {
            ways.remove(way);
        }

        @Override
        public void wayAdd(Way way) {
            ways.add(way);
        }

        @Override
        public void nodeRemove(Node node) {
            nodes.remove(node);
        }

        @Override
        public void nodeAdd(Node node) {
            nodes.add(node);
        }
    };

    DefaultGraphAdapter adapter1 = new GraphAdapter1() {

        @Override
        public void wayRemove(Way way) {
            ways.remove(way);
        }

        @Override
        public void wayAdd(Way way) {
            ways.add(way);
        }

        @Override
        public void nodeRemove(Node node) {
            nodes.remove(node);
        }

        @Override
        public void nodeAdd(Node node) {
            nodes.add(node);
        }

    };

    @Override
    public void windowOpened(WindowEvent e) {
        Graph graph = new Graph();//GraphManager.Graph1();
        browser.setGraph(graph);
        nodes.setValues(graph.nodes);
        ways.setValues(graph.ways);
        browser.reset();
        setAdapter(adapter0);
    }
    
    class ElementListListener extends KeyAdapter{

        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()){
                case KeyEvent.VK_DELETE:
                    GraphElementList list = (GraphElementList)e.getSource();
                    Object obj = list.getSelectedValue();
                    System.out.println("DELETE:"+ obj);
                    if (obj instanceof Node){
                        browser.graph.remove((Node)obj);
                        list.remove(obj);
                    }
                    if (obj instanceof Way){
                        browser.graph.remove((Way)obj);
                        list.remove(obj);
                    }
                    
                    if (obj instanceof Relation){
                        browser.graph.remove((Relation)obj);
                        list.remove(obj);
                    }
                    break;
            }
        }
        
    }
    
    ElementListListener keyListener = new ElementListListener();

    public Test5() {
        add(browser);
        add(sideBar, BorderLayout.EAST);
        add(statusBar,BorderLayout.PAGE_END);
        add(commandManager.commandBar(), BorderLayout.PAGE_START);
        
        nodes.addKeyListener(keyListener);
        ways.addKeyListener(keyListener);
        relations.addKeyListener(keyListener);
        
        nodes.addListSelectionListener(this);
        ways.addListSelectionListener(this);        
        relations.addListSelectionListener(this);
        
        addMenu(fileManager.menu());
                

    }

    public static void main(String[] args) {
        new Test5().execute();
    }

}
