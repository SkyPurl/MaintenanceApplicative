package com.gildedrose;

import com.gildedrose.Factory.ItemUpdaterFactory;

public class GildedRose {
    Item[] items;

    public GildedRose(Item[] items) {
        this.items = items;
    }

    public void updateQuality() {
        for (Item item : items) {
            ItemUpdater updater = ItemUpdaterFactory.create(item);
            updater.updateQuality();
        }
    }
}
