/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import osmgraph3.Browser;
import osmgraph3.controls.GraphElementList;
import osmgraph3.controls.GraphViewAdapter;
import osmgraph3.controls.SideBar;
import osmgraph3.controls.StatusBar;
import osmgraph3.graph.Node;
import osmgraph3.graph.Way;

class MouseAdapter2 extends MouseAdapter {

    Browser browser;
    Node start = null;

    public MouseAdapter2(Browser browser) {
        this.browser = browser;
        browser.addMouseListener(this);
        browser.addMouseMotionListener(this);
        browser.addMouseWheelListener(this);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Node node = browser.nodeAt(e.getPoint());
        if (node == null){
            node = browser.node(e.getPoint());
            
            if (start!=null && !start.equals(node)){
                browser.graph.add(new Way(start,node));
            }
            start = null;
        }
    }

    
    
    @Override
    public void mousePressed(MouseEvent e) {
        Node node = browser.nodeAt(e.getPoint());
        if (node == null) {
            node = browser.node(e.getPoint());
            browser.graph.add(node);
        }
        start = node;
    }

}


/**
 *
 * @author viljinsky
 */
public class Test3 extends Container {

    Browser browser = new Browser();
    StatusBar statusBar = new StatusBar();
    GraphViewAdapter adapter = new GraphViewAdapter(browser);
    FileManager fileManager = new FileManager(browser);
    GraphElementList nodesList = new GraphElementList();
    SideBar sideBar = new SideBar(nodesList.view());
    
    MouseAdapter2 adapter2 = new MouseAdapter2(browser);

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
