package com.lmnes.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Menu {
	
	public String[] options = {"novo jogo","carregar","sair"};
	
	public boolean down,up,enter;
	public boolean pause = false;
	
	public int currentOption = 0;
	public int maxOption = options.length - 1;
	
	public void tick() {
		
		if(up) {
			up = false;
			currentOption--;
			if(currentOption < 0)
				currentOption = maxOption;
		}
		if(down) {
			down = false;
			currentOption++;
			if(currentOption > maxOption)
				currentOption = 0;
		}
		if(enter) {
			enter = false;
			if(options[currentOption] == "novo jogo" || options[currentOption] == "pause ") {
				Game.gameState = "NORMAL";
				pause = false;
			}else if(options[currentOption] == "sair") {
				System.exit(1); 
			}
		}
		
	}

	public void render(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0,0,0,200));
			//parte de cima do menu
			g2.fillRect(0, 0, Game.WIDTH*Game.SCALE, Game.HEIGHT*Game.SCALE);
			g.setColor(Color.yellow);
			g.setFont(new Font("arial", Font.BOLD,36)); 
			//opções de menu
			//aki fica a logo do menu
			g.drawString("ADVENTURE",(Game.WIDTH*Game.SCALE) /2 - 100, (Game.HEIGHT*Game.SCALE) /2 - 100);
			g.setColor(Color.white);
			g.setFont(new Font("arial",Font.BOLD,24));
		//opcoes do menu
		if(pause == false) 
			g.drawString("Novo Jogo", (Game.WIDTH*Game.SCALE) / 2 - 50, 160);
			else 
			g.drawString("Resumir",(Game.WIDTH*Game.SCALE) / 2 -50,200);
			
		g.drawString("Carregar", (Game.WIDTH*Game.SCALE) / 2 - 50, 240);
		g.drawString("Sair",(Game.WIDTH*Game.SCALE) / 2 - 10, 280);
		// setas do menu
			if(options[currentOption] == "novo jogo") { 
				g.drawString(">",(Game.WIDTH*Game.SCALE) / 2 - 90, 200);
			}else if(options[currentOption] == "carregar") {
				g.drawString(">",(Game.WIDTH*Game.SCALE) / 2 - 90, 245);
			}else if(options[currentOption] == "sair") {
				g.drawString(">",(Game.WIDTH*Game.SCALE) / 2 - 90, 280);
			}
		}
	}