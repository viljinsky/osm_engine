/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.test;

import osmgraph3.controls.FileManager;
import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import osmgraph3.Browser;
import osmgraph3.controls.GraphElementList;
import osmgraph3.controls.GraphAdapter;
import osmgraph3.controls.GraphManager;
import osmgraph3.controls.GraphAdapter2;
import osmgraph3.controls.SideBar;
import osmgraph3.controls.StatusBar;
import osmgraph3.graph.Graph;



/**
 *
 * @author viljinsky
 */
public class Test3 extends Container implements FileManager.FileManagerListener{

    @Override
    public void onGraphNew(FileManager.FileManagerEvent e) {
        Graph g = new Graph();
        browser.setGraph(new GraphManager.Graph4());
        browser.reset();
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
    FileManager fileManager = new FileManager(this){};
    GraphElementList nodesList = new GraphElementList();
    SideBar sideBar = new SideBar(nodesList.view());
    
    GraphAdapter2 adapter2 = new GraphAdapter2(browser);

    public static void main(String[] args) {
        new Test3().execute();
    }

    public void open() {
//        browser.setGraph(new GraphManager.Graph4());
//        browser.reset();
        fileManager.create();
    }

    public Test3() {
        setLayout(new BorderLayout());
        add(browser);
        add(statusBar, BorderLayout.PAGE_END);

        add(sideBar, BorderLayout.EAST);
        browser.addMouseListener(adapter);
        browser.addMouseMotionListener(adapter);
        browser.addMouseWheelListener(adapter);
        browser.addChangeListener(e -> {
            statusBar.setStatusText(browser.statusText());
        });
    }

    private void execute() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileManager.menu());
        frame.setJMenuBar(menuBar);

        frame.pack();
        frame.setLocationRelativeTo(getParent());
        frame.setVisible(true);
        open();
    }

}
