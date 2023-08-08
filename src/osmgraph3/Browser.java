package osmgraph3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import osmgraph3.controls.NodeList;
import osmgraph3.controls.RelationList;
import osmgraph3.controls.WayList;

class GraphRenderer {

    Graph graph;

    public GraphRenderer(Graph graph) {
        this.graph = graph;
    }

    public void render(Browser browser, Graphics g, boolean selected) {

        g.setColor(graph.color);

        int xoffset = -(int) (browser.minlon * browser.zoom);
        int yoffset = -(int) (browser.minlat * browser.zoom);

        for (Node node : graph.nodes) {
            Rectangle r = browser.nodeBound(node);
            g.drawRect(xoffset + r.x, yoffset + r.y, r.width, r.height);

        }

        Rectangle r;

        for (Way way : graph.ways) {

            g.setColor(Color.LIGHT_GRAY);
            r = browser.wayBound(way);
            g.drawRect(xoffset + r.x, yoffset + r.y, r.width, r.height);

            for (Edge edge : way.edges()) {
                g.setColor(graph.color);
                int x1 = xoffset + (int) (edge.node1.lon * browser.zoom);
                int y1 = yoffset + (int) (edge.node1.lat * browser.zoom);
                int x2 = xoffset + (int) (edge.node2.lon * browser.zoom);
                int y2 = yoffset + (int) (edge.node2.lat * browser.zoom);
                g.drawLine(x1, y1, x2, y2);

                r = browser.nodeBound(edge.center());
                g.drawLine(xoffset + r.x, yoffset + r.y, xoffset + r.x + r.width, yoffset + r.y + r.height);
                g.drawLine(xoffset + r.x, yoffset + r.y + r.height, xoffset + r.x + r.width, yoffset + r.y);
            }

            if (way.size() > 2) {
                g.setColor(Color.LIGHT_GRAY);
                Node center = browser.wayCenter(way);
                r = browser.nodeBound(center);
                g.drawLine(xoffset + r.x - 3, yoffset + r.y - 3, xoffset + r.x + 3, yoffset + r.y + 3);
                g.drawLine(xoffset + r.x - 3, yoffset + r.y + 3, xoffset + r.x + 3, yoffset + r.y - 3);
            }

        }
    }
}



class BrowserMouseAdapter extends MouseAdapter {

    public static final int MODE0 = 0;
    public static final int MODE1 = 1;
    public static final int MODE2 = 2;

    Graph graph;
    Browser browser;

    int mode = MODE2;

    Way way;
    Way way1;
    Node node1;
    Node node2;

    public void setMode(int mode) {
        this.mode = mode;
    }

    public BrowserMouseAdapter(Browser browser, Graph graph) {
        this.browser = browser;
        this.graph = graph;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (node1 != null) {
            Node node = browser.node(e.getPoint());
            node1.lon = node.lon;
            node1.lat = node.lat;
            graph.change();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (node1 != null) {
            node1 = null;
        }
        if (way1 != null) {
            way1 = null;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

        int xoffset = -(int) (browser.minlon * browser.zoom);
        int yoffset = -(int) (browser.minlat * browser.zoom);

        Point p = new Point(xoffset + e.getX(), yoffset + e.getY());
        Node node = browser.nodeAt(e.getPoint());

        switch (mode) {
            case MODE0:
                for (Way w : graph.ways) {
                    Node cenetr = browser.wayCenter(w);
                    Rectangle r = browser.nodeBound(cenetr);
//                    r.x+=xoffset;
//                    r.y+=yoffset;
                    if (r.contains(e.getPoint())) {
                        way1 = w;
                        System.out.println("" + way1);
                        return;
                    }
                }
                node1 = node;
                break;
            case MODE1:
                if (node != null) {
                    graph.remove(node);
                } else {
                    graph.add(browser.node(e.getPoint()));
                }
                break;
            case MODE2:
                if (node == null) {

                    // isEdgeCenter
                    for (Way w : graph.ways) {
                        for (Edge edge : w.edges()) {
                            Rectangle r = browser.nodeBound(edge.center());
                            if (r.contains(p)) {
                                graph.add(edge.center());
                                w.add(w.indexOf(edge.node2), edge.center());
                                return;
                            }
                        }
                    }
                    node = graph.add(browser.node(e.getPoint()));
                }

                if (way != null && node == way.last()) {
                    way = null;
                    break;
                }

                if (way != null && way.size()>1 && node == way.first()) {
                    way.close();//add(node);
                    way = null;
                    graph.change();
                    break;
                }

                if (way == null) {
                    way = new Way();
                    graph.add(way);
                    graph.change();
                }

                if (node != way.last()) {
                    way.add(node);
                    graph.change();
                }
                break;
        }
    }
}


/**
 *
 * @author viljinsky
 */
class Browser extends JComponent implements ChangeListener {
    
    int mode;
    double zoom;
    double minlon;
    double minlat;
    double maxlon;
    double maxlat;
    Graph graph;
    Iterable<Graph> graphList;
    public WayList wayList = new WayList();
    public NodeList nodeList = new NodeList();
    public RelationList relationList = new RelationList();
    BrowserMouseAdapter mouseAdapter;

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

    Node wayCenter(Way way) {
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
        return new Node(min_lon + (max_lon - min_lon) / 2, min_lat + (max_lat - min_lat) / 2);
    }

    Rectangle nodeBound(Node node) {
        int x = (int) ((node.lon) * zoom);
        int y = (int) ((node.lat) * zoom);
        return new Rectangle(x - 3, y - 3, 6, 6);
    }

    Rectangle wayBound(Way way) {
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
            if (mouseAdapter != null) {
                removeMouseListener(mouseAdapter);
                removeMouseMotionListener(mouseAdapter);
            }
            mouseAdapter = new BrowserMouseAdapter(this, graph);
            setMode(BrowserMouseAdapter.MODE0);
            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
        } else {
            if (this.graph != null) {
                removeMouseListener(mouseAdapter);
                removeMouseMotionListener(mouseAdapter);
                mouseAdapter = null;
            }
            nodeList.clear();
            wayList.clear();
            relationList.clear();
        }
        this.graph = graph;
        repaint();
    }

    public Browser() {
        setPreferredSize(new Dimension(400, 400));
        addMouseListener(mouseAdapter);
    }

    public Browser(Iterable<Graph> graphList) {
        this();
        this.graphList = graphList;
    }

    @Override
    public void paint(Graphics g) {
        if (graphList != null) {
            for (Graph gr : graphList) {
                gr.renderer.render(this, g, true);
                //                gr.paint(g, gr == graph);
            }
        }
    }

    public double getZoom() {
        return zoom;
    }

    void zoom_in() {
        setZoom(zoom * 2.0);
    }

    void zoom_out() {
        setZoom(zoom * 0.5);
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
        repaint();
    }

    void setMode(int mode) {
        this.mode = mode;
        if (mouseAdapter != null) {
            mouseAdapter.mode = mode;
        }
    }

    void onClickNode(Node node) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
