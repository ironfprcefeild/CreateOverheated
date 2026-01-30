package net.ironf.overheated.mixin;

import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.ironf.overheated.Overheated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(com.simibubi.create.compat.jei.category.MysteriousItemConversionCategory.class)
public class mysteriousConversionMixin {

    public void draw(ConversionRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 20);
        AllGuiTextures.JEI_QUESTION_MARK.render(graphics, 77, 5);

        Component explanation = getExplanation(recipe);
        if (explanation != null){
            graphics.drawString(Minecraft.getInstance().font,
                    explanation,
                    5, 39,0xffffff, false);
        }

    }

    public Component getExplanation(ConversionRecipe recipe){
        String outputName = recipe.getResultItem(null).getDescriptionId() + ".conversion_explanation";
        Component explainComponent = Component.translatable(outputName);
        return (!explainComponent.getString().equals(outputName))
                ? explainComponent
                : null;
    }
}
