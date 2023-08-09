package osmgraph3;

import osmgraph3.graph.Graph;
import osmgraph3.graph.Way;
import osmgraph3.graph.Relation;
import osmgraph3.graph.Node;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
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
    
    public int mode;
    public double zoom;
    public double minlon;
    public double minlat;
    public double maxlon;
    public double maxlat;
    public Graph graph;
    Iterable<Graph> graphList;
    public WayList wayList = new WayList();
    public NodeList nodeList = new NodeList();
    public RelationList relationList = new RelationList();
//    BrowserMouseAdapter mouseAdapter;

    @Override
    public void stateChanged(ChangeEvent e) {
        repaint();
    }

    /**
     * Создание нового нода
     *
     * @param point точка броузера
     * @return Новый нод
     */
    Node node(Point point) {
        double lon = minlon + point.x / zoom;
        double lat = minlat + point.y / zoom;
        return new Node(lon, lat);
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
        int x = (int) ((node.lon) * zoom);
        int y = (int) ((node.lat) * zoom);
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
        int x = (int) (min_lon * zoom);
        int y = (int) (min_lat * zoom);
        int w = (int) ((max_lon - min_lon) * zoom);
        int h = (int) ((max_lat - min_lat) * zoom);
        return new Rectangle(x, y, w, h);
    }

    public void setGraph(Graph graph) {
        //        zoom = 100.0;
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
//            if (mouseAdapter != null) {
//                removeMouseListener(mouseAdapter);
//                removeMouseMotionListener(mouseAdapter);
//            }
//            mouseAdapter = new BrowserMouseAdapter(this, graph);
            setMode(BrowserMouseAdapter.MODE0);
//            addMouseListener(mouseAdapter);
//            addMouseMotionListener(mouseAdapter);
        } else {
            if (this.graph != null) {
//                removeMouseListener(mouseAdapter);
//                removeMouseMotionListener(mouseAdapter);
//                mouseAdapter = null;
            }
            nodeList.clear();
            wayList.clear();
            relationList.clear();
        }
        this.graph = graph;
        repaint();
    }

    public void setBound(double[] bound){
        setBound(bound[0], bound[1], bound[2], bound[3]);
    }
    
    public void setBound(double minlon, double minlat, double maxlon, double maxlat){
        this.minlon = minlon;
        this.minlat = minlat;
        this.maxlon = maxlon;
        this.maxlat = maxlat;
    }
    
    

    @Override
    public void paint(Graphics g) {
        if (graphList == null){
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
//        minlon += (maxlon-minlon)/2.0;
//        minlat += (maxlat-minlat)/2.0;
//        maxlon -= (maxlon-minlon)/2.0;
//        minlat -= (maxlat-minlat)/2.0;
        setZoom(zoom * 2.0);
    }

    public void zoom_out() {
//        minlon -= (maxlon-minlon)/2.0;
//        minlat -= (maxlat-minlat)/2.0;
//        maxlon += (maxlon-minlon)/2.0;
//        minlat += (maxlat-minlat)/2.0;
        setZoom(zoom * 0.5);
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
        repaint();
    }
    
    private void move(double dlon,double dlat){
       maxlon += dlon;
       minlon += dlon;
       minlat +=dlat;
       maxlat += dlat;
       repaint();
        
    }
    public void move_south(){
        move(.0,(maxlat-minlat)*.25);
    }
    public void move_noth(){
        move(.0,(minlat-maxlat)*.25);
    }
    public void move_west(){
        move((maxlon-minlon)*.25,.0);
    }
    public void move_eact(){
        move((minlon-maxlon)*.25,.0);
    }

    public void setMode(int mode) {
        this.mode = mode;
//        if (mouseAdapter != null) {
//            mouseAdapter.mode = mode;
//        }
    }

    void onClickNode(Node node) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public Browser(Iterable<Graph> graphList) {
        setPreferredSize(new Dimension(400, 400));
//        addMouseListener(mouseAdapter);
        this.graphList = graphList;
    }

    public void reset() {
        int w = getWidth();
        int h = getHeight();
        minlon = Double.MAX_VALUE;
        minlat = Double.MAX_VALUE;
        maxlon = Double.MIN_VALUE;
        maxlat = Double.MIN_VALUE;
        for(Node node:graph.nodes){
            minlon = Math.min(minlon,node.lon);
            minlat = Math.min(minlat,node.lat);
            maxlon = Math.max(maxlon,node.lon);
            maxlat = Math.max(maxlat,node.lat);
        }
        zoom = Math.max(w / (maxlon - minlon),h /maxlat- minlat);
        repaint();
    }

    
}
