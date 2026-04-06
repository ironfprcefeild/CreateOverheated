package net.ironf.overheated.ponder.scenes;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

public class SolarPanelScenes {

    public static void sceneOne(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("solar_panel_one", "Heating with Solar Panels");
        scene.scaleSceneView(.9f);
        scene.world().showSection(util.select().fromTo(0,0,0,5,0,5),Direction.UP);
        scene.idle(5);
    }

    public static void sceneTwo(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("solar_panel_two", "Heating Diodes with Solar Panels");
        scene.scaleSceneView(.9f);
        scene.world().showSection(util.select().fromTo(0,0,0,5,0,5),Direction.UP);
        scene.idle(5);
    }
}
