package com.gildedrose.Factory;

import com.gildedrose.*;

public class ItemUpdaterFactory {
    public static ItemUpdater create(Item item) {
        if (item.name.contains("Sulfuras")) {
            return new SulfurasUpdater(item);
        } else if (item.name.contains("Aged Brie")) {
            return new AgedBrieUpdater(item);
        } else if (item.name.contains("Backstage passes")) {
            return new BackstagePassUpdater(item);
        } else if (item.name.contains("Conjured")) {
            return new ConjuredItemUpdater(item);
        } else {
            return new RegularItemUpdater(item);
        }
    }
}
