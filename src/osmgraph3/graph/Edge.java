/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.graph;

/**
 *
 * @author viljinsky
 */
public class Edge {
    
    public Node node1;
    public Node node2;

    public Edge(Node node1, Node node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    public Node center() {
        return new Node(node1.lon + (node2.lon - node1.lon) / 2, node1.lat + (node2.lat - node1.lat) / 2);
    }
    
}
