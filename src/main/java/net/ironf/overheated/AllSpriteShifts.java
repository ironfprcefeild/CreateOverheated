package net.ironf.overheated;

import com.simibubi.create.Create;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.createmod.catnip.render.SpriteShifter;

public class AllSpriteShifts {

    public static final SpriteShiftEntry OVERHEATED_BURNER_FLAME =
            SpriteShifter.get(  Create.asResource( "block/blaze_burner_flame"),
                    Overheated.asResource("block/blaze_crucible_flame_overheated_scroll"));

    static void init(){}


}
