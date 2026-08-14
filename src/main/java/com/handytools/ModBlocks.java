package com.handytools;

import com.handytools.block.ExpTankBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModBlocks {
	public static final Block EXP_TANK = new ExpTankBlock();

	public static void init() {
		Registry.register(BuiltInRegistries.BLOCK, HandyToolsMod.id("exp_tank"), EXP_TANK);
		Registry.register(BuiltInRegistries.ITEM, HandyToolsMod.id("exp_tank"), new BlockItem(EXP_TANK,
				new Item.Properties().setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), HandyToolsMod.id("exp_tank")))));
	}
}
