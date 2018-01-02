package main;

import types.BasicAbility;
import types.InformationBar;
import types.MapItem;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class Render {
  
  private static Canvas canvas = Main.getCanvas();
  private static GraphicsContext gc = Main.getGc();
  
  // Various resources and backgrounds.
  private static Image profileSelect = new Image("file:resources/profile select.png");
  private static Image titleScreen = new Image("file:resources/title screen.png");
  private static Image levelSelect = new Image("file:resources/level select.png");
  private static Image charSelect = new Image("file:resources/character_select.png");
  private static Image defeatPineapple = new Image("file:resources/defeat_pineapple.png");
  
  // Select game profile to play on.
  public static void renderProfile() {
    
    int y = 335;
    // Change the y coordinate at which to draw the indicator depending on which profile is selected.
    Render.gc.drawImage(Render.profileSelect, 0, 0);
    switch (Main.getSelection()) {
    case 2:
      y += 140;
      break;
    case 3:
      y += 280;
      break;
    default:
      break;
    }
    
    // Draw the indicator.
    Render.gc.fillRect(335, y, 330, 10);
    
  }
  
  public static void renderTitle() {
    
    int y = 350;
    Render.gc.drawImage(Render.titleScreen, 0, 0);
    // Change the y position at which to draw the indicator.
    switch (Main.getSelection()) {
    case 2:
      y = 435;
      break;
    case 3:
      y = 518;
      break;
    case 4:
      y = 600;
      break;
    }
    
    // Draw the indicator in the form of the selected character's icon.
    Render.gc.drawImage(Main.getProtag().getIcon(), 220, y - (Main.getProtag().getIcon().getHeight() / 2));
    
  }
  
  // Render the character select window.
  public static void renderCharacterSelect() {
    
    Render.gc.drawImage(Render.charSelect, 0, 0);
    Main.setFont(100);
    Render.gc.fillText("Character Select", 182, 80);
    // Render the current selected character and it's name.
    Render.gc.drawImage(Constants.CHARACTERS[Main.getSelection() - 1].getSelectSprite(), 500 - (Constants.CHARACTERS[Main.getSelection() - 1].getSelectSprite().getWidth() / 2), 230 - (Constants.CHARACTERS[Main.getSelection() - 1].getSelectSprite().getHeight() / 2));
    Render.gc.fillText(Constants.CHARACTERS[Main.getSelection() - 1].name, 500 - (Constants.CHARACTERS[Main.getSelection() - 1].getNameWidth()), 500 - (Constants.CHARACTERS[Main.getSelection() - 1].getNameHeight() / 2));
    
  }
  
  // Render the level select.
  public static void renderLevelSelect() {
    
    Render.gc.drawImage(Render.levelSelect, 0, 0);
    Main.setFont(800);
    Render.gc.fillText(Integer.toString(Main.getSelection()), 100, 600);
    
  }
  
  public static synchronized void renderLevel() {
    
    // Render the background in the appropriate position.
    Render.gc.fillRect(0, 0, Render.canvas.getWidth(), Render.canvas.getHeight());
    Render.gc.drawImage(Main.getCurrentLevel().getBackground(), (Main.visibleX / 4) + 500, (Main.visibleY / 4) + 500, 1000, 600, 0, 0, 1000, 600);
    
    for (MapItem item : Main.getCurrentLevel().getMapItems()) {
      
    	// Draws all map items to the screen.
      Render.gc.drawImage(item.sprite, item.vx, item.vy);
      
    }
    
    // Renders the character.
    if (!Main.getProtag().isSpriteDirectional()) {
      Render.gc.drawImage(Main.getProtag().hurt ? Main.getProtag().getHurtSprite() : Main.getProtag().sprite, Main.getProtag().vx, Main.getProtag().vy);
    }
    else {
      if (Main.getProtag().hurt) {
        Render.gc.drawImage(Main.getProtag().lastDirection == 1 ? Main.getProtag().getHurtSprite() : Main.getProtag().getHurtLeft(), Main.getProtag().vx, Main.getProtag().vy);
      }
      else {
        Render.gc.drawImage(Main.getProtag().lastDirection == 1 ? Main.getProtag().sprite : Main.getProtag().getSpriteLeft(), Main.getProtag().vx, Main.getProtag().vy);
      }
    }
    
    // Renders a black bar to be used to display information.
    Render.gc.fillRect(0, 600, 1000, 80);
    
    // Renders current health and bar representing time before the character can shoot again.
    Render.gc.drawImage(InformationBar.getBackground(), 0, 600);
    double healthWidth = ((double) 380 / 100 * Main.getProtag().hp);
    Render.gc.drawImage(InformationBar.getHealthbar(), 0, 0, healthWidth < 0 ? 0 : healthWidth, 35, 450, 610, healthWidth < 0 ? 0 : healthWidth, 35);
    double ammobarWidth = ((double) 380 / 10 * Main.getProtag().getProjCooldownFraction());
    Render.gc.drawImage(InformationBar.getAmmobar(), 0, 0, ammobarWidth, 25, 450, 645, ammobarWidth, 25);
    
    // Loop over and render all abilities.
    int startingX = 240;
    for (BasicAbility ability : Main.getProtag().getAbilities()) {
      
      if (ability.isAllowed()) {
        // If the ability is available for use, draw its icon and keybind to activate it.
        Main.setFont(24);
        Image basicIcon = ability.getIcon();
        Render.gc.drawImage(basicIcon, startingX, 610);
        Render.gc.fillText(Constants.keyCodeMap.get(ability.getActivator()), startingX + 15, 670);
        
      }
      else {
        // If the ability is on cooldown, draw an X over it.
        Main.setFont(68);
        Render.gc.fillText("X", startingX + 2, 655);
        
      }
      // Increment the x position at which the ability appears on the information bar.
      startingX += 70;
      
    }
    
    // Renders information about the character in the bottom left of the screen.
    Main.setFont(28);
    Render.gc.drawImage(InformationBar.getProfile(), 5, 605);
    Render.gc.fillText(Main.getProtag().name, 80, 622);
    Render.gc.drawImage(InformationBar.getCharStats(), 80, 630);
    
    Render.gc.fillText("Time: " + String.valueOf(Main.getCurrentLevel().time), 850, 630);
    Render.gc.fillText("Score: " + String.valueOf(Main.getCurrentLevel().score), 850, 660);
    
  }
  
  public static void renderVictory() {
    
    Render.gc.setFill(Color.BLACK);
    Render.gc.fillRect(0, 0, 1000, 680);
    Render.gc.setFill(Color.WHITE);
    Main.setFont(120);
    Render.gc.fillText("VICTORY!", 314, 180);
    Main.setFont(60);
    String display = "Score: " + String.valueOf(Main.getCurrentLevel().score) + "\n" + "Time: " + String.valueOf(Main.getCurrentLevel().time);
    Text t = new Text(display);
    t.setFont(Main.getFont());
    Render.gc.fillText(display, 500 - (t.getLayoutBounds().getWidth() / 2), 330 - (t.getLayoutBounds().getHeight() / 2));
    Render.gc.drawImage(Main.getProtag().sprite, 500 - (Main.getProtag().sprite.getWidth() / 2), 400 - (Main.getProtag().sprite.getHeight() / 2));
    if (Main.getTick() >= 30) {
      Render.gc.fillText("Press ENTER to return.", 253, 600);
    }
    
  }
  
  public static void renderDefeat() {
    
    Render.gc.setFill(Color.BLACK);
    Render.gc.fillRect(0, 0, 1000, 680);
    Render.gc.setFill(Color.WHITE);
    Main.setFont(120);
    Render.gc.fillText("DEFEAT!", 345, 180);
    Main.setFont(60);
    Render.gc.fillText("Press ENTER to retry.\nPress ESC to return to main menu.", 121, 270);
    Render.gc.drawImage(Render.defeatPineapple, 445, 380);
    
  }

}