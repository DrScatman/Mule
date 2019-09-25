package Mule;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Login;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.*;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
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

import java.io.IOException;

import static org.rspeer.runetek.event.types.LoginResponseEvent.Response.*;

@ScriptMeta(developer = "DrScatman", desc = "Mule", name = "SS Mule", version = 0.1)

public class Mule extends Script implements ChatMessageListener, RenderListener, LoginResponseListener {

    private int Gold;
    private int Gold2;
    private int gold3;
    private String status;
    private String user;
    private GUI Gui;
    private boolean startScript;
    private String Username;
    private String Password;
    private int muleWorld;
    private Area muleArea;
    private boolean startGoldSet = false;
    private static final String MULE_FILE_PATH = Script.getDataDirectory() + "\\mule.txt";
    private boolean chain = false;
    private StopWatch runtime;

    //public final String API_KEY = "JV5ML4DE4M9W8Z5KBE00322RDVNDGGMTMU1EH9226YCVGFUBE6J6OY1Q2NJ0RA8YAPKO70";
    //private CheckInstances instanceChecker;

    @Override
    public void onStart() {
        LoginScreen ctx = new LoginScreen(this);
        ctx.setStopScriptOn(INVALID_CREDENTIALS);
        ctx.setStopScriptOn(RUNESCAPE_UPDATE);
        ctx.setStopScriptOn(RUNESCAPE_UPDATE_2);
        ctx.setDelayOnLoginLimit(true);
        //instanceChecker = new CheckInstances(this);
        runtime = StopWatch.start();

        try {
            File file = new File(MULE_FILE_PATH);

            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter pw = new PrintWriter(file);
            pw.println("done");
            pw.close();

            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            while (((status = br.readLine())) != null) {
                Log.info(status + ":\t" + format(Gold));
            }

            br.close();
        } catch (IOException e) {
            Log.info("File not found");
        }

        removeBlockingEvent(LoginScreen.class);
        Gui = new GUI(this);

        Username = Gui.getUsername();
        Password = Gui.getPassword();
        muleWorld = Gui.getMuleWorld();
        muleArea = Gui.getMuleArea().getMuleArea();
    }

    public void notify(LoginResponseEvent loginResponseEvent) {
        if (loginResponseEvent.getResponse().equals(LoginResponseEvent.Response.RUNESCAPE_UPDATE) ||
                loginResponseEvent.getResponse().equals(RUNESCAPE_UPDATE_2)) {
            chain = true;
            setStopping(true);
        }

        if (loginResponseEvent.getResponse().equals(TOO_MANY_ATTEMPTS) || loginResponseEvent.getResponse().equals(LOGIN_LIMIT)) {
            Time.sleep(40000, 90000);
        }
    }

    public void onStop() {
        if (chain) {
            String launcher = "C:\\Users\\bllit\\OneDrive\\Desktop\\MuleLauncher.bat";

            try {
                Runtime.getRuntime().exec(
                        "cmd /c " + launcher);

                System.exit(0);
            } catch (Exception e) {
                System.out.println("HEY Buddy ! U r Doing Something Wrong ");
                e.printStackTrace();
            }
        }
    }

    NumberFormat myFormat = NumberFormat.getInstance();

    public int loop() {
        if (startScript) {
            /*if (instanceChecker.isBadInstanceTime()) {
                instanceChecker.checkBadInstances(10);
            }*/

            inRead();
            if (status != null) {
                status = status.trim();
            }
            if (org.rspeer.runetek.api.component.Dialog.canContinue()) {
                Dialog.processContinue();
                Time.sleep(1000);
            }
            if (Game.isLoggedIn() && Players.getLocal() != null && !muleArea.contains(Players.getLocal())) {
                Movement.walkToRandomized(muleArea.getCenter());
                return 1000;
            }

            if (Inventory.getFirst(995) != null) {
                Gold = Inventory.getFirst(995).getStackSize();
                if (!startGoldSet) {
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

                // Click to play bug
                if (Game.isLoggedIn()) {
                    InterfaceComponent clickPlay = Interfaces.getComponent(413, 73);
                    if (clickPlay != null && clickPlay.isVisible()) {
                        clickPlay.interact(ActionOpcodes.INTERFACE_ACTION);
                    }
                }

                // Hop to muleWorld
                if (Game.isLoggedIn() && Worlds.getCurrent() != muleWorld) {
                    WorldHopper.hopTo(muleWorld);
                    Time.sleepUntil(() -> Worlds.getCurrent() == muleWorld, 8000);
                }

                // Deposit all items
                if (Game.isLoggedIn() && Inventory.containsAnyExcept(995) && !Trade.isOpen()) {
                    while (!Bank.isOpen()) {
                        Bank.open();
                    }
                    Bank.depositAllExcept(995);
                    Time.sleepUntil(() -> Inventory.containsOnly(995), 5000);
                    Bank.close();
                    Time.sleepUntil(Bank::isClosed, 5000);
                }

                if (Players.getNearest(user) != null && !Trade.isOpen() && muleArea.contains(Players.getLocal())) {
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
                        //int Coins = Inventory.getFirst(995).getStackSize();
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

    private static String format(long value) {
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
            File file = new File(MULE_FILE_PATH);

            if (!file.exists()) {
                file.createNewFile();
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                status = sb.toString();
                Log.info(status);
            }
        } catch (IOException e) {
            Log.info("File not found");
        }
    }

    public void notify(RenderEvent renderEvent) {
        Graphics g = renderEvent.getSource();
        g.drawString("Runtime: " + runtime.toElapsedString(), 300, 330);
        g.drawString("Gp Received: " + format(gold3), 300, 350);
        g.drawString("Gp /h: " + format((long) runtime.getHourlyRate(gold3)), 300, 370);
        g.drawString("Total Gp: " + format(Gold), 300, 390);

    }

    void setStartScript() {
        this.startScript = true;
    }

    public void notify(ChatMessageEvent Chatevent) {

        if (Chatevent.getMessage().contains("Accepted Trade")) {
            if (Inventory.getFirst(995) != null) {
                Gold2 = Inventory.getFirst(995).getStackSize();
            }
        }
        ChatMessageType type = Chatevent.getType();

        if (type.equals(ChatMessageType.TRADE) && muleArea.contains(Players.getLocal())) {
            user = Chatevent.getSource();
            // Do stuff
            Log.info(user + " is Trading");

        }
    }

    private static int randInt(int min, int max) {
        java.util.Random rand = new java.util.Random();
        return rand.nextInt(max - min + 1) + min;
    }

}

