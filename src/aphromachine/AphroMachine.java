package aphromachine;

import java.util.ArrayList;

import processing.core.*;
import processing.opengl.*;
import geomerative.*;
import toxi.color.*;
import toxi.color.theory.*;
import java.nio.FloatBuffer;
import javax.media.opengl.*;


public class AphroMachine extends PApplet {

    RFont font;
    
    int fontSize = 65;
    int svgCount = 6;
    int svgPos = 0;
    int hue, satur, br;
    boolean seek = false, newAphro = true, auto = true;
    PImage image;
    
    String[] svgs  = new String[svgCount];
    
    ArrayList<Boid> seekers;
    ArrayList<Point> coords;
    
    RShape shp;
    RPoint[] shpPts;
    
    //font-to-geometry settings
    int segmentLength = 4;
    int baseR = 2;
    int alpha = 20;
    int mode = 1;  //0: points, 1:ellipses
    
    public void setup() {
        size(1280, 720, OPENGL);
        smooth();
        colorMode(HSB, 360, 100, 100);
        randomSeed(360);
        background(0);
        //noCursor();
        noStroke();
        
        image = loadImage("texture.png");
        hue = (int)random(360);
        
        fill(200, 99, 99);
        stroke(200, 85, 83, alpha );
        
        //initialize RG library
        RG.init(this);
        RCommand.setSegmentLength(segmentLength);
        RCommand.setSegmentator(RCommand.UNIFORMLENGTH);
                
        //initialize seekers and cords arrayList
        seekers = new ArrayList<Boid>();
        coords = new ArrayList<Point>();
        
        //load all svgs into array for later use
        for (int i = 0; i < svgCount; i++) {
        //for (int i = 6; i < svgCount; i++) {
            svgs[i] = "aphroisms_svg-0" + (i+1) + ".svg";
            //println(svgs[i]); //sanity check
        } 
        
        //load first svg
        loadSvg();
    }

    public void draw() {
        if (mode == 1) {
            background(0);
        }
        
        //OPENGL settings
        GL gl = ((PGraphicsOpenGL)g).gl;
        
        // additive blending
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
        // disable depth test
        gl.glDisable(GL.GL_DEPTH_TEST);
        
        //get all the points in the shape
        shpPts = shp.getPoints();
        //println("\tTotal Points in current SVG: "+shpPts.length);
        //shp.draw();
        
        if(shpPts != null) {
            if (newAphro) {
                coords = new ArrayList<Point>();
                println("\t\tcoords ArrayList created...");
            }
        }
        
        if (shpPts.length > 0) {
            update(shpPts.length, shpPts, baseR);               
        }
        
        if (newAphro) {
            newAphro = false;
        }
        
        //add seekers if there are more points than seekers
        checkSeekerCount(shpPts.length);
        
        if (arrived() == 100) {
            println("\n\t\t100 Boids arrived");
            newLoad();
        }  
    }
    
    public void newLoad() {
        println("segmentLength is now: "+segmentLength+"\tMode is now: "+mode);
        RCommand.setSegmentLength(segmentLength);
        loadSvg();
        hue = (int)random(360);
        satur = (int)random(50,70);
        br = (int)random(70,90);
        if (mode == 0) {
          stroke(hue, satur, br, alpha);
        }
    }
    
    public void keyPressed() {
        newLoad();
    }
    
    void loadSvg() {
        hue = (int)random(360);
        satur = (int)random(50,70);
        br = (int)random(70,90);
        
        if (svgPos < svgCount) {
            //load in next svg
            shp = RG.loadShape(svgs[svgPos]);
            println("\t"+svgs[svgPos]+" SVG LOADED...");
            
            //ready new simulation sequence
            newAphro = true;
            svgPos++;
            println("\t\tsvgPos incremented to: "+svgPos);
            
            //(optional) clear the canvas
            //background(0);
            
            if (mode == 0) {
              //clear the boid ArrayList
              seekers = new ArrayList<Boid>();
              //dim previous aphroism
              fill(0, 0, 0, 128);
              rect(0,0,width, height);
            }
            
        } else {
            println("\n\tReseting svgPos to 0");
            svgPos = 0;
            //start the load process again
            loadSvg();
        }
    }
    
    void update(int count, RPoint[] pnts, int baseR) {
        for (int i = 0; i < count; i++) {
            float mx = (pnts[i].x);
            float my = (pnts[i].y);
            
            if (newAphro) {
                //add all the point location data from RShape to the coords ArrayList
                coords.add(new Point(mx, my, false));
            }
            
            if ((i < seekers.size()) && (i < coords.size()) ) {
                Boid seeker = (Boid) seekers.get(i);
                Point coord = (Point) coords.get(i);
                seeker.arrive(new PVector(coord.x, coord.y));
                seeker.update();
                float r = (seeker.vel.mag()/2)+baseR;

                //draw Boids
                //println("\t\tboid loc: " + seeker.loc.x + "," + seeker.loc.y);
                
                if(mode == 1) {
                    //segmentLength = 4;
                    //fill(hue, satur, 100, 255);
                    tint(hue, satur, 100, 255);
                    //ellipse(seeker.loc.x, seeker.loc.y, r, r);
                    beginShape(QUADS);
                    texture(image);
                    textureMode(NORMALIZED);
                    vertex(seeker.loc.x + -r, seeker.loc.y + -r, 0, 0);
                    vertex(seeker.loc.x + -r, seeker.loc.y +  r, 0, 1);
                    vertex(seeker.loc.x +  r, seeker.loc.y +  r, 1, 1);
                    vertex(seeker.loc.x +  r, seeker.loc.y + -r, 1, 0);
                    endShape();
                }
                
                if (mode == 0) {
                  point(seeker.loc.x , seeker.loc.y);
                }
                
                if (  (seeker.loc.x > mx-1) && (seeker.loc.x < mx+1) && 
                      (seeker.loc.y > my-1) && (seeker.loc.y < my+1) && 
                      (coord.arrived == false)  ) {
                    
                    coord.arrived = true;
                }
            }
        }
    }
    void checkSeekerCount(int count) {
        if (count > 1) {
            if(seekers.size() < count) {
                for (int y = 0; y < 15; y++) { //mb
                //for (int y = 0; y < 150; y++) {
                    //newSeeker(random(width), random(height)); //mb
                    newSeeker(random(width), (height+50));
                }
            } else if(seekers.size() > count) {
                for (int z = 0; z < seekers.size() - count; z++) {
                    seekers.remove(seekers.size()-1);
                }
            }
        }
    }
    
    //create a new seeker, add it the seekers ArrayList
    void newSeeker(float x, float y) {
        float maxspeed = random(15, 20);
        //float maxforce = random(0.5f, 0.6f);
        float maxforce = random(0.5f, 0.9f);
        seekers.add(new Boid(new PVector(x, y), maxspeed, maxforce));
    }
    
    float arrived() {
        float arrived = 0;
        if(coords != null) {
            for (int i = 0; i < coords.size(); i++) {
                Point coord = (Point) coords.get(i);
                if(coord.arrived == true) {
                    arrived++;
                }
            }
            //println("\tarrived: "+(arrived/coords.size())*100);
            return (arrived/coords.size())*100;
        } else {
            return 0;
        }
    }
}
