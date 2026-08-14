package com.handytools;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandyToolsMod implements ModInitializer {
	public static final String MOD_ID = "handytools";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ModConfig CONFIG = ModConfig.load();

	@Override
	public void onInitialize() {
		ModItems.init();
		ModBlocks.init();
		ModBlockEntities.init();

		PayloadTypeRegistry.serverboundPlay().register(SortInventoryPayload.TYPE, SortInventoryPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(SortInventoryPayload.TYPE, SortInventoryPayload::receive);

		VeinMiningHandler.init();
		TreeFellingHandler.init();
		NightVisionHandler.init();
		ExpTankInteractionHandler.init();

		registerCreativeTabs();

		LOGGER.info("[HandyTools] 便捷工具 Mod 已加载！");
	}

	private static void registerCreativeTabs() {
		CreativeModeTabEvents.modifyOutputEvent(vanillaTab("tools_and_utilities")).register(output -> {
			output.accept(new ItemStack(ModItems.VEIN_PICKAXE), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			output.accept(new ItemStack(ModItems.TREE_AXE), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			output.accept(new ItemStack(ModItems.NIGHT_VISION_GOGGLES), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
		});
		CreativeModeTabEvents.modifyOutputEvent(vanillaTab("redstone_blocks")).register(output -> {
			output.accept(new ItemStack(ModBlocks.EXP_TANK), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
		});
	}

	private static ResourceKey<CreativeModeTab> vanillaTab(String path) {
		return ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath("minecraft", path));
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
