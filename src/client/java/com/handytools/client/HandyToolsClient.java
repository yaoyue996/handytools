package com.handytools.client;

import com.handytools.HandyToolsMod;
import com.handytools.SortInventoryPayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class HandyToolsClient implements ClientModInitializer {
	public static KeyMapping sortInventoryKey;

	@Override
	public void onInitializeClient() {
		sortInventoryKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
				"key.handytools.sort_inventory",
				InputConstants.Type.KEYSYM,
				GLFW.GLFW_KEY_R,
				KeyMapping.Category.register(HandyToolsMod.id("handytools"))
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (sortInventoryKey.consumeClick()) {
				ClientPlayNetworking.send(new SortInventoryPayload());
			}
		});
	}
}
