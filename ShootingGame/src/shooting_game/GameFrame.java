/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gpa;

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

	boolean keyUp = false;
	boolean keyDown = false;
	boolean keyLeft = false;
	boolean keyRight = false;
	boolean KeySpace = false;

	private boolean paused;

	int cnt;
	
	long gameScore = 0L;
	int health;

	Thread th;
	Toolkit tk = Toolkit.getDefaultToolkit();
	Image craft_img = tk.getImage("hero.gif");
	Image missile_img = tk.getImage("missile.gif");
	Image enemy1_img = tk.createImage("fb-mon.gif");
	Image enemy2_img = tk.createImage("netflix-mon.gif");
	ArrayList<Missile> Missile_List = new ArrayList<Missile>();
	ArrayList<Enemy> Enemy_List = new ArrayList<Enemy>();
	ArrayList<Image> Enemy_ImageList = new ArrayList<Image>();
	int [] Enemy_SpeedList;

	Image buffImage;
	Graphics buffg;
	Missile ms;
	Enemy en;


	//method

	//constructor
	GameFrame(){

		init();
		start();

		setTitle("GPA: Grade Point Avenger");
		setSize(frame_wid, frame_hgt);

		paused = false;

		Dimension screen = tk.getScreenSize();

		int f_xpos = (int)(screen.getWidth()-frame_wid)/2;
		int f_ypos = (int)(screen.getHeight()-frame_hgt)/2;

		setLocation(f_xpos, f_ypos);

		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



	}//constructor

	public void init(){

		x = 100;
		y = 100;
		health = 100;
		
	}//init

	public void start(){
		addKeyListener(this);
		th = new Thread(this);
		th.start();
	}//start

	public void pause() {
		paused = !paused;
		if (!paused) {
			synchronized(th) {
				th.notify();
			}
		}
	}

	public void run(){
		try{
			while(true){
				if (paused) {
					synchronized(th) {
						while (paused) {
							th.wait();
						}
					}
				}
				keyProcess();
				randomEnemyProcess();
				missileProcess();
				repaint();
				Thread.sleep(20);
				cnt++;
			}//while

		}catch(InterruptedException ie) {

		}
	}//run

	public void missileProcess(){
		if(KeySpace){
			ms = new Missile(x, y);
			Missile_List.add(ms);
		}//if          
	}//missileProcess

/*	public void enemyProcess(){
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
*/
	
	public void randomEnemyProcess(){
		for(int i=0; i<Enemy_List.size();i++){
			en =(Enemy)Enemy_List.get(i);
			en.move((int)Enemy_SpeedList[i]);
		}//fori

		if(cnt% 200 == 0){        	
			int numberOfEnemies = 7;

			for(int i=0; i<numberOfEnemies; i++){
				en = new Enemy(frame_wid, (int) (Math.random() * frame_hgt), (int) (Math.random() * 2));
				Enemy_List.add(en);
				if (en.enemyType == 0) {
					Enemy_ImageList.add(enemy1_img);
				} else if (en.enemyType == 1) {
					Enemy_ImageList.add(enemy2_img);
				}

			}
			Enemy_SpeedList = new int [Enemy_List.size()];
			for(int i=0; i<Enemy_SpeedList.length;i++){
				int speed = getRandom(10);
				System.out.println(speed);
				Enemy_SpeedList[i]=speed;

			}//fori stores random speed for each monster
		}//if
	}//random enemy process

	public Image getRandImage(){
		Image randImage;
		if(getRandom(10) > 5){
			randImage = enemy1_img;
		}
		else{
			randImage = enemy2_img;
		}

		return randImage;
	}//choose random enemy

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
		drawScore();
		drawHealth();
		g.drawImage(buffImage, 0, 0, this);

	}//update

	public void drawChar(){
		buffg.clearRect(0, 0, frame_wid, frame_hgt);
		buffg.drawImage(craft_img, x, y, this);

	}//drawChar

	public void drawMissile(){
		for(int i=0; i<Missile_List.size(); ++i){
			ms = (Missile)Missile_List.get(i);
			buffg.drawImage(missile_img, ms.pos.x+125, ms.pos.y+25, this);
			ms.move();
		}//fori
	}//drawMissile

	public void drawEnemy(){
		for(int i=0; i<Enemy_List.size();i++){
			en =(Enemy)(Enemy_List.get(i));
			buffg.drawImage((Image) Enemy_ImageList.get(i), en.x, en.y, this);
		}//fori
	}//drawEnemy

	public void drawScore() {
		buffg.drawString("SCORE: " + gameScore, 10, frame_hgt - 15);
	}

	public void drawHealth() {
		buffg.drawString("HEALTH: " + health, 10, 45);
	}

	public void keyPressed(KeyEvent e){
		switch(e.getKeyCode()){
		case KeyEvent.VK_UP:
			keyUp = true;
			break;
		case KeyEvent.VK_DOWN:
			keyDown = true;
			break;
		case KeyEvent.VK_LEFT:
			keyLeft = true;
			break;
		case KeyEvent.VK_RIGHT:
			keyRight = true;
			break;
		case KeyEvent.VK_SPACE:
			KeySpace = true;
			break;
		case KeyEvent.VK_ESCAPE:
			pause();
			break;
		}
	}//pressed

	public void keyReleased(KeyEvent e){
		switch(e.getKeyCode()){
		case KeyEvent.VK_UP:
			keyUp = false;
			break;
		case KeyEvent.VK_DOWN:
			keyDown = false;
			break;
		case KeyEvent.VK_LEFT:
			keyLeft = false;
			break;
		case KeyEvent.VK_RIGHT:
			keyRight = false;
			break;
		case KeyEvent.VK_SPACE:
			KeySpace = false;
			break;     
		}
	}//released

	public void keyTyped(KeyEvent e){

	}//typed

	public void keyProcess() {
		if (keyUp) {
			if (y - 5 >= 0) {
				y -= 5;
			} else {
				y = 0;
			}
		}
		if (keyDown) {
			if (y + craft_img.getHeight(this) + 5 <= this.frame_hgt) {
				y += 5;
			} else {
				y = this.frame_hgt - craft_img.getHeight(this);
			}
		}
		if (keyLeft) {
			if (x - 5 >= 0) {
				x -= 5;
			} else {
				x = 0;
			}
		}
		if (keyRight) {
			if (x + craft_img.getWidth(this) + 5 <= this.frame_wid) {
				x += 5;
			} else {
				x = this.frame_wid - craft_img.getWidth(this);
			}
		}
	}//proc

	public static int getRandom(int max){
		Random rand = new Random();
		int num = rand.nextInt(max);
		return num;
	}//random number generator

}//gameFrame
