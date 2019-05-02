package Mule;

import net.miginfocom.swing.MigLayout;
import org.rspeer.runetek.api.Game;
import javax.swing.*;
import java.awt.*;

public class GUI {

    private String username;
    private String password;

    private JLabel mLabel;
    private JLabel m2Label;
    private JTextField userN;
    private JTextField pass;
    private JButton startBtn;

    private JFrame frame;

    public GUI(){
        frame = new JFrame("SS Mule");
        frame.setLayout(new MigLayout());
        frame.setPreferredSize(new Dimension(300, 500));

        mLabel = new JLabel("Mules Login Username:");
        m2Label = new JLabel("Mules Login Password:");
        userN   = new JTextField();
        pass = new JTextField();
        startBtn = new JButton("Start");

        frame.add(mLabel, "wrap, growx");
        frame.add(userN, "wrap, growx");
        frame.add(m2Label, "wrap, growx");
        frame.add(pass, "wrap, growx");
        frame.add(startBtn, "wrap, growx");

        startBtn.addActionListener(x -> startBtnHandler());

        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(Game.getCanvas());
        frame.pack();

        frame.setVisible(true);
    }

    private void startBtnHandler() {
        username = userN.getText();
        password = pass.getText();
        Mule.startScript = true;

        frame.setVisible(false);
    }

    public String getUser(){return username;}

    public String getPass(){return password;}
}
