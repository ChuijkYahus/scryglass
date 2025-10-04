package miyucomics.scryglass.state

import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.asInt
import miyucomics.scryglass.ScryglassMain.Companion.PRIMER_CHANNEL
import miyucomics.scryglass.ScryglassMain.Companion.UPDATE_CHANNEL
import miyucomics.scryglass.ScryglassMain.Companion.VISION_REGISTRY
import miyucomics.scryglass.visions.Vision
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.DrawContext
import net.minecraft.nbt.NbtElement
import net.minecraft.util.Identifier

object ClientManager {
	var frame: MutableMap<Int, Vision> = mutableMapOf()

	fun init() {
		ClientPlayNetworking.registerGlobalReceiver(PRIMER_CHANNEL) { client, _, buf, _ ->
			val nbt = buf.readNbt() ?: return@registerGlobalReceiver
			val frame = mutableMapOf<Int, Vision>()

			val visionNbt = nbt.getList("visions", NbtElement.COMPOUND_TYPE.toInt())
			for (element in visionNbt) {
				val visionNbt = element.asCompound
				val typeId = Identifier(visionNbt.getString("type"))
				val type = VISION_REGISTRY.get(typeId) ?: continue
				val index = visionNbt.getInt("index")
				frame[index] = type.fromNBT(visionNbt)
			}

			client.execute {
				ClientManager.frame = frame
			}
		}

		ClientPlayNetworking.registerGlobalReceiver(UPDATE_CHANNEL) { client, _, buf, _ ->
			val nbt = buf.readNbt() ?: return@registerGlobalReceiver
			val deltaNbt = nbt.getCompound("deltas")

			client.execute {
				for (addedElement in deltaNbt.getList("added", NbtElement.COMPOUND_TYPE.toInt())) {
					val visionNbt = addedElement.asCompound
					val typeId = Identifier(visionNbt.getString("type"))
					val type = VISION_REGISTRY.get(typeId) ?: continue
					val index = visionNbt.getInt("index")
					frame[index] = type.fromNBT(visionNbt)
				}

				for (removedElement in deltaNbt.getList("removed", NbtElement.INT_TYPE.toInt()))
					frame.remove(removedElement.asInt)
			}
		}
	}

	fun render(drawContext: DrawContext, tickDelta: Float) {
		frame.forEach { it.value.render(drawContext, tickDelta) }
	}
}