package com.lmnes.entities;

//import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.lmnes.main.Game;
import com.lmnes.main.Sound;
import com.lmnes.world.Camera;
import com.lmnes.world.World;

public class Enemy extends Entity {
	
	private double speed = 0.6;

	private int maskx = 8, masky = 8, maskw = 10, maskh = 10;
	
	private int frames = 0, maxFrames = 15, index = 0, maxIndex = 1;
	
	private boolean isDamaged = false;
	private int damageFrames = 10, damageCurrent = 0; 
	
	private BufferedImage[] sprites;
	
	private int life = 5;

	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage[2]; //Quantidade de sprites que o inimigo tem tendo do Array BufferedImage[2]; no caso são 2 
		sprites[0] = Game.spritesheet.getSprite(112,16,16,16);//posição do sptire inicial do inimigo
		sprites[1] = Game.spritesheet.getSprite(112+16,16,16,16);//posição dos sprites do inimigo

	}

	//logica o inimigo vai atras de voce baseado na posição do player 
	//depois dos && esta a colisão a soma do spide puchando do isFree
	
	
	//TICK esta certo agr
	
	public void tick() {
			if(isColiddingWithPlayer() == false) {
			if((int)x < Game.player.getX() && World.isFree((int)(x+speed), this.getY())
					&& !isColidding((int)(x+speed), this.getY())){
				x+=speed;	
			}
			else if((int)x > Game.player.getX() && World.isFree((int)(x-speed), this.getY())
					&& !isColidding((int)(x-speed), this.getY())) {
				x-=speed; 
			}
		
			if((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y+speed))
					&& !isColidding(this.getX(), (int)(y+speed))) {
				y+=speed;
			}
			else if((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y-speed))
					&& !isColidding(this.getX(), (int)(y-speed))) {
				y-=speed;
			}
				/* o  código acima e apra fazer o inimigo seguir o Player aonde estiver  no mapa
				 * somando e subtraindo a posição dele perante o player 
				 * */			
			}else {
				//dano do inimigo 
				if(Game.rand.nextInt(100)<10){   
					Sound.hurtSound.play();
				Game.player.life-=Game.rand.nextInt(3);
				Game.player.isDamaged = true;
				if(Game.player.life <= 0) {
					
					//game over
					//System.exit(1);
				}
				System.out.println("Vida " + Game.player.life);
				}
			}
			
			//faz a animação do inimigo
				frames++;
				if(frames == maxFrames) {
					frames = 0;
					index++;
					if(index > maxIndex)
						index = 0;

		}
				collidingBullet();
				
				if(life <= 0) {
					destroiSelf(); 
				}
				if(isDamaged ) {
					this.damageCurrent++;
					if(this.damageCurrent == this.damageFrames) {
						this.damageCurrent = 0;
						
						this.isDamaged = false;
					}
				}
	}
		
		public void destroiSelf() {
			Game.enemies.remove(this);
			Game.entities.remove(this);
			
			return;
		}
	
		public void collidingBullet() {
			for(int i = 0; i < Game.bullets.size(); i++) {
				Entity e = Game.bullets.get(i);
				if(e instanceof BulletShoot) {
					if(Entity.isColidding(this,e)) {
						isDamaged = true;
						life--;
						Game.bullets.remove(i);
						return;
					}
				}
			}
		}
	
		public boolean isColiddingWithPlayer() {
			Rectangle enemyCurrent = new Rectangle(this.getX() + maskx, this.getY() + masky, maskw, maskh);
			Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(),16,16);
			
			return enemyCurrent.intersects(player);
		}
	
		public boolean isColidding(int xnext, int ynext) {
			Rectangle enemyCurrent = new Rectangle(xnext + maskx, ynext + masky, maskw, maskh);
			for(int i = 0; i < Game.enemies.size(); i++) {
				Enemy e = Game.enemies.get(i);
				if(e == this)
					continue;
				Rectangle targetEnemy = new Rectangle(e.getX() + maskx, e.getY() + masky, maskw, maskh);
				if(enemyCurrent.intersects(targetEnemy)) {
					return true;
				}
			}
			return false;
		}
		
		/*Aonde cria a colisão em retangulo do inimigo usando a biblioteca Rectangle na classe siColliding
		 * */
		
		public void render(Graphics g) {
			if(!isDamaged) {
			g.drawImage(sprites[index],this.getX() - Camera.x, this.getY() - Camera.y,null);
			}else {
				g.drawImage(Entity.ENEMY_FEEDBACK,this.getX() - Camera.x, this.getY() - Camera.y,null);
			}
			/*
			 * nesse bloco de cogido esta sendo renderizado o inimigo da classe render  
			 * 
			 *abaixo test de colisão colocando cor azul no inimigo 
			g.setColor(Color.BLUE);
			g.fillRect(this.getX() + maskx - Camera.x, this.getY() + masky- Camera.y,maskw,maskh);
			*/
		}
}