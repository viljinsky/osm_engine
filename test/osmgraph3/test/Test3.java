/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.test;

import osmgraph3.controls.FileManager;
import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import osmgraph3.controls.Browser;
import osmgraph3.Base;
import osmgraph3.controls.GraphElementList;
import osmgraph3.controls.GraphAdapter;
import osmgraph3.controls.GraphManager;
import osmgraph3.controls.GraphAdapter2;
import osmgraph3.SideBar;
import osmgraph3.StatusBar;
import osmgraph3.graph.Graph;

/**
 *
 * @author viljinsky
 */
public class Test3 extends Base implements FileManager.FileManagerListener {

    @Override
    public void onGraphNew(FileManager.FileManagerEvent e) {
        try {
            Graph g = new GraphManager.Graph4();
            browser.setGraph(g);
            nodesList.setValues(g.nodes);
            browser.reset();
        } catch (Exception h) {
            h.printStackTrace();
        }
    }

    @Override
    public void onGraphOpen(FileManager.FileManagerEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onGraphSave(FileManager.FileManagerEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    Browser browser = new Browser();
    StatusBar statusBar = new StatusBar();
    GraphAdapter adapter = new GraphAdapter(browser);
    FileManager fileManager = new FileManager(this);
    GraphElementList nodesList = new GraphElementList();
    SideBar sideBar = new SideBar(nodesList.view());

    GraphAdapter2 adapter2 = new GraphAdapter2(browser);

    @Override
    public void windowOpened(WindowEvent e) {
        fileManager.create();
    }

    public Test3() {
        add(browser);
        add(statusBar, BorderLayout.PAGE_END);

        add(sideBar, BorderLayout.EAST);
        browser.setAdapter(adapter);
        browser.addChangeListener(e -> {
            statusBar.setStatusText(browser.statusText());
        });
    }

    public static void main(String[] args) {
        new Test3().execute();
    }

}
