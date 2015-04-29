package gpa;

import java.awt.*;

/**
 *
 * @author forainy
 */
public class Missile {
	//field
	Point pos;
	//method
	public void move(){
		pos.x = pos.x + 10;
	}//move
	//constructor
	Missile(int x, int y){
		pos = new Point(x, y);
	}
}//missile
