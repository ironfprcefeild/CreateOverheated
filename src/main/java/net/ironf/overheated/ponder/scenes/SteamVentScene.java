package net.ironf.overheated.ponder.scenes;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

public class SteamVentScene {

    public static void mainScene(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("steam_vent", "Generating Steam with Vents");
        scene.scaleSceneView(.9f);
        scene.world().showSection(util.select().fromTo(0,0,0,5,0,5),Direction.UP);
        scene.idle(5);

        //Show Boiler
        scene.world().showSection(util.select().fromTo(4,1,4,3,4,3),Direction.DOWN);
        scene.idle(5);
        //Show vents
        scene.world().showSection(util.select().fromTo(2,2,3,2,3,4),Direction.WEST);
        scene.overlay().showText(100)
                .text("Vents can be attached like Steam Engines")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().of(2,3,4));
        scene.idle(100);

        scene.overlay().showText(100)
                .text("They produce steam, which can be pumped out")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().of(2,3,4));
        //Show pipes
        scene.world().showSection(util.select().fromTo(0,1,2,1,4,4),Direction.EAST);
        scene.idle(100);

        scene.overlay().showText(100)
                .text("The pressure of the steam is based on the tier of the boiler")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().of(2,3,4));
        scene.idle(100);

        scene.overlay().showText(60)
                .text("1-6: Low, 7-12: Medium, 13-18: High, 19+ Insane")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().of(2,3,4));
        scene.idle(60);

        scene.overlay().showText(60)
                .text("Insane Pressure requires Overheat to produce")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().of(2,3,4));
        scene.idle(60);

        scene.overlay().showText(100)
                .text("Attaching more vents than the Tier is inefficient.")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().of(2,3,4));
        scene.idle(100);

    }
}
