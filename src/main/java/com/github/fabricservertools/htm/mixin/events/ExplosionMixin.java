package com.github.fabricservertools.htm.mixin.events;

import com.github.fabricservertools.htm.events.BlockExplodeCallback;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
	@Mutable
	@Shadow @Final private ObjectArrayList<BlockPos> affectedBlocks;

	@Shadow @Final private ExplosionBehavior behavior;

	@Shadow @Final private World world;

	@Shadow @Final private float power;

	@Inject(method = "affectWorld", at = @At(value = "NEW", target = "it/unimi/dsi/fastutil/objects/ObjectArrayList"))
	private void HTMExplosionProtectionCheck(boolean bl, CallbackInfo ci) {
		this.affectedBlocks.removeIf(pos -> {
			return BlockExplodeCallback.EVENT.invoker().explode(this.behavior, (Explosion) (Object) this, this.world, pos, this.world.getBlockState(pos), this.power) != ActionResult.PASS;
		});

	}
}
