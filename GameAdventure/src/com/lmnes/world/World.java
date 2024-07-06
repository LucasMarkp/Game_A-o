package com.lmnes.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.lmnes.entities.Bullet;
import com.lmnes.entities.Enemy;
import com.lmnes.entities.Entity;
import com.lmnes.entities.LifePack;
import com.lmnes.entities.Player;
import com.lmnes.entities.Weapon;
import com.lmnes.graficos.Spritesheet;
import com.lmnes.main.Game;

public class World {

	public static Tile[] tiles;
	public static int WIDTH, HEIGHT; 
	public static final int TILE_SIZE = 16;
	
	//LEITURA DE DO MAPA 
	public World(String path) {// antes do try cath comando BufferedImage map = ImageIO.read(getClass().getResource(path)); 
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			int[] pixels = new int[map.getWidth() * map.getHeight()]; //multiplica largura e altura do mapa para poder saber a localização dos pixels
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			tiles = new Tile[map.getWidth() * map.getHeight()];
			map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());//(startX , startY, w tamanho , h altura , array == pixel, offset, scansize == )
			for(int xx = 0; xx < map.getWidth(); xx++) {
				for(int yy = 0; yy < map.getHeight(); yy++) {
					int leituraPixel = pixels[xx + (yy * map.getWidth())];
					tiles[xx + ( yy *WIDTH)] = new FloorTile(xx*16,yy*16,Tile.TILE_FLOOR);
					if( leituraPixel == 0xFF000000) {
						//floor/chão do map
						tiles[xx + ( yy *WIDTH)] = new FloorTile(xx*16,yy*16,Tile.TILE_FLOOR);
					}else if(leituraPixel == 0xFFFFFFFF){
						//parede
						tiles[xx + ( yy *WIDTH)] = new WallTile(xx*16,yy*16,Tile.TILE_WALL);
					}else if(leituraPixel == 0xFF0026FF) {
							//player 
						Game.player.setX(xx*16);
						Game.player.setY(yy*16);
					}else if(leituraPixel == 0xFFFF0000) {
						//Enemy
						/*Nessa forma que foi instaciado os inimigos ele vao ter colisão somente entre eles  
						 * e vão ignorar os outros objetos como LIFEPACK BULLET etc
						 * */
						Enemy en = new Enemy(xx*16, yy*16,16,16 ,Entity.ENEMY_EN);
						Game.entities.add(en);
						Game.enemies.add(en);
					}else if(leituraPixel == 0xFF6B3F7F) {
						//weapon
						Game.entities.add(new Weapon  (xx*16, yy*16,16,16, Entity.WEAPON_EN));
					}else if(leituraPixel == 0xFFFFD800) {
						//LifePack 
						Game.entities.add(new LifePack(xx*16, yy*16,16,16, Entity.LIFEPACK_EN));
					}else if(leituraPixel == 0xFFFF66D3) {
						//bullet
						Game.entities.add(new Bullet(xx*16, yy*16,16,16, Entity.BULLET_EN));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//colisão do game
	public static boolean isFree(int xnext, int ynext) {
		
		int x1 = xnext / TILE_SIZE;
		int y1 = ynext / TILE_SIZE;
		
		int x2 = (xnext+TILE_SIZE-1) / TILE_SIZE;
		int y2 = ynext / TILE_SIZE;
		
		int x3 = xnext / TILE_SIZE;
		int y3 = (ynext+TILE_SIZE-1) / TILE_SIZE;
		
		int x4 = (xnext+TILE_SIZE-1) / TILE_SIZE;
		int y4 = (ynext+TILE_SIZE-1) / TILE_SIZE;
		
		return !((tiles[x1 + (y1*World.WIDTH)] instanceof WallTile) ||
				 (tiles[x2 + (y2*World.WIDTH)] instanceof WallTile) ||
				 (tiles[x3 + (y3*World.WIDTH)] instanceof WallTile) ||
				 (tiles[x4 + (y4*World.WIDTH)] instanceof WallTile));
	}
	
	public static void restartGame(String level) {
		
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.spritesheet = new Spritesheet("/spritesheet.png");//spitesheet tem que ser carregado antes do mundo
		Game.player = new Player(0,0,16,16,Game.spritesheet.getSprite(32, 0, 16, 16));//Sprite do player 32 e a soma dos sprites ate aonde se encontra ele 16 po 16 e o tamanho da sprite psitesheet nome do arquivo/classe
		Game.entities.add(Game.player);//player aonde ele puxa as preferencias de entidades e coloca na classe player 
		Game.word = new World("/"+level);
		return;
	}
	
	//render do jogo
	public void render(Graphics g){
		int xstart = Camera.x >> 4;
		int ystart = Camera.y >> 4;
		
		int xfinal = xstart + (Game.WIDTH >> 4);
		int yfinal = ystart + (Game.HEIGHT >> 4);
		
		for(int xx = xstart; xx <= xfinal; xx++) {
			for(int yy = ystart; yy <= yfinal; yy++) {
				if(xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT)
					continue;
				Tile tile = tiles[xx + (yy*WIDTH)];
				tile.render(g);
			}
		}
	}
}