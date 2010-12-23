/**
 * 
 */
package aphromachine;

import processing.core.*;

/**
 * @author Miguel Bermudez
 * @see Danield Shifttman Autonomous Stering Behviours 
 * http://www.shiffman.net/teaching/nature/steering/
 * 
 */
public class Boid extends PApplet {
    PVector loc;
    PVector vel;
    PVector acc;
    float r;
    float maxforce;
    float maxspeed;
    
    Boid(PVector l, float ms, float mf) {
        acc = new PVector(0,0);
        vel = new PVector(0,0);
        loc = l.get();
        r = (float) 3.0;
        maxspeed = ms;
        maxforce = mf;
    }
    
    /**
     * Update located of boid
     * 
     */
    void update() {
        //update velocity
        vel.add(acc);
        //limit speed
        vel.limit(maxspeed);
        loc.add(vel);
        //reset acceleration to 0 each cycle
        acc.mult(0);
    }
    
    void arrive(PVector target) {
        acc.add(steer(target, true));
    }
    
    /**
     * Calculate steering vector towards target
     *
     * @param target
     * @param slowdown    if true, slows down as boid approaches target     
     * @return PVector steer
     * 
     */
    PVector steer(PVector target, boolean slowdown) {
        //steering vector
        PVector steer;    
        // a Vector pointing from the loc to the target
        PVector desired = PVector.sub(target, loc);     
        //Distance from the target is the magnitude of the vector
        float d = desired.mag();    
        //If the distance is > 0, calc steering (otherwise return 0 vector)
        if (d > 0) {
            //normalize deisred
            desired.normalize();
            //Two option for desired vector magnitude (1-based on distance, 2-maxspeed)
            if ((slowdown) && (d < 100.0f)) {
                //damping is somewhat arbitrary
                desired.mult(maxspeed* (d/100.0f));
            } else {
                desired.mult(maxspeed);
            }
            //Steering = Desired minus Velocity
            steer = PVector.sub(desired,vel);
            steer.limit(maxforce);
        } else {
            steer = new PVector(0, 0);
        }
        
        return steer;
    }
}
