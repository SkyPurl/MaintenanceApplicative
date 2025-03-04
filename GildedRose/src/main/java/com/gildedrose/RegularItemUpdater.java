package com.gildedrose;

public class RegularItemUpdater extends ItemUpdater {

    public RegularItemUpdater(Item item) {
        super(item);
    }

    @Override
    public void updateQuality() {
        decreaseQuality();
        decreaseSellIn();
        if (item.sellIn < 0) {
            decreaseQuality();
        }
    }
}
