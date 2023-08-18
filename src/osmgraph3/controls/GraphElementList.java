package osmgraph3.controls;

import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import osmgraph3.graph.GraphElement;

/**
 *
 * @author viljinsky
 */
public class GraphElementList extends JList implements ListSelectionListener {

    List list = new ArrayList<>();
    TagValues tagList = null;

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (tagList != null) {
            tagList.setValues((GraphElement) getSelectedValue());
        }
    }

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
        this(null);
    }

    public GraphElementList(TagValues tagLst) {
        this.tagList = tagLst;
        addListSelectionListener(this);
        setModel(model);
    }

    public void setValues(List list) {
        this.list = list;
        setModel(new MyModel());
    }

    public JComponent view() {
        return new JScrollPane(this);
    }

    public void add(Object element) {
//        list.add(element);
        model = new MyModel();
        setModel(model);
        setSelectedValue(element, true);
        
    }

    public void remove(Object element) {
//        list.remove(element);
        model = new MyModel();
        setModel(model);
    }

    public void clear() {
        list.clear();
    }

}
