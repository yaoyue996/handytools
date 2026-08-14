package com.handytools.block;

import com.handytools.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ExpTankBlockEntity extends BlockEntity {
	public static final int MAX_EXPERIENCE = 1_000_000;

	private int experience;

	public ExpTankBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.EXP_TANK, pos, state);
	}

	public int getExperience() {
		return experience;
	}

	public int getFreeSpace() {
		return MAX_EXPERIENCE - experience;
	}

	public void setExperience(int experience) {
		this.experience = Math.max(0, Math.min(experience, MAX_EXPERIENCE));
		markForSync();
	}

	public void addExperience(int amount) {
		setExperience(experience + amount);
	}

	public int removeAllExperience() {
		int all = experience;
		experience = 0;
		markForSync();
		return all;
	}

	private void markForSync() {
		setChanged();
		if (level instanceof ServerLevel serverLevel) {
			serverLevel.getChunkSource().blockChanged(getBlockPos());
		}
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		experience = input.getIntOr("Experience", 0);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putInt("Experience", experience);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = super.getUpdateTag(registries);
		tag.putInt("Experience", experience);
		return tag;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
}
