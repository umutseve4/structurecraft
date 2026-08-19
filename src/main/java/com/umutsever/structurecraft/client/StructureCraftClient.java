package com.umutsever.structurecraft.client;

import com.umutsever.structurecraft.client.model.AirplaneModel;
import com.umutsever.structurecraft.client.model.SkateboardModel;
import com.umutsever.structurecraft.client.render.AirplaneRenderer;
import com.umutsever.structurecraft.client.render.SkateboardRenderer;
import com.umutsever.structurecraft.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class StructureCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(SkateboardModel.LAYER, SkateboardModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(AirplaneModel.LAYER, AirplaneModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.SKATEBOARD, SkateboardRenderer::new);
        EntityRendererRegistry.register(ModEntities.AIRPLANE, AirplaneRenderer::new);
    }
}
