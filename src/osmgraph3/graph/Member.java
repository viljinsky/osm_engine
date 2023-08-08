/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.graph;

/**
 *
 * @author viljinsky
 */
public class Member {
    
    public String type;
    public String role;
    public long ref;

    public String toString() {
        return String.format("member %s %s %s", type, ref, role);
    }
    
}
