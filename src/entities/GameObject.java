package entities;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class GameObject {
    private int x, y;
    private int width, height;
    private String name;
    private String description;
    private boolean isInteractable;
    private boolean isInteracting;
    private boolean isRemoved;
    private int interactionProgress;
    private BufferedImage sprite;
    private Rectangle interactionBounds;

    public GameObject(int x, int y, int width, int height, String name, String description, boolean isInteractable) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
        this.description = description;
        this.isInteractable = isInteractable;
        this.isInteracting = false;
        this.isRemoved = false;
        this.interactionProgress = 0;
        this.interactionBounds = new Rectangle(x, y, width, height);
        loadSprite();
    }

    private void loadSprite() {
        try {
            // Try to load sprite from resources
            String spritePath = "res/sprites/objects/" + name.toLowerCase().replace(" ", "_") + ".png";
            sprite = ImageIO.read(new File(spritePath));
        } catch (IOException e) {
            // If sprite loading fails, we'll use a colored rectangle as fallback
            sprite = null;
        }
    }

    public void update() {
        if (isInteracting) {
            interactionProgress += 2;
            if (interactionProgress >= 100) {
                isInteracting = false;
                isRemoved = true;
                interactionProgress = 0;
            }
        }
    }

    public void draw(Graphics2D g2d, int offsetX, int offsetY) {
        if (isRemoved) return;

        if (sprite != null) {
            g2d.drawImage(sprite, x + offsetX, y + offsetY, width, height, null);
        } else {
            // Draw fallback rectangle
            g2d.setColor(Color.GRAY);
            g2d.fillRect(x + offsetX, y + offsetY, width, height);
        }

        // Draw interaction progress if interacting
        if (isInteracting) {
            g2d.setColor(Color.WHITE);
            g2d.drawRect(x + offsetX, y + offsetY - 20, width, 10);
            g2d.setColor(Color.GREEN);
            g2d.fillRect(x + offsetX, y + offsetY - 20, (width * interactionProgress) / 100, 10);
        }
    }

    public void startInteraction() {
        if (isInteractable && !isInteracting && !isRemoved) {
            isInteracting = true;
            interactionProgress = 0;
        }
    }

    public boolean isInteracting() {
        return isInteracting;
    }

    public boolean isInteractable() {
        return isInteractable && !isRemoved;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public Rectangle getInteractionBounds() {
        return interactionBounds;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
} 