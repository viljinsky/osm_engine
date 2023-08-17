package osmgraph3;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JComponent;

/**
 *
 * @author viljinsky
 */
public class CommandBar extends JComponent {

    public CommandBar() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new JButton("button"));
    }

}

