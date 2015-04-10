package e_shooting_game;

/**
 * @author Erick Ayers
 *
 */
public class Enemy {
	int x, y;
	
	public Enemy(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void move() {
		x -= 2;
	}
}
