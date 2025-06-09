package entities;
import java.awt.Graphics2D;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import states.PlayState;

public class Human {
    private int x, y;
    private int speed = 1;
    private boolean isMoving = false;
    private boolean isFollowing = false;
    private String currentTask = "idle";
    private String currentAnimation = "idle";
    private int currentRoom = 0;
    private BufferedImage sprite;
    private BufferedImage walking;
    private Cat targetCat;
    private boolean isFacingRight = true;
    private int startX;  // Store starting X position
    private boolean shouldReturnToStart = false;
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
    private static final long WAIT_DURATION = 6000; 
    private int velocityY = 0;
    private int gravity = 1;
    private static final int GROUND_Y = 400; 
    private boolean isEncouraged = false;
    private long encouragedTime = 0;
    private GameObject taskAfterCat = null;
    private boolean catPermissionToMoveRoom = false;
    private int spriteWidth = 256;  
    private int spriteHeight = 256;

    public Human(int x, int y) {
        this.x = x;
        this.y = y;  
        this.startX = x;
        loadSprite();
    }

    public void setPlayState(PlayState playState) {
        this.playState = playState;
        // Resize the roomTasksCompleted array to match the actual number of rooms
        if (playState != null && playState.rooms != null) {
            this.roomTasksCompleted = new boolean[playState.rooms.size()];
        }
    }

    private void loadSprite() {
        try {
            sprite = ImageIO.read(new File("res/sprites/human/idle.png"));
            walking = ImageIO.read(new File("res/sprites/human/walking.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if ((isFollowing && targetCat != null) || 
            (targetObject != null && !isInteractingWithObject) || 
            isTransitioning || 
            shouldReturnToStart) {
            currentAnimation = "walking";
        } else {
            currentAnimation = "idle";
        }
        // Add gravity and ground collision at the start of update
        velocityY += gravity;
        y += velocityY;
        
        // Ground collision
        if (y > GROUND_Y) {
            y = GROUND_Y;
            velocityY = 0;
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
                // Reached the cat
                stopFollowing();
                
                // If there's a task to do after reaching cat, start it
                if (taskAfterCat != null) {
                    interactWithObject(taskAfterCat);
                    taskAfterCat = null;
                } else {
                    // Otherwise, start waiting
                    isWaiting = true;
                    waitStartTime = System.currentTimeMillis();
                    currentTask = "Waiting...";
                }
            }
        }

        // Check if waiting period is over
        if (isWaiting) {
            if (System.currentTimeMillis() - waitStartTime >= WAIT_DURATION) {
                isWaiting = false;
                shouldReturnToStart = true;
                currentTask = "Returning to start...";
            }
        }

        // Update interaction with current object
        if (isInteractingWithObject && currentObject != null) {
            // Face the object while interacting
            isFacingRight = currentObject.getX() > x;
            currentObject.update();
            if (!currentObject.isInteracting()) {
                isInteractingWithObject = false;
                currentObject = null;
                completedTasksInCurrentRoom++;
                // Only mark room as completed when all 3 tasks are done
                if (completedTasksInCurrentRoom >= 2) {
                    roomTasksCompleted[currentRoom] = true;
                }
                // Reset task state to allow for next interaction
                currentTask = "idle";
            }
        }

        if (isEncouraged && System.currentTimeMillis() - encouragedTime > 3000) {
            isEncouraged = false;
        }
    }

    

    public void draw(Graphics2D g2d, int offsetX, int offsetY) {
        BufferedImage imageToDraw = sprite;
        
        // Select the appropriate sprite based on current animation
        switch (currentAnimation) {
            case "walking":
                imageToDraw = walking;
                break;
            default:
                imageToDraw = sprite;
                break;
        }

        if (imageToDraw != null) {
            if (!isFacingRight) {
                g2d.drawImage(imageToDraw, x + offsetX + spriteWidth, y + offsetY, -spriteWidth, spriteHeight, null);
            } else {
                g2d.drawImage(imageToDraw, x + offsetX, y + offsetY, spriteWidth, spriteHeight, null);
            }
        }
    }

    public void draw(Graphics2D g2d) {
        draw(g2d, 0, 0);
    }

    public void moveToNextRoom() {
        if (!isMoving && !isTransitioning && catPermissionToMoveRoom) {
            isTransitioning = true;
            isMoving = true;  // Set isMoving to true
            currentRoom++; // Increment current room
            targetX = currentRoom * 1200 + 100; // Move to next room's starting position (room width is 1200)
            currentTask = "Moving to next room...";
            isInteractingWithObject = false;
            currentObject = null;
            completedTasksInCurrentRoom = 0;
            catPermissionToMoveRoom = false; // Reset permission after use
        }
    }

    public void interactWithObject(GameObject object) {
        if (object == null || object.isRemoved()) return;

        // Stop following the cat (if we were)
        stopFollowing();

        this.targetObject = object;
        this.currentTask = "Working on task...";
    }

    public boolean isCurrentRoomTasksCompleted() {
        return roomTasksCompleted[currentRoom];
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y; 
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
        // Cancel any ongoing interaction or movement toward an object
        this.isInteractingWithObject = false;
        this.targetObject = null;
        this.currentObject = null;
        this.isTransitioning = false;
        // Cancel returning to start position and waiting
        this.shouldReturnToStart = false;
        this.isWaiting = false;

        this.targetCat = cat;
        this.isFollowing = true;
        this.currentTask = "Following cat...";
        // Face the cat when starting to follow
        this.isFacingRight = cat.getX() > this.x;
    }

    public void stopFollowing() {
        this.isFollowing = false;
        this.targetCat = null;
        // Don't reset currentTask here as we want to show "Waiting..."
    }

    public void setShouldReturnToStart(boolean shouldReturn) {
        this.shouldReturnToStart = shouldReturn;
    }

    public void setFacingDirection(boolean faceRight) {
        this.isFacingRight = faceRight;
    }

    public void moveToCatPosition(Cat cat) {
        // Cancel any ongoing interaction or movement toward an object
        this.isInteractingWithObject = false;
        this.targetObject = null;
        this.currentObject = null;
        this.isTransitioning = false;
        // Cancel returning to start position and waiting
        this.shouldReturnToStart = false;
        this.isWaiting = false;

        this.targetCat = cat;
        this.isFollowing = true;
        this.currentTask = "Moving to cat...";
        // Face the cat when starting to move
        this.isFacingRight = cat.getX() > this.x;
    }

    public void setTaskAfterCatInteraction(GameObject object) {
        this.taskAfterCat = object;
    }

    public void grantCatPermissionToMoveRoom() {
        this.catPermissionToMoveRoom = true;
    }
}