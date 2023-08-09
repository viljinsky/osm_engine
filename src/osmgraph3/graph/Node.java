package osmgraph3.graph;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 *
 * @author viljinsky
 */
public class Node implements GraphElement {
    
    public long id;
    public double lon;
    public double lat;
    public Tags tags;

    public Node(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "node  %d : %8.6f %8.6f", id, lon, lat);
    }

    @Override
    public Set<String> keySet() {
        return (tags == null) ? new HashSet<>() : tags.keySet();
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
        if (tags == null || !tags.containsKey(key)) {
            return null;
        }
        return tags.get(key);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof Node) {
            Node other = (Node) obj;
            return lon == other.lon && lat == other.lat;
        }
        return false;
    }
    
}
