package states;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import input.MouseInput;

public class PauseState extends State {
    private String[] options = {"Resume"};
    private int currentOption = 0;
    private Font titleFont;
    private Font optionFont;
    private Rectangle[] optionBounds;
    private MouseInput mouseInput;

    public PauseState(GameStateManager gsm) {
        super(gsm);
        titleFont = new Font("Montserrat", Font.BOLD, 48);
        optionFont = new Font("Montserrat", Font.PLAIN, 32);
        optionBounds = new Rectangle[options.length];
        mouseInput = gsm.getPanel().getMouseInput();
    }

    @Override
    public void update() {
        // Update option bounds
        for (int i = 0; i < options.length; i++) {
            int y = 400 + i * 50;
            optionBounds[i] = new Rectangle(0, y - 30, 1280, 40);
        }

        // Check mouse hover and click
        int mouseX = mouseInput.getMouseX();
        int mouseY = mouseInput.getMouseY();

        // Update current option based on mouse position
        for (int i = 0; i < optionBounds.length; i++) {
            if (optionBounds[i].contains(mouseX, mouseY)) {
                currentOption = i;
                if (mouseInput.isMousePressed()) {
                    select();
                }
                break;
            }
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        // Draw semi-transparent overlay
        g2d.setColor(new Color(0, 0, 0, 128));
        g2d.fillRect(0, 0, 1280, 720);

        // Draw title
        g2d.setColor(Color.WHITE);
        g2d.setFont(titleFont);
        String title = "PAUSED";
        int titleWidth = g2d.getFontMetrics().stringWidth(title);
        g2d.drawString(title, (1280 - titleWidth) / 2, 200);

        // Draw menu options
        g2d.setFont(optionFont);
        for (int i = 0; i < options.length; i++) {
            if (i == currentOption) {
                g2d.setColor(Color.RED);
            } else {
                g2d.setColor(Color.WHITE);
            }
            int optionWidth = g2d.getFontMetrics().stringWidth(options[i]);
            g2d.drawString(options[i], (1280 - optionWidth) / 2, 400 + i * 50);
        }
    }

    public void moveUp() {
        currentOption = 0;
    }

    public void moveDown() {
        currentOption = 0;
    }

    public void select() {
        gsm.setState(GameState.PLAYING);
    }
} 