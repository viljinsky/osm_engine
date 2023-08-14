/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.test;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import osmgraph3.Browser;
import osmgraph3.controls.Base;
import osmgraph3.controls.FileManager;
import osmgraph3.controls.GraphAdapter;
import osmgraph3.controls.GraphElementList;
import osmgraph3.controls.GraphManager;
import osmgraph3.controls.SideBar;
import osmgraph3.controls.StatusBar;
import osmgraph3.controls.TagList;

/**
 *
 * @author viljinsky
 */
public class Test5 extends Base implements FileManager.FileManagerListener{

    @Override
    public void onGraphNew(FileManager.FileManagerEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onGraphOpen(FileManager.FileManagerEvent e) {
//        browser.clear();
        browser.setGraph(e.getGraph());
        browser.reset();
    }

    @Override
    public void onGraphSave(FileManager.FileManagerEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void windowOpened(WindowEvent e) {
        
        browser.addLayer(new GraphManager.Graph1Background());
        
        browser.setGraph(new GraphManager.Graph1());
        nodes.setValues(browser.graph.nodes);
        ways.setValues(browser.graph.ways);
        relations.setValues(browser.graph.relations);
        browser.reset();
    }
    
    
    
    FileManager fileManager = new FileManager(this);
    
    Browser browser = new Browser();
    StatusBar statusBar = new StatusBar();
    TagList tagList = new TagList();
    GraphElementList nodes = new GraphElementList(tagList);
    GraphElementList ways = new GraphElementList(tagList);
    GraphElementList relations = new GraphElementList(tagList);
    SideBar sideBar = new SideBar(nodes.view(),ways.view(),relations.view(),tagList.view());
    GraphAdapter adapter = new GraphAdapter(browser);

    public Test5() {
        add(browser);
        add(statusBar,BorderLayout.PAGE_END);
        add(sideBar,BorderLayout.EAST);
        addMenu(fileManager.menu());
        
                
    }
    
    
    
    public static void main(String[] args) {
        new Test5().execute();
    }
    
}
