package data;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.allatori.annotations.DoNotRename;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.ui.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PriceCheckService {

    private static final String OSBUDDY_EXCHANGE_SUMMARY_URL = "https://storage.googleapis.com/osb-exchange/summary.json";
    private static final String RSBUDDY_EXCHANGE_SUMMARY_URL = "https://rsbuddy.com/exchange/summary.json";
    private static Gson g = new Gson();
    private static Map<String, Integer> itemNameMapping = new HashMap<>();
    private static Map<Integer, ItemPrice> prices = new HashMap<>();
    private static int reloadMinutes = 30;
    private static boolean isReloadEnabled = true;

    private static ScheduledThreadPoolExecutor executor = ExecutorUtil.newScheduledExecutorPool(1, Throwable::printStackTrace);
    private static ScheduledFuture<?> task;

    private static int bankValue;
    private static int inventoryValue;

    public static int getTotalValue() {
        return bankValue + inventoryValue;
    }

    public static int getBankValue() { return bankValue; }

    public static int getInventoryValue() {
        return inventoryValue;
    }

    public static void updateBankValue() {
        int newValue = getCurrentBankValue();
        if (newValue > -1) {
            bankValue = newValue;
        }
    }

    public static void updateInventoryValue() {
        int newValue = getCurrentInventoryValue();
        if (newValue > -1) {
            inventoryValue = newValue;
        }
    }

    private static int getCurrentInventoryValue() {
        try {
            int total = Inventory.getCount(true, 995);
            Item[] invItems = Inventory.getItems();

            for (Item item : invItems) {
                if (item.isExchangeable()) {
                    int itemValue = PriceCheckService.getPrice(item.getId()).getSellAverage() * item.getStackSize();

                    if (itemValue <= 0) {
                        reload(OSBUDDY_EXCHANGE_SUMMARY_URL);
                    }
                    total += PriceCheckService.getPrice(item.getId()).getSellAverage() * item.getStackSize();
                }
            }
            return total;

        } catch (Exception ignored) {
            Log.severe("Failed Updating Inventory Value");
            return -1;
        }
    }

    private static int getCurrentBankValue() {
        try {
            int total = Bank.getCount(995);
            Item[] bankItems = Bank.getItems();

            for (Item item : bankItems) {
                if (item.isExchangeable()) {
                    int itemValue = PriceCheckService.getPrice(item.getId()).getSellAverage() * item.getStackSize();

                    if (itemValue <= 0) {
                        reload(OSBUDDY_EXCHANGE_SUMMARY_URL);
                    }
                    total += PriceCheckService.getPrice(item.getId()).getSellAverage() * item.getStackSize();
                }
            }
            return total;
        }
        catch (Exception ignored) {
            Log.severe("Failed Updating Bank Value");
            return -1;
        }
    }

    public static ItemPrice getPrice(String name) {
        if(prices.size() == 0) {
            reload();
        }
        int id = itemNameMapping.getOrDefault(name.toLowerCase(), -1);
        return id == -1 ? null : getPrice(id);
    }

    public static ItemPrice getPrice(int id) {
        if(prices.size() == 0) {
            reload();
        }
        return prices.getOrDefault(id, null);
    }

    public static void reload() {
        reload(RSBUDDY_EXCHANGE_SUMMARY_URL);
    }

    public static void reload(String url) {
        if(!isReloadEnabled && prices.size() > 0) {
            return;
        }
        if(task == null && isReloadEnabled) {
            task = executor.scheduleAtFixedRate(PriceCheckService::reload, reloadMinutes, reloadMinutes, TimeUnit.MINUTES);
        }
        try {
            HttpResponse<String> node = Unirest.get(url).asString();
            if(node.getStatus() != 200) {
                System.out.println(node.getBody());
                Log.severe("PriceCheck", "Failed to load prices. Result: " + node.getBody());
                return;
            }
            JsonObject o = g.fromJson(node.getBody(), JsonObject.class);
            for (String s : o.keySet()) {
                ItemPrice price = g.fromJson(o.get(s).getAsJsonObject(), ItemPrice.class);
                int id = Integer.parseInt(s);
                String name = price.name.toLowerCase();
                itemNameMapping.remove(name);
                itemNameMapping.put(name, id);
                prices.remove(id);
                prices.put(id, price);
            }
        } catch (UnirestException e) {
            e.printStackTrace();
            Log.severe(e);
        }
    }

    public static void dispose() {
        if (task != null) {
            task.cancel(true);
        }
        executor.shutdown();
    }

    public static void setShouldReload(boolean value) {
        isReloadEnabled = value;
    }

    @DoNotRename
    public class ItemPrice {

        @DoNotRename
        private String name;
        @DoNotRename
        private boolean members;
        @DoNotRename
        @SerializedName("buy_average")
        private int buyAverage;
        @DoNotRename
        @SerializedName("sell_average")
        private int sellAverage;
        @DoNotRename
        @SerializedName("overall_average")
        private int overallAverage;

        public String getName() {
            return name;
        }

        public boolean isMembers() {
            return members;
        }

        public int getBuyAverage() {
            return buyAverage;
        }

        public int getSellAverage() {
            return sellAverage;
        }

        public int getOverallAverage() {
            return overallAverage;
        }
    }
}
