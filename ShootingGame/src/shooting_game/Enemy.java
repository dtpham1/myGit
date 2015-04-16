/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gpa;

/**
 *
 * @author forainy
 */
public class Enemy {

	/**
	 * @param args the command line arguments
	 */

	//field
	int x;
	int y;
	int enemyType;
	static final String[] ENEMY_TYPES = {"fb-mon.gif", "netflix-mon.gif"};


	//method

	public void move (){
		x = x-3;
	}//move

	public void move (int speed){
		x = x-speed;
	}


	//constructor
	Enemy(int x, int y, int enemyType){

		this.x = this.x+x;
		this.y = this.y+y;
		this.enemyType = enemyType % ENEMY_TYPES.length;
	}//Enemy con

}


