package com.github.fabricservertools.htm;

import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public final class HTMTexts {
    private static final String PREFIX = "text.htm.";

    public static final Component NOT_LOCKABLE = error("error.unlockable");
    public static final Component NOT_LOCKED = error("error.no_lock");
    public static final Component NOT_OWNER = error("error.not_owner");
    public static final Component ALREADY_LOCKED = error("error.already_locked");
    public static final Component INVALID_LOCK_TYPE = error("error.lock_type");
    public static final Component INVALID_FLAG_TYPE = error("error.flag_type");
    public static final Component CANNOT_TRUST_SELF = error("error.trust_self");
    public static final TranslatableTextBuilder ALREADY_TRUSTED = errorBuilder("error.already_trusted");
    public static final TranslatableTextBuilder PLAYER_NOT_TRUSTED = errorBuilder("error.not_trusted");
    public static final Component CONTAINER_LOCKED = error("locked");
    public static final Component OVERRIDING = error("overriding");

    public static final Component CLICK_TO_SELECT = info("select");
    public static final Component DIVIDER = info("divider");
    public static final TranslatableTextBuilder CONTAINER_LOCK_TYPE = infoBuilder("type");
    public static final TranslatableTextBuilder CONTAINER_OWNER = infoBuilder("owner");
    public static final TranslatableTextBuilder CONTAINER_TRUSTED = infoBuilder("trusted");
    public static final TranslatableTextBuilder TRUSTED_GLOBALLY = infoBuilder("trusted.global");
    public static final TranslatableTextBuilder TRUST = infoBuilder("trust");
    public static final TranslatableTextBuilder UNTRUST = infoBuilder("untrust");
    public static final Component GLOBAL = info("global").withStyle(ChatFormatting.BOLD);
    public static final TranslatableTextBuilder CONTAINER_TRANSFER = infoBuilder("transfer");
    public static final Component CONTAINER_UNLOCKED = info("unlocked");
    public static final TranslatableTextBuilder CONTAINER_KEY = infoBuilder("key");
    public static final TranslatableTextBuilder CONTAINER_SET = infoBuilder("set");
    public static final TranslatableTextBuilder CONTAINER_KEY_SET = infoBuilder("key_set");
    public static final TranslatableTextBuilder CONTAINER_OVERRIDE = infoBuilder("override").andThen(text -> text.append(CommonComponents.space()).append(OVERRIDING));
    public static final TranslatableTextBuilder CONTAINER_FLAG_SET = infoBuilder("set_flag");
    public static final TranslatableTextBuilder CONTAINER_FLAG_RESET = infoBuilder("reset_flag");
    public static final TranslatableTextBuilder CONTAINER_FLAG = infoBuilder("flag");
    public static final Component ON = translatable("on")
            .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD);
    public static final Component OFF = translatable("off")
            .withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
    public static final Component TOGGLE_PERSIST_ON = info("persist").append(CommonComponents.SPACE).append(ON);
    public static final Component TOGGLE_PERSIST_OFF = info("persist").append(CommonComponents.SPACE).append(OFF);
    public static final Component TOGGLE_NO_MSG_ON = info("no_msg").append(CommonComponents.SPACE).append(ON);
    public static final Component TOGGLE_NO_MSG_OFF = info("no_msg").append(CommonComponents.SPACE).append(OFF);

    private static MutableComponent error(String key) {
        return errorBuilder(key).apply();
    }

    private static TranslatableTextBuilder errorBuilder(String key) {
        return builder(key, style -> style.withColor(ChatFormatting.RED));
    }
    
    private static MutableComponent info(String key) {
        return infoBuilder(key).apply();
    }

    private static TranslatableTextBuilder infoBuilder(String key) {
        return builder(key, style -> style.withColor(ChatFormatting.AQUA));
    }

    private static TranslatableTextBuilder builder(String key, UnaryOperator<Style> style) {
        return args -> translatable(key, args).withStyle(style);
    }

    private static MutableComponent translatable(String translation, Object... args) {
        return Component.translatable(PREFIX + translation, args);
    }

    @FunctionalInterface
    public interface TranslatableTextBuilder {

        MutableComponent apply(Object... arg);

        default TranslatableTextBuilder andThen(UnaryOperator<MutableComponent> operator) {
            return args -> operator.apply(apply(args));
        }
    }

    private HTMTexts() {}
}
