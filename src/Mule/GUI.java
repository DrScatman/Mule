package Mule;

import data.MuleArea;
import net.miginfocom.swing.MigLayout;
import org.rspeer.runetek.api.Game;
import javax.swing.*;
import java.awt.*;

public class GUI {

    private String username;
    private String password;
    private MuleArea muleArea;
    private JLabel mLabel;
    private JLabel m2Label;
    private JTextField userN;
    private JTextField pass;
    private JButton startBtn;
    private JLabel posLabel;
    private JComboBox mulePos;

    private JFrame frame;

    public GUI(){
        frame = new JFrame("SS Mule");
        frame.setLayout(new MigLayout());
        frame.setPreferredSize(new Dimension(300, 500));

        mLabel = new JLabel("Mules Login Username:");
        m2Label = new JLabel("Mules Login Password:");
        userN   = new JTextField();
        pass = new JTextField();
        posLabel = new JLabel("Select Mule Area:");
        mulePos = new JComboBox(MuleArea.values());
        startBtn = new JButton("Start");

        userN.setText("mj682435@gmail.com");
        pass.setText("Xb32y0x5");
        mulePos.setSelectedIndex(0);

        frame.add(mLabel, "wrap, growx");
        frame.add(userN, "wrap, growx");
        frame.add(m2Label, "wrap, growx");
        frame.add(pass, "wrap, growx");
        frame.add(posLabel, "wrap, growx");
        frame.add(mulePos, "wrap, growx");
        frame.add(startBtn, "wrap, growx");

        startBtn.addActionListener(x -> startBtnHandler());

        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(Game.getCanvas());
        frame.pack();

        // Auto GUI
        frame.setVisible(false);
        startBtnHandler();
    }

    private void startBtnHandler() {
        username = userN.getText();
        password = pass.getText();
        muleArea = (MuleArea) mulePos.getSelectedItem();
        Mule.startScript = true;

        frame.setVisible(false);
    }

    public String getUser(){return username;}

    public String getPass(){return password;}

    public MuleArea getMuleArea(){return muleArea;}
}
