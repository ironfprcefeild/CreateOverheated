package net.ironf.overheated.utility.registration;

import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;
import net.ironf.overheated.Overheated;

public class AllSpriteShifts {

    public static final CTSpriteShiftEntry
            PRESSURIZED_CASING = omni("pressurized_casing"),
            LASER_CASING = omni("laser_casing"),
            HEATED_GEOTHERMAL_VENT = omni("heated_geothermal_vent"),
            SUPERHEATED_GEOTHERMAL_VENT = omni("superheated_geothermal_vent");

    public static CTSpriteShiftEntry omni(String name) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
    }
    public static CTSpriteShiftEntry horizontal(String name) {
        return getCT(AllCTTypes.HORIZONTAL_KRYPPERS, name);
    }
    private static CTSpriteShiftEntry vertical(String name) {
        return getCT(AllCTTypes.VERTICAL, name);
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(type, Overheated.asResource("block/" + blockTextureName), Overheated.asResource("block/" + connectedTextureName + "_connected"));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
        return getCT(type, blockTextureName, blockTextureName);
    }

}
