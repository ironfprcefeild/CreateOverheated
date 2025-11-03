package net.ironf.overheated.cooling.colants;

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

import static com.simibubi.create.compat.jei.category.CreateRecipeCategory.getRenderedSlot;

public class CoolingRecipeCategory implements IRecipeCategory<CoolantRecipe> {

    private final IGuiHelper helper;
    public final static ResourceLocation UID = Overheated.asResource("cooling");
    public CoolingRecipeCategory(IGuiHelper helper) {
        this.helper = helper;
    }

    @Override
    public IDrawable getIcon() {
        return helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(AllBlocks.DIODE.get()));
    }

    @Override
    public RecipeType<CoolantRecipe> getRecipeType() {
        return JEIPlugin.COOLING_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("coverheated.jei.cooling.recipe_category");
    }

    @Override
    public int getHeight() {
        return 48;
    }

    @Override
    public int getWidth() {
        return 177;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CoolantRecipe recipe, IFocusGroup focuses) {
        builder
                .addSlot(RecipeIngredientRole.INPUT,25 , (int) (getHeight() * 0.5) -20)
                .addFluidStack(recipe.getInput().getMatchingFluidStacks().get(0).getFluid(),1000)
                .setBackground(getRenderedSlot(),-1,-1);
    }

    @Override
    public void draw(CoolantRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        JEIAssistant assistant = new JEIAssistant(guiGraphics);
        int diodeX = (int) (getWidth() * 0.6);
        int condenserX = (int) (getWidth() * 0.3);

        assistant.animatedBlock(AllBlocks.DIODE.getDefaultState(), diodeX,18);
        assistant.animatedBlock(AllBlocks.CONDENSER.getDefaultState(), condenserX,18);
        assistant.translate("coverheated.jei.cooling.quality", ChatFormatting.BLUE, condenserX +7,27);
        assistant.text(recipe.getHeat().toString(),ChatFormatting.BLUE,condenserX + 7,35);

    }
}
