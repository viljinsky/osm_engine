/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;
import osmgraph3.graph.Graph;
import osmgraph3.graph.Member;
import osmgraph3.graph.Node;
import osmgraph3.graph.Relation;
import osmgraph3.graph.Tags;
import osmgraph3.graph.Way;

/**
 *
 * @author viljinsky
 */
public class OSMWriter {

    private static void writeTags(OutputStreamWriter writer,Tags tags) throws Exception{
        for (String k : tags.keySet()) {
            String value = String.valueOf(tags.get(k)).replaceAll("&","&amp;").replaceAll("'", "&apos;").replaceAll(">", "&gt;").replaceAll("<","&lt;");
            writer.write(String.format("\t<tag k='%s' v='%s'/>\n", k, value));
        }
    }
    
    public static void write(Graph graph, OutputStream out) throws Exception {
        try (OutputStreamWriter writer = new OutputStreamWriter(out, "utf-8");) {

            writer.write("<?xml version='1.0' encoding='UTF-8'?>\n<osm version='0.6' generator='JOSM'>\n");

            for (Node node : graph.nodes) {
                if (node.tags == null){
                    writer.write(String.format(Locale.US, "<node id='%d' lon='%8.6f' lat = '%8.6f' visible='true' version='30'/>\n", node.id, node.lon, node.lat));
                } else {
                    writer.write(String.format(Locale.US, "<node id='%d' lon='%8.6f' lat = '%8.6f' visible='true' version='30'>\n", node.id, node.lon, node.lat));
                    writeTags(writer, node.tags);
                    writer.write("</node>");
                }
            }
            for (Way way : graph.ways) {
                writer.write(String.format("<way id='%d' visible='true' version='30'>\n", way.id));
                for (Node node : way) {
                    writer.write(String.format("\t<nd ref = '%d' />\n", node.id));
                }
                if (way.tags != null) {
                    writeTags(writer, way.tags);
//                    for (String k : way.keySet()) {
//                        writer.write(String.format("\t<tag k='%s' v='%s'/>\n", k, way.get(k)));
//                    }
                }
                writer.write("</way>\n");
            }

            for (Relation relation : graph.relations) {
                writer.write(String.format("<relation id='%d' visible='true' version='30'>\n", relation.id));
                for (Member m : relation) {
                    writer.write(String.format("\t<member ref='%d' type='%s' role='%s'/>\n", m.ref, m.type, m.role));
                }
                if(relation.tags !=null){
                    writeTags(writer, relation.tags);
                }
                writer.write("</relation>\n");
            }

            writer.write("</osm>\n");
        }
    }
}
