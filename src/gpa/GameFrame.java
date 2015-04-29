package gpa;

import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
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
	
	double spawnRate;

	boolean keyUp = false;
	boolean keyDown = false;
	boolean keyLeft = false;
	boolean keyRight = false;
	boolean KeySpace = false;

	private boolean paused;

	int cnt;
	
	long gameScore = 0L;
	double gpa_double;
	String gpa_String;
	

	Thread th;
	Toolkit tk = Toolkit.getDefaultToolkit();
	
	String craftPath = "img/hero.gif", missilePath = "img/missile.gif", boomPath = "img/boom.gif";
	
	Image craft_img = tk.getImage(craftPath);
	Image missile_img = tk.getImage(missilePath);
	Image boom_img = tk.getImage(boomPath);
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
		addKeyListener(this);
	}//constructor

	public void init(){

		x = 100;
		y = 100;
		gpa_double = 4.0;
		spawnRate=100;
		
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
				
				//increase spawn rate after every wave (smaller the faster! Can't be equal to or lower than 0!
				if(spawnRate>1){
					spawnRate = spawnRate-0.01;
					System.out.println("Current spawn rate: " + spawnRate + " (the lower the faster!)");
				}
			
				
				keyProcess();
				randomEnemyProcess(spawnRate);
				missileProcess();
				repaint();
				collisionProcess();
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

	public void randomEnemyProcess(double spawnRate){
		for(int i=0; i<Enemy_List.size();i++){
			en = Enemy_List.get(i);
			en.move(Enemy_SpeedList.get(i));
			if (en.x + ew[en.enemyType] < 0) {
				Enemy_List.remove(i);
				Enemy_SpeedList.remove(i);
				Enemy_ImageList.remove(i);
				--i;
			}
		}//fori
		
	
		
		if(cnt % 10 == 0){        	
			int numberOfEnemies = 7;
			
			
			for(int i=0; i<numberOfEnemies; i++){
				int type = (int) (Math.random() * totalEnemyTypes);
				en = new Enemy(frame_wid, (int) (Math.random() * (frame_hgt - eh[type])), type);
				Enemy_List.add(en);
				Enemy_ImageList.add(enemies[en.enemyType]);
				int speed = getRandom(10);
				Enemy_SpeedList.add(speed);
				
				
			}
			
		}//if
		
	
	}//random enemy process
	
	public void collisionProcess() {
		heroRect.setLocation(x, y);
		
		//Run through the missile list once, as it is likely longer than the enemy list.
		for (int i = 0; i < Missile_List.size(); ++i) {
			ms = Missile_List.get(i);
			missileRect.setRect(ms.pos.x, ms.pos.y, mw, mh);
			for (int j = 0; j < Enemy_List.size(); ++j) {
				en = Enemy_List.get(j);
				enemyRect.setRect(en.x, en.y, ew[en.enemyType], eh[en.enemyType]);
				if(missileRect.intersects(enemyRect)) {
					dealDamage(j);
					break;
				}
			}
		}
		
		/* Run through the Enemy List one extra time to check collisions
		 * with the player, rather than running this check every time
		 * the list is run through with missiles. 
		 */
		for(int i=0; i<Enemy_List.size();i++){
			en = Enemy_List.get(i);
			enemyRect.setRect(en.x, en.y, ew[en.enemyType], eh[en.enemyType]);
			if (heroRect.intersects(enemyRect)) {
				takeDamage(i);
			}
		}
	}
	
	public void takeDamage() {
		
		if (gpa_double == 0) {
			
		}
		else
		{
			gpa_double -= 0.05;
		}
	}
	
	public void takeDamage(int i) {
		
		Enemy_List.remove(i);
		Enemy_SpeedList.remove(i);
		Enemy_ImageList.remove(i);
		
		//If statement ensures GPA does not go negative.
		if (gpa_double <= 0) {
			gpa_String = "FLUNKED!!!";
			
		}
		else{
			gpa_double -= 0.05;
		}
		
	}
	
	public void dealDamage(int i) {
		Enemy_List.remove(i);
		Enemy_SpeedList.remove(i);
		Enemy_ImageList.remove(i);
		if (gpa_double + .05 <= 4) {
			gpa_double += 0.05;
		}
		gameScore = gameScore+5;
	}

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
	}//drawChar

	public void drawMissile(){
		for(int i=0; i<Missile_List.size(); ++i){
			ms = Missile_List.get(i);
			
			//if statement removes missile if it leaves the visible area
			if(ms.pos.x > frame_wid){
				Missile_List.remove(i);
				break;
			}
			else{
				buffg.drawImage(missile_img, ms.pos.x+125, ms.pos.y+25, this);
				ms.move();
			}
			
		}//fori
	}//drawMissile

	public void drawEnemy(){
		for(int i=0; i<Enemy_List.size();i++){
			en = Enemy_List.get(i);
	
				buffg.drawImage(Enemy_ImageList.get(i), en.x, en.y, this);
			
		}//fori
	}//drawEnemy

	public void drawBoom(int x, int y) {
		buffg.drawImage(boom_img, x, y, this);
	}
	
	public void drawScore() {
		buffg.drawString("SCORE: " + gameScore, 10, frame_hgt - 15);
	}

	public void drawHealth() {
		String pattern = "###.###";
		String gpa_String = Double.toString(gpa_double) + "0";
		gpa_String = gpa_String.substring(0, 4);
		buffg.drawString("GPA: " + gpa_String, 10, 45);
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
		case KeyEvent.VK_ENTER:
			if (th == null) {
				start();
			}
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
			if (y > 0) {
				y -= 5;
			} else {
				y = 0;
			}
		}
		if (keyDown) {
			if (y + craft_img.getHeight(this) < this.frame_hgt) {
				y += 5;
			} else {
				y = this.frame_hgt - craft_img.getHeight(this);
			}
		
		}
		if (keyLeft) {
			if (x > 0) {
				x -= 5;
			} else {
				x = 0;
			}
		}
		if (keyRight) {
			if (x + craft_img.getWidth(this) < this.frame_wid) {
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
