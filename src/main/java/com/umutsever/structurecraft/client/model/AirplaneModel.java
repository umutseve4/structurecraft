package com.umutsever.structurecraft.client.model;

import com.umutsever.structurecraft.StructureCraft;
import com.umutsever.structurecraft.entity.AirplaneEntity;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/** Blocky plane: fuselage, wings, tail fin, tail wing. Iron block texture. */
public class AirplaneModel extends EntityModel<AirplaneEntity> {
    public static final EntityModelLayer LAYER =
            new EntityModelLayer(new Identifier(StructureCraft.MOD_ID, "airplane"), "main");

    private final ModelPart root;

    public AirplaneModel(ModelPart root) {
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        var root = data.getRoot();
        // Fuselage along +Z (forward): 8 wide, 8 tall, 36 long.
        root.addChild("fuselage",
                ModelPartBuilder.create().uv(0, 0)
                        .cuboid(-4.0f, -12.0f, -18.0f, 8.0f, 8.0f, 36.0f, new Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));
        // Main wings.
        root.addChild("wings",
                ModelPartBuilder.create().uv(0, 0)
                        .cuboid(-28.0f, -10.0f, -4.0f, 56.0f, 2.0f, 10.0f, new Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));
        // Tail fin (vertical).
        root.addChild("tail_fin",
                ModelPartBuilder.create().uv(0, 0)
                        .cuboid(-1.0f, -20.0f, -18.0f, 2.0f, 8.0f, 6.0f, new Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));
        // Tail wing (horizontal).
        root.addChild("tail_wing",
                ModelPartBuilder.create().uv(0, 0)
                        .cuboid(-10.0f, -13.0f, -18.0f, 20.0f, 2.0f, 5.0f, new Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));
        // Propeller block on the nose.
        root.addChild("nose",
                ModelPartBuilder.create().uv(0, 0)
                        .cuboid(-2.0f, -10.0f, 18.0f, 4.0f, 4.0f, 2.0f, new Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));
        return TexturedModelData.of(data, 16, 16);
    }

    @Override
    public void setAngles(AirplaneEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay,
                       float red, float green, float blue, float alpha) {
        root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
