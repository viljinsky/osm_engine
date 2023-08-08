/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package osmgraph3.graph;

import java.util.Set;

/**
 *
 * @author viljinsky
 */
public interface TagsObject {

    public void put(String key, Object value);

    public Object get(String key);

    public Set<String> keySet();

}


