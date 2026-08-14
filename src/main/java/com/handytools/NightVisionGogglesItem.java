package com.handytools;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class NightVisionGogglesItem extends Item {
	public NightVisionGogglesItem(Properties properties) {
		super(properties);
	}

	public static boolean isEnabled(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data == null || data.copyTag() == null || !data.copyTag().contains("handytools_disabled");
	}

	public static void setEnabled(ItemStack stack, boolean enabled) {
		CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		CustomData updated = data.update(tag -> {
			if (enabled) {
				tag.remove("handytools_disabled");
			} else {
				tag.putBoolean("handytools_disabled", true);
			}
		});
		stack.set(DataComponents.CUSTOM_DATA, updated);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			boolean enabled = !isEnabled(stack);
			setEnabled(stack, enabled);
			if (!level.isClientSide()) {
				player.sendSystemMessage(
						Component.translatable(enabled
								? "item.handytools.night_vision_goggles.on"
								: "item.handytools.night_vision_goggles.off")
				);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		tooltip.accept(Component.translatable("item.handytools.night_vision_goggles.tooltip"));
		tooltip.accept(Component.translatable(isEnabled(stack)
				? "item.handytools.night_vision_goggles.status.on"
				: "item.handytools.night_vision_goggles.status.off"));
	}
}
