package net.ironf.overheated.mixin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.DummyCraftingContainer;
import net.createmod.catnip.data.Iterate;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.steamworks.blocks.reinforcement.ReinforcementHandler;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.simibubi.create.content.processing.basin.BasinBlockEntity.getHeatLevelOf;

@Mixin(com.simibubi.create.content.processing.basin.BasinRecipe.class)
public class basinRecipeMixin {

    private static boolean apply(BasinBlockEntity basin, Recipe<?> recipe, boolean test) {
        boolean isBasinRecipe = recipe instanceof BasinRecipe;
        boolean reinforcementPresent = basin.getLevel().getBlockState(basin.getBlockPos().above()).is(AllBlocks.REINFORCEMENT.get());

        if (isBasinRecipe
                && ReinforcementHandler.requiresReinforcement.contains(recipe.getId().getPath())
                && !reinforcementPresent){
            //If this recipe requires reinforcement and there is no reinforcement we return false.
            return false;
        }


        IItemHandler availableItems = basin.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .orElse(null);
        IFluidHandler availableFluids = basin.getCapability(ForgeCapabilities.FLUID_HANDLER)
                .orElse(null);

        if (availableItems == null || availableFluids == null)
            return false;

        BlazeBurnerBlock.HeatLevel heat =  getHeatLevelOf(basin.getLevel().getBlockState(basin.getBlockPos().below(1)));
        if (isBasinRecipe && !((BasinRecipe) recipe).getRequiredHeat()
                .testBlazeBurner(heat))
            return false;

        List<ItemStack> recipeOutputItems = new ArrayList<>();
        List<FluidStack> recipeOutputFluids = new ArrayList<>();

        List<Ingredient> ingredients = new LinkedList<>(recipe.getIngredients());
        List<FluidIngredient> fluidIngredients =
                isBasinRecipe ? ((BasinRecipe) recipe).getFluidIngredients() : Collections.emptyList();

        for (boolean simulate : Iterate.trueAndFalse) {

            if (!simulate && test)
                return true;

            int[] extractedItemsFromSlot = new int[availableItems.getSlots()];
            int[] extractedFluidsFromTank = new int[availableFluids.getTanks()];

            Ingredients:
            for (Ingredient ingredient : ingredients) {
                for (int slot = 0; slot < availableItems.getSlots(); slot++) {
                    if (simulate && availableItems.getStackInSlot(slot)
                            .getCount() <= extractedItemsFromSlot[slot])
                        continue;
                    ItemStack extracted = availableItems.extractItem(slot, 1, true);
                    if (!ingredient.test(extracted))
                        continue;
                    if (!simulate)
                        availableItems.extractItem(slot, 1, false);
                    extractedItemsFromSlot[slot]++;
                    continue Ingredients;
                }

                // something wasn't found
                return false;
            }

            boolean fluidsAffected = false;
            FluidIngredients:
            for (FluidIngredient fluidIngredient : fluidIngredients) {
                int amountRequired = fluidIngredient.getRequiredAmount();

                for (int tank = 0; tank < availableFluids.getTanks(); tank++) {
                    FluidStack fluidStack = availableFluids.getFluidInTank(tank);
                    if (simulate && fluidStack.getAmount() <= extractedFluidsFromTank[tank])
                        continue;
                    if (!fluidIngredient.test(fluidStack))
                        continue;
                    int drainedAmount = Math.min(amountRequired, fluidStack.getAmount());
                    if (!simulate) {
                        fluidStack.shrink(drainedAmount);
                        fluidsAffected = true;
                    }
                    amountRequired -= drainedAmount;
                    if (amountRequired != 0)
                        continue;
                    extractedFluidsFromTank[tank] += drainedAmount;
                    continue FluidIngredients;
                }

                // something wasn't found
                return false;
            }

            if (fluidsAffected) {
                basin.getBehaviour(SmartFluidTankBehaviour.INPUT)
                        .forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
                basin.getBehaviour(SmartFluidTankBehaviour.OUTPUT)
                        .forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
            }

            if (simulate) {
                CraftingContainer remainderContainer = new DummyCraftingContainer(availableItems, extractedItemsFromSlot);

                if (recipe instanceof BasinRecipe basinRecipe) {
                    recipeOutputItems.addAll(basinRecipe.rollResults());

                    for (FluidStack fluidStack : basinRecipe.getFluidResults())
                        if (!fluidStack.isEmpty())
                            recipeOutputFluids.add(fluidStack);
                    for (ItemStack stack : basinRecipe.getRemainingItems(remainderContainer))
                        if (!stack.isEmpty())
                            recipeOutputItems.add(stack);

                } else {
                    recipeOutputItems.add(recipe.getResultItem(basin.getLevel()
                            .registryAccess()));

                    if (recipe instanceof CraftingRecipe craftingRecipe) {
                        for (ItemStack stack : craftingRecipe.getRemainingItems(remainderContainer))
                            if (!stack.isEmpty())
                                recipeOutputItems.add(stack);
                    }
                }
            }

            if (!basin.acceptOutputs(recipeOutputItems, recipeOutputFluids, simulate))
                return false;
        }

        return true;
    }
}
