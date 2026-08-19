package com.umutsever.structurecraft.client.model;

import com.umutsever.structurecraft.StructureCraft;
import com.umutsever.structurecraft.entity.SkateboardEntity;
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

/** Simple cuboid deck + 4 wheels, UV-mapped onto oak planks. */
public class SkateboardModel extends EntityModel<SkateboardEntity> {
    public static final EntityModelLayer LAYER =
            new EntityModelLayer(new Identifier(StructureCraft.MOD_ID, "skateboard"), "main");

    private final ModelPart root;

    public SkateboardModel(ModelPart root) {
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        var root = data.getRoot();
        // Deck: 14 wide (x), 1 tall, 6 deep -> lying flat near ground.
        root.addChild("deck",
                ModelPartBuilder.create().uv(0, 0)
                        .cuboid(-3.0f, -4.0f, -7.0f, 6.0f, 1.0f, 14.0f, new Dilation(0.0f)),
                ModelTransform.pivot(0.0f, 24.0f, 0.0f));
        // Wheels: small cubes at 4 corners.
        float[][] w = {{-2.5f, -5.5f}, {1.5f, -5.5f}, {-2.5f, 4.5f}, {1.5f, 4.5f}};
        for (int i = 0; i < 4; i++) {
            root.addChild("wheel" + i,
                    ModelPartBuilder.create().uv(0, 0)
                            .cuboid(w[i][0], -2.0f, w[i][1], 1.0f, 2.0f, 1.0f, new Dilation(0.0f)),
                    ModelTransform.pivot(0.0f, 24.0f, 0.0f));
        }
        return TexturedModelData.of(data, 16, 16);
    }

    @Override
    public void setAngles(SkateboardEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay,
                       float red, float green, float blue, float alpha) {
        root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
