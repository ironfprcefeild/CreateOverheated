package net.ironf.overheated;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class AllPartialModels {

    public static final PartialModel
        IMPACT_DRILL_HEAD = block("impact_drill_head"),

        BELLOW_HEAD = block("bellow_head"),

        //TODO make the turbine fans look good
        SMALL_TURBINE_FAN = block("small_turbine_fan"),
        MEDIUM_TURBINE_FAN = block("medium_turbine_fan"),
        LARGE_TURBINE_FAN = block("large_turbine_fan"),

        BLAZE_CRUCIBLE_OVERHEATED = block("blaze_crucible_overheat"),
        BLAZE_OVERHEAT_RODS = block("overheated_rods_small"),
        BLAZE_OVERHEAT_RODS_2 = block("overheated_rods_large");

    private static PartialModel block(String path) {
        return PartialModel.of(Overheated.asResource("block/" + path));
    }
    public static void init() {
        // init static fields
    }

}
