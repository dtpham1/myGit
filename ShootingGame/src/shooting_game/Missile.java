package e_shooting_game;

/**
 * @author Erick Ayers
 *
 */

public class Missile {
	int x, y, w;
	
	public Missile(int x, int y, int w) {
		this.x = x;
		this.y = y;
		this.w = w;
	}
	
	public void move() {
		if (w == 0) {
			this.x += 15;
		} else {
			this.x -= 7;
		}
	}
}
