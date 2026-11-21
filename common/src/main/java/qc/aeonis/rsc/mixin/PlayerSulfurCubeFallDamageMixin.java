package qc.aeonis.rsc.mixin;

import qc.aeonis.rsc.SulfurCubeState;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerSulfurCubeFallDamageMixin {
	@Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
	private void rsc$preventFallDamageWhileMounted(double distance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
		Player self = (Player)(Object)this;
		Entity vehicle = self.getVehicle();
		if (vehicle != null && SulfurCubeState.isSulfurCube(vehicle)) {
			self.fallDistance = 0.0F;
			cir.setReturnValue(false);
		}
	}
}
