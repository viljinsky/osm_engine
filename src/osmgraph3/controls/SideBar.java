package osmgraph3.controls;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JComponent;

/**
 *
 * @author viljinsky
 */
public class SideBar extends Container {
    
    public SideBar(JComponent... comp) {
        setLayout(new GridLayout(-1, 1, 1, 1));
        setPreferredSize(new Dimension(200, 400));
        for (JComponent c : comp) {
            add(c);
        }
    }
    
    
    
}
