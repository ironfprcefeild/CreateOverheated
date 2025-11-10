package net.ironf.overheated.utility;

import joptsimple.internal.Strings;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.WHITE;

public class GoggleHelper {


    public static void heatTooltip(List<Component> tooltip, HeatData h, HeatDisplayType hdt){
        heatTooltip(tooltip,"coverheated.tooltip.heat_info",h,hdt,2);
    }

    public static void heatTooltip(List<Component> tooltip, HeatData h, HeatDisplayType hdt, int decimals){
        heatTooltip(tooltip,"coverheated.tooltip.heat_info",h,hdt,decimals);
    }
    public static void heatTooltip(List<Component> tooltip, String headerKey, HeatData h, HeatDisplayType hdt, int decimals){
        String heatDisplayKey = "coverheated.tooltip.display_type." + switch (hdt){
            case EMIT -> "emit";
            case ABSORB -> "absorb";
            case READING -> "reading";
            case SUPPLYING -> "supplying";
        };


        tooltip.add(addIndent(Component.translatable(headerKey)
                .append(Component.translatable(heatDisplayKey))
                .withStyle(WHITE)));

        int displayUpToLevel = (h.Heat > 0 ? 1 : 0) + (h.SuperHeat > 0 ? 2 : 0) + (h.OverHeat > 0 ? 4 : 0);
        if (displayUpToLevel == 0) {
            tooltip.add(addIndent(Component.translatable("coverheated.tooltip.no_heat")
                    .withStyle(GRAY),1));
        } else {
            tooltip.add(addIndent(Component.translatable("coverheated.tooltip.heat")
                    .append(easyFloat(h.Heat,decimals))
                    .withStyle(h.Heat > 0 ? ChatFormatting.RED : GRAY), 1));
            if (displayUpToLevel >= 2) {
                tooltip.add(addIndent(Component.translatable("coverheated.tooltip.superheat")
                        .append(easyFloat(h.SuperHeat,decimals))
                        .withStyle(h.SuperHeat > 0 ? ChatFormatting.BLUE : GRAY), 1));
            }
            if (displayUpToLevel >= 4) {
                tooltip.add(addIndent(Component.translatable("coverheated.tooltip.overheat")
                        .append(easyFloat(h.OverHeat,decimals))
                        .withStyle(h.OverHeat > 0 ? ChatFormatting.LIGHT_PURPLE : GRAY), 1));
            }
        }
    }


    public static void newLine(List<Component> tooltip){
        tooltip.add(Component.literal(""));
    }

    public static String easyFloat(float num){
        return easyFloat(num,2);
    }
    public static String easyFloat(float num, int decimals){
        String s = String.format("%."+ decimals+ "f",num);
        if (s.contains("."+"0".repeat(decimals))){
            return s.split("\\.")[0];
        }
        return s;

        //String[] splits = String.valueOf(num).split("\\.");
        //return splits[0] + (splits[1].toCharArray()[0] != '0' ? "." + splits[1].toCharArray()[0] : "");
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

