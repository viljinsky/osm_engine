/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package osmgraph3.test;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import osmgraph3.controls.Base;

/**
 *
 * @author viljinsky
 */
public class Test7 extends Base{

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        Graphics2D g2 = (Graphics2D)g;
        
        String s = "09202980298029029029";
        Font f = getFont();
        FontRenderContext frc = g2.getFontRenderContext();
        
        GlyphVector gv =  f.createGlyphVector(frc, s);
        
       
        
        g2.drawGlyphVector(gv, 10, 10);
        
        
        
        
    }
    
    
    
    public static void main(String[] args) {
        new Test7().execute();
    }
    
}
