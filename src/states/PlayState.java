package states;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;
import entities.Human;
import entities.Cat;
import entities.Room;
import entities.GameObject;
import input.KeyboardInput;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlayState extends State {
    private Human human;
    private Cat cat;
    private KeyboardInput keyboardInput;
    private Clip bgMusic;
    private int cameraX = 0;
    private final int SCREEN_WIDTH = 1280;
<<<<<<< HEAD
    private final int WORLD_WIDTH = 4800; // Increased for 4 rooms
=======
    private final int WORLD_WIDTH = 15000; // Increased for 4 rooms
>>>>>>> 8cfcec4 (2.6)
    public List<Room> rooms;
    public Room currentRoom;

    public PlayState(GameStateManager gsm, KeyboardInput keyboardInput) {
        super(gsm);
<<<<<<< HEAD
        human = new Human(100, 300);
        cat = new Cat(50, 300);
=======
        human = new Human(50, 150);
        cat = new Cat(25, 150);
        human.setPlayState(this);
>>>>>>> 8cfcec4 (2.6)
        cat.setPlayState(this);
        this.keyboardInput = keyboardInput;
        initializeRooms();
        playMusic();
    }

    private void initializeRooms() {
        rooms = new ArrayList<>();
<<<<<<< HEAD
        // Living Room (0-1200)
        Room room1 = new Room(0, "Living Room", 0, 1200, new Color(240, 240, 220));
=======
        // Bedroom (0-1200) -- USE IMAGE
        Room room1 = new Room(
            0,
            "Bedroom",
            0,
            1200,
            new Color(220, 220, 240),
            "res/sprites/room/bedroom.png"
        );
>>>>>>> 8cfcec4 (2.6)
        room1.addObject(new GameObject(200, 400, 100, 100, "TV", "A large flat-screen TV", true));
        room1.addObject(new GameObject(500, 500, 150, 80, "Sofa", "A comfortable sofa", true));
        room1.addObject(new GameObject(900, 450, 120, 100, "Bookshelf", "A wooden bookshelf", true));
        rooms.add(room1);
<<<<<<< HEAD
=======

>>>>>>> 8cfcec4 (2.6)
        // Kitchen (1200-2400)
        Room room2 = new Room(1, "Kitchen", 1200, 1200, new Color(220, 240, 220));
        room2.addObject(new GameObject(1500, 400, 120, 120, "Fridge", "A refrigerator with food", true));
        room2.addObject(new GameObject(1900, 500, 100, 100, "Stove", "A cooking stove", true));
        room2.addObject(new GameObject(2300, 450, 100, 100, "Sink", "A kitchen sink", true));
        rooms.add(room2);
<<<<<<< HEAD
        // Bedroom (2400-3600)
        Room room3 = new Room(2, "Bedroom", 2400, 1200, new Color(220, 220, 240));
        room3.addObject(new GameObject(2700, 400, 150, 100, "Bed", "A cozy bed", true));
        room3.addObject(new GameObject(3100, 500, 100, 100, "Desk", "A study desk", true));
        room3.addObject(new GameObject(3500, 450, 120, 100, "Wardrobe", "A wooden wardrobe", true));
        rooms.add(room3);
=======

        // Living Room (2400-3600)
        Room room3 = new Room(2, "Living Room", 2400, 1200, new Color(240, 220, 220));
        room3.addObject(new GameObject(2700, 400, 120, 120, "Table", "A coffee table", true));
        room3.addObject(new GameObject(3000, 500, 150, 80, "Chair", "A comfortable chair", true));
        room3.addObject(new GameObject(3300, 450, 100, 100, "Lamp", "A floor lamp", true));
        rooms.add(room3);

>>>>>>> 8cfcec4 (2.6)
        // Empty Room (3600-4800)
        Room room4 = new Room(3, "Empty Room", 3600, 1200, new Color(200, 200, 200));
        // No objects added
        rooms.add(room4);
<<<<<<< HEAD
=======
        
>>>>>>> 8cfcec4 (2.6)
        currentRoom = room1;
    }

    private void playMusic() {
        if (isMusicPlaying()) return;
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("res/sounds/bgmusicmenu.wav"));
            bgMusic = AudioSystem.getClip();
            bgMusic.open(audioInputStream);
            bgMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isMusicPlaying() {
        return bgMusic != null && bgMusic.isRunning();
    }

    public void resetMusic() {
        playMusic();
    }

    public void pauseMusic() {
        stopMusic();
    }

    private void stopMusic() {
        if (bgMusic != null && bgMusic.isRunning()) {
            bgMusic.stop();
            bgMusic.close();
        }
    }

    @Override
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
            cat.meow(human);
        }
        if (keyboardInput.headbuttPressed) {
            cat.headbutt(human);
        }
        cat.update();
<<<<<<< HEAD
        // Update current room and its objects
        updateCurrentRoom();
=======
        
        // Update current room and its objects
        updateCurrentRoom();
        
        // Check cat proximity to objects
        if (currentRoom != null) {
            for (GameObject object : currentRoom.getObjects()) {
                object.checkCatProximity(cat);
            }
        }
        
>>>>>>> 8cfcec4 (2.6)
        // Update camera position
        updateCamera();
        // Check for pause
        if (keyboardInput.escapePressed) {
            gsm.setState(GameState.PAUSED);
        }
    }

    private void updateCamera() {
        // Simple camera following
        int catCenterX = cat.getX() + 32;
        int targetX = catCenterX - SCREEN_WIDTH / 2;
        // Clamp camera position
        if (targetX < 0) targetX = 0;
        if (targetX > WORLD_WIDTH - SCREEN_WIDTH) targetX = WORLD_WIDTH - SCREEN_WIDTH;
        cameraX = targetX;
    }

    private void updateCurrentRoom() {
        // Find the current room based on cat's position
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (room.isPointInRoom(cat.getX())) {
                if (currentRoom != room) {
                    currentRoom = room;
                }
                // Update current room and its objects
                currentRoom.update();
                // --- Wall collision for cat ---
                Rectangle catBounds = new Rectangle(cat.getX(), cat.getY(), 64, 64);
                boolean leftWallActive = false;
                boolean rightWallActive = false;
                if (cat.getX() < currentRoom.getX() + 30 && i > 0) {
                    Room prevRoom = rooms.get(i - 1);
                    leftWallActive = !currentRoom.isCompleted() && !prevRoom.isCompleted();
                }
                if (cat.getX() > currentRoom.getX() + currentRoom.getWidth() - 94 && i < rooms.size() - 1) {
                    Room nextRoom = rooms.get(i + 1);
                    rightWallActive = !currentRoom.isCompleted() && !nextRoom.isCompleted();
                }
                if (leftWallActive && cat.getX() < currentRoom.getX() + 20) {
                    cat.setPosition(currentRoom.getX() + 20, cat.getY());
                }
                if (rightWallActive && cat.getX() > currentRoom.getX() + currentRoom.getWidth() - 84) {
                    cat.setPosition(currentRoom.getX() + currentRoom.getWidth() - 84, cat.getY());
                }
                // --- Wall collision for human ---
                Rectangle humanBounds = new Rectangle(human.getX(), human.getY(), 64, 64);
                boolean humanLeftWallActive = false;
                boolean humanRightWallActive = false;
                if (human.getX() < currentRoom.getX() + 30 && i > 0) {
                    Room prevRoom = rooms.get(i - 1);
                    humanLeftWallActive = !currentRoom.isCompleted() && !prevRoom.isCompleted();
                }
                if (human.getX() > currentRoom.getX() + currentRoom.getWidth() - 94 && i < rooms.size() - 1) {
                    Room nextRoom = rooms.get(i + 1);
                    humanRightWallActive = !currentRoom.isCompleted() && !nextRoom.isCompleted();
                }
                if (humanLeftWallActive && human.getX() < currentRoom.getX() + 20) {
                    human.setPosition(currentRoom.getX() + 20, human.getY());
                }
                if (humanRightWallActive && human.getX() > currentRoom.getX() + currentRoom.getWidth() - 84) {
                    human.setPosition(currentRoom.getX() + currentRoom.getWidth() - 84, human.getY());
                }
                break;
            }
        }
    }

    public GameObject findNearestInteractableObject(Human human) {
        GameObject nearestObject = null;
        double minDistance = Double.MAX_VALUE;
        List<GameObject> objects = currentRoom.getObjects();
        for (GameObject object : objects) {
            if (object.isInteractable()) {
                double distance = Math.abs(object.getX() - human.getX());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestObject = object;
                }
            }
        }
        if (minDistance <= 100) {
            return nearestObject;
        }
        return null;
    }

    @Override
    public void draw(Graphics2D g2d) {
        // Draw all room backgrounds first
        for (Room room : rooms) {
            room.drawBackground(g2d, cameraX);
        }
        // Draw human (behind objects)
        human.draw(g2d, -cameraX, 0);
        // Draw all room objects
        for (Room room : rooms) {
            room.drawObjects(g2d, cameraX);
        }
        // Draw cat last (in front of everything)
        cat.draw(g2d, -cameraX, 0);
        // Draw darkness overlay for unopened rooms
        for (int i = 1; i < rooms.size(); i++) {
            Room prevRoom = rooms.get(i - 1);
            Room thisRoom = rooms.get(i);
            // If previous room is not completed, cover this room with darkness
            if (!prevRoom.isCompleted()) {
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRect(thisRoom.getX() - cameraX, 0, thisRoom.getWidth(), 720);
            }
<<<<<<< HEAD
        }
=======
        } 
>>>>>>> 8cfcec4 (2.6)
    }

    public KeyboardInput getKeyboardInput() {
        return keyboardInput;
    }
} 