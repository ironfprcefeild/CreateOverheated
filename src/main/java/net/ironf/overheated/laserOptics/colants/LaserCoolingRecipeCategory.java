package net.ironf.overheated.laserOptics.colants;

import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.foundation.gui.AllGuiTextures;
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

import static net.ironf.overheated.utility.GoggleHelper.easyFloat;
import static com.simibubi.create.compat.jei.category.CreateRecipeCategory.getRenderedSlot;

public class LaserCoolingRecipeCategory implements IRecipeCategory<LaserCoolantRecipe> {

    private final IGuiHelper helper;
    public final static ResourceLocation UID = new ResourceLocation(Overheated.MODID, "laser_cooling");
    public LaserCoolingRecipeCategory(IGuiHelper helper) {
        this.helper = helper;
    }

    @Override
    public IDrawable getIcon() {
        return helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(AllBlocks.DIODE.get()));
    }

    @Override
    public RecipeType<LaserCoolantRecipe> getRecipeType() {
        return JEIPlugin.LASER_COOLING_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("coverheated.jei.cooling.recipe_category");
    }

    @Override
    public IDrawable getBackground() {
        return new EmptyBackground(177,48);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LaserCoolantRecipe recipe, IFocusGroup focuses) {
        builder
                .addSlot(RecipeIngredientRole.INPUT,25 , (int) (getBackground().getHeight() * 0.5) -20)
                .addFluidStack(recipe.getInput().getMatchingFluidStacks().get(0).getFluid(),1000)
                .setBackground(getRenderedSlot(),-1,-1);
    }

    @Override
    public void draw(LaserCoolantRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        JEIAssistant assistant = new JEIAssistant(guiGraphics);
        int diodeX = (int) (getBackground().getWidth() * 0.6);
        int condenserX = (int) (getBackground().getWidth() * 0.3);

        assistant.animatedBlock(AllBlocks.DIODE.getDefaultState(), diodeX,18);
        assistant.animatedBlock(AllBlocks.CONDENSER.getDefaultState(), condenserX,18);
        assistant.translate("coverheated.jei.cooling.quality", ChatFormatting.BLUE, condenserX +7,27);
        assistant.translate("coverheated.jei.cooling.power",ChatFormatting.RED ,diodeX + 7,27);
        assistant.text(recipe.getHeat().toString(),ChatFormatting.BLUE,condenserX + 7,35);
        assistant.text(recipe.getVolatility().toString(),ChatFormatting.RED, diodeX + 7,35);

    }
}
