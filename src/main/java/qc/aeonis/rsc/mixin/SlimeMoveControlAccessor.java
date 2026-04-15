package qc.aeonis.rsc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net.minecraft.world.entity.monster.cubemob.AbstractCubeMob$CubeMobMoveControl")
public interface SlimeMoveControlAccessor {
	@Invoker("setDirection")
	void rsc$setDirection(float yRot, boolean aggressive);

	@Invoker("setWantedMovement")
	void rsc$setWantedMovement(double speed);
}
