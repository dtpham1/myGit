/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shooting_game;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

/**
 *
 * @author forainy
 */
public class GameFrame extends JFrame implements KeyListener, Runnable{
    
    //field
    int frame_wid = 800;
    int frame_hgt = 600;
    
    int x, y;
    
    boolean KeyUp = false;
    boolean KeyDown = false;
    boolean KeyLeft = false;
    boolean KeyRight = false;
    boolean KeySpace = false;
    
    int cnt;
    
    Thread th;
    Toolkit tk = Toolkit.getDefaultToolkit();
    Image craft_img = tk.getImage("craft.png");
    Image missile_img = tk.getImage("missile.png");
    Image enemy_img = tk.createImage("enemy.png");
    ArrayList Missile_List = new ArrayList();
    ArrayList Enemy_List = new ArrayList();
    
    Image buffImage;
    Graphics buffg;
    Missile ms;
    Enemy en;
    
    
    //method
    
    //constructor
    GameFrame(){
        
        init();
        start();
        
        setTitle("shooting game");
        setSize(frame_wid, frame_hgt);
        
        Dimension screen = tk.getScreenSize();
        
        int f_xpos = (int)(screen.getWidth()-frame_wid)/2;
        int f_ypos = (int)(screen.getHeight()-frame_hgt)/2;
        
        setLocation(f_xpos, f_ypos);
        
        setResizable(false);
        setVisible(true);
       
       
        
    }//constructor
    
    public void init(){
        
        x = 100;
        y = 100;
        
    }//init
    
    public void start(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);
        th = new Thread(this);
        th.start();
    }//start
    
    public void run(){
        try{
           while(true){
               keyProcess();
               enemyProcess();
               missileProcess();
               repaint();
               Thread.sleep(20);
               cnt++;
           }//while 
        }catch(Exception e){}
    }//run
    
    public void missileProcess(){
        if(KeySpace){
           ms = new Missile(x, y);
           Missile_List.add(ms);
        }//if          
    }//missileProcess
    
    public void enemyProcess(){
        for(int i=0; i<Enemy_List.size();i++){
            en =(Enemy)Enemy_List.get(i);
            en.move();
        }//fori
        if(cnt% 300 == 0){
            en = new Enemy(frame_wid, 100);
            Enemy_List.add(en);
            en = new Enemy(frame_wid, 200);
            Enemy_List.add(en);
            en = new Enemy(frame_wid, 300);
            Enemy_List.add(en);
            en = new Enemy(frame_wid, 400);
            Enemy_List.add(en);
            en = new Enemy(frame_wid, 500);
            Enemy_List.add(en);
        }//if
    }//enemyProcess
    
    public void paint(Graphics g){
        buffImage = createImage(frame_wid, frame_hgt);
        buffg = buffImage.getGraphics();
        update(g);
        
        //
    }//paint
    
    public void update(Graphics g){
        drawChar();
        drawMissile();
        drawEnemy();
        g.drawImage(buffImage, 0, 0, this);
        
    }//update
    
    public void drawChar(){
        buffg.clearRect(0, 0, frame_wid, frame_hgt);
        buffg.drawImage(craft_img, x, y, this);
        
    }//drawChar
    
    public void drawMissile(){
        for(int i=0; i<Missile_List.size(); ++i){
            ms = (Missile)Missile_List.get(i);
            buffg.drawImage(missile_img, ms.pos.x+50, ms.pos.y+15, this);
            ms.move();
        }//fori
    }//drawMissile
    
    public void drawEnemy(){
        for(int i=0; i<Enemy_List.size();i++){
            en =(Enemy)(Enemy_List.get(i));
            buffg.drawImage(enemy_img, en.x, en.y, this);
        }//fori
    }//drawEnemy
    
    public void keyPressed(KeyEvent e){
        switch(e.getKeyCode()){
            case KeyEvent.VK_UP: KeyUp = true; break;
            case KeyEvent.VK_DOWN: KeyDown = true; break;
            case KeyEvent.VK_LEFT: KeyLeft = true; break;
            case KeyEvent.VK_RIGHT: KeyRight = true; break;
            case KeyEvent.VK_SPACE: KeySpace = true; break;    
        }
    }//pressed
    
    public void keyReleased(KeyEvent e){
        switch(e.getKeyCode()){
            case KeyEvent.VK_UP: KeyUp = false; break;
            case KeyEvent.VK_DOWN: KeyDown = false; break;
            case KeyEvent.VK_LEFT: KeyLeft = false; break;
            case KeyEvent.VK_RIGHT: KeyRight = false; break;
            case KeyEvent.VK_SPACE: KeySpace = false; break;     
        }
    }//released
    
    public void keyTyped(KeyEvent e){
        
    }//typed
    
    public void keyProcess(){
        if(KeyUp == true) y = y - 5;
        else if(KeyDown == true) y = y + 5;
        else if(KeyLeft == true) x = x - 5;
        else if(KeyRight == true) x = x + 5;
        
    }//proc
    
    
    
}//gameFrame
