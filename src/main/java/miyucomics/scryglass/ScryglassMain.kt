package miyucomics.scryglass

import miyucomics.scryglass.state.PlayerEntityMinterface
import miyucomics.scryglass.visions.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.fabricmc.fabric.api.event.registry.RegistryAttribute
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry
import net.minecraft.util.Identifier
import net.minecraft.util.math.ColorHelper
import net.minecraft.util.math.Vec3d
import org.joml.Vector3f

class ScryglassMain : ModInitializer {
	override fun onInitialize() {
		ScryglassActions.init()

		registerVisionType(LineVision.TYPE)
		registerVisionType(TextVision.TYPE)
		registerVisionType(RectVision.TYPE)

		ServerPlayNetworking.registerGlobalReceiver(DIMENSIONS_CHANNEL) { _, player, _, buf, _ ->
			(player as PlayerEntityMinterface).setWindowSize(Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()))
			(player as PlayerEntityMinterface).getScryglassState().prime(player)
		}

		ServerPlayerEvents.AFTER_RESPAWN.register { old, new, _ ->
			(old as PlayerEntityMinterface).setWindowSize((new as PlayerEntityMinterface).getWindowSize())
		}
	}

	fun registerVisionType(type: VisionType<*>) {
		Registry.register(VISION_REGISTRY, type.identifier, type)
	}

	companion object {
		val VISION_REGISTRY: SimpleRegistry<VisionType<out Vision>> = FabricRegistryBuilder.createSimple<VisionType<out Vision>>(RegistryKey.ofRegistry(id("visions"))).attribute(RegistryAttribute.MODDED).buildAndRegister()

		fun id(string: String) = Identifier("scryglass", string)
		val DIMENSIONS_CHANNEL = id("dimensions")
		val PRIMER_CHANNEL = id("full_sync")
		val UPDATE_CHANNEL = id("update")

		fun floatVector(vec: Vec3d) = Vector3f(vec.x.toFloat(), vec.y.toFloat(), vec.z.toFloat())
		fun interpretColor(vec: Vec3d) = ColorHelper.Argb.getArgb(255, (vec.x * 255).toInt(), (vec.y * 255).toInt(), (vec.z * 255).toInt())
	}
}