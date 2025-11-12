package miyucomics.scryglass.misc

import miyucomics.scryglass.ScryglassMain.Companion.PRIMER_CHANNEL
import miyucomics.scryglass.ScryglassMain.Companion.UPDATE_CHANNEL
import miyucomics.scryglass.visions.AbstractVision
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.DrawContext

object ClientManager {
	var frame: MutableMap<Int, AbstractVision> = mutableMapOf()

	fun init() {
		ClientPlayNetworking.registerGlobalReceiver(PRIMER_CHANNEL) { client, _, buf, _ ->
			val reconstructed = mutableMapOf<Int, AbstractVision>()
			val count = buf.readInt()
			for (i in 0 until count)
				reconstructed[buf.readInt()] = AbstractVision.createFromBuf(buf)
			client.execute { frame = reconstructed }
		}

		ClientPlayNetworking.registerGlobalReceiver(UPDATE_CHANNEL) { client, _, buf, _ ->
			val removedIndices = List(buf.readInt()) { buf.readInt() }

			val additionCount = buf.readInt()
			val addedVisions = mutableMapOf<Int, AbstractVision>()
			for (i in 0 until additionCount)
				addedVisions[buf.readInt()] = AbstractVision.createFromBuf(buf)

			client.execute {
				removedIndices.forEach(frame::remove)
				addedVisions.forEach { (index, vision) -> frame[index] = vision }
			}
		}
	}

	fun render(drawContext: DrawContext, tickDelta: Float) {
		frame.forEach { (_, vision) -> vision.render(drawContext, tickDelta) }
	}
}