package com.plsbekind;

import com.plsbekind.storage.EternalIceStorage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoIceMelt implements ModInitializer {
	public static final String MOD_ID = "noicemelt";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("NoIceMelt mod initialized!");

		EternalIceStorage.init();

		// Register block placement callback
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (world.isClientSide()) {
				return InteractionResult.PASS;
			}

			ItemStack stack = player.getItemInHand(hand);
			
			LOGGER.info("User placed block: " + stack.getItem() + " " + stack.getItemName());

			// Check if the item is ice and has a custom name
			if (stack.is(Items.ICE) && stack.has(DataComponents.CUSTOM_NAME)) {
				Component customName = stack.get(DataComponents.CUSTOM_NAME);
				LOGGER.info("Custom Name: " + customName);
				if (customName != null && customName.getString().equalsIgnoreCase("eternalice")) {
					LOGGER.info("got the correct item!");
					// Get the position where the ice will be placed
					var blockPos = hitResult.getBlockPos().relative(hitResult.getDirection());
					
					LOGGER.info("Target position: {}", blockPos);
					LOGGER.info("Block at position BEFORE placement: {}", world.getBlockState(blockPos).getBlock());
					
					// the placement will happen after this callback for some fucking reason
					ServerLevel serverWorld = (ServerLevel) world;
					
					// Add a delayed task to verify and register the ice block
					// holy fucking shit why not place it instantly
					new Thread(() -> {
						try {
							// Wait 50ms for the block to be placed
							Thread.sleep(50);
							// Execute on server thread
							serverWorld.getServer().execute(() -> {
								LOGGER.info("Checking block at position AFTER placement (50ms delay): {}", serverWorld.getBlockState(blockPos).getBlock());
								
								if (serverWorld.getBlockState(blockPos).is(Blocks.ICE)) {
									LOGGER.info("Confirmed ice block at {}", blockPos);
									EternalIceStorage storage = EternalIceStorage.get(serverWorld);
									storage.addEternalIce(blockPos);
									LOGGER.info("EternalIce placed at {}", blockPos);
								} else {
									LOGGER.warn("Expected ice at {} but found: {}", blockPos, serverWorld.getBlockState(blockPos).getBlock());
								}
							});
						} catch (InterruptedException e) {
							LOGGER.error("Thread interrupted", e);
						}
					}).start();
				}
			}
			
			return InteractionResult.PASS;
		});

		// Register block break callback to clean up storage
		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
			if (!world.isClientSide() && state.is(Blocks.ICE)) {
				EternalIceStorage storage = EternalIceStorage.get((ServerLevel) world);
				if (storage.isEternalIce(pos)) {
					storage.removeEternalIce(pos);
					LOGGER.info("EternalIce removed from {}", pos);
				}
			}
			return true;
		});
	}
}