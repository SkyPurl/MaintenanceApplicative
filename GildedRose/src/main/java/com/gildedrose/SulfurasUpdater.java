package com.gildedrose;

public class SulfurasUpdater extends ItemUpdater {

    public SulfurasUpdater(Item item) {
        super(item);
    }

    @Override
    public void updateQuality() {
        // Sulfuras ne change jamais.
    }
}
