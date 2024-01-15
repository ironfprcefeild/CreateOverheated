package net.ironf.overheated;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.ironf.overheated.creativeModeTab.AllCreativeModeTabs;
import net.ironf.overheated.laserOptics.backend.beam.BeamBlock;
import net.ironf.overheated.laserOptics.backend.beam.laserCyst.LaserCystBlock;
import net.ironf.overheated.laserOptics.backend.beam.laserCyst.LaserCystBlockEntity;
import net.ironf.overheated.laserOptics.blazeCrucible.BlazeCrucibleBlock;
import net.ironf.overheated.steamworks.blocks.steamVent.steamVentBlock;
import net.ironf.overheated.steamworks.blocks.turbine.multiblock.turbineBlock;
import net.ironf.overheated.steamworks.blocks.turbine.multiblock.turbineBlockCTBehavior;
import net.ironf.overheated.steamworks.blocks.turbine.multiblock.turbineItem;
import net.ironf.overheated.steamworks.blocks.turbine.turbineInterface.turbineInterfaceBlock;
import net.ironf.overheated.steamworks.blocks.turbine.turbineShaft.turbineShaftBlock;
import net.ironf.overheated.steamworks.blocks.turbine.turbineVent.turbineVentBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.client.model.generators.ConfiguredModel;

import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static net.ironf.overheated.Overheated.REGISTRATE;

public class AllBlocks {


    static {
        Overheated.REGISTRATE.creativeModeTab(() -> AllCreativeModeTabs.OVERHEATED_TAB);
    }
    ////Steam Works
    public static final BlockEntry<steamVentBlock> STEAM_VENT = REGISTRATE
            .block("steam_vent", steamVentBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();

    //Turbine

    public static final BlockEntry<turbineBlock> TURBINE = REGISTRATE.block("turbine", turbineBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.color(MaterialColor.TERRACOTTA_ORANGE)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .explosionResistance(1100))
            .transform(pickaxeOnly())
            .blockstate((c, p) -> p.getVariantBuilder(c.get())
                    .forAllStates(s -> ConfiguredModel.builder()
                            .modelFile(AssetLookup.standardModel(c, p))
                            .rotationY(s.getValue(turbineBlock.HORIZONTAL_AXIS) == Direction.Axis.X ? 90 : 0)
                            .build()))
            .onRegister(connectedTextures(turbineBlockCTBehavior::new))
            .item(turbineItem::new)
            .build()
            .register();

    //Turbine Interface
    public static final BlockEntry<turbineInterfaceBlock> TURBINE_INTERFACE = REGISTRATE
            .block("turbine_interface", turbineInterfaceBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();
    //Turbine Shaft
    public static final BlockEntry<turbineShaftBlock> TURBINE_SHAFT = REGISTRATE
            .block("turbine_shaft", turbineShaftBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();

    //Turbine Vent
    public static final BlockEntry<turbineVentBlock> TURBINE_VENT = REGISTRATE
            .block("turbine_vent", turbineVentBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();

    //// Lazer Optics

    //Beam
    public static final BlockEntry<BeamBlock> BEAM = REGISTRATE
            .block("laser_beam", BeamBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.METAL))
            .register();

    //Cyst
    public static final BlockEntry<LaserCystBlock> LASER_CYST = REGISTRATE
            .block("laser_beam", LaserCystBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.color(MaterialColor.METAL))
            .register();
    //Blaze Crucible

    public static final BlockEntry<BlazeCrucibleBlock> BLAZE_CRUCIBLE = REGISTRATE
            .block("blaze_crucible", BlazeCrucibleBlock::new)
            .initialProperties(SharedProperties::netheriteMetal)
            .properties(p -> p.color(MaterialColor.METAL))
            .simpleItem()
            .register();
    public static void register(){

    }

}
