package qc.aeonis.rsc.mixin;

import qc.aeonis.rsc.SulfurCubeState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.cubemob.SulfurCube;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SulfurCube.class)
public abstract class SulfurCubeMovementMixin {
	@Inject(method = "customServerAiStep", at = @At("TAIL"))
	private void rsc$applyRiderMovement(ServerLevel serverLevel, CallbackInfo ci) {
		SulfurCube self = (SulfurCube)(Object)this;
		if (!SulfurCubeState.isSulfurCube(self)) {
			return;
		}

		Entity passenger = self.getFirstPassenger();
		if (!(passenger instanceof Player player) || !SulfurCubeState.isOwnedBy(self, player)) {
			return;
		}

		if (!(self.getMoveControl() instanceof SlimeMoveControlAccessor controlAccessor)) {
			return;
		}

		float forward = player.zza;
		float strafe = player.xxa;
		float inputMagnitude = Math.max(Math.abs(forward), Math.abs(strafe));
		if (inputMagnitude <= 0.01F) {
			controlAccessor.rsc$setWantedMovement(0.0);
			return;
		}

		float yawOffset = 0.0F;
		if (Math.abs(strafe) > 0.01F) {
			if (Math.abs(forward) > 0.01F) {
				yawOffset = strafe > 0.0F ? (forward > 0.0F ? -45.0F : -135.0F) : (forward > 0.0F ? 45.0F : 135.0F);
			} else {
				yawOffset = strafe > 0.0F ? -90.0F : 90.0F;
			}
		}

		controlAccessor.rsc$setDirection(player.getYRot() + yawOffset, false);
		controlAccessor.rsc$setWantedMovement(1.0 + 0.35 * inputMagnitude);
	}
}
