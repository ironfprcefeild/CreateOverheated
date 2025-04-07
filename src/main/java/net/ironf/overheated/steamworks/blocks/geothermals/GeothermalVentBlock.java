package net.ironf.overheated.steamworks.blocks.geothermals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class GeothermalVentBlock extends MagmaBlock {
    public GeothermalVentBlock(Properties p_54800_) {
        super(p_54800_);
    }
    public void stepOn(@NotNull Level p_153777_, @NotNull BlockPos p_153778_, @NotNull BlockState p_153779_, Entity p_153780_) {
        if (!p_153780_.isSteppingCarefully() && p_153780_ instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)p_153780_)) {
            p_153780_.hurt(p_153777_.damageSources().hotFloor(), 4.0F);
        }

        super.stepOn(p_153777_, p_153778_, p_153779_, p_153780_);
    }

}
