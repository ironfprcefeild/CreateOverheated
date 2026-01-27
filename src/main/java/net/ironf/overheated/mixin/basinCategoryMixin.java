package net.ironf.overheated.mixin;

import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.utility.CreateLang;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.ironf.overheated.steamworks.blocks.reinforcement.ReinforcementHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(com.simibubi.create.compat.jei.category.BasinCategory.class)
public class basinCategoryMixin {

    //Dummy constructor
    public basinCategoryMixin(boolean needsHeating) {
        this.needsHeating = needsHeating;
    }


    private final boolean needsHeating;
    public void draw(BasinRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        HeatCondition requiredHeat = recipe.getRequiredHeat();

        boolean noHeat = requiredHeat == HeatCondition.NONE;

        int vRows = (1 + recipe.getFluidResults().size() + recipe.getRollableResults().size()) / 2;

        if (vRows <= 2)
            AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 136, -19 * (vRows - 1) + 32);

        AllGuiTextures shadow = noHeat ? AllGuiTextures.JEI_SHADOW : AllGuiTextures.JEI_LIGHT;
        shadow.render(graphics, 81, 58 + (noHeat ? 10 : 30));

        if (ReinforcementHandler.requiresReinforcement.contains(recipe.getId().getPath())){
            graphics.drawString(Minecraft.getInstance().font,
                    Component.translatable("coverheated.jei.reinforcement") ,
                            9, 71, requiredHeat.getColor(), false);
        }

        if (!needsHeating)
            return;

        AllGuiTextures heatBar = noHeat ? AllGuiTextures.JEI_NO_HEAT_BAR : AllGuiTextures.JEI_HEAT_BAR;
        heatBar.render(graphics, 4, 80);
        graphics.drawString(Minecraft.getInstance().font, CreateLang.translateDirect(requiredHeat.getTranslationKey()), 9,
                86, requiredHeat.getColor(), false);
    }
}
