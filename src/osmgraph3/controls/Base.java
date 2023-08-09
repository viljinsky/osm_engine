/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.controls;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;

/**
 *
 * @author viljinsky
 */
public class Base extends Container implements WindowListener{

    JFrame frame;
    
    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        frame.setVisible(false);
        frame.dispose();
        frame = null;
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
    

    public Base() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(800,600));
    }
    
    public void execute(){
        frame = new JFrame();
        frame.setContentPane(this);
        frame.addWindowListener(this);
        frame.pack();
        frame.setLocationRelativeTo(getParent());
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        new Base().execute();
        
    }
    
}
