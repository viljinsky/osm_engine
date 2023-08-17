/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.test;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLDocument;
import osmgraph3.OSMParser;
import osmgraph3.Base;
import osmgraph3.controls.GraphManager;
import osmgraph3.SideBar;
import osmgraph3.controls.TagValues;
import osmgraph3.graph.Graph;
import osmgraph3.graph.GraphElement;
import osmgraph3.graph.Node;
import osmgraph3.graph.Relation;
import osmgraph3.graph.Way;

class Graph8 extends Graph{

    public Graph8() throws Exception{
        File file = new File("D:\\my\\netbeans\\OSM\\data\\RU-NGR.osm");
        OSMParser parser = new OSMParser(file);
        nodes = parser.nodes;
        ways = parser.ways;
        relations = parser.relations;
    }
    
}

/**
 *
 * @author viljinsky
 */
public class Test8 extends Base{

    
    
    JList<GraphElement> list = new JList<>();
    SideBar sideBar = new SideBar();
    TagValues tagValues = new TagValues();

    public Test8() throws Exception{
        super();
        add(sideBar,BorderLayout.EAST);
        
        Graph graph = new Graph8();//*/ new GraphManager.Graph2();
        System.out.println("read ok");
        list.setModel(new ElementListModel(graph, "building"));
        sideBar.add(new JScrollPane(list));
        sideBar.add(tagValues.view());
        list.addListSelectionListener(e->{
            tagValues.setValues(list.getSelectedValue());
            
        });
        
    }
    
    

    
    public static void main(String[] args){
        try{
            new Test8().execute();
        } catch (Exception e){
            e.printStackTrace();
        }
        
        
        
    }
    
}
