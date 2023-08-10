/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.graph;

import java.util.Locale;

/**
 *
 * @author viljinsky
 */
public class Bound {
    public double minlon;
    public double minlat;
    public double maxlon;
    public double maxlat;

    public Bound(double minlon, double minlat, double maxlon, double maxlat) {
        this.minlon = minlon;
        this.minlat = minlat;
        this.maxlon = maxlon;
        this.maxlat = maxlat;
    }

    public Node center(){
        return new Node(minlon + (maxlon-minlon)/2, minlat+(maxlat-minlat)/2);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%.3f %.3f %.3f %.3f",minlon,minlat,maxlon,maxlat);
    }
    
    
}
