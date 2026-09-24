package net.ironf.overheated.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(net.createmod.catnip.render.FluidRenderHelper.class)
public class fluidRenderHelperMixin {

    private static void putVertex(VertexConsumer builder, PoseStack ms, float x, float y, float z, int color, float u,
                                  float v, Direction face, int light) {

        Vec3i normal = face.getNormal();
        PoseStack.Pose peek = ms.last();
        /*
        int a = color >> 24 & 0xff;
        int r = color >> 16 & 0xff;
        int g = color >> 8 & 0xff;
        int b = color & 0xff;


         */
        builder.addVertex(peek.pose(), x, y, z)
                .setColor(color)
                .setUv(u, v)
                //.overlayCoords(OverlayTexture.NO_OVERLAY)
                .setUv2(light,light)
                .setNormal(peek, normal.getX(), normal.getY(), normal.getZ());
    }
}
