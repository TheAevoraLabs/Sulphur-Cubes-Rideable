package qc.aeonis.rsc.mixin;

import qc.aeonis.rsc.SulfurCubeState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class SulfurCubeControllingPassengerMixin {
	@Inject(method = "getControllingPassenger", at = @At("HEAD"), cancellable = true)
	private void rsc$getControllingPassenger(CallbackInfoReturnable<LivingEntity> cir) {
		Entity self = (Entity)(Object)this;
		if (!SulfurCubeState.isSulfurCube(self) || !SulfurCubeState.isTamed(self)) {
			return;
		}

		Entity passenger = self.getFirstPassenger();
		if (passenger instanceof Player player && SulfurCubeState.isOwnedBy(self, player)) {
			cir.setReturnValue(player);
		}
	}
}
