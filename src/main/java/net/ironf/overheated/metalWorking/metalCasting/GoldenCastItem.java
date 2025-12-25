package net.ironf.overheated.metalWorking.metalCasting;

import net.ironf.overheated.AllItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GoldenCastItem extends Item {
    public GoldenCastItem(Properties p) {
        super(p);
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return AllItems.EMPTY_GOLD_CAST.asStack(1);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }
}
