package net.ironf.overheated.batteries.charger;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleRotatingVisual;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ChargerShaftInstance extends SingleRotatingVisual<ChargerBlockEntity> {
    public ChargerShaftInstance(VisualizationContext context, ChargerBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
    }
    @Override
    protected Model model() {
        return Models.partial(AllPartialModels.SHAFT_HALF, Direction.UP);
    }

}
