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


