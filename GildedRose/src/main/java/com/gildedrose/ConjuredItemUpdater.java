package com.gildedrose;

public class ConjuredItemUpdater extends ItemUpdater {

    public ConjuredItemUpdater(Item item) {
        super(item);
    }

    @Override
    public void updateQuality() {
        decreaseQuality();
        decreaseQuality();
        decreaseSellIn();
        if (item.sellIn < 0) {
            decreaseQuality();
            decreaseQuality();
        }
    }
}
