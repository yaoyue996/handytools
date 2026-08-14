package com.handytools;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class ModItems {
	public static final Item VEIN_PICKAXE = new Item(
			properties("vein_pickaxe")
					.durability(780)
					.pickaxe(ToolMaterial.DIAMOND, 1.0F, -2.8F)
	);

	public static final Item TREE_AXE = new Item(
			properties("tree_axe")
					.durability(125)
					.axe(ToolMaterial.IRON, 6.0F, -3.1F)
	);

	public static final Item NIGHT_VISION_GOGGLES = new NightVisionGogglesItem(
			properties("night_vision_goggles")
					.stacksTo(1)
					.equippable(EquipmentSlot.HEAD)
	);

	public static void init() {
		register("vein_pickaxe", VEIN_PICKAXE);
		register("tree_axe", TREE_AXE);
		register("night_vision_goggles", NIGHT_VISION_GOGGLES);
	}

	public static Item.Properties properties(String name) {
		return new Item.Properties().setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), HandyToolsMod.id(name)));
	}

	private static void register(String name, Item item) {
		Registry.register(BuiltInRegistries.ITEM, HandyToolsMod.id(name), item);
	}
}
