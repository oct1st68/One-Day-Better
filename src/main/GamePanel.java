package main;

import input.KeyboardInput;
import input.MouseInput;
import states.GameStateManager;

import javax.swing.JPanel;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {
    // Screen settings
    final int originalTitleSize = 16;
    final int scale = 3;
    public final int TitleSize = originalTitleSize * scale;
    public final int maxScreenCol = 20;
    public final int maxScreenRow = 20;
    final int ScreenWidth = 1280;
    final int ScreenHeight = 720;

    // FPS
    int FPS = 60;

    // Game state manager
    private GameStateManager gsm;
    private KeyboardInput keyboardInput;
    private MouseInput mouseInput;

    public GamePanel(int width, int height) {
        this.setPreferredSize(new Dimension(width, height));
        this.setMinimumSize(new Dimension(800, 600)); // Set minimum size
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        
        // Initialize input
        keyboardInput = new KeyboardInput();
        mouseInput = new MouseInput();
        
        // Initialize game state manager with this panel
        gsm = new GameStateManager(this);
        
        // Setup input handling
        this.setFocusable(true);
        this.addKeyListener(keyboardInput);
        this.addMouseListener(mouseInput);
        this.addMouseMotionListener(mouseInput);
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
        gsm.update();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        gsm.draw(g2);
        
        g2.dispose();
    }

    public MouseInput getMouseInput() {
        return mouseInput;
    }

    public KeyboardInput getKeyboardInput() {
        return keyboardInput;
    }
}
