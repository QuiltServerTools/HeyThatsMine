package com.github.fabricservertools.htm.mixin.events;

import com.github.fabricservertools.htm.events.EnderDragonBreakBlockCallback;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnderDragonEntity.class)
public abstract class EnderDragonEntityMixin {
	@Redirect(method = "destroyBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
	private boolean mobTick(World world, BlockPos pos, boolean move) {
		ActionResult result = EnderDragonBreakBlockCallback.EVENT.invoker().blockBreak(world, pos, move);

		if (result != ActionResult.PASS) {
			return false;
		}

		return world.removeBlock(pos, move);
	}
}
