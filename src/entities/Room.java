package entities;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
<<<<<<< HEAD
=======
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
>>>>>>> 8cfcec4 (2.6)

public class Room {
    private int id;
    private String name;
    private int x;
    private int width;
    private List<GameObject> objects;
    private Color backgroundColor;
    private boolean isCompleted;
    private Rectangle leftWall;
    private Rectangle rightWall;
<<<<<<< HEAD

    public Room(int id, String name, int x, int width, Color backgroundColor) {
=======
    private BufferedImage backgroundImage;
    
    public Room(int id, String name, int x, int width, Color backgroundColor, String backgroundImagePath) {
>>>>>>> 8cfcec4 (2.6)
        this.id = id;
        this.name = name;
        this.x = x;
        this.width = width;
        this.backgroundColor = backgroundColor;
        this.objects = new ArrayList<>();
        this.isCompleted = false;
        this.leftWall = new Rectangle(x, 0, 20, 720);
        this.rightWall = new Rectangle(x + width - 20, 0, 20, 720);
<<<<<<< HEAD
=======
        
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
>>>>>>> 8cfcec4 (2.6)
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
        boolean allObjectsRemoved = true;
        for (GameObject object : objects) {
            if (!object.isRemoved()) {
                allObjectsRemoved = false;
                break;
            }
        }
        if (allObjectsRemoved && !objects.isEmpty()) {
            isCompleted = true;
            // Remove walls when room is completed
            leftWall = null;
            rightWall = null;
        }
    }

    public void drawBackground(Graphics2D g2d, int cameraX) {
<<<<<<< HEAD
        // Draw room background
        g2d.setColor(backgroundColor);
        g2d.fillRect(x - cameraX, 0, width, 720);

        // Only draw walls if room is not completed
        if (!isCompleted && leftWall != null && rightWall != null) {
            g2d.setColor(new Color(100, 100, 100));
            g2d.fillRect(leftWall.x - cameraX, leftWall.y, leftWall.width, leftWall.height);
            g2d.fillRect(rightWall.x - cameraX, rightWall.y, rightWall.width, rightWall.height);
=======
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
        } else {
            g2d.setColor(backgroundColor);
            g2d.fillRect(x - cameraX, 0, width, 720);
        }
        
        if(leftWall != null && rightWall != null) {
            g2d.setColor(Color.BLACK);
            g2d.fill(leftWall);
            g2d.fill(rightWall);
>>>>>>> 8cfcec4 (2.6)
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