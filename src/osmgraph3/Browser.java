package osmgraph3;

import osmgraph3.graph.Graph;
import osmgraph3.graph.Way;
import osmgraph3.graph.Relation;
import osmgraph3.graph.Node;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import osmgraph3.controls.NodeList;
import osmgraph3.controls.RelationList;
import osmgraph3.controls.WayList;

/**
 *
 * @author viljinsky
 */
public class Browser extends JComponent implements ChangeListener {

    public double zoom;
    public double minlon;
    public double minlat;
    public double maxlon;
    public double maxlat;
    public Graph graph;
    public Iterable<Graph> graphList;
    public WayList wayList = new WayList();
    public NodeList nodeList = new NodeList();
    public RelationList relationList = new RelationList();

    @Override
    public void stateChanged(ChangeEvent e) {
        repaint();
    }

    public double xToLon(int x) {
        return minlon + x / zoom;
    }

    public double yToLat(int y) {
        return minlat + y / zoom;
    }

    public int lonToX(double lon) {
        return (int) (lon * zoom - minlon * zoom);
    }

    public int latToY(double lat) {
        return (int) (lat * zoom - minlat * zoom);
    }

    public Node node(Point point) {
        return new Node(xToLon(point.x), yToLat(point.y));
    }

    Node nodeAt(Point p) {
        if (graph != null) {
            int xoffset = (int) (minlon * zoom);
            int yoffset = (int) (minlat * zoom);
            Point p1 = new Point(xoffset + p.x, yoffset + p.y);
            for (Node node : graph.nodes) {
                if (nodeBound(node).contains(p1)) {
                    return node;
                }
            }
        }
        return null;
    }

    public Rectangle nodeBound(Node node) {
        int x = lonToX(node.lon);
        int y = latToY(node.lat);
        return new Rectangle(x - 3, y - 3, 6, 6);
    }

    public Rectangle wayBound(Way way) {
        double min_lon = Double.MAX_VALUE;
        double min_lat = Double.MAX_VALUE;
        double max_lon = Double.MIN_VALUE;
        double max_lat = Double.MIN_VALUE;
        for (Node node : way) {
            min_lon = Math.min(min_lon, node.lon);
            min_lat = Math.min(min_lat, node.lat);
            max_lon = Math.max(max_lon, node.lon);
            max_lat = Math.max(max_lat, node.lat);
        }
        int x = lonToX(min_lon);
        int y = latToY(min_lat);
        int w = (int) ((max_lon - min_lon) * zoom);
        int h = (int) ((max_lat - min_lat) * zoom);
        return new Rectangle(x, y, w, h);
    }

    public void setGraph(Graph graph) {
        if (graph != null) {

            nodeList.clear();
            for (Node node : graph.nodes) {
                nodeList.add(node);
            }
            graph.nodeList = nodeList;

            wayList.clear();
            for (Way way : graph.ways) {
                wayList.add(way);
            }
            graph.wayList = wayList;

            relationList.clear();
            for (Relation r : graph.relations) {
                relationList.add(r);
            }
            graph.relationList = relationList;

            graph.addChangeListener(this);
        } else {
            nodeList.clear();
            wayList.clear();
            relationList.clear();
        }
        this.graph = graph;
        repaint();
    }

    public void setBound(double[] bound) {
        setBound(bound[0], bound[1], bound[2], bound[3]);
    }

    public void setBound(double minlon, double minlat, double maxlon, double maxlat) {
        this.minlon = minlon;
        this.minlat = minlat;
        this.maxlon = maxlon;
        this.maxlat = maxlat;
    }

    @Override
    public void paint(Graphics g) {
        if (graphList == null) {
            throw new RuntimeException("Browser : graphlist is null");
        }
        for (Graph gr : graphList) {
            gr.draw(this, g);
        }
    }

    public double getZoom() {
        return zoom;
    }

    public void zoom_in() {
        setZoom(zoom * 2.0);
    }

    public void zoom_out() {
        setZoom(zoom * 0.5);
    }

    public void setZoom(double zoom) {
        int w = getWidth();
        int h = getHeight();
        this.zoom = zoom;
        maxlon = minlon + w / zoom;
        maxlat = minlat + h / zoom;
        repaint();
        change();
    }

    private void move(double dlon, double dlat) {
        maxlon += dlon;
        minlon += dlon;
        minlat += dlat;
        maxlat += dlat;
        repaint();
        change();

    }

    public void move_south() {
        move(.0, (maxlat - minlat) * .25);
    }

    public void move_noth() {
        move(.0, (minlat - maxlat) * .25);
    }

    public void move_west() {
        move((maxlon - minlon) * .25, .0);
    }

    public void move_east() {
        move((minlon - maxlon) * .25, .0);
    }

    void onClickNode(Node node) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public Browser(Iterable<Graph> graphList) {
        setPreferredSize(new Dimension(400, 400));
        this.graphList = graphList;
    }

    public void reset() {
        int w = getWidth();
        int h = getHeight();
        minlon = Double.MAX_VALUE;
        minlat = Double.MAX_VALUE;
        maxlon = Double.MIN_VALUE;
        maxlat = Double.MIN_VALUE;
        for (Node node : graph.nodes) {
            minlon = Math.min(minlon, node.lon);
            minlat = Math.min(minlat, node.lat);
            maxlon = Math.max(maxlon, node.lon);
            maxlat = Math.max(maxlat, node.lat);
        }
        zoom = Math.max(w / (maxlon - minlon), h / maxlat - minlat);
        maxlon = minlon + w / zoom;
        maxlat = minlat + h / zoom;
        repaint();
        change();
    }

    List<ChangeListener> listeners = new ArrayList<>();

    public void addChangeListener(ChangeListener e) {
        listeners.add(e);
    }

    public void removeChangeListener(ChangeListener e) {
        listeners.remove(e);
    }

    public void change() {
        for (ChangeListener listener : listeners) {
            listener.stateChanged(new ChangeEvent(this));
        }
    }

    public String statusText() {
        return String.format(Locale.US, "zoom : %.3f min : %.3f %.3f max : %.3f %.3f", zoom, minlon, minlat, maxlon, maxlat);
    }

}
