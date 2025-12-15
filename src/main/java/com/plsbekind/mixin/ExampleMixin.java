package com.plsbekind.mixin;

import com.plsbekind.storage.EternalIceStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IceBlock.class)
public class ExampleMixin {
	@Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
	private void preventEternalIceMelting(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
		EternalIceStorage storage = EternalIceStorage.get(world);
		if (storage.isEternalIce(pos)) {
			// Cancel the random tick that would cause the ice to melt
			ci.cancel();
		}
	}
}