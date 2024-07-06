package com.lmnes.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.lmnes.entities.BulletShoot;
import com.lmnes.entities.Enemy;
import com.lmnes.entities.Entity;
import com.lmnes.entities.Player;
import com.lmnes.graficos.Spritesheet;
import com.lmnes.graficos.UI;
import com.lmnes.world.World;

public class Game extends Canvas implements Runnable, KeyListener,MouseListener{
	
	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;  
	
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	public static final int SCALE = 3;
	
	public Menu menu;
	
	private int CUR_LEVEL = 1,MAX_LEVEL = 2;
	private BufferedImage image;
	
	
	public static List<Entity> entities;//entidades do games
	public static List<Enemy> enemies;
	public static List<BulletShoot> bullets;
	public static Spritesheet spritesheet;//passou a ser static para acesso mais facil
	
	
	public static World word;//Mapas do game
	
	public static  Player player;//player do game

	public static Random rand;//biblioteca para numeros randomicos 
	
	public UI ui;
	
	public static String gameState = "NORMAL";//aki são as states do game caso padrão e menu
	private boolean showMessageGameOver = true;
	private int framesGameover = 0;
	private boolean restartGame = false;
	
	public Game() {
		Sound.musicBackGround.loop();
		rand = new Random();
		addKeyListener(this);//this porque a os metodos de inputs dos botões estão nesta classe 
		addMouseListener(this);
		setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));
		initFrame();
		
		//inicair objs
		ui = new UI();
		image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<BulletShoot>(); 
		
		spritesheet = new Spritesheet("/spritesheet.png");//spitesheet tem que ser carregado antes do mundo
		player = new Player(0,0,16,16,spritesheet.getSprite(32, 0, 16, 16));//Sprite do player 32 e a soma dos sprites ate aonde se encontra ele 16 po 16 e o tamanho da sprite psitesheet nome do arquivo/classe
		entities.add(player);//player aonde ele puxa as preferencias de entidades e coloca na classe player 
		word = new World("/level1.png");
		
		menu = new Menu();
	}
	
	public void initFrame() { 
		frame = new JFrame("Game");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void tick(){
		if(gameState == "NORMAL") {
		this.restartGame = false;
		for(int i = 0; i < entities.size(); i++) {
			Entity  e = entities.get(i);
			e.tick();
		}
		
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).tick();
		}
		
		if(enemies.size() == 0) {
			CUR_LEVEL++;
			if(CUR_LEVEL > MAX_LEVEL) {
				CUR_LEVEL = 1;
			}
			
			String newWorld = "level"+CUR_LEVEL+".png";
			World.restartGame(newWorld);
		}
		}else if(gameState == "GAME_OVER") {
			this.framesGameover++;
			if( this.framesGameover == 15 ) {
				this.framesGameover = 0;
				if(this.showMessageGameOver)
					this.showMessageGameOver = false;
				else  
					this.showMessageGameOver = true;
			}
			
			if(restartGame) {
				this.restartGame = false;
				gameState =  "NORMAL";
				CUR_LEVEL = 1;
				String newWorld = "level"+CUR_LEVEL+".png";
				World.restartGame(newWorld);
			}
		}else if(gameState == "MENU") {	
			menu.tick();
		}
	}
	
	/*as coisas renderizam em ordem de prioridade caso o player esteja na linha 1 e o mundo na linha 0 o mundo esta acima do player 
	 * */
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
			if(bs == null) {
				this.createBufferStrategy(3);
				return;
			}
		Graphics g = image.getGraphics();
			g.setColor(new Color(0,0,0));
			g.fillRect(0, 0, WIDTH, HEIGHT);
		
		//renderização do game
		word.render(g);
		
		for(int i = 0; i < entities.size(); i++) {
			Entity  e = entities.get(i);
			e.render(g);
		}
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).render(g);
		}

		ui.render(g);
																				
			g.dispose();
			g = bs.getDrawGraphics();
			g.drawImage(image, 0, 0, WIDTH*SCALE,HEIGHT*SCALE, null);
			g.setFont(new Font("arial",Font.BOLD,20));
			g.drawString("Ammo:"+ Player.ammo, 580,20);
			if(gameState == "GAME_OVER") {
				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(new Color(0,0,0,100));
				g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
				g.setFont(new Font("arial",Font.BOLD,40));
				g.setColor(Color.WHITE);
				g.drawString("GAME_OVER", (WIDTH* SCALE) /2 - 110,(HEIGHT* SCALE) /2 - 110);
				g.setFont(new Font("arial",Font.BOLD,20));
			if(showMessageGameOver) 
					g.drawString ("Press Enter Continue", (WIDTH* SCALE) /2 - 110,(HEIGHT* SCALE) /2 - 30);
			}else if(gameState == "MENU") {
				menu.render(g);
			}
			bs.show();
		}
			
		public void run() {
			long lastTime = System.nanoTime();
			double amoutOfTicks = 60.0;
			double ns = 1000000000 / amoutOfTicks;
			double delta = 0;
			int frames = 0;
			double timer = System.currentTimeMillis();
			requestFocus();//inicializa e não precisa mais clica nele pra jogar :3
			while(isRunning) {
				long now = System.nanoTime();
				delta+= (now - lastTime) / ns;
				lastTime = now;
				if(delta >= 1) {
					tick();
					render();
					frames++;
					delta--;
				}
				if(System.currentTimeMillis() - timer >= 1000) {
					System.out.println("FPS:" + frames);
					frames = 0;
					timer+=1000;
				}
			}
			stop();
		}
		
		
		//TECLAS E FUNÇÃO DE MOVIMENTO DO GAME 
		@Override
		public void keyTyped(KeyEvent e) {
		}
		//fazer if separado para que possar movimentar separado caso seja else if o movimento sera o mesmo;

		@Override
		//Quando preciona o botão 
		public void keyPressed(KeyEvent e) {
			//direita 
			if(e.getKeyCode() == KeyEvent.VK_RIGHT || 
					e.getKeyCode() == KeyEvent.VK_D) {
				player.right = true;
			}
			//esquerda 
			else if(e.getKeyCode() == KeyEvent.VK_LEFT|| 
					e.getKeyCode() == KeyEvent.VK_A) {
				player.left = true;
			}			
			//cima 
			 if(e.getKeyCode() == KeyEvent.VK_UP || 
					e.getKeyCode() == KeyEvent.VK_W) {
				player.up = true;
				if(gameState == "MENU") {
					menu.up = true;
				}
			}
			 //baixo
			 else if(e.getKeyCode() == KeyEvent.VK_DOWN ||
					e.getKeyCode() == KeyEvent.VK_S) {
				player.down = true;
				
				if(gameState == "MENU") {
					menu.down = true;
				}
			}
			 if(e.getKeyCode() == KeyEvent.VK_X) {
					player.shoot = true;
				}
			 
			 if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				 this.restartGame = true;
				 if(gameState == "MENU") {
					 menu.enter = true;
				 }
			 }
			 if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				 gameState = "MENU";
				 menu.pause = true;
			 }
		}
		
		@Override 
		//Quando solta o botão
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_RIGHT || 
					e.getKeyCode() == KeyEvent.VK_D) {
				
				player.right = false;
			}
			else if(e.getKeyCode() == KeyEvent.VK_LEFT || 
					e.getKeyCode() == KeyEvent.VK_A) {
				
				player.left = false;
			}			
			if(e.getKeyCode() == KeyEvent.VK_UP || 
					e.getKeyCode() == KeyEvent.VK_W) {
				player.up = false;
			}
			else if(e.getKeyCode() == KeyEvent.VK_DOWN ||
					e.getKeyCode() == KeyEvent.VK_S) {
				
				player.down = false;
			}
			if(e.getKeyCode() == KeyEvent.VK_X) {
				player.shoot = false;
			}
			
		}
		
		

		@Override
		public void mouseClicked(MouseEvent e) {
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
 			player.mouseShoot = true;
 			player.mx = (e.getX() / 3 );
 			player.my = (e.getY() / 3 );
 			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			
		}
		//principal
		public static void main(String args[]) {
			Game game = new Game();
			game.start();
		
		}
	}	