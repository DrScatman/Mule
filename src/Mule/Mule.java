package Mule;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Login;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.EnterInput;
import org.rspeer.runetek.api.component.Trade;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.listeners.ChatMessageListener;
import org.rspeer.runetek.event.listeners.LoginResponseListener;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.*;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptMeta;
import org.rspeer.script.events.LoginScreen;
import org.rspeer.ui.Log;

import java.io.*;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

@ScriptMeta(developer = "DrScatman", desc = "Mule", name = "SS Mule", version = 0.1)

public class Mule extends Script implements ChatMessageListener, RenderListener {

    public int Gold;
    public int Gold2;
    public int gold3;
    public String status;
    String user;
    private GUI Gui;
    public static boolean startScript;
    public static String Username;
    public static String Password;
    public static Area muleArea;
    private boolean startGoldSet = false;



    @Override
    public void onStart() {

        try {
            File file = new File("C:" + File.separator + "Mule"+ File.separator + "mule.txt");

            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter pw = new PrintWriter(file);
            pw.println("done");
            pw.close();

            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            while (((status = br.readLine())) != null) {
                Log.info(status);
            }

            br.close();
        } catch (IOException e) {
            Log.info("File not found");
        }

        removeBlockingEvent(LoginScreen.class);
        Gui = new GUI();
    }

    public void onStop() {

    }
    public boolean TradeStatus;
    NumberFormat myFormat = NumberFormat.getInstance();


    public int loop() {
        if(startScript) {
            Username = Gui.getUser();
            Password = Gui.getPass();
            muleArea = Gui.getMuleArea().getMuleArea();

            inRead();
            if (status != null) {
                status = status.trim();
            }
            if (org.rspeer.runetek.api.component.Dialog.canContinue()) {
                Dialog.processContinue();
                Time.sleep(1000);
            }
            if (!muleArea.contains(Players.getLocal())) {
                Movement.setWalkFlag(muleArea.getTiles().get(randInt(0, muleArea.getTiles().size() -1)));
            }

            if (Inventory.getFirst(995) != null) {
                Gold = Inventory.getFirst(995).getStackSize();
                if(!startGoldSet){
                    Gold2 = Inventory.getFirst(995).getStackSize();
                    startGoldSet = true;
                }
            }

            gold3 = Gold - Gold2;


            if (status.contains("mule")) {
                if (!Game.isLoggedIn() && Username != null && Password != null) {
                    Login.enterCredentials(Username, Password);
                    Keyboard.pressEnter();
                    Time.sleep(200);
                    Keyboard.pressEnter();
                    Time.sleep(200);
                    Keyboard.pressEnter();
                }
                if (Players.getNearest(user) != null && !Trade.isOpen()) {
                    Players.getNearest(user).interact("Trade with");
                    Time.sleep(3000);
                }
                if (Trade.hasOtherAccepted()) {
                    Trade.accept();
                    Log.info("Trade accepted");
                }
            }
            if (status.contains("done")) {
                Game.logout();
            }
            if (status.contains("needgold")) {
                if (!Game.isLoggedIn() && Username != null && Password != null) {
                    Login.enterCredentials(Username, Password);
                    Keyboard.pressEnter();
                    Time.sleep(200);
                    Keyboard.pressEnter();
                    Time.sleep(200);
                    Keyboard.pressEnter();
                }
                if (Players.getNearest(user) != null && !Trade.isOpen()) {
                    Players.getNearest(user).interact("Trade with");
                    Time.sleep(3000);
                }
                if (Inventory.getFirst(995) != null) {
                    if (!Trade.contains(true, 995)) {
                        int Coins = Inventory.getFirst(995).getStackSize();
                        if (Trade.isOpen(false)) {
                            // handle first trade window...
                            Trade.offer("Coins", x -> x.contains("Offer-X"));
                            Time.sleep(1000);
                            if (EnterInput.isOpen()) {
                                EnterInput.initiate(4000000);
                                Time.sleep(1000);
                            }
                            if (Trade.contains(true, 995)) {
                                Trade.accept();
                                Time.sleep(700);
                            }
                        } else if (Trade.isOpen(true)) {
                            // handle second trade window...
                            Trade.accept();
                            Time.sleep(700);
                        }

                    }
                }
            }
        }



            return 500;
        }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

        private void inRead() {
        try {
            File file = new File("C:" + File.separator + "Mule"+ File.separator + "mule.txt");

            if(!file.exists()) {
                file.createNewFile();
            }

            BufferedReader br = new BufferedReader(new FileReader(file));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                status = sb.toString();
                Log.info(status);
            } finally {
                br.close();
            }
            br.close();
        } catch (IOException e) {
            Log.info("File not found");
        }
        }

    public void notify(RenderEvent renderEvent) {
        Graphics g = renderEvent.getSource();
        g.drawString("status =  " + status, 300, 330);
        g.drawString("Gp Received: " + format(gold3), 300, 350);
        g.drawString("Total Gp: " + format(Gold), 300, 370);

    }

    public void setStartScript(boolean startScript) {
        this.startScript = startScript;
    }

    public void notify(ChatMessageEvent Chatevent) {

        if (Chatevent.getMessage().contains("Accepted Trade")) {
            if(Inventory.getFirst(995) != null){
                Gold2 = Inventory.getFirst(995).getStackSize();
            }
        }
        ChatMessageType type = Chatevent.getType();

        if (type.equals(ChatMessageType.TRADE)) {
            user = Chatevent.getSource();
            // Do stuff
            Log.info(user + " is Trading");

        }
    }

    public static int randInt(int min, int max) {
        java.util.Random rand = new java.util.Random();
        int randomNum = rand.nextInt(max - min + 1) + min;
        return randomNum;
    }

}

