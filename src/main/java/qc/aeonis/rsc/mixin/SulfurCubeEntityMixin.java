package qc.aeonis.rsc.mixin;

import qc.aeonis.rsc.SulfurCubeState;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SulfurCube.class)
public abstract class SulfurCubeEntityMixin {
	@Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
	private void rsc$onMobInteract(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
		SulfurCube self = (SulfurCube)(Object)this;
		if (!SulfurCubeState.isSulfurCube(self)) {
			return;
		}

		ItemStack stack = player.getItemInHand(interactionHand);
		if (stack.is(Items.SLIME_BALL)) {
			if (self.isBaby()) {
				if (!self.level().isClientSide()) {
					stack.consume(1, player);
					self.setBaby(false);
				}
				cir.setReturnValue(self.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER);
				return;
			}

			if (!SulfurCubeState.isTamed(self)) {
				if (!self.level().isClientSide()) {
					stack.consume(1, player);
					SulfurCubeState.setTamed(self, true);
					SulfurCubeState.setOwnerUuid(self, player.getUUID());
					self.level().broadcastEntityEvent(self, (byte)7);
				}
				cir.setReturnValue(self.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER);
				return;
			}
		}

		if (!stack.isEmpty() || !SulfurCubeState.isTamed(self) || !SulfurCubeState.isOwnedBy(self, player)) {
			return;
		}

		if (!self.level().isClientSide()) {
			if (!SulfurCubeState.isPetted(self)) {
				SulfurCubeState.setPetted(self, true);
				self.playSound(SoundEvents.FOX_SNIFF, 1.0F, 1.0F);
			}

			if (self.getFirstPassenger() == null && !player.isPassenger()) {
				player.startRiding(self, true, true);
			}
		}

		cir.setReturnValue(self.level().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER);
	}

	@Inject(method = "customServerAiStep", at = @At("TAIL"))
	private void rsc$showFirstEncounterHint(ServerLevel serverLevel, CallbackInfo ci) {
		SulfurCube self = (SulfurCube)(Object)this;
		if (!SulfurCubeState.isSulfurCube(self) || self.tickCount % 40 != 0) {
			return;
		}

		List<Player> players = serverLevel.getEntitiesOfClass(Player.class, self.getBoundingBox().inflate(8.0), player -> player.isAlive() && !player.isSpectator());
		for (Player player : players) {
			if (!SulfurCubeState.hasSeenRideHint(player)) {
				player.sendSystemMessage(Component.literal("Hint: Use slime balls to tame Sulfur Cubes and ride them!"));
				SulfurCubeState.setSeenRideHint(player, true);
			}
		}
	}
}
