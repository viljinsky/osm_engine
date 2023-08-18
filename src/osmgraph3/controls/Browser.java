package osmgraph3.controls;

import java.awt.Color;
import osmgraph3.graph.Graph;
import osmgraph3.graph.Way;
import osmgraph3.graph.Node;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import osmgraph3.controls.GraphRenderer;
import osmgraph3.graph.Bound;

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

    GraphRenderer renderer = new GraphRenderer();
    
    public void setDefaultRenderer(GraphRenderer renderer){
        this.renderer = renderer;
    }
    
    protected GraphRenderer getGraphRenedere(Graph graph){
        return renderer;
    }
    
//    public void setAdapter(MouseAdapter adapter){       
//        addMouseListener(adapter);
//        addMouseMotionListener(adapter);
//        addMouseWheelListener(adapter);
//    }
//    
//    public void removeMousAdapter(MouseAdapter adapter){
//        removeMouseListener(adapter);
//        removeMouseMotionListener(adapter);
//        removeMouseWheelListener(adapter);
//    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        repaint();
    }

    public double xToLon(int x) {
        return minlon + x / zoom;
    }

    public double yToLat(int y) {
//        return minlat + y / zoom;
        return maxlat - y /zoom;
        
    }

    public int lonToX(double lon) {
        return (int) (lon * zoom - minlon * zoom);
    }

    public int latToY(double lat) {
//        return (int) (lat * zoom - minlat * zoom);
        return (int) (maxlat * zoom - lat * zoom );
    }

    public Node node(Point point) {
        return new Node(xToLon(point.x), yToLat(point.y));
    }

    public Node nodeAt(Point p) {
        if (graph != null) {
            for (Node node : graph.nodes) {
                int x = lonToX(node.lon);
                int y = latToY(node.lat);
                if (new Rectangle(x - 3, y - 3, 6, 6).contains(p)) {
                    return node;
                }
            }
        }
        return null;
    }

    public Rectangle nodeRectangle(Node node) {
        int x = lonToX(node.lon);
        int y = latToY(node.lat);
        return new Rectangle(x - 3, y - 3, 6, 6);
    }

    public Rectangle wayRectangle(Way way) {
        Bound bound = way.bound();
        int x = lonToX(bound.minlon);
        int y = latToY(bound.maxlat);
        int w = (int) ((bound.maxlon - bound.minlon) * zoom);
        int h = (int) ((bound.maxlat - bound.minlat) * zoom);
        return new Rectangle(x, y, w, h);
    }

    public void setGraph(Graph g) {
        if(this.graph!=null){
            this.graph.removeChangeListener(this);
        }
        g.addChangeListener(this);
        this.graph = g;
        repaint();
    }
    
    List<Graph> layers = new ArrayList<>();
    
    public void addLayer(Graph layer){
        layers.add(layer);
    }
    
    public void removeLayer(Graph layer){
        layers.remove(layer);
    }

    @Override
    public void paint(Graphics g) {
        for(Graph layer: layers){
            getGraphRenedere(layer).render(this,layer, g, true);
        }
        
        if (graph != null) {
            getGraphRenedere(graph).render(this,graph, g, true);
        }
        // drow center
        int w = getWidth() / 2;
        int h = getHeight() / 2;
        g.setColor(Color.BLUE);
        g.drawOval(w - 3, h - 3, 6, 6);

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

        Node aldCenter = node(new Point(w / 2, h / 2));
        this.zoom = zoom;
        maxlon = minlon + w / zoom;
        maxlat = minlat + h / zoom;
        setCenter(aldCenter);
    }

    public void setCenter(Node center) {
        int w = getWidth();
        int h = getHeight();
        Node newCenter = node(new Point(w / 2, h / 2));

        double dlon = center.lon - newCenter.lon;
        double dlat = center.lat - newCenter.lat;
        move(dlon, dlat);
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

    public void move(double dlon, double dlat) {
        maxlon += dlon;
        minlon += dlon;
        minlat += dlat;
        maxlat += dlat;
        repaint();
        change();
    }

    public Browser() {
        setPreferredSize(new Dimension(400, 400));
    }

    public void reset() {
        int w = getWidth();
        int h = getHeight();
        if (graph.nodes.isEmpty()) {
            minlon = 10.0;
            minlat = 20.0;
            maxlon = 10.1;
            maxlat = 10.2;
        } else {
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

    public String statusText(Point p) {
        return statusText() + " " + String.format(Locale.US, "lon : %.3f,lat : %.3f", xToLon(p.x), yToLat(p.y));
    }

    public void setBound(Bound bound) {
        minlon = bound.minlon;
        minlat = bound.maxlat;
        maxlon = bound.maxlon;
        maxlat = bound.maxlat;
        zoom = Math.min((getWidth() * .8) / (maxlon - minlon), (getHeight() * .8) / (maxlat - minlat));
        setCenter(bound.center());
    }

}
