package e_shooting_game;

import javax.swing.*;
import javax.imageio.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Erick Ayers
 *
 */
@SuppressWarnings("serial")
public class SFrame2 extends JFrame implements KeyListener, Runnable {
	private int f_width;
	private int f_height;
	
	Toolkit tk = Toolkit.getDefaultToolkit();
	
	Image myCraft;
	Image myMsl;
	Image myNme;
	Image nmeMsl;
	
	String craftImg = "aircraft_small.png", mslImg = "missile_small.png",
			enemyImg = "enemy_ship_small.png", nmeMslImg = "nme_missile_small.png";
	
	Rectangle shipRect, mslRect, nmeRect, nmeMslRect;
	
	int x, y;
	boolean keyUp = false;
	boolean keyDown = false;
	boolean keyLeft = false;
	boolean keyRight = false;
	boolean spaceBar = false;
	
	int sw, sh, mw, mh, ew, eh, emw, emh;
	int health = 100;
	
	int loopCount = 0;
	
	Missile msl;
	ArrayList<Missile> mList = new ArrayList<Missile>();
	
	Enemy nme;
	ArrayList<Enemy> nmeList = new ArrayList<Enemy>();
	
	Thread ship;
	Image buffImage;
	Graphics buffGrfx;
	
	AudioClip bg, enemyDeath, death, shoot;
	
	long gameScore = 0L;
	
	private boolean paused;
	
	SFrame2() {
		init();
		start();
		
		setTitle("Shooting Game");
		setSize(f_width, f_height);
		
		paused = false;
		
		Dimension screen = tk.getScreenSize();
		
		int f_xpos = (int) (screen.getWidth() - f_width) / 2;
		int f_ypos = (int) (screen.getHeight() - f_height) / 2;
		
		setLocation(f_xpos, f_ypos);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);
	}
	
	void init() {
		f_width = 900;
		f_height = 600;
		
		myCraft = tk.getImage(craftImg);
		myMsl = tk.getImage(mslImg);
		myNme = tk.getImage(enemyImg);
		nmeMsl = tk.getImage(nmeMslImg);
		
		sw = getImgWidth(craftImg);
		sh = getImgHeight(craftImg);
		mw = getImgWidth(mslImg);
		mh = getImgHeight(mslImg);
		ew = getImgWidth(enemyImg);
		eh = getImgHeight(enemyImg);
		emw = getImgWidth(nmeMslImg);
		emh = getImgHeight(nmeMslImg);
		
		x = sw;
		y = (f_height - sh) / 2;
		shipRect = new Rectangle(x, y, sw, sh);
		mslRect = new Rectangle();
		nmeRect = new Rectangle();
		nmeMslRect = new Rectangle();
	}
	
	void start() {
		addKeyListener(this);
		ship = new Thread(this);
		ship.start();
		
		bg = createSound("AirBrushed.wav");
		bg.loop();
		
		enemyDeath = createSound("enemyDeath.wav");
		death = createSound("death.wav");
		shoot = createSound("shoot.wav");
	}
	
	public void paint(Graphics g) {
		buffImage = createImage(f_width, f_height);
		buffGrfx = buffImage.getGraphics();
		
		update(g);
	}
	
	public void update (Graphics g) {
		buffGrfx.clearRect(0, 0, f_width, f_height);
		
		drawCraft();
		drawMissile();
		drawEnemy();
		drawScore();
		drawHealth();
		g.drawImage(buffImage, 0, 0, this);
	}
	
	public void drawCraft() {
		buffGrfx.drawImage(myCraft, x, y, this);
		shipRect.setRect(x, y, sw, sh);//.setLocation(x, y);
		for (int i = 0; i < nmeList.size(); ++i) {
			nme = nmeList.get(i);
			nmeRect = new Rectangle(nme.x, nme.y, ew, eh);
			if (shipRect.intersects(nmeRect)) {
				nmeList.remove(i);
				takeDamage();
				break;
			}
		}
	}
	
	public void drawMissile() {
		for (int i = 0; i < mList.size(); ++i) {
			msl = mList.get(i);
			if (msl.w == 0) {
				buffGrfx.drawImage(myMsl, msl.x, msl.y, this);
			} else {
				buffGrfx.drawImage(nmeMsl, msl.x, msl.y, this);
			}
		}
	}
	
	public void drawEnemy() {
		for(int i = 0; i < nmeList.size(); ++i) {
			nme = nmeList.get(i);
			buffGrfx.drawImage(myNme, nme.x, nme.y, this);
			nme.move();
			if (nme.x < -ew) {
				nmeList.remove(i);
			}
		}
	}
	
	public void drawScore() {
		buffGrfx.drawString("SCORE: " + gameScore, 10, 50);
	}
	
	public void drawHealth() {
		buffGrfx.drawString("HEALTH: " + health, f_width - 84, 50);
	}
	
	void stop() {
		ship.interrupt();
		bg.stop();
		buffImage = createImage(f_width, f_height);
		buffGrfx = buffImage.getGraphics();
		buffGrfx.clearRect(0, 0, f_width, f_height);
		buffGrfx.drawString("GAME OVER", f_width / 2 - 10, f_height / 2 - 1);
		Graphics g = this.getGraphics();
		g.clearRect(0, 0, f_width, f_height);
		g.drawImage(buffImage, 0, 0, this);
	}

	public void keyPressed(KeyEvent ke) {
		switch (ke.getKeyCode()) {
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
				spaceBar = true;
				break;
			case KeyEvent.VK_ESCAPE:
				pause();
				break;
		}
	}
	
	public void keyReleased(KeyEvent ke) {
		switch (ke.getKeyCode()) {
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
				spaceBar = false;
				break;
		}
	}
	
	public void keyTyped(KeyEvent ke) {
		
	}
	
	public void keyProcess() {
		if (keyUp) {
			if (y - 5 >= 0) {
				y -= 5;
			} else {
				y = 0;
			}
		}
		if (keyDown) {
			if (y + myCraft.getHeight(this) + 5 <= this.f_height) {
				y += 5;
			} else {
				y = this.f_height - myCraft.getHeight(this);
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
			if (x + myCraft.getWidth(this) + 5 <= this.f_width) {
				x += 5;
			} else {
				x = this.f_width - myCraft.getWidth(this);
			}
		}
	}
	
	public void barProcess() {
		if (spaceBar && loopCount % 3 == 0) {
			msl = new Missile(x + myCraft.getWidth(this) - 10, y + ((myCraft.getHeight(this) - myMsl.getHeight(this)) /2), 0);
			mList.add(msl);
			shoot.play();
		}
		
		out:
		for (int i = 0; i < mList.size(); ++i) {
			msl = mList.get(i);
			msl.move();
			if (msl.w == 0) {
				mslRect = new Rectangle(msl.x, msl.y, mw, mh); 
				
				for (int j = 0; j < nmeList.size(); ++j) {
					nme = nmeList.get(j);
					nmeRect = new Rectangle(nme.x, nme.y, ew, eh);

					if (mslRect.intersects(nmeRect)) {
						mList.remove(msl);
						nmeList.remove(nme);
						gameScore += 10;
						enemyDeath.play();
						break out;
					}
				}
				for (int j = 0; j < mList.size(); ++j) {
					Missile temp = mList.get(j);
					if (temp.w == 0) {
						continue;
					} else {
						nmeMslRect = new Rectangle(temp.x, temp.y, emw, emh);
						
						if (mslRect.intersects(nmeMslRect)) {
							mList.remove(msl);
							mList.remove(temp);
							enemyDeath.play();
							break out;
						}
					}
				}
			} else {
				nmeMslRect = new Rectangle(msl.x, msl.y, emw, emh);
				if (nmeMslRect.intersects(shipRect)) {
					mList.remove(msl);
					takeDamage();
					enemyDeath.play();
					break out;
				}
				for (int j = 0; j < mList.size(); ++j) {
					Missile temp = mList.get(j);
					if (temp.w == 0) {
						mslRect = new Rectangle(temp.x, temp.y, mw, mh);
						
						if (mslRect.intersects(nmeMslRect)) {
							mList.remove(msl);
							mList.remove(temp);
							enemyDeath.play();
						}
					} else {
						continue;
					}
				}
			}
			
			if (msl.x < -mw || msl.x > f_width + 150) {
				mList.remove(i);
			}
		}
	}
	
	public void enemyProcess() {
		if (loopCount % 210 == 0) {
			for (int i = 0; i < 5; ++i) {
				nme = new Enemy(f_width, (int) (Math.random() * (f_height - eh)));
				nmeList.add(nme);
			}
		}
		
		if (loopCount % 75 == 0) {
			for (int i = 0; i < nmeList.size(); ++i) {
				nme = nmeList.get(i);
				msl = new Missile(nme.x - mw, nme.y + (eh - emh) / 2, 1);
				mList.add(msl);
			}
		}
	}
	
	public void takeDamage() {
		health -= 5;
		if (health <= 0) {
			death.play();
			stop();
			stop();
		}
	}
	
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
	
	public void pause() {
		paused = !paused;
		if (!paused) {
			synchronized(ship) {
				ship.notify();
			}
		}
	}
	
	public void run() {
		try {
			while (true) {
				if (paused) {
					synchronized(ship) {
						while (paused) {
							ship.wait();
						}
					}
				}
				
				keyProcess();
				barProcess();
				enemyProcess();
				repaint();
				Thread.sleep(20);
				++loopCount;
			}
		} catch (InterruptedException ie) {
			
		}
	}
	
	public AudioClip createSound(String file) {
		AudioClip clip = null;
		try {
			URL url = new File(file).toURI().toURL();
			clip = Applet.newAudioClip(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return clip;
	}
}
