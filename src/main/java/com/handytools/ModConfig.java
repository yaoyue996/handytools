package com.handytools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public boolean veinMiningEnabled = true;
	public int veinMiningMaxBlocks = 128;
	public boolean veinMiningRequiresSneak = false;

	public boolean treeFellingEnabled = true;
	public int treeFellingMaxBlocks = 512;
	public boolean treeFellingRequireRoot = true;
	public boolean treeFellingCollectLeaves = false;

	public boolean sortInventoryEnabled = true;
	public boolean experienceTankEnabled = true;
	public boolean nightVisionGogglesEnabled = true;

	public static ModConfig load() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve("handytools.json");
		ModConfig config = new ModConfig();
		if (Files.exists(path)) {
			try (Reader reader = Files.newBufferedReader(path)) {
				config = GSON.fromJson(reader, ModConfig.class);
				if (config == null) config = new ModConfig();
			} catch (IOException e) {
				HandyToolsMod.LOGGER.error("读取配置文件失败，使用默认配置", e);
				config = new ModConfig();
			}
		}
		config.save();
		return config;
	}

	public void save() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve("handytools.json");
		try {
			Files.createDirectories(path.getParent());
			try (Writer writer = Files.newBufferedWriter(path)) {
				GSON.toJson(this, writer);
			}
		} catch (IOException e) {
			HandyToolsMod.LOGGER.error("保存配置文件失败", e);
		}
	}
}
