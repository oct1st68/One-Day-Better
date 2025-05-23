package states;

import java.awt.Graphics2D;

public abstract class State {
    protected GameStateManager gsm;

    public State(GameStateManager gsm) {
        this.gsm = gsm;
    }

    public abstract void update();
    public abstract void draw(Graphics2D g2d);
} 