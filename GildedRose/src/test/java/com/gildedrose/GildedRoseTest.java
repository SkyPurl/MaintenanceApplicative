package com.gildedrose;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class GildedRoseTest {

    @Test
    public void testStandardItemBeforeSellDate() {
        Item item = new Item("foo", 5, 10);
        GildedRose app = new GildedRose(new Item[]{item});
        app.updateQuality();
        assertEquals(4, item.sellIn);
        assertEquals(9, item.quality);
    }

    @Test
    public void testStandardItemAtZeroQuality() {
        Item item = new Item("foo", 5, 0);
        GildedRose app = new GildedRose(new Item[]{item});
        app.updateQuality();
        assertEquals(4, item.sellIn);
        assertEquals(0, item.quality);
    }

    @Test
    public void testStandardItemAfterSellDate() {
        Item item = new Item("foo", 0, 10);
        GildedRose app = new GildedRose(new Item[]{item});
        app.updateQuality();
        assertEquals(-1, item.sellIn);
        assertEquals(8, item.quality);
    }

    @Test
    public void testAgedBrieBeforeSellDate() {
        Item item = new Item("Aged Brie", 5, 10);
        GildedRose app = new GildedRose(new Item[]{item});
        app.updateQuality();
        assertEquals(4, item.sellIn);
        assertEquals(11, item.quality);
    }

    @Test
    public void testAgedBrieAtMaxQuality() {
        Item item = new Item("Aged Brie", 5, 50);
        GildedRose app = new GildedRose(new Item[]{item});
        app.updateQuality();
        assertEquals(4, item.sellIn);
        assertEquals(50, item.quality);
    }

    @Test
    public void testAgedBrieAfterSellDate() {
        Item item = new Item("Aged Brie", 0, 10);
        GildedRose app = new GildedRose(new Item[]{item});
        app.updateQuality();
        assertEquals(-1, item.sellIn);
        assertEquals(12, item.quality);
    }

    @Test
    public void testSulfuras() {
        Item item = new Item("Sulfuras, Hand of Ragnaros", 5, 80);
        GildedRose app = new GildedRose(new Item[]{item});
        app.updateQuality();
        assertEquals(5, item.sellIn);
        assertEquals(80, item.quality);
    }

    @Test
    public void testBackstagePassesMoreThan10Days() {
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20);
        GildedRose app = new GildedRose(new Item[]{item});
        app.updateQuality();
        assertEquals(14, item.sellIn);
        assertEquals(21, item.quality);
    }

    @Test
    public void testBackstagePasses10DaysOrLess() {
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 10, 20);
        GildedRose app = new GildedRose(new Item[]{item});
        app.updateQuality();
        assertEquals(9, item.sellIn);
        assertEquals(22, item.quality);
    }

    @Test
    public void testBackstagePasses5DaysOrLess() {
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 5, 20);
        GildedRose app = new GildedRose(new Item[]{item});
        app.updateQuality();
        assertEquals(4, item.sellIn);
        assertEquals(23, item.quality);
    }

    @Test
    public void testBackstagePassesAfterConcert() {
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 0, 20);
        GildedRose app = new GildedRose(new Item[]{item});
        app.updateQuality();
        assertEquals(-1, item.sellIn);
        assertEquals(0, item.quality);
    }

    @Test
    public void testBackstagePassesQualityNeverExceeds50() {
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 5, 49);
        GildedRose app = new GildedRose(new Item[]{item});
        app.updateQuality();
        assertEquals(4, item.sellIn);
        assertEquals(50, item.quality);
    }

    @Test
    public void testConjuredItemQualityNotNegative() {
        Item conjured = new Item("Conjured Mana Cake", 5, 1);
        GildedRose app = new GildedRose(new Item[]{conjured});
        app.updateQuality();
        assertEquals(0, conjured.quality);
        assertEquals(4, conjured.sellIn);
    }
}
