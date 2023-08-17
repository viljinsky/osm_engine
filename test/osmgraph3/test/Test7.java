/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.test;

import java.io.File;
import osmgraph3.OSMParser;
import osmgraph3.controls.FileManager;
import osmgraph3.graph.Graph;
import osmgraph3.graph.Member;
import osmgraph3.graph.Relation;
import osmgraph3.graph.Way;


/**
 *
 * @author viljinsky
 */
public class Test7 {
    
    
    
    public static void main(String[] args) throws Exception{
        new Test7().execute();
    }

    private void execute() throws Exception{
//        FileManager fileManager = new FileManager(null);
        OSMParser parser = new OSMParser(new File(System.getProperty("user.home") +"/osm","test.osm"));
//        graph.nodes = parser.nodes;
//        graph.ways = parser.ways;
//        graph.relations = parser.relations;

        for(Relation r:parser.relations){
            System.out.println(r);
            for(Member m:r){
                System.out.println("-->"+m);
                
                if (m.type.equals(Graph.WAY)){
                    for(Way way: parser.ways){
                        if (way.id.equals(m.ref)){
                            System.out.println("OK");
                            break;
                        }
                    }
                }
                
                
            }
        }
        
    }
    
}
