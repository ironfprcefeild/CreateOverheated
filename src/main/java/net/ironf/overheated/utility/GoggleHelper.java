package net.ironf.overheated.utility;

import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
import joptsimple.internal.Strings;
import net.ironf.overheated.Overheated;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.WHITE;

public class GoggleHelper {



    public static void heatTooltip(List<Component> tooltip, HeatData h, HeatDisplayType hdt ){
        String heatDisplayKey = "coverheated.tooltip.display_type." + switch (hdt){
            case EMIT -> "emit";
            case ABSORB -> "absorb";
            case READING -> "reading";
            case SUPPLYING -> "supplying";
        };

        tooltip.add(addIndent(Component.translatable("coverheated.tooltip.heat_info")
                .append(Component.translatable(heatDisplayKey))
                .withStyle(WHITE)));
        int displayUpToLevel = (h.Heat > 0 ? 1 : 0) + (h.SuperHeat > 0 ? 2 : 0) + (h.OverHeat > 0 ? 4 : 0);
        if (displayUpToLevel == 0) {
            tooltip.add(addIndent(Component.translatable("coverheated.tooltip.no_heat")
                    .withStyle(GRAY),1));
        } else {
            tooltip.add(addIndent(Component.translatable("coverheated.tooltip.heat")
                    .append(easyFloat(h.Heat))
                    .withStyle(ChatFormatting.RED), 1));
            if (displayUpToLevel >= 2) {
                tooltip.add(addIndent(Component.translatable("coverheated.tooltip.superheat")
                        .append(easyFloat(h.SuperHeat))
                        .withStyle(ChatFormatting.RED), 1));
            }
            if (displayUpToLevel >= 4) {
                tooltip.add(addIndent(Component.translatable("coverheated.tooltip.overheat")
                        .append(easyFloat(h.OverHeat))
                        .withStyle(ChatFormatting.RED), 1));
            }
        }
    }


    public static void newLine(List<Component> tooltip){
        tooltip.add(Component.literal(""));
    }

    public static String easyFloat(float num){
        String[] splits = String.valueOf(num).split("\\.");
        return splits[0] + (splits[1].toCharArray()[0] != '0' ? "." + splits[1].toCharArray()[0] : "");
    }

    public static void addIndents(List<Component> prep, int indents){
        for (Component c : prep){
            c = addIndent(c,indents);
        }
    }
    public static Component addIndent(Component prep, int indents){
        return Component.literal(Strings.repeat(' ', 4 + indents)).append(prep);
    }
    public static Component addIndent(Component prep){
        return addIndent(prep,0);
    }
    public static Component addIndent(String key){
        return addIndent(Component.translatable(key),0);
    }

}

