package osmgraph3.controls;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;

/**
 *
 * @author viljinsky
 */
public class GraphElementList extends JList {
    
    List list = new ArrayList<>();

    class MyModel extends DefaultListModel {

        @Override
        public int getSize() {
            return list.size();
        }

        @Override
        public Object getElementAt(int index) {
            return list.get(index);
        }
    }
    MyModel model = new MyModel();

    public GraphElementList() {
        setModel(model);
    }

    public void setList(List list) {
        this.list = list;
        setModel(new MyModel());
    }

    public JComponent view() {
        return new JScrollPane(this);
    }
    
    public void add(Object element) {
        list.add(element);
       
    }

    public void remove(Object element) {
        list.remove(element);
    }

    public void clear() {
        list.clear();
    }
    
    
}
