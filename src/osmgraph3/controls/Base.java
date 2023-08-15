package osmgraph3.controls;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

/**
 *
 * @author viljinsky
 */
public class Base extends Container implements WindowListener{
    
    String appName = "appName";

    JFrame frame;
    
    JMenuBar menuBar;
    
    public void addMenu(JMenu menu){
        if (menuBar == null){
            menuBar = new JMenuBar();
        }
        menuBar.add(menu);
    }
    
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
        frame = new JFrame(appName);
        if (menuBar!=null){
            frame.setJMenuBar(menuBar);
        }
        frame.setContentPane(this);
        frame.addWindowListener(this);
        frame.pack();
        frame.setLocationRelativeTo(getParent());
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        new Base().execute();
        
    }
    
    public void showMessage(String message){
        JOptionPane.showMessageDialog(getParent(), message,appName+" Information",JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void showErrorMessage(String message){
        JOptionPane.showMessageDialog(getParent(), message,appName+" Error",JOptionPane.ERROR_MESSAGE);
    }
}
