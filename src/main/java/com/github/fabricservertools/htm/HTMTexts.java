package com.github.fabricservertools.htm;

import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.UnaryOperator;

public final class HTMTexts {
    private static final String PREFIX = "text.htm.";

    public static final Text NOT_LOCKABLE = error("error.unlockable");
    public static final Text NOT_LOCKED = error("error.no_lock");
    public static final Text NOT_OWNER = error("error.not_owner");
    public static final Text ALREADY_LOCKED = error("error.already_locked");
    public static final Text INVALID_LOCK_TYPE = error("error.lock_type");
    public static final Text INVALID_FLAG_TYPE = error("error.flag_type");
    public static final Text CANNOT_TRUST_SELF = error("error.trust_self");
    public static final TranslatableTextBuilder ALREADY_TRUSTED = errorBuilder("error.already_trusted");
    public static final TranslatableTextBuilder PLAYER_NOT_TRUSTED = errorBuilder("error.not_trusted");
    public static final Text CONTAINER_LOCKED = error("locked");
    public static final Text OVERRIDING = error("overriding");

    public static final Text CLICK_TO_SELECT = info("select");
    public static final Text DIVIDER = info("divider");
    public static final TranslatableTextBuilder CONTAINER_LOCK_TYPE = infoBuilder("type");
    public static final TranslatableTextBuilder CONTAINER_OWNER = infoBuilder("owner");
    public static final TranslatableTextBuilder CONTAINER_TRUSTED = infoBuilder("trusted");
    public static final TranslatableTextBuilder TRUSTED_GLOBALLY = infoBuilder("trusted.global");
    public static final TranslatableTextBuilder TRUST = infoBuilder("trust");
    public static final TranslatableTextBuilder UNTRUST = infoBuilder("untrust");
    public static final Text GLOBAL = info("global").formatted(Formatting.BOLD);
    public static final TranslatableTextBuilder CONTAINER_TRANSFER = infoBuilder("transfer");
    public static final Text CONTAINER_UNLOCKED = info("unlocked");
    public static final TranslatableTextBuilder CONTAINER_KEY = infoBuilder("key");
    public static final TranslatableTextBuilder CONTAINER_SET = infoBuilder("set");
    public static final TranslatableTextBuilder CONTAINER_KEY_SET = infoBuilder("key_set");
    public static final TranslatableTextBuilder CONTAINER_OVERRIDE = infoBuilder("override").andThen(text -> text.append(ScreenTexts.space()).append(OVERRIDING));
    public static final TranslatableTextBuilder CONTAINER_FLAG_SET = infoBuilder("set_flag");
    public static final TranslatableTextBuilder CONTAINER_FLAG_RESET = infoBuilder("reset_flag");
    public static final TranslatableTextBuilder CONTAINER_FLAG = infoBuilder("flag");
    public static final Text ON = translatable("on")
            .formatted(Formatting.GREEN, Formatting.BOLD);
    public static final Text OFF = translatable("off")
            .formatted(Formatting.RED, Formatting.BOLD);
    public static final Text TOGGLE_PERSIST_ON = info("persist").append(ScreenTexts.SPACE).append(ON);
    public static final Text TOGGLE_PERSIST_OFF = info("persist").append(ScreenTexts.SPACE).append(OFF);
    public static final Text TOGGLE_NO_MSG_ON = info("no_msg").append(ScreenTexts.SPACE).append(ON);
    public static final Text TOGGLE_NO_MSG_OFF = info("no_msg").append(ScreenTexts.SPACE).append(OFF);

    private static MutableText error(String key) {
        return errorBuilder(key).apply();
    }

    private static TranslatableTextBuilder errorBuilder(String key) {
        return builder(key, style -> style.withColor(Formatting.RED));
    }
    
    private static MutableText info(String key) {
        return infoBuilder(key).apply();
    }

    private static TranslatableTextBuilder infoBuilder(String key) {
        return builder(key, style -> style.withColor(Formatting.AQUA));
    }

    private static TranslatableTextBuilder builder(String key, UnaryOperator<Style> style) {
        return args -> translatable(key, args).styled(style);
    }

    private static MutableText translatable(String translation, Object... args) {
        return Text.translatable(PREFIX + translation, args);
    }

    @FunctionalInterface
    public interface TranslatableTextBuilder {

        MutableText apply(Object... arg);

        default TranslatableTextBuilder andThen(UnaryOperator<MutableText> operator) {
            return args -> operator.apply(apply(args));
        }
    }

    private HTMTexts() {}
}
