package main;

import java.io.File;
import java.io.PrintWriter;
import java.util.Timer;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import types.BasicAbility;
import types.InformationBar;
import types.LevelParser;
import types.ProfileLoader;
import types.ResetBasicActive;
import types.ResetBasicCooldown;
import types.Vegetable;

public class KeyboardPressedHandler implements EventHandler<KeyEvent> {

  
  public void handle(KeyEvent key) {
	  
    //If the player has clicked the enter button the state will change by one to skip over the intro or select a profile/level/character
    if (Main.getState() == 0 && key.getCode() == KeyCode.ENTER) {
        Main.getIntroPlayer().dispose();
        Main.setState((byte) (Main.getState() + 1));
        return;
    }
    //This will check if any of the arrow keys are clicked during what state to change the selected profile/level/character/exit
    else if (Main.getState() == 1) {
      if (key.getCode() == KeyCode.UP) {
        Main.setSelection(Main.getSelection() == 1 ? (byte) 3 : (byte) (Main.getSelection() - 1));
      }
      else if (key.getCode() == KeyCode.DOWN) {
        Main.setSelection(Main.getSelection() == 3 ? (byte) 1 : (byte) (Main.getSelection() + 1));
      }
      else if (key.getCode() == KeyCode.ENTER) {
        Main.setState((byte) 2);
        ProfileLoader.loadProfile(Main.getSelection());
        Main.setSelection((byte) 1);
        Main.getGc().setFill(Color.WHITE);
      }
    }
    else if (Main.getState() == 2) {
      if (key.getCode() == KeyCode.UP) {
        Main.setSelection(Main.getSelection() == 1 ? (byte) 4 : (byte) (Main.getSelection() - 1));
      }
      else if (key.getCode() == KeyCode.DOWN) {
        Main.setSelection(Main.getSelection() == 4 ? (byte) 1 : (byte) (Main.getSelection() + 1));
      }
      else if (key.getCode() == KeyCode.ENTER) {
        if (Main.getSelection() == 1) {
          Main.setState((byte) 3);
        }
        else if (Main.getSelection() == 2) {
          Main.setState((byte) 4);
        }
        else if (Main.getSelection() == 3) {
          Main.setState((byte) -1);
        }
        else {
          System.exit(0);
        }
        Main.setSelection((byte) 1);
      }
    }
    else if (Main.getState() == 3) {
      
      if (key.getCode() == KeyCode.RIGHT) {
        byte selection = (byte) (Main.getSelection() + 1);
        if (selection == 0) {
          selection = (byte) (Constants.CHARACTERS.length);
        }
        else if (selection == Constants.CHARACTERS.length + 1) {
          selection = 1;
        }
        Main.setSelection(selection);
      }
      else if (key.getCode() == KeyCode.LEFT) {
        byte selection = (byte) (Main.getSelection() - 1);
        if (selection == 0) {
          selection = (byte) (Constants.CHARACTERS.length);
        }
        else if (selection == Constants.CHARACTERS.length + 1) {
          selection = 1;
        }
        Main.setSelection(selection);
      }
      else if (key.getCode() == KeyCode.ENTER) {
        Main.setProtag(Constants.CHARACTERS[Main.getSelection() - 1]);
        InformationBar.setCharStats(Constants.CHARACTERS[Main.getSelection() - 1].getStats());
        InformationBar.setProfile(Constants.CHARACTERS[Main.getSelection() - 1].getProfile());
        Main.setSelection((byte) 1);
        Main.setState((byte) 2);
      }
      
    }
    //This is used to choose and set the level
    else if (Main.getState() == 4) {
      
      if (key.getCode() == KeyCode.RIGHT) {
        if (Main.getLevelsUnlocked() < Main.getSelection() + 1) {
          return;
        }
        Main.setSelection((byte) (Main.getSelection() + 1));
      }
      else if (key.getCode() == KeyCode.LEFT) {
        if (Main.getSelection() == 1) {
          return;
        }
        Main.setSelection((byte) (Main.getSelection() - 1));
      }
      else if (key.getCode() == KeyCode.ENTER) {
        Main.setCurrentLevel(LevelParser.parseLevel(Main.getSelection()));
        Main.setState((byte) 5);
      }
      
    }
    //This is used to check for movement, shooting, and ability usage
    else if (Main.getState() == 5) {
      Vegetable protag = Main.getProtag();
      
      //If the W button is clicked on the keyboard and the character is not in ability mode, the jump method will be executed 
      if (key.getCode() == KeyCode.W) {
        boolean physicsOff = false;
        for (BasicAbility ability : protag.getAbilities()) {
          if (ability.isActive() && ability.isPhysics()) {
            physicsOff = true;
            break;
          }
        }
        if (physicsOff) {
          return;
        }
        protag.jump();
        protag.jumpReleased = false;
      }
      //If the D button is clicked on the keyboard, you are moving to the right and set the last direction to be 1 (Right).
      else if (key.getCode() == KeyCode.D) {
        protag.right = true;
        protag.xVel = protag.getSpeed();
        protag.lastDirection = 1;
      }
      //If the A button is clicked on the keyboard, you are moving to the Left and set the last direction to be 2 (Left).
      else if (key.getCode() == KeyCode.A) {
        protag.left = true;
        protag.xVel = protag.getSpeed();
        protag.lastDirection = 2;
      }
      //If the M button is pressed, the shootProjectile method will run
      else if (key.getCode() == KeyCode.M) {
        protag.shootProjectile();
      }
      else {
        for (int i = 0; i < protag.getAbilities().length; i++) {
          BasicAbility ability = protag.getAbilities()[i];
          if (key.getCode() != ability.getActivator()) {
            continue;
          }
          if (ability.isActive() || !ability.isAllowed()) {
            SoundManager.playPlayer(Sounds.ABILITYUNAVAILABLE);
            continue;
          }
          
          ability.setActive(true);
          ability.basic();
          Timer timer = new Timer();
          timer.schedule(new ResetBasicActive(protag, i), ability.getLength());
          Timer cooldownResetter = new Timer();
          cooldownResetter.schedule(new ResetBasicCooldown(ability.getUser(), ability.getIndex()), ability.getLength() + ability.getCooldown());
          ability.setAllowed(false);
          
        }
      }
    }
    // Listener to handle the victory screen.
    else if (Main.getState() == 6) {
      if (key.getCode() != KeyCode.ENTER) {
        return;
      }
      try {
        Main.setState((byte) 2);
        if (Main.getLevelsUnlocked() == Main.getCurrentLevel().getLevelNumber()) {
          PrintWriter profileWriter;
          profileWriter = new PrintWriter(new File("").getAbsolutePath() + "/resources/save/profile" + String.valueOf(Main.getCurrentProfile()) + ".veggiedata", "UTF-8");
          profileWriter.println(String.valueOf(Main.getCurrentLevel().getLevelNumber() + 1));
          profileWriter.close();
          Main.setLevelsUnlocked((byte) (Main.getCurrentLevel().getLevelNumber() + 1));
        }
        Main.setCurrentLevel(null);
      }
      catch(Exception e) {
        e.printStackTrace();
      }
    }
    // Listener to handle the defeat screen.
    else if (Main.getState() == 7) {
      if (key.getCode() != KeyCode.ENTER && key.getCode() != KeyCode.ESCAPE) {
        return;
      }
      if (key.getCode() == KeyCode.ENTER) {
        Main.getProtag().refresh();
        LevelParser.parseLevel(Main.getCurrentLevel().getLevelNumber());
        Main.setState((byte) 5);
      }
      else {
        Main.setCurrentLevel(null);
        Main.setState((byte) 2);
      }
    }
    
  }

}
