package com.handytools;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record SortInventoryPayload() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SortInventoryPayload> TYPE =
			new CustomPacketPayload.Type<>(HandyToolsMod.id("sort_inventory"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SortInventoryPayload> CODEC =
			StreamCodec.unit(new SortInventoryPayload());

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void receive(SortInventoryPayload payload, ServerPlayNetworking.Context context) {
		ServerPlayer player = context.player();
		if (!HandyToolsMod.CONFIG.sortInventoryEnabled) return;

		List<ItemStack> toSort = new ArrayList<>();
		for (int i = 9; i < 36; i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if (!stack.isEmpty()) {
				toSort.add(stack.copy());
				player.getInventory().setItem(i, ItemStack.EMPTY);
			}
		}

		toSort.sort(Comparator
				.comparingInt(SortInventoryPayload::itemId)
				.thenComparingInt(ItemStack::getCount));

		int index = 9;
		for (ItemStack stack : toSort) {
			player.getInventory().setItem(index, stack);
			index++;
		}

		player.getInventory().setChanged();
		player.inventoryMenu.broadcastChanges();
	}

	private static int itemId(ItemStack stack) {
		return BuiltInRegistries.ITEM.getId(stack.getItem());
	}
}
