package osmgraph3.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author viljinsky
 */
public class Relation extends ArrayList<Member> implements GraphElement {

    public Tags tags;
    public Long id;

    @Override
    public Node center() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
    @Override
    public void put(String key, Object value) {
        if (tags == null) {
            tags = new Tags();
        }
        tags.put(key, value);
    }

    @Override
    public Object get(String key) {
        return tags == null || !tags.containsKey(key) ? null : tags.get(key);
    }

    @Override
    public Set<String> keySet() {
        return tags == null ? new HashSet<>() : tags.keySet();
    }

    @Override
    public boolean containsKey(Object key) {
        return tags.containsKey(key);
    }

    
    
    
    public String toString() {
        return String.format("relation %d : %d", id, size());
    }
    
    public boolean add(Long ref,String type,String role){
        Member member = new Member(ref,type,role);
        return add(member);
    }

    @Override
    public boolean add(Member e) {
        return super.add(e);
    }
    
}


