package net.ironf.overheated.nuclear.radiation;

import net.ironf.overheated.Overheated;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RadioactiveItem extends Item {
    public RadioactiveItem(Properties p) {
        super(p);
    }
    int radioactivityChance = 20;
    int radioactivityStrength = 1;

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int p_41407_, boolean p_41408_) {
        super.inventoryTick(stack,level,entity, p_41407_, p_41408_);
        if (level.getRandom().nextInt(0,radioactivityChance) == 0){
            RadiationMap.pulseRadiation(level,entity.getOnPos(),radioactivityStrength);
        }
    }
}
