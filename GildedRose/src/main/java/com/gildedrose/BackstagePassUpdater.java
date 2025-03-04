package com.gildedrose;

public class BackstagePassUpdater extends ItemUpdater {

    public BackstagePassUpdater(Item item) {
        super(item);
    }

    @Override
    public void updateQuality() {
        increaseQuality();
        if (item.sellIn <= 10) {
            increaseQuality();
        }
        if (item.sellIn <= 5) {
            increaseQuality();
        }
        decreaseSellIn();
        if (item.sellIn < 0) {
            item.quality = 0;
        }
    }
}
