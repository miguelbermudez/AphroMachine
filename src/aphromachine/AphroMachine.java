package aphromachine;

import java.util.ArrayList;

import processing.core.*;
import geomerative.*;
import toxi.color.*;
import toxi.color.theory.*;


public class AphroMachine extends PApplet {

	RFont font;
	
	int fontSize = 65;
	int svgCount = 6;
	int svgPos = 0;
	
	String[] svgs  = new String[svgCount];
	
	ArrayList<Boid> seekers;
	ArrayList<Point> coords;
	
	RShape shp;
	RPoint[] shpPts;
	
	//font-to-geometry settings
	int segmentLength = 4;
	int baseR = 4;
	
    
    public void setup() {
        size(1280, 720, OPENGL); smooth();
        colorMode(HSB, 360, 100, 100);
        //background(0);
        //noCursor();
        noStroke();
        
        fill(200, 99, 99);
        stroke(200, 99, 99, 15 );
        
        //initialize RG library
        RG.init(this);
        RCommand.setSegmentLength(segmentLength);
        RCommand.setSegmentator(RCommand.UNIFORMLENGTH);
        
        //initialize seekers and cords arrayList
        seekers = new ArrayList<Boid>();
        coords = new ArrayList<Point>();
        
        //load all svgs into array for later use
        for (int i = 0; i < svgCount; i++) {
            svgs[i] = "aphroisms_svg-0" + (i+1) + ".svg";
            //println(svgs[i]); //sanity check
        } 
        
        //load first svg
        loadSvg();
        loadSeekers();
        
        
	}

	public void draw() {
	    //background(0);
	    
	    if (shpPts != null) {
	        
	        update(shpPts.length, shpPts, baseR);   
	        
	    }
	    
	    
	    
	}
	
	void loadSvg() {
	    shp = RG.loadShape(svgs[svgPos]);
	    println("\t"+svgs[svgPos]+" SVG LOADED...");
	    if (svgPos < svgCount) {
	        svgPos++;
	    } else {
	        println("\treseting svgPos to 0");
	        svgPos = 0;
	    }
	    
	    //get all the points in the shape
        shpPts = shp.getPoints();
        println("\tTotal Points in current SVG: "+shpPts.length);
        //shp.draw();
        
	    coords = new ArrayList<Point>();
        seekers = new ArrayList<Boid>();
	}
	
	
	void loadSeekers() {
	    for (int i = 0; i < shpPts.length; i++) {
	        newSeeker(random(width), random(height));
        }
	}
	
	void update(int count, RPoint[] pnts, int baseR) {
	    for (int i = 0; i < count; i++) {
            float mx = (pnts[i].x);
            float my = (pnts[i].y);
            
            //add all the point location data from RShape to the coords ArrayList
            coords.add(new Point(mx, my, false));
            
            if ((i < seekers.size()) && (i < coords.size()) ) {
                Boid seeker = (Boid) seekers.get(i);
                Point coord = (Point) coords.get(i);
                seeker.arrive(new PVector(coord.x, coord.y));
                seeker.update();
                float r = (seeker.vel.mag()/2)+baseR;

                //draw Boids
                //println("boid loc: " + seeker.loc.x + "," + seeker.loc.y);
                ellipse(seeker.loc.x, seeker.loc.y, r, r);
                //point(seeker.loc.x , seeker.loc.y);
               
                if (  (seeker.loc.x > mx-1) && (seeker.loc.x < mx+1) && 
                      (seeker.loc.y > my-1) && (seeker.loc.y < my+1) && 
                      (coord.arrived == false)  ) {
                    
                    coord.arrived = true;
                }
            }
        }
	}
	
	
	//create a new seeker, add it the seekers ArrayList
    void newSeeker(float x, float y) {
        float maxspeed = random(15, 20);
        float maxforce = random(0.5f, 0.6f);
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
            println("\tarrived: "+(arrived/coords.size())*100);
            return (arrived/coords.size())*100;
        } else {
            return 0;
        }
    }
}
