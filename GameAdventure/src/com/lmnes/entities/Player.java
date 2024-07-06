package com.lmnes.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.lmnes.main.Game;
import com.lmnes.main.Sound;
import com.lmnes.world.Camera;
import com.lmnes.world.World;

//classe principal do player
public class Player extends Entity {

	public boolean right, up, left, down;
	public int right_dir = 0, left_dir = 1;
	public int dir = right_dir;
	public double speed = 1.4 ;//velocidade padrão do Player 
	
	
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	
	private boolean hasGun = false;
	
	private BufferedImage playerDamage;
	
	
	public static  int ammo =0;
	
	public boolean isDamaged = false;
	
	private int damagedFrames = 0;
	
	public boolean shoot = false, mouseShoot = false;
	public int mx, my;
	
	public double life = 100,maxlife = 100;
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		//4 quantidade de sprites 
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		
		playerDamage = Game.spritesheet.getSprite(0, 16, 16, 16);
		
		for(int i =0; i< 4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 0, 16, 16);
		}//looping de sprites para direita;d
		
		for(int i =0; i< 4; i++ ) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 16, 16, 16);
		}//looping de sprites para esquerda;
	}
	
	//toda a logica do game é colocada e tick();
	public void tick(){
		moved = false;
		if(right && World.isFree((int)(x+speed),this.getY())) {
			moved = true;
			dir = right_dir;
			x+=speed;
		}
		else if(left && World.isFree((int)(x-speed),this.getY())) {
			moved = true;
			dir = left_dir;
			x-=speed;
		}
		if(up && World.isFree(this.getX(),(int)(y-speed))){
			moved = true;
			y-=speed;
		}
		else if(down && World.isFree(this.getX(),(int)(y+speed))){
			moved = true;
			y+=speed;
		}
		
		if(moved) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex)
					index = 0;
			}
		}
		
		checkCollisionLifePack();
		checkCollisionAmmo();
		checkCollidionGun();
		
		if(isDamaged) {
			this.damagedFrames++;
			if(this.damagedFrames == 8) {
				this.damagedFrames = 0;
				isDamaged = false;
			}
			
		}
		
		if(shoot && hasGun && ammo > 0) {
			shoot = false;
			ammo--;
			int dx = 0;
			int px = 0;
			int py = 6;
			if(dir == right_dir ) {
				px = 4; 
				dx = 1;
				 
			}else {
				px = -6;
				dx = -1;
			}
		
			 BulletShoot bullet = new BulletShoot(this.getX()+px, this.getY()+py,3,3,null,dx,0);
			 Game.bullets.add(bullet);
		}
		/*ativa o mouse para atirar com o mesmo
		 * */
		if(mouseShoot) {
			
			mouseShoot = false;
			
			if(hasGun && ammo > 0) {
			ammo--;
			
			int py = 0, px = 0;
			double angle = 0;
			if(dir == right_dir ) {
				px = 12; 
				angle = Math.atan2(my - (this.getY()+py - Camera.y), mx - (this.getX()+px - Camera.x));
			}else {
				px = 10;
				angle = Math.atan2(my - (this.getY()+py - Camera.y), mx - (this.getX()+px - Camera.x));
			}
			double dx = Math.cos(angle);
			double dy = Math.sin(angle);
		
			 BulletShoot bullet = new BulletShoot(this.getX()+px,this.getY()+py,3,3,null,dx,dy);
			 Game.bullets.add(bullet);
			}
		}
		
		if(life <= 0) {
			//game over 
			life = 0;
			Game.gameState = "GAME_OVER";
		}
		
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2),0,World.WIDTH*16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2),0,World.HEIGHT*16 - Game.HEIGHT);
	}
	
	public void checkCollisionAmmo() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Bullet) {
				if(Entity.isColidding(this, atual)) {
					ammo = 50;
					
					//System.out.println("Munciçãoo atual:" + ammo);
					//linha para testar no console se esta pegando a ammo
					Game.entities.remove(atual);
				}
			}
		}
	}
	
	/*checkCollisionLifePack() chega a colisão para as balas só aparece após pegar as armas 
	 * */
	public void checkCollisionLifePack() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof LifePack) {
				if(Entity.isColidding(this, atual)) {
					life+=10;
					if(life > 100)
						life = 100;
					Game.entities.remove(atual);
				}
			}
		}
		
		
	}
	
	/*checkCollidionGun() é uma classe para pegar a arma 
	 */
	public void checkCollidionGun() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof Weapon) {
				if(Entity.isColidding(this,e)) {
					hasGun = true;
					System.out.println("ok");
					Game.entities.remove(i);
					return;
				}
			}
		}
	}
	
	/*checkItens lifepack para ver se a vida está igual a 100 caso não esteja ele completa para 100
	 * */
	public void checkItens() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof LifePack) {
				if(Entity.isColidding(this,e)) {
					life+=8;
					if(life >= 100)
						life = 100;
					Game.entities.remove(i);
					return;
				}
			}
		}
	}
	
	//aonde vai renderizar o player dentro da classe do render
	public void render(Graphics g) {
		if(!isDamaged) {
		if(dir == right_dir) {//renderiza a arma para direita 
			g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			if(hasGun) {
				g.drawImage(Entity.GUN_RIGHT,this.getX()+3 - Camera.x, this.getY()+3 - Camera.y,null);
			}
		}else if(dir == left_dir) {//renderiza a arma para direita
				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
				if(hasGun) {
					g.drawImage(Entity.GUN_LEFT,this.getX()-3 - Camera.x, this.getY()+3 - Camera.y,null);
				}
		}
			}else{
			g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y,null);
			if(hasGun) {
					if(dir == left_dir) {
					g.drawImage(Entity.GUN_RIGHT_DAMAGE, this.getX() - Camera.x, this.getY() - Camera.y, null);
				}else {
					g.drawImage(Entity.GUN_LEFT_DAMAGE, this.getX() - Camera.x, this.getY() - Camera.y, null);
				}
			}
		}
	}
}