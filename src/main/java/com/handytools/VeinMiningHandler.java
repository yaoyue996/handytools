package com.handytools;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class VeinMiningHandler {
	private static final BlockPos[] DIRECTIONS = {
			BlockPos.ZERO.north(), BlockPos.ZERO.south(), BlockPos.ZERO.east(), BlockPos.ZERO.west(),
			BlockPos.ZERO.above(), BlockPos.ZERO.below()
	};

	public static void init() {
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			if (!HandyToolsMod.CONFIG.veinMiningEnabled) return;
			if (!(player instanceof ServerPlayer serverPlayer)) return;
			if (!(world instanceof ServerLevel serverLevel)) return;

			ItemStack tool = serverPlayer.getMainHandItem();
			if (tool.getItem() != ModItems.VEIN_PICKAXE) return;
			if (HandyToolsMod.CONFIG.veinMiningRequiresSneak && !serverPlayer.isShiftKeyDown()) return;
			if (tool.isBroken() || tool.isEmpty()) return;

			veinMine(serverLevel, serverPlayer, pos, state, blockEntity, tool);
		});
	}

	private static void veinMine(ServerLevel world, ServerPlayer player, BlockPos origin, BlockState originState, BlockEntity originEntity, ItemStack tool) {
		Block targetBlock = originState.getBlock();

		boolean isOre = originState.is(BlockTags.COAL_ORES, s -> true) || originState.is(BlockTags.IRON_ORES, s -> true)
				|| originState.is(BlockTags.COPPER_ORES, s -> true) || originState.is(BlockTags.GOLD_ORES, s -> true)
				|| originState.is(BlockTags.REDSTONE_ORES, s -> true) || originState.is(BlockTags.EMERALD_ORES, s -> true)
				|| originState.is(BlockTags.LAPIS_ORES, s -> true) || originState.is(BlockTags.DIAMOND_ORES, s -> true);
		boolean isLog = originState.is(BlockTags.LOGS, s -> true);
		if (!isOre && !isLog) return;

		List<BlockPos> toMine = new ArrayList<>();
		Queue<BlockPos> queue = new ArrayDeque<>();
		queue.add(origin);
		int maxBlocks = Math.max(1, HandyToolsMod.CONFIG.veinMiningMaxBlocks);

		while (!queue.isEmpty() && toMine.size() < maxBlocks) {
			BlockPos current = queue.poll();
			for (BlockPos offset : DIRECTIONS) {
				BlockPos neighbor = current.offset(offset);
				if (toMine.contains(neighbor) || neighbor.equals(origin)) continue;
				if (Math.abs(neighbor.getX() - origin.getX()) > 12
						|| Math.abs(neighbor.getY() - origin.getY()) > 12
						|| Math.abs(neighbor.getZ() - origin.getZ()) > 12) continue;

				BlockState neighborState = world.getBlockState(neighbor);
				if (neighborState.isAir()) continue;
				if (isOre && neighborState.getBlock() == targetBlock) {
					toMine.add(neighbor);
					queue.add(neighbor);
				} else if (isLog && neighborState.is(BlockTags.LOGS, s -> true) && neighborState.getBlock() == targetBlock) {
					toMine.add(neighbor);
					queue.add(neighbor);
				}
			}
		}

		if (toMine.isEmpty()) return;

		int totalMined = 0;
		for (BlockPos target : toMine) {
			if (tool.isBroken() || tool.isEmpty()) break;

			BlockState targetState = world.getBlockState(target);
			BlockEntity targetEntity = world.getBlockEntity(target);

			List<ItemStack> drops = Block.getDrops(targetState, world, target, targetEntity, player, tool);
			world.setBlock(target, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);

			for (ItemStack drop : drops) {
				if (!player.getInventory().add(drop)) {
					world.addFreshEntity(new ItemEntity(world, target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, drop));
				}
			}

			tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
			totalMined++;
		}

		if (totalMined > 0) {
			player.causeFoodExhaustion(0.05F * totalMined);
		}
	}
}
