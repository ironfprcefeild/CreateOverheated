package net.ironf.overheated.laserOptics.Diode;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;

public class DiodeCogInstance extends SingleRotatingInstance<DiodeBlockEntity> {
    public DiodeCogInstance(MaterialManager materialManager, DiodeBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    protected Instancer<RotatingData> getModel() {
        return getRotatingMaterial().getModel(AllPartialModels.ARM_COG, blockEntity.getBlockState());
    }
}
