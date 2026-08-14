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

public class TreeFellingHandler {
	private static final BlockPos[] ALL_DIRECTIONS = {
			BlockPos.ZERO.north(), BlockPos.ZERO.south(), BlockPos.ZERO.east(), BlockPos.ZERO.west(),
			BlockPos.ZERO.above(), BlockPos.ZERO.below()
	};

	public static void init() {
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			if (!HandyToolsMod.CONFIG.treeFellingEnabled) return;
			if (!(player instanceof ServerPlayer serverPlayer)) return;
			if (!(world instanceof ServerLevel serverLevel)) return;

			ItemStack tool = serverPlayer.getMainHandItem();
			if (tool.getItem() != ModItems.TREE_AXE) return;
			if (!state.is(BlockTags.LOGS, s -> true)) return;
			if (tool.isBroken() || tool.isEmpty()) return;
			if (HandyToolsMod.CONFIG.treeFellingRequireRoot && !isRoot(serverLevel, pos)) return;

			fellTree(serverLevel, serverPlayer, pos, state, tool);
		});
	}

	private static boolean isRoot(ServerLevel world, BlockPos pos) {
		BlockState below = world.getBlockState(pos.below());
		return !below.is(BlockTags.LOGS, s -> true);
	}

	private static void fellTree(ServerLevel world, ServerPlayer player, BlockPos origin, BlockState startState, ItemStack tool) {
		Block treeBlock = startState.getBlock();

		List<BlockPos> logs = new ArrayList<>();
		List<BlockPos> leaves = new ArrayList<>();
		Queue<BlockPos> queue = new ArrayDeque<>();
		queue.add(origin);
		int maxBlocks = Math.max(1, HandyToolsMod.CONFIG.treeFellingMaxBlocks);

		while (!queue.isEmpty() && logs.size() + leaves.size() < maxBlocks) {
			BlockPos current = queue.poll();
			for (BlockPos offset : ALL_DIRECTIONS) {
				BlockPos neighbor = current.offset(offset);
				if (logs.contains(neighbor) || leaves.contains(neighbor) || neighbor.equals(origin)) continue;
				if (Math.abs(neighbor.getX() - origin.getX()) > 16 || Math.abs(neighbor.getZ() - origin.getZ()) > 16) continue;

				BlockState neighborState = world.getBlockState(neighbor);
				if (neighborState.isAir()) continue;

				if (neighborState.is(BlockTags.LOGS, s -> true) && neighborState.getBlock() == treeBlock) {
					logs.add(neighbor);
					queue.add(neighbor);
				} else if (neighborState.is(BlockTags.LEAVES, s -> true)) {
					leaves.add(neighbor);
					queue.add(neighbor);
				}
			}
		}

		if (logs.isEmpty()) return;

		for (BlockPos leafPos : leaves) {
			world.destroyBlock(leafPos, false, null, 512);
		}

		int totalMined = 0;
		for (BlockPos logPos : logs) {
			if (tool.isBroken() || tool.isEmpty()) break;

			BlockState logState = world.getBlockState(logPos);
			BlockEntity logEntity = world.getBlockEntity(logPos);

			List<ItemStack> drops = Block.getDrops(logState, world, logPos, logEntity, player, tool);
			world.setBlock(logPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);

			for (ItemStack drop : drops) {
				if (!player.getInventory().add(drop)) {
					world.addFreshEntity(new ItemEntity(world, logPos.getX() + 0.5, logPos.getY() + 0.5, logPos.getZ() + 0.5, drop));
				}
			}

			tool.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
			totalMined++;
		}

		if (totalMined > 0) {
			player.causeFoodExhaustion(0.03F * totalMined);
		}
	}
}
