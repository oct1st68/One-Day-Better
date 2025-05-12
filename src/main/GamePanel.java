package main;

import input.KeyboardInput;
import entities.Human;
import entities.Cat;

import javax.swing.JPanel;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {
    // Screen settings
    final int originalTitleSize = 16;
    final int scale = 3;
    public final int TitleSize = originalTitleSize * scale;
    public final int maxScreenCol = 20;
    public final int maxScreenRow = 20;
    final int ScreenWidth = maxScreenCol * TitleSize;
    final int ScreenHeight = maxScreenRow * TitleSize;

    // FPS
    int FPS = 60;

    // Game entities
    private Human human;
    private Cat cat;
    private KeyboardInput keyboardInput;

    public GamePanel(int width, int height) {
        this.setPreferredSize(new Dimension(ScreenWidth, ScreenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        
        // Initialize game entities
        human = new Human(100, 300);
        cat = new Cat(50, 300);
        keyboardInput = new KeyboardInput();
        
        // Setup input handling
        this.setFocusable(true);
        this.addKeyListener(keyboardInput);
        this.requestFocus();
    }

    Thread GameThread;
    public void StartGameThread() {
        GameThread = new Thread(this);
        GameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        long lastTime = System.nanoTime();
        long timer = 0;
        int frameCount = 0;

        while (GameThread != null) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastTime) / drawInterval;

            timer += currentTime - lastTime;
            lastTime = currentTime;

            update();

            repaint();

            long sleepTime = (long) ((drawInterval - (System.nanoTime() - lastTime)) / 1_000_000);

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            frameCount++;
            if (timer >= 1_000_000_000) {
                System.out.println("FPS: " + frameCount);
                frameCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        // Update human
        human.update();
        
        // Update cat based on keyboard input
        if (keyboardInput.leftPressed) {
            cat.moveLeft();
        }
        if (keyboardInput.rightPressed) {
            cat.moveRight();
        }
        if (keyboardInput.upPressed) {
            cat.jump();
        }
        if (keyboardInput.interactPressed) {
            cat.paw(human);
        }
        if (keyboardInput.meowPressed) {
            cat.meow();
        }
        cat.update();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        
        // Draw entities
        human.draw(g2);
        cat.draw(g2);
        
        g2.dispose();
    }
}
