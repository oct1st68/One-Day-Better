package input;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class KeyboardInput implements KeyListener {
    public boolean upPressed, leftPressed, rightPressed;
    public boolean interactPressed, meowPressed, headbuttPressed;
    public boolean escapePressed, enterPressed;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch(key){
            case KeyEvent.VK_W:
                upPressed = true;
                break;
            case KeyEvent.VK_A:
                leftPressed = true;
                break;
            case KeyEvent.VK_D:
                rightPressed = true;
                break;
            case KeyEvent.VK_E:
                interactPressed = true;
                break;
            case KeyEvent.VK_SPACE:
                meowPressed = true;
                break;
            case KeyEvent.VK_Q:
                headbuttPressed = true;
                break;
            case KeyEvent.VK_ESCAPE:
                escapePressed = true;
                break;
            case KeyEvent.VK_ENTER:
                enterPressed = true;
                break;
        }

    }
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch(key){
            case KeyEvent.VK_W:
                upPressed = false;
                break;
            case KeyEvent.VK_A:
                leftPressed = false;
                break;
            case KeyEvent.VK_D:
                rightPressed = false;
                break;
            case KeyEvent.VK_E:
                interactPressed = false;
                break;
            case KeyEvent.VK_SPACE:
                meowPressed = false;
                break;
            case KeyEvent.VK_Q:
                headbuttPressed = false;
                break;
            case KeyEvent.VK_ESCAPE:
                escapePressed = false;
                break;
            case KeyEvent.VK_ENTER:
                enterPressed = false;
                break;
        }
    }
}
