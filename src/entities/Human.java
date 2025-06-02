package entities;
import java.awt.Graphics2D;
import java.awt.Color;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
<<<<<<< HEAD

public class Human {
    private int x, y;
    private int speed = 3;
=======
import states.PlayState;

public class Human {
    private int x, y;
    private int speed = 1;
>>>>>>> 8cfcec4 (2.6)
    private boolean isMoving = false;
    private boolean isFollowing = false;
    private String currentTask = "idle";
    private int taskProgress = 0;
    private int currentRoom = 0;
    private BufferedImage sprite;
    private Cat targetCat;
    private boolean isFacingRight = true;
    private int startX;  // Store starting X position
    private int startY;  // Store starting Y position
    private boolean shouldReturnToStart = false;
    private long followEndTime = 0;
<<<<<<< HEAD
    private static final long RETURN_DELAY = 4000; // 4 seconds delay
    private boolean[] roomTasksCompleted = new boolean[3]; // Track completed tasks for each room
    private boolean isInteractingWithObject = false;  // New field to track if human is currently interacting
    private GameObject currentObject = null;
    private boolean isTransitioning = false;
    private int targetX = 0;
    private int completedTasksInCurrentRoom = 0;

    public Human(int x, int y) {
        this.x = x;
        this.y = y;
        this.startX = x;  // Initialize starting position
        this.startY = y;
        loadSprite();
    }

=======
    private static final long RETURN_DELAY = 10000; // 10 seconds delay
    private boolean[] roomTasksCompleted = new boolean[3]; // Track completed tasks for each room
    private boolean isInteractingWithObject = false;  // New field to track if human is currently interacting
    private GameObject currentObject = null;
    private GameObject targetObject = null;
    private boolean isTransitioning = false;
    private int targetX = 0;
    private int completedTasksInCurrentRoom = 0;
    private PlayState playState;
    private boolean isWaiting = false;
    private long waitStartTime = 0;
    private static final long WAIT_DURATION = 10000; // 10 seconds in milliseconds
    private int velocityY = 0;
    private int gravity = 1;
    private boolean isJumping = false;
    private static final int GROUND_Y = 400;  // Same ground level as cat
    private boolean isHeadbutting = false;
    private boolean isPawing = false;
    private boolean isMeowing = false;
    private String currentAnimation = "idle";
    private int headbuttTimer = 0;
    private boolean isEncouraged = false;
    private long encouragedTime = 0;

    public Human(int x, int y) {
        this.x = x;
        this.y = y;  
        this.startX = x;
        this.startY = y; 
        loadSprite();
    }

    public void setPlayState(PlayState playState) {
        this.playState = playState;
    }

>>>>>>> 8cfcec4 (2.6)
    private void loadSprite() {
        try {
            sprite = ImageIO.read(new File("res/sprites/human/human.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
<<<<<<< HEAD
        if (isFollowing && targetCat != null) {
            // If cat leaves the room, stop following
            int catRoom = (targetCat.getX()) / 1200;
            if (catRoom != currentRoom) {
                stopFollowing();
            }
        }
        if (isTransitioning) {
            // Move towards target position
            int diff = targetX - x;
            if (Math.abs(diff) < speed) {
                x = targetX;
                isTransitioning = false;
                currentRoom = currentRoom + 1;
                currentTask = "idle";
                isInteractingWithObject = false;
                currentObject = null;
                completedTasksInCurrentRoom = 0;
            } else {
                x += diff > 0 ? speed : -speed;
                isFacingRight = diff > 0;
            }
        } else if (isFollowing && targetCat != null) {
            // Move towards the cat
            int targetX = targetCat.getX();
            int distanceX = Math.abs(x - targetX);
            if (distanceX > 10) {
                if (x < targetX) {
                    x += speed;
                    isFacingRight = true;
                } else {
                    x -= speed;
                    isFacingRight = false;
                }
            } else {
                // Stop when close enough
                stopFollowing();
                followEndTime = System.currentTimeMillis();
                shouldReturnToStart = true;
            }
        } else if (shouldReturnToStart && currentTask.equals("idle") && !isInteractingWithObject) {
            if (System.currentTimeMillis() - followEndTime >= RETURN_DELAY) {
                returnToStart();
=======
        // Add gravity and ground collision at the start of update
        velocityY += gravity;
        y += velocityY;
        
        // Ground collision
        if (y > GROUND_Y) {
            y = GROUND_Y;
            velocityY = 0;
            isJumping = false;
        }

        if (shouldReturnToStart) {
            if (x < startX) {
                x += speed;
                isFacingRight = true;
            } else if (x > startX) {
                x -= speed;
                isFacingRight = false;
            } else {
                shouldReturnToStart = false;
            }
            return;
        }

        // Handle room transition movement
        if (isTransitioning) {
            if (x < targetX) {
                x += speed;
                isFacingRight = true;
            } else if (x > targetX) {
                x -= speed;
                isFacingRight = false;
            } else {
                isTransitioning = false;
                isMoving = false;  // Reset isMoving when transition is complete
                currentTask = "idle";
            }
            return;
        }

        // 1. If currently moving toward a target object, handle movement first
        if (targetObject != null && !isInteractingWithObject) {
            // Face the object while moving
            isFacingRight = targetObject.getX() > x;

            int distanceX = Math.abs(x - targetObject.getX());
            if (distanceX > 10) {
                // Move one step toward the object
                if (x < targetObject.getX()) {
                    x += speed;
                } else {
                    x -= speed;
                }
            } else {
                // Reached the object – begin interaction
                isInteractingWithObject = true;
                currentObject = targetObject;
                currentTask = "Working on task...";
                targetObject.startInteraction();
                targetObject = null; // Clear target
            }
        } else if (isFollowing && targetCat != null) {
            // Update facing direction based on cat's position
            isFacingRight = targetCat.getX() > x;

            // Move towards the cat
            int targetXPos = targetCat.getX();
            int distanceX = Math.abs(x - targetXPos);
            if (distanceX > 10) {
                if (x < targetXPos) {
                    x += speed;
                } else {
                    x -= speed;
                }
            } else {
                // Reached the cat, start waiting
                stopFollowing();
                isWaiting = true;
                waitStartTime = System.currentTimeMillis();
                currentTask = "Waiting...";
            }
        }

        // Check if waiting period is over
        if (isWaiting) {
            if (System.currentTimeMillis() - waitStartTime >= WAIT_DURATION) {
                isWaiting = false;
                shouldReturnToStart = true;
                currentTask = "Returning to start...";
>>>>>>> 8cfcec4 (2.6)
            }
        }

        // Update interaction with current object
        if (isInteractingWithObject && currentObject != null) {
<<<<<<< HEAD
=======
            // Face the object while interacting
            isFacingRight = currentObject.getX() > x;
>>>>>>> 8cfcec4 (2.6)
            currentObject.update();
            if (!currentObject.isInteracting()) {
                isInteractingWithObject = false;
                currentObject = null;
                completedTasksInCurrentRoom++;
                // Only mark room as completed when all 3 tasks are done
                if (completedTasksInCurrentRoom >= 3) {
                    roomTasksCompleted[currentRoom] = true;
                }
                // Reset task state to allow for next interaction
                currentTask = "idle";
            }
        }
<<<<<<< HEAD
    }

    private void returnToStart() {
        if (x < startX) {
            x += speed;
            isFacingRight = true;
        } else if (x > startX) {
            x -= speed;
            isFacingRight = false;
        } else {
            shouldReturnToStart = false;
            currentTask = "idle";
        }
=======

        if (isEncouraged && System.currentTimeMillis() - encouragedTime > 3000) {
            isEncouraged = false;
        }
    }

    private GameObject findNearestObject() {
        if (playState == null) return null;
        
        GameObject nearestObject = null;
        double minDistance = Double.MAX_VALUE;
        
        for (GameObject object : playState.currentRoom.getObjects()) {
            if (object.isInteractable() && !object.isRemoved()) {
                double distance = Math.abs(object.getX() - this.x);
                if (distance < minDistance && distance <= 150) { // Increased interaction range
                    minDistance = distance;
                    nearestObject = object;
                }
            }
        }
        return nearestObject;
>>>>>>> 8cfcec4 (2.6)
    }

    public void draw(Graphics2D g2d) {
        g2d.drawImage(sprite, x, y, null);
    }

    public void draw(Graphics2D g2d, int offsetX, int offsetY) {
        if (sprite != null) {
            if (!isFacingRight) {
                g2d.drawImage(sprite, x + offsetX + sprite.getWidth(), y + offsetY, -sprite.getWidth(), sprite.getHeight(), null);
            } else {
                g2d.drawImage(sprite, x + offsetX, y + offsetY, null);
            }
        }
    }

    public void moveToNextRoom() {
<<<<<<< HEAD
        if (!isMoving && !isFollowing && !isTransitioning) {
            isTransitioning = true;
            targetX = (currentRoom + 1) * 1200 + 100; // Move to next room's starting position (room width is 1200)
=======
        if (!isMoving && !isTransitioning) {
            isTransitioning = true;
            isMoving = true;  // Set isMoving to true
            currentRoom++; // Increment current room
            targetX = currentRoom * 1200 + 100; // Move to next room's starting position (room width is 1200)
>>>>>>> 8cfcec4 (2.6)
            currentTask = "Moving to next room...";
            isInteractingWithObject = false;
            currentObject = null;
            completedTasksInCurrentRoom = 0;
        }
    }

    public void interactWithObject(GameObject object) {
<<<<<<< HEAD
        if (!isInteractingWithObject && currentTask.equals("idle") && object != null) {
            isInteractingWithObject = true;
            currentObject = object;
            currentTask = "Working on task...";
            object.startInteraction();
        }
=======
        if (object == null || object.isRemoved()) return;

        // Stop following the cat (if we were)
        stopFollowing();

        this.targetObject = object;
        this.currentTask = "Working on task...";
>>>>>>> 8cfcec4 (2.6)
    }

    public boolean isCurrentRoomTasksCompleted() {
        return roomTasksCompleted[currentRoom];
    }

    public void setPosition(int x, int y) {
        this.x = x;
<<<<<<< HEAD
        this.y = y;
=======
        this.y = y; 
>>>>>>> 8cfcec4 (2.6)
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getCurrentRoom() {
        return currentRoom;
    }

    public void startFollowing(Cat cat) {
        this.targetCat = cat;
        this.isFollowing = true;
        this.currentTask = "Following cat...";
<<<<<<< HEAD
=======
        // Face the cat when starting to follow
        this.isFacingRight = cat.getX() > this.x;
>>>>>>> 8cfcec4 (2.6)
    }

    public void stopFollowing() {
        this.isFollowing = false;
        this.targetCat = null;
<<<<<<< HEAD
        this.currentTask = "idle";
=======
        // Don't reset currentTask here as we want to show "Waiting..."
    }

    public void setShouldReturnToStart(boolean shouldReturn) {
        this.shouldReturnToStart = shouldReturn;
    }

    public void setFacingDirection(boolean faceRight) {
        this.isFacingRight = faceRight;
>>>>>>> 8cfcec4 (2.6)
    }
}