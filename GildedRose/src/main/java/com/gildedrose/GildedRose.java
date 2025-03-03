package com.gildedrose;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GildedRose {
    Item[] items;
    private static final Map<String, Consumer<Item>> updateStrategies = new HashMap<>();
    static {
        updateStrategies.put("Aged Brie", GildedRose::updateAgedBrie);
        updateStrategies.put("Backstage passes to a TAFKAL80ETC concert", GildedRose::updateBackstagePass);
        updateStrategies.put("Sulfuras, Hand of Ragnaros", item -> {});
    }
    public GildedRose(Item[] items) {
        this.items = items;
    }
    public void updateQuality() {
        for (Item item : items) {
            Consumer<Item> updater = updateStrategies.getOrDefault(item.name, GildedRose::updateRegularItem);
            updater.accept(item);
            if (!"Sulfuras, Hand of Ragnaros".equals(item.name)) {
                decrementSellIn(item);
            }
            if (item.sellIn < 0) {
                applyExpiredRules(item);
            }
        }
    }
    private static void updateRegularItem(Item item) {
        decrementQuality(item);
    }
    private static void updateAgedBrie(Item item) {
        increaseQuality(item);
    }
    private static void updateBackstagePass(Item item) {
        increaseQuality(item);
        if (item.sellIn <= 10) {
            increaseQuality(item);
        }
        if (item.sellIn <= 5) {
            increaseQuality(item);
        }
    }
    private static void applyExpiredRules(Item item) {
        switch (item.name) {
            case "Aged Brie":
                increaseQuality(item);
                break;
            case "Backstage passes to a TAFKAL80ETC concert":
                item.quality = 0;
                break;
            case "Sulfuras, Hand of Ragnaros":
                break;
            default:
                decrementQuality(item);
                break;
        }
    }
    private static void increaseQuality(Item item) {
        if (item.quality < 50) {
            item.quality++;
        }
    }
    private static void decrementQuality(Item item) {
        if (item.quality > 0) {
            item.quality--;
        }
    }
    private static void decrementSellIn(Item item) {
        item.sellIn--;
    }
}
