package net.ironf.overheated.mixin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.recipe.DummyCraftingContainer;
import net.createmod.catnip.data.Iterate;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.steamworks.blocks.reinforcement.ReinforcementHandler;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.simibubi.create.content.processing.basin.BasinBlockEntity.getHeatLevelOf;

/// TODO test this
@Mixin(com.simibubi.create.content.processing.basin.BasinRecipe.class)
public class basinRecipeMixin {

    /*
        boolean isBasinRecipe = recipe instanceof BasinRecipe;
        boolean reinforcementPresent = basin.getLevel().getBlockState(basin.getBlockPos().above()).is(AllBlocks.REINFORCEMENT.get());
            if (isBasinRecipe
                && ReinforcementHandler.requiresReinforcement.contains(recipe.getId().getPath())
            && !reinforcementPresent){
        //If this recipe requires reinforcement and there is no reinforcement we return false.
        return false;
    }

     */

    @Inject(method = "apply", at = @At("HEAD"), remap = false)
    private static void apply(BasinBlockEntity basin, Recipe<?> recipe, boolean test, CallbackInfoReturnable<Boolean> ci) {
        boolean isBasinRecipe = recipe instanceof BasinRecipe;
        boolean reinforcementPresent = basin.getLevel().getBlockState(basin.getBlockPos().above()).is(AllBlocks.REINFORCEMENT.get());
        if (isBasinRecipe
                && ReinforcementHandler.reinforcedRecipe(recipe)
                && !reinforcementPresent) {
            //If this recipe requires reinforcement and there is no reinforcement we return false.
            ci.setReturnValue(false);
            ci.cancel();
        }
    }
}
