package com.handytools;

import com.handytools.block.ExpTankBlockEntity;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

public class ExpTankInteractionHandler {
	public static void init() {
		UseBlockCallback.EVENT.register(ExpTankInteractionHandler::onUseBlock);
	}

	private static InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		BlockPos pos = hit.getBlockPos();
		if (level.getBlockState(pos).getBlock() != ModBlocks.EXP_TANK) {
			return InteractionResult.PASS;
		}

		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (!(blockEntity instanceof ExpTankBlockEntity tank)) {
			return InteractionResult.PASS;
		}

		if (!HandyToolsMod.CONFIG.experienceTankEnabled) {
			player.sendSystemMessage(Component.translatable("block.handytools.exp_tank.disabled"));
			return InteractionResult.FAIL;
		}

		if (player.isShiftKeyDown()) {
			int amount = tank.removeAllExperience();
			if (amount > 0) {
				player.giveExperiencePoints(amount);
				player.sendOverlayMessage(Component.translatable("block.handytools.exp_tank.withdraw", amount, tank.getExperience()));
			} else {
				player.sendOverlayMessage(Component.translatable("block.handytools.exp_tank.empty"));
			}
		} else {
			int toStore = Math.min(player.totalExperience, tank.getFreeSpace());
			if (toStore > 0) {
				player.giveExperiencePoints(-toStore);
				tank.addExperience(toStore);
				player.sendOverlayMessage(Component.translatable("block.handytools.exp_tank.deposit", toStore, tank.getExperience()));
			} else {
				player.sendOverlayMessage(Component.translatable("block.handytools.exp_tank.no_exp"));
			}
		}
		return InteractionResult.SUCCESS;
	}
}

