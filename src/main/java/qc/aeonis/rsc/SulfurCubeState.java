package qc.aeonis.rsc;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

public final class SulfurCubeState {
	private static final Identifier SULFUR_CUBE_ID = Identifier.withDefaultNamespace("sulfur_cube");
	private static final String KEY_TAMED = "Tamed";
	private static final String KEY_PETTED = "Petted";
	private static final String KEY_OWNER = "Owner";
	private static final String KEY_HINT_SEEN = "RdscFirstRideHintSeen";

	private SulfurCubeState() {
	}

	public static boolean isSulfurCube(Entity entity) {
		return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).equals(SULFUR_CUBE_ID);
	}

	public static boolean isTamed(Entity entity) {
		return getStateTag(entity).getBoolean(KEY_TAMED).orElse(false);
	}

	public static void setTamed(Entity entity, boolean tamed) {
		setStateFlag(entity, KEY_TAMED, tamed);
	}

	public static boolean isPetted(Entity entity) {
		return getStateTag(entity).getBoolean(KEY_PETTED).orElse(false);
	}

	public static void setPetted(Entity entity, boolean petted) {
		setStateFlag(entity, KEY_PETTED, petted);
	}

	public static boolean isOwnedBy(Entity entity, Player player) {
		Optional<UUID> ownerUuid = getOwnerUuid(entity);
		return ownerUuid.isEmpty() || ownerUuid.get().equals(player.getUUID());
	}

	public static boolean hasSeenRideHint(Player player) {
		return getStateTag(player).getBoolean(KEY_HINT_SEEN).orElse(false);
	}

	public static void setSeenRideHint(Player player, boolean seen) {
		setStateFlag(player, KEY_HINT_SEEN, seen);
	}

	public static void setOwnerUuid(Entity entity, @Nullable UUID ownerUuid) {
		CompoundTag tag = getStateTag(entity);
		if (ownerUuid == null) {
			tag.remove(KEY_OWNER);
		} else {
			tag.putString(KEY_OWNER, ownerUuid.toString());
		}
		setStateTag(entity, tag);
	}

	private static Optional<UUID> getOwnerUuid(Entity entity) {
		return getStateTag(entity).getString(KEY_OWNER).flatMap(string -> {
			try {
				return Optional.of(UUID.fromString(string));
			} catch (IllegalArgumentException ignored) {
				return Optional.empty();
			}
		});
	}

	private static void setStateFlag(Entity entity, String key, boolean value) {
		CompoundTag tag = getStateTag(entity);
		if (value) {
			tag.putBoolean(key, true);
		} else {
			tag.remove(key);
		}
		setStateTag(entity, tag);
	}

	private static CompoundTag getStateTag(Entity entity) {
		return entity.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
	}

	private static void setStateTag(Entity entity, CompoundTag tag) {
		if (tag.isEmpty()) {
			entity.setComponent(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
			return;
		}
		entity.setComponent(DataComponents.CUSTOM_DATA, CustomData.of(tag));
	}
}
