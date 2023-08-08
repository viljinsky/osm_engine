/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Relation extends ArrayList<Member> implements TagsObject {

    public Tags tags;
    public long id;

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

    public String toString() {
        return String.format("relation %d : %d", id, size());
    }
}


