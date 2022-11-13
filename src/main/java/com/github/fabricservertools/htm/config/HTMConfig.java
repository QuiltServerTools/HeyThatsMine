package com.github.fabricservertools.htm.config;

import com.github.fabricservertools.htm.HTM;
import com.google.gson.*;
import net.minecraft.util.Identifier;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HTMConfig {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Identifier.class, new IdentifierSerializer())
            .registerTypeAdapter(Identifier.class, new IdentifierDeserializer())
            .setPrettyPrinting()
            .create();
    private static final Gson OLD_GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public boolean canTrustedPlayersBreakChests = false;

    public final Map<String, Boolean> defaultFlags = new HashMap<>();

    public final ArrayList<Identifier> autolockingContainers = new ArrayList<>(Arrays.asList(
            new Identifier("chest"),
            new Identifier("trapped_chest"),
            new Identifier("barrel"),

            new Identifier("furnace"),
            new Identifier("blast_furnace"),
            new Identifier("smoker"),

            new Identifier("shulker_box"),
            new Identifier("white_shulker_box"),
            new Identifier("orange_shulker_box"),
            new Identifier("magenta_shulker_box"),
            new Identifier("light_blue_shulker_box"),
            new Identifier("yellow_shulker_box"),
            new Identifier("lime_shulker_box"),
            new Identifier("pink_shulker_box"),
            new Identifier("gray_shulker_box"),
            new Identifier("light_gray_shulker_box"),
            new Identifier("cyan_shulker_box"),
            new Identifier("purple_shulker_box"),
            new Identifier("blue_shulker_box"),
            new Identifier("brown_shulker_box"),
            new Identifier("green_shulker_box"),
            new Identifier("red_shulker_box"),
            new Identifier("black_shulker_box")
    ));

    public HTMConfig() {
        defaultFlags.put("hoppers", true);
    }

    public static HTMConfig loadConfig(File file) {
        HTMConfig config;

        if (file.exists() && file.isFile()) {
            try (
                    FileInputStream fileInputStream = new FileInputStream(file);
                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
            ) {
                config = GSON.fromJson(bufferedReader, HTMConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("[HTM] Failed to load config", e);
            }
        } else {
            config = new HTMConfig();
        }

        config.saveConfig(file);

        return config;
    }

    public void saveConfig(File config) {
        try (
                FileOutputStream stream = new FileOutputStream(config);
                Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)
        ) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            HTM.LOGGER.error("Failed to save config");
        }
    }

    public static class IdentifierSerializer implements JsonSerializer<Identifier> {
        @Override
        public JsonElement serialize(Identifier src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    public static class IdentifierDeserializer implements JsonDeserializer<Identifier> {
        @Override
        public Identifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonObject()) {
                return OLD_GSON.fromJson(json, Identifier.class);
            }

            return Identifier.tryParse(json.getAsString());
        }
    }
}
