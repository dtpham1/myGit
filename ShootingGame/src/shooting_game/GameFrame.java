/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gpa;

import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

/**
 *
 * @author forainy, dtpham, eayers
 */
public class GameFrame extends JFrame implements KeyListener, Runnable{
	private static final long serialVersionUID = 1407918162155261307L;
	
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
	
	String craftPath = "hero.gif", missilePath = "missile.gif";
	
	Image craft_img = tk.getImage(craftPath);
	Image missile_img = tk.getImage(missilePath);
	Image[] enemies;
	int totalEnemyTypes;
	
	ArrayList<Missile> Missile_List = new ArrayList<Missile>();
	ArrayList<Enemy> Enemy_List = new ArrayList<Enemy>();
	ArrayList<Image> Enemy_ImageList = new ArrayList<Image>();
	ArrayList<Integer> Enemy_SpeedList = new ArrayList<Integer>(Enemy_List.size());

	Image buffImage;
	Graphics buffg;
	Missile ms;
	Enemy en;
	
	int hw, hh, mw, mh;
	int[] ew, eh;
	Rectangle heroRect, missileRect, enemyRect;


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
		
		totalEnemyTypes = Enemy.ENEMY_TYPES.length;

		hw = getImgWidth(craftPath);
		hh = getImgHeight(craftPath);
		mw = getImgWidth(missilePath);
		mh = getImgHeight(missilePath);
		ew = new int[totalEnemyTypes];
		eh = new int[totalEnemyTypes];
		
		enemies = new Image[totalEnemyTypes];
		for (int i = 0; i < totalEnemyTypes; ++i) {
			enemies[i] = tk.getImage(Enemy.ENEMY_TYPES[i]);
			ew[i] = getImgWidth(Enemy.ENEMY_TYPES[i]);
			eh[i] = getImgHeight(Enemy.ENEMY_TYPES[i]);
		}
		
		heroRect = new Rectangle(x, y, hw, hh);
		missileRect = new Rectangle();
		enemyRect = new Rectangle();
		
		
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
			en = Enemy_List.get(i);
			en.move(Enemy_SpeedList.get(i));
		}//fori

		if(cnt% 200 == 0){        	
			int numberOfEnemies = 7;

			for(int i=0; i<numberOfEnemies; i++){
				en = new Enemy(frame_wid, (int) (Math.random() * frame_hgt), (int) (Math.random() * totalEnemyTypes));
				Enemy_List.add(en);
				Enemy_ImageList.add(enemies[en.enemyType]);
				int speed = getRandom(10);
				System.out.println(speed);
				Enemy_SpeedList.add(speed);
			}
			
			/*Enemy_SpeedList = new ArrayList<Integer>(Enemy_List.size());
			for(int i=0; i<Enemy_SpeedList.size();i++){
				int speed = getRandom(10);
				System.out.println(speed);
				Enemy_SpeedList.set(i, speed);

			}//fori stores random speed for each monster
			*/
		}//if
	}//random enemy process
	
	public void takeDamage() {
		health -= 5;
		if (health <= 0) {
			
		}
	}
	
	public void dealDamage(int i) {
		Enemy_List.remove(i);
		Enemy_SpeedList.remove(i);
		Enemy_ImageList.remove(i);
	}


/*	public Image getRandImage(){
		Image randImage;
		if(getRandom(10) > 5){
			randImage = enemy1_img;
		}
		else{
			randImage = enemy2_img;
		}

		return randImage;
	}//choose random enemy
*/

	public void paint(Graphics g){
		buffImage = createImage(frame_wid, frame_hgt);
		buffg = buffImage.getGraphics();
		update(g);

		//
	}//paint

	public void update(Graphics g){
		buffg.clearRect(0, 0, frame_wid, frame_hgt);
		
		drawChar();
		drawMissile();
		drawEnemy();
		drawScore();
		drawHealth();
		g.drawImage(buffImage, 0, 0, this);

	}//update

	public void drawChar(){
		buffg.drawImage(craft_img, x, y, this);
		heroRect.setLocation(x, y);
	}//drawChar

	public void drawMissile(){
		for(int i=0; i<Missile_List.size(); ++i){
			ms = Missile_List.get(i);
			buffg.drawImage(missile_img, ms.pos.x+125, ms.pos.y+25, this);
			ms.move();
		}//fori
	}//drawMissile

	public void drawEnemy(){
		for(int i=0; i<Enemy_List.size();i++){
			en = Enemy_List.get(i);
			buffg.drawImage(Enemy_ImageList.get(i), en.x, en.y, this);

			enemyRect.setRect(en.x, en.y, ew[en.enemyType], eh[en.enemyType]);
			if (heroRect.intersects(enemyRect)) {
				takeDamage();
				dealDamage(i);
			}
			for (int j = 0; j < Missile_List.size(); ++j) {
				ms = Missile_List.get(j);
				missileRect.setRect(ms.pos.x, ms.pos.y, mw, mh);
				if(missileRect.intersects(enemyRect)) {
					dealDamage(i);
					break;
				}
			}
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
	
	public int getImgWidth(String file) {
		int result = -1;
		try {
			File f = new File(file);
			BufferedImage bi = ImageIO.read(f);
			result = bi.getWidth();
		} catch(Exception e) {
			
		}
		return result;
	}
	
	public int getImgHeight(String file) {
		int result = -1;
		try {
			File f = new File(file);
			BufferedImage bi = ImageIO.read(f);
			result = bi.getHeight();
		} catch(Exception e) {
			
		}
		return result;
	}

	public static int getRandom(int max){
		Random rand = new Random();
		int num = rand.nextInt(max);
		return num;
	}//random number generator

}//gameFrame
