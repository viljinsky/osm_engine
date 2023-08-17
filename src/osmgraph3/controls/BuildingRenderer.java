/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.controls;

import java.awt.Graphics;
import java.awt.Rectangle;
import osmgraph3.graph.Graph;
import osmgraph3.graph.Member;
import osmgraph3.graph.Node;
import osmgraph3.graph.Relation;
import osmgraph3.graph.Way;

/**
 *
 * @author viljinsky
 */
public class BuildingRenderer extends GraphRenderer {
    
    
    
    void drawNode(Browser browser,Graphics g,Node node){
        Rectangle rect = browser.nodeRectangle(node);
        g.drawRect(rect.x, rect.y, rect.width, rect.height);
    }
    
    void drawWay(Browser browser,Graphics g,Way way){
        Rectangle r = browser.wayRectangle(way);
        if (r.width<5 && r.height<5) return;
        int[] xPoints = new int[way.size()];
        int[] yPoints = new int[way.size()];
        for (int i = 0; i < way.size(); i++) {
            Node n = way.get(i);
            xPoints[i] = browser.lonToX(n.lon);
            yPoints[i] = browser.latToY(n.lat);
        }
        g.drawPolyline(xPoints, yPoints, way.size());        
    }
    
    void drawRelation(Browser browser,Graph graph,Graphics g,Relation relation){
        for(Member member: relation){
            switch(member.type){
                case Graph.NODE:
                    Node node = graph.nodeById(member.ref);
                    if (node!=null){
                        drawNode(browser, g, node);
                    }
                    break;
                case Graph.WAY:
                    Way way = graph.wayById(member.ref);
                    if (way!=null){                        
                        drawWay(browser, g, way);
                    }
                    break;
                case Graph.RELATION:
                    Relation r = graph.relationById(member.ref);
                    if (r!=null){
                        drawRelation(browser, graph, g, r);
                    }
                    break;
            }
        }
    }
    
    @Override
    public void render(Browser browser, Graph graph, Graphics g, boolean selected) {
        
        for(Node node: graph.nodes){
            if (!node.keySet().isEmpty()){
                drawNode(browser, g, node);
            }
        }
        
        for(Way way: graph.ways){
            if(!way.keySet().isEmpty()){
                drawWay(browser, g, way);
            }
        }
        
        for(Relation relation: graph.relations){
            drawRelation(browser,graph,g,relation);
        }
        
    }
    
}
