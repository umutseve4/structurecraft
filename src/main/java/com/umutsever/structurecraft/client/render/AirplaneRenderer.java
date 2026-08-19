package com.umutsever.structurecraft.client.render;

import com.umutsever.structurecraft.client.model.AirplaneModel;
import com.umutsever.structurecraft.entity.AirplaneEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class AirplaneRenderer extends EntityRenderer<AirplaneEntity> {
    private static final Identifier TEXTURE = new Identifier("minecraft", "textures/block/iron_block.png");

    private final AirplaneModel model;

    public AirplaneRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new AirplaneModel(ctx.getPart(AirplaneModel.LAYER));
        this.shadowRadius = 1.2f;
    }

    @Override
    public void render(AirplaneEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.translate(0.0, 1.9, 0.0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
        float pitch = MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch());
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));
        matrices.scale(-1.0f, -1.0f, 1.0f);
        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
        model.render(matrices, buffer, light, 0x0, 1f, 1f, 1f, 1f);
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(AirplaneEntity entity) {
        return TEXTURE;
    }
}
