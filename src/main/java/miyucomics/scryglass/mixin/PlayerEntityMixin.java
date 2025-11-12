package miyucomics.scryglass.mixin;

import miyucomics.scryglass.misc.PlayerEntityMinterface;
import miyucomics.scryglass.misc.ScryglassState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements PlayerEntityMinterface {
	@Unique
	private Vec3d windowSize = Vec3d.ZERO;
	@Unique
	private @NotNull ScryglassState scryglassState = new ScryglassState();

	@Override
	public @NotNull Vec3d getWindowSize() {
		return windowSize;
	}

	@Override
	public void setWindowSize(@NotNull Vec3d size) {
		windowSize = size;
	}

	@Override
	public @NotNull ScryglassState getScryglassState() {
		return scryglassState;
	}

	@Inject(method = "tick", at = @At("RETURN"))
	public void updateClient(CallbackInfo ci) {
		if (!((Entity) (Object) this).getWorld().isClient) {
			//noinspection DataFlowIssue
			scryglassState.push((ServerPlayerEntity) (Object) this);
		}
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
	public void saveData(NbtCompound compound, CallbackInfo ci) {
		compound.put("visions", scryglassState.serialize());
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
	public void readData(NbtCompound compound, CallbackInfo ci) {
		scryglassState = ScryglassState.deserialize(compound.getList("visions", NbtElement.COMPOUND_TYPE));
	}
}