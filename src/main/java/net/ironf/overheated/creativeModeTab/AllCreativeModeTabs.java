package net.ironf.overheated.creativeModeTab;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.decoration.palettes.PalettesCreativeModeTab;
import com.simibubi.create.infrastructure.item.BaseCreativeModeTab;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class AllCreativeModeTabs {
    public static final CreativeModeTab OVERHEATED_TAB = new CreativeModeTab("overheatedtab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(AllItems.BRASS_HAND.get());
        }
    };

    public static void init() {
    }
}
