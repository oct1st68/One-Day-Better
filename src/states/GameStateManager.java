package states;

import java.awt.Graphics2D;
import main.GamePanel;

public class GameStateManager {
    private GameState currentState;
    private MenuState menuState;
    private PlayState playState;
    private PauseState pauseState;
    private GamePanel panel;

    public GameStateManager(GamePanel panel) {
        this.panel = panel;
        currentState = GameState.MENU;
        menuState = new MenuState(this);
        playState = new PlayState(this, panel.getKeyboardInput());
        pauseState = new PauseState(this);
    }

    public void setState(GameState state) {
        if (currentState == GameState.MENU) {
            menuState.stopMusic();
        } else if (currentState == GameState.PLAYING) {
            playState.pauseMusic();
        }
        currentState = state;
        if (state == GameState.MENU) {
            menuState.reset();
            menuState.playMusic();
        } else if (state == GameState.PLAYING) {
            playState.resetMusic();
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
        }
    }

    public GamePanel getPanel() {
        return panel;
    }
} 