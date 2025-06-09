package entities;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Room {
    private int id;
    private String name;
    private int x;
    private int width;
    private List<GameObject> objects;
    private boolean isCompleted;
    private Rectangle leftWall;
    private Rectangle rightWall;
    private BufferedImage backgroundImage;
    
    public Room(int id, String name, int x, int width, Color backgroundColor, String backgroundImagePath) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.width = width;
        this.objects = new ArrayList<>();
        this.isCompleted = false;
        this.leftWall = new Rectangle(x, 0, 20, 720);
        this.rightWall = new Rectangle(x + width - 20, 0, 20, 720);
        
        // Load background image if path is provided
        if (backgroundImagePath != null && !backgroundImagePath.isEmpty()) {
            try {
                backgroundImage = ImageIO.read(new File(backgroundImagePath));
            } catch (IOException e) {
                backgroundImage = null;
            }
        }
    }

    // Overload for old usage (no image)
    public Room(int id, String name, int x, int width, Color backgroundColor) {
        this(id, name, x, width, backgroundColor, null);
    }

    public void addObject(GameObject object) {
        objects.add(object);
    }

    public void update() {
        for (GameObject object : objects) {
            object.update();
        }
        checkRoomCompletion();
    }

    private void checkRoomCompletion() {
        boolean allTasksCompleted = true;
        for (GameObject object : objects) {
            // Check if object is either removed OR task is completed
            if (!object.isRemoved() && !object.isTaskCompleted()) {
                allTasksCompleted = false;
                break;
            }
        }
        // Allow empty rooms to be completed automatically
        if (allTasksCompleted) {
            isCompleted = true;
            // Remove walls when room is completed
            leftWall = null;
            rightWall = null;
        }
    }

    public void drawBackground(Graphics2D g2d, int cameraX) {
        // Draw room background image if available, else color
        if (backgroundImage != null) {
            // Calculate scaling to maintain aspect ratio while filling the room
            double scaleX = (double) width / backgroundImage.getWidth();
            double scaleY = (double) 720 / backgroundImage.getHeight();
            double scale = Math.max(scaleX, scaleY); // Use the larger scale to ensure full coverage
            
            int scaledWidth = (int) (backgroundImage.getWidth() * scale);
            int scaledHeight = (int) (backgroundImage.getHeight() * scale);
            
            // Center the image in the room
            int xOffset = (width - scaledWidth) / 2;
            int yOffset = (720 - scaledHeight) / 2;
            
            g2d.drawImage(backgroundImage, 
                x - cameraX + xOffset, yOffset, 
                scaledWidth, scaledHeight, null);
        } 
    }

    public void drawDividingWalls(Graphics2D g2d, int cameraX, boolean drawLeftWall, boolean drawRightWall) {
        g2d.setColor(Color.BLACK);
        
        // Draw left wall if needed
        if (drawLeftWall && leftWall != null) {
            g2d.fillRect(leftWall.x - cameraX, leftWall.y, leftWall.width, leftWall.height);
        }
        
        // Draw right wall if needed  
        if (drawRightWall && rightWall != null) {
            g2d.fillRect(rightWall.x - cameraX, rightWall.y, rightWall.width, rightWall.height);
        }
    }

    public void drawObjects(Graphics2D g2d, int cameraX) {
        // Draw room objects
        for (GameObject object : objects) {
            object.draw(g2d, -cameraX, 0);
        }
    }

    public boolean isPointInRoom(int x) {
        return x >= this.x && x < this.x + width;
    }

    public boolean checkCollision(Rectangle entityBounds) {
        // Only check for collisions if room is not completed
        if (!isCompleted && leftWall != null && rightWall != null) {
            return leftWall.intersects(entityBounds) || rightWall.intersects(entityBounds);
        }
        return false;
    }

    public List<GameObject> getObjects() {
        return objects;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return width;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
        if (completed) {
            leftWall = null;
            rightWall = null;
        }
    }

    public int getCenterX() {
        return x + width / 2;
    }
} 