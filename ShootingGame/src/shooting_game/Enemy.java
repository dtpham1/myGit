/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shooting_game;

/**
 *
 * @author forainy
 */
public class Enemy {
	
	//field
	int x;
	int y;


	//method

	public void move (){
		x = x-3;
	}//move


	//constructor
	Enemy(int x, int y){

		this.x = this.x+x;
		this.y = this.y+y;
	}//Enemy con

}


