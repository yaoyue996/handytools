package com.handytools.block;

import com.handytools.HandyToolsMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ExpTankBlock extends Block implements EntityBlock {
	public ExpTankBlock() {
		super(BlockBehaviour.Properties.of()
				.setId(ResourceKey.create(BuiltInRegistries.BLOCK.key(), HandyToolsMod.id("exp_tank")))
				.strength(3.5F, 6.0F)
				.sound(SoundType.GLASS));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ExpTankBlockEntity(pos, state);
	}
}
