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
    private boolean isInteractable;
    private boolean isInteracting;
    private boolean isRemoved;
    private int interactionProgress;
    private BufferedImage sprite;
    private BufferedImage afterSprite; // Sprite to show after task completion
    private Rectangle interactionBounds;
    private boolean isCatNear = false;
    private boolean isTaskCompleted = false; // Track if task is completed
    private static final int CAT_DETECTION_RANGE = 100;
    private long glowTimer = 0; // Add this field for pulsing effect

    public GameObject(int x, int y, int width, int height, String name, boolean isInteractable) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
        this.isInteractable = isInteractable;
        this.isInteracting = false;
        this.isRemoved = false;
        this.isTaskCompleted = false;
        this.interactionProgress = 0;
        this.interactionBounds = new Rectangle(x, y, width, height);
        loadSprite();
    }

    private void loadSprite() {
        try {
            // Try to load sprite from resources
            String objectName = name.toLowerCase().replace(" ", "_");
            String spritePath;
            String afterSpritePath = null;
            
            // Check for bedroom-specific sprites first
            if (name.equalsIgnoreCase("Bed")) {
                spritePath = "res/sprites/room/Bedroom/bed.png";
            } else if (name.equalsIgnoreCase("Clothes")) {
                spritePath = "res/sprites/room/Bedroom/clothes.png";
            } else if (name.equalsIgnoreCase("Trash Can") || name.equalsIgnoreCase("TrashCan")) {
                spritePath = "res/sprites/room/Bedroom/emptytrashcan.png";
                afterSpritePath = "res/sprites/room/Bedroom/fulltrashcan.png"; // After cleaning
            } else if (name.equalsIgnoreCase("Floor Trash") || name.equalsIgnoreCase("Trash")) {
                spritePath = "res/sprites/room/Bedroom/trash.png";
            // Check for bathroom-specific sprites
            } else if (name.equalsIgnoreCase("Bathtub") || name.equalsIgnoreCase("Bath")) {
                spritePath = "res/sprites/room/Bathroom/Bathtub before.png";
                afterSpritePath = "res/sprites/room/Bathroom/Bathtub after.png"; // After cleaning
            // Check for kitchen-specific sprites
            } else if (name.equalsIgnoreCase("Cat food") || name.equalsIgnoreCase("Cat Food")) {
                spritePath = "res/sprites/room/Kitchen/Cat food before.png";
                afterSpritePath = "res/sprites/room/Kitchen/Cat food after.png"; // After filling
            } else if (name.equalsIgnoreCase("Dishes") || name.equalsIgnoreCase("Dirty dishes")) {
                spritePath = "res/sprites/room/Kitchen/Dirty dishes.png";
                afterSpritePath = "res/sprites/room/Kitchen/Clean dishes.png"; // After cleaning
            } else {
                spritePath = "res/sprites/objects/" + objectName + ".png";
            }
            
            sprite = ImageIO.read(new File(spritePath));
            
            // Load after sprite if available
            if (afterSpritePath != null) {
                try {
                    afterSprite = ImageIO.read(new File(afterSpritePath));
                } catch (IOException e) {
                    afterSprite = null;
                }
            }
        } catch (IOException e) {
            // If sprite loading fails, we'll use a colored rectangle as fallback
            sprite = null;
            afterSprite = null;
        }
    }

    public void update() {
        if (isInteracting) {
            interactionProgress += 2;
            if (interactionProgress >= 100) {
                isInteracting = false;
                isTaskCompleted = true;
                interactionProgress = 0;
                
                // Different behavior based on object type
                if (name.equalsIgnoreCase("Floor Trash") || name.equalsIgnoreCase("Trash")) {
                    // Floor trash disappears when cleaned
                    isRemoved = true;
                } else if (name.equalsIgnoreCase("Trash Can") || name.equalsIgnoreCase("TrashCan") ||
                          name.equalsIgnoreCase("Bathtub") || name.equalsIgnoreCase("Bath") ||
                          name.equalsIgnoreCase("Toilet") || 
                          name.equalsIgnoreCase("Bathroom Sink") || name.equalsIgnoreCase("Sink") ||
                          name.equalsIgnoreCase("Cat food") || name.equalsIgnoreCase("Cat Food") ||
                          name.equalsIgnoreCase("Dishes") || name.equalsIgnoreCase("Dirty dishes") ||
                          name.equalsIgnoreCase("magic"))
                         {
                    // These objects change sprite but stay visible
                    isRemoved = false;
                } else {
                    // Default behavior - object disappears
                    isRemoved = true;
                }
            }
        }
        
        // Update glow timer for pulsing effect
        if (isCatNear && isInteractable && !isTaskCompleted) {
            glowTimer += 2; // Increment timer for animation
        }
    }

    public void checkCatProximity(Cat cat) {
        if (cat != null) {
            int distance = Math.abs(cat.getX() - this.x);
            isCatNear = distance <= CAT_DETECTION_RANGE;
        } else {
            isCatNear = false;
        }
    }

    public void draw(Graphics2D g2d, int offsetX, int offsetY) {
        if (isRemoved) return;

        BufferedImage currentSprite = sprite;
        
        // Use after sprite if task is completed and after sprite exists
        if (isTaskCompleted && afterSprite != null) {
            currentSprite = afterSprite;
        }

        if (currentSprite != null) {
            // Draw the sprite
            g2d.drawImage(currentSprite, x + offsetX, y + offsetY, width, height, null);
            
            // Add animated lighting effect when cat is near and object is interactable
            if (isCatNear && isInteractable && !isTaskCompleted) {
                // Create pulsing effect using sine wave
                double pulseIntensity = Math.abs(Math.sin(glowTimer * 0.1)) * 0.5 + 0.3; // Range: 0.3 to 0.8
                int alpha = (int)(pulseIntensity * 120); // Convert to alpha value
                
                // Create a glowing effect with pulsing transparency
                g2d.setColor(new Color(255, 255, 100, alpha)); // Yellow with pulsing transparency
                g2d.fillRect(x + offsetX, y + offsetY, width, height);
                
                // Add a bright border that also pulses
                int borderAlpha = (int)(pulseIntensity * 200);
                g2d.setColor(new Color(255, 255, 0, borderAlpha)); // Brighter yellow border
                g2d.drawRect(x + offsetX - 2, y + offsetY - 2, width + 4, height + 4);
                g2d.drawRect(x + offsetX - 1, y + offsetY - 1, width + 2, height + 2);
            }
        } else {
            // Draw fallback rectangle with color based on cat proximity and interactability
            if (isCatNear && isInteractable && !isTaskCompleted) {
                // Pulsing effect for fallback rectangles too
                double pulseIntensity = Math.abs(Math.sin(glowTimer * 0.1)) * 0.5 + 0.5;
                int colorValue = (int)(255 * pulseIntensity);
                g2d.setColor(new Color(255, colorValue, 0)); // Pulsing yellow
            } else if (isCatNear) {
                g2d.setColor(new Color(255, 200, 0)); // Orange-yellow when cat is near but not interactable
            } else {
                g2d.setColor(Color.GRAY);
            }
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isTaskCompleted() {
        return isTaskCompleted;
    }

    public void setTaskCompleted(boolean completed) {
        this.isTaskCompleted = completed;
    }
} 