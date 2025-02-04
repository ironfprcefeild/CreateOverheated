package net.ironf.overheated.steamworks.blocks.condensor;

import com.simibubi.create.compat.jei.EmptyBackground;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.ironf.overheated.AllBlocks;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.recipes.JEI.JEIAssistant;
import net.ironf.overheated.recipes.JEI.JEIPlugin;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import static com.simibubi.create.compat.jei.category.CreateRecipeCategory.getRenderedSlot;

public class CondensingRecipeCategory implements IRecipeCategory<CondenserRecipe> {

    private final IGuiHelper helper;
    public final static ResourceLocation UID = new ResourceLocation(Overheated.MODID, "laser_cooling");
    public CondensingRecipeCategory(IGuiHelper helper) {
        this.helper = helper;
    }

    @Override
    public IDrawable getIcon() {
        return helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(AllBlocks.DIODE.get()));
    }

    @Override
    public RecipeType<CondenserRecipe> getRecipeType() {
        return JEIPlugin.CONDENSING_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("coverheated.jei.condensing.recipe_category");
    }

    @Override
    public IDrawable getBackground() {
        return new EmptyBackground(177,125);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CondenserRecipe recipe, IFocusGroup focuses) {
        FluidStack output = recipe.getOutput();
        builder
                .addSlot(RecipeIngredientRole.INPUT,120 , 25)
                .addFluidStack(recipe.getInput().getMatchingFluidStacks().get(0).getFluid(),1000 / output.getAmount())
                .setBackground(getRenderedSlot(),-1,-1);
        builder
                .addSlot(RecipeIngredientRole.OUTPUT,120 , 65)
                .addFluidStack(output.getFluid(),1000)
                .setBackground(getRenderedSlot(),-1,-1);
    }

    @Override
    public void draw(CondenserRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        JEIAssistant assistant = new JEIAssistant(guiGraphics);

        assistant.animatedBlock(com.simibubi.create.AllBlocks.FLUID_TANK.getDefaultState(), 80,45,false);
        assistant.animatedBlock(AllBlocks.CONDENSER.getDefaultState(), 80,65,false);
        assistant.animatedBlock(com.simibubi.create.AllBlocks.FLUID_TANK.getDefaultState(), 80,85,true);

        assistant.translate("coverheated.jei.condensing.temp", ChatFormatting.BLUE, 56,5);
        assistant.text(recipe.getMinTemp().toString(),ChatFormatting.BLUE,56,15);
        assistant.translate("coverheated.jei.condensing.gained_temp", ChatFormatting.BLUE, 56,30);
        assistant.text(recipe.getMinTemp().toString(),ChatFormatting.BLUE,56,40);

    }
}
