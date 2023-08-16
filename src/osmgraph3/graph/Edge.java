package osmgraph3.graph;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author viljinsky
 */
public class Edge implements GraphElement{
    
    public Node node1;
    public Node node2;

    public Edge(Node node1, Node node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    public Node center() {
        return new Node(node1.lon + (node2.lon - node1.lon) / 2, node1.lat + (node2.lat - node1.lat) / 2);
    }
    
    public String toString(){
        return String.format("edge  : %d %d", node1.id,node2.id);
    }

    @Override
    public void put(String key, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Object get(String key) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public Set<String> keySet() {
        return new HashSet<>();
    }

    @Override
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
    
    
}
