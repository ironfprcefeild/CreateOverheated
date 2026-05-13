package net.ironf.overheated.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

public record SimpleFluidInput (FluidStack fluid) implements RecipeInput {

    @Override
    public ItemStack getItem(int i) {
        throw new IllegalArgumentException("No ItemStack for this recipe type");
    }

    @Override
    public int size() {
        return 1;
    }
}
