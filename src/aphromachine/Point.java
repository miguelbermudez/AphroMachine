
package aphromachine;

import processing.core.*;

/**
 * @author Miguel Bermudez
 *
 */
public class Point extends PApplet {
    
    float x, y, z;
    boolean arrived;
    
    Point(float x, float y, boolean arrived) {
        this.arrived = arrived;
        this.x = x;
        this.y = y;
    }

}
