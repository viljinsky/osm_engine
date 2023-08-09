/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.graph;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/**
 *
 * @author viljinsky
 */
public class Way extends ArrayList<Node> implements TagsObject {
    
    public long id;
    
    public Tags tags;

    CharSequence write(OutputStreamWriter writer) {
        String result = "";
        for (Node node : this) {
            result += "'ref' : " + node.id + "\n";
        }
        return result;
    }

    public Node center() {
        double minlon = Double.MAX_VALUE;
        double minlat = Double.MAX_VALUE;
        double maxlon = Double.MIN_VALUE;
        double maxlat = Double.MIN_VALUE;
        for(Node node: this){
            minlon = Math.min(minlon, node.lon);
            minlat = Math.min(minlat, node.lat);
            maxlon = Math.max(maxlon, node.lon);
            maxlat = Math.max(maxlat, node.lat);
        }
        return new Node(minlon + (maxlon-minlon)/2, minlat+(maxlat-minlat)/2);
    }

    class EdgeIterator implements Iterator<Edge> {

        int index = 0;

        @Override
        public boolean hasNext() {
            return ++index < size();
        }

        @Override
        public Edge next() {
            return new Edge(get(index - 1), get(index));
        }
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
    public Set<String> keySet() {
        return tags == null ? new HashSet<>() : tags.keySet();
    }

    public Iterable<Edge> edges() {
        return new Iterable<Edge>() {
            @Override
            public Iterator<Edge> iterator() {
                return new EdgeIterator();
            }
        };
    }

    public void add(double  lon,double  lat){
        super.add(new Node(lon,lat));
    }
    
    public Node first() {
        return isEmpty() ? null : get(0);
    }

    public Node last() {
        return isEmpty() ? null : get(size() - 1);
    }

    public String toString() {
        return String.format(Locale.US, "way %d : %d nodes %s", id, size(), isClosed() ? "closed" : "");
    }

    public void close() {
        add(first());
    }

    public boolean isClosed() {
        return size() > 1 && first().equals(last());
    }
    
}
