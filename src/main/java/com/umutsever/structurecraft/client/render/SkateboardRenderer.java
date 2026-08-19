package com.umutsever.structurecraft.client.render;

import com.umutsever.structurecraft.client.model.SkateboardModel;
import com.umutsever.structurecraft.entity.SkateboardEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class SkateboardRenderer extends EntityRenderer<SkateboardEntity> {
    private static final Identifier TEXTURE = new Identifier("minecraft", "textures/block/oak_planks.png");

    private final SkateboardModel model;

    public SkateboardRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new SkateboardModel(ctx.getPart(SkateboardModel.LAYER));
        this.shadowRadius = 0.4f;
    }

    @Override
    public void render(SkateboardEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(0.0, 1.45, 0.0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
        // Trick animation: full 360 roll around the travel axis.
        float trick = entity.getTrickProgress(tickDelta);
        if (trick > 0f) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(trick * 360f));
        }
        matrices.scale(-1.0f, -1.0f, 1.0f);
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
        model.render(matrices, buffer, light, 0x0, 1f, 1f, 1f, 1f);
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(SkateboardEntity entity) {
        return TEXTURE;
    }
}
