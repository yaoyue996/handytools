package com.handytools;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class NightVisionHandler {
	public static void init() {
		ServerTickEvents.END_SERVER_TICK.register(NightVisionHandler::onServerTick);
	}

	private static void onServerTick(MinecraftServer server) {
		if (!HandyToolsMod.CONFIG.nightVisionGogglesEnabled) return;
		if (server.getTickCount() % 20 != 0) return;

		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
			if (helmet.getItem() != ModItems.NIGHT_VISION_GOGGLES) continue;
			if (!NightVisionGogglesItem.isEnabled(helmet)) continue;

			player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, true, false));
		}
	}
}
