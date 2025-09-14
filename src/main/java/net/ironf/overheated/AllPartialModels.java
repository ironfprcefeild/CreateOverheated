package net.ironf.overheated;

import com.simibubi.create.Create;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class AllPartialModels {

    public static final PartialModel
        IMPACT_DRILL_HEAD = block("impact_drill_head");

    private static PartialModel block(String path) {
        return PartialModel.of(Overheated.asResource("block/" + path));
    }
    public static void init() {
        // init static fields
    }

}
