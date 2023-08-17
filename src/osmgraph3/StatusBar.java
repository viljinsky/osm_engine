/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3;

import java.awt.Container;
import java.awt.FlowLayout;
import javax.swing.JLabel;

/**
 *
 * @author viljinsky
 */
public class StatusBar extends Container {

    JLabel label = new JLabel("StatusBar");

    public StatusBar() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(label);
    }
    
    public void setStatusText(String text){
        label.setText(text);
    }

}


