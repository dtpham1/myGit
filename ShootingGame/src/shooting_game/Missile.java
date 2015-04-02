/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shooting_game;

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
