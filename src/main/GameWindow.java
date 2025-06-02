package main;

import javax.swing.JFrame;

public class GameWindow extends JFrame {
    public  GameWindow(){
        setTitle("One Day Better");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
<<<<<<< HEAD
        setContentPane(new GamePanel(1280,720));
=======
        setContentPane(new GamePanel(1280, 720));
>>>>>>> 8cfcec4 (2.6)
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        ((GamePanel) getContentPane()).StartGameThread();
    }
}
