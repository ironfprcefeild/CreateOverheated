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

public class GoggleHelper {

    public static void heatTooltip(List<Component> tooltip, HeatData h){


        if (h != HeatData.empty()) {
            tooltip.add(addIndent(Component.translatable("coverheated.tooltip.heat_info")
                    .withStyle(GRAY)));
            tooltip.add(addIndent(Component.translatable("coverheated.tooltip.heat")
                    .append(easyFloat(h.Heat))
                    .withStyle(ChatFormatting.RED),1));
            tooltip.add(addIndent(Component.translatable("coverheated.tooltip.superheat")
                    .append(easyFloat(h.SuperHeat))
                    .withStyle(ChatFormatting.RED),1));
            tooltip.add(addIndent(Component.translatable("coverheated.tooltip.overheat")
                    .append(easyFloat(h.OverHeat))
                    .withStyle(ChatFormatting.RED),1));
            tooltip.add(addIndent(Component.translatable("coverheated.tooltip.laser_power")
                    .append(easyFloat(h.Volatility))
                    .withStyle(ChatFormatting.RED),1));



        } else {
            tooltip.add(addIndent(Component.translatable("coverheated.tooltip.no_heat")
                    .withStyle(GRAY)));
        }



    }




    public static String easyFloat(float num){
        int compare = (int) num;
        if (num == compare){
            return String.valueOf(compare);
        } else {
            return String.valueOf(num);
        }
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
}

