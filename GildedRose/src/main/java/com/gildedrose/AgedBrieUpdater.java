package com.gildedrose;

public class AgedBrieUpdater extends ItemUpdater {

    public AgedBrieUpdater(Item item) {
        super(item);
    }

    @Override
    public void updateQuality() {
        increaseQuality();
        decreaseSellIn();
        if (item.sellIn < 0) {
            increaseQuality();
        }
    }
}
