package states;

import java.awt.Graphics2D;
import main.GamePanel;

public class GameStateManager {
    private GameState currentState;
    private MenuState menuState;
    private PlayState playState;
    private PauseState pauseState;
<<<<<<< HEAD
    private EndingState endingState;
=======
>>>>>>> 11cb779a08db09d579c06ac82b879a228bbfae49
    private GamePanel panel;

    public GameStateManager(GamePanel panel) {
        this.panel = panel;
        currentState = GameState.MENU;
        menuState = new MenuState(this);
        playState = new PlayState(this, panel.getKeyboardInput());
        pauseState = new PauseState(this);
<<<<<<< HEAD
        endingState = new EndingState(this);
    }

    public void setState(GameState state) {
        // Capture previous state
        GameState prevState = currentState;
        if (prevState == GameState.MENU) {
            menuState.stopMusic();
        } else if (prevState == GameState.PLAYING && state != GameState.ENDING) {
            playState.pauseMusic();
        }
        
        currentState = state;
        
=======
    }

    public void setState(GameState state) {
        if (currentState == GameState.MENU) {
            menuState.stopMusic();
        } else if (currentState == GameState.PLAYING) {
            playState.pauseMusic();
        }
        currentState = state;
>>>>>>> 11cb779a08db09d579c06ac82b879a228bbfae49
        if (state == GameState.MENU) {
            menuState.reset();
            menuState.playMusic();
        } else if (state == GameState.PLAYING) {
<<<<<<< HEAD
            // If coming from menu, start a fresh game
            if (prevState == GameState.MENU) {
                playState = new PlayState(this, panel.getKeyboardInput());
            }
            playState.resetMusic();
        } else if (state == GameState.ENDING) {
            endingState = new EndingState(this);
=======
            playState.resetMusic();
>>>>>>> 11cb779a08db09d579c06ac82b879a228bbfae49
        }
    }

    public void update() {
        switch (currentState) {
            case MENU:
                menuState.update();
                break;
            case PLAYING:
                playState.update();
                break;
<<<<<<< HEAD
            case ENDING:
                endingState.update();
                break;
=======
>>>>>>> 11cb779a08db09d579c06ac82b879a228bbfae49
            case PAUSED:
                pauseState.update();
                break;
        }
    }

    public void draw(Graphics2D g2d) {
        switch (currentState) {
            case MENU:
                menuState.draw(g2d);
                break;
            case PLAYING:
                playState.draw(g2d);
                break;
            case PAUSED:
                playState.draw(g2d); // Draw game in background
                pauseState.draw(g2d);
                break;
<<<<<<< HEAD
            case ENDING:
                endingState.draw(g2d);
                break;
=======
>>>>>>> 11cb779a08db09d579c06ac82b879a228bbfae49
        }
    }

    public GamePanel getPanel() {
        return panel;
    }
} 