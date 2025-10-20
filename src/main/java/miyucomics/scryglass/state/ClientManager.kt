package miyucomics.scryglass.state

import miyucomics.scryglass.ScryglassMain.Companion.PRIMER_CHANNEL
import miyucomics.scryglass.ScryglassMain.Companion.UPDATE_CHANNEL
import miyucomics.scryglass.visions.Vision
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.gui.DrawContext

object ClientManager {
	var frame: MutableMap<Int, Vision> = mutableMapOf()

	fun init() {
		ClientPlayNetworking.registerGlobalReceiver(PRIMER_CHANNEL) { client, _, buf, _ ->
			val reconstructed = mutableMapOf<Int, Vision>()
			val count = buf.readInt()
			for (i in 0 until count)
				reconstructed[buf.readInt()] = Vision.createFromBuf(buf)
			client.execute { frame = reconstructed }
		}

		ClientPlayNetworking.registerGlobalReceiver(UPDATE_CHANNEL) { client, _, buf, _ ->
			val removedIndices = List(buf.readInt()) { buf.readInt() }

			val additionCount = buf.readInt()
			val addedVisions = mutableMapOf<Int, Vision>()
			for (i in 0 until additionCount)
				addedVisions[buf.readInt()] = Vision.createFromBuf(buf)

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