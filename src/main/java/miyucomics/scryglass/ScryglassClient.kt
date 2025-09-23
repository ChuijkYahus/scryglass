package miyucomics.scryglass

import at.petrak.hexcasting.api.mod.HexConfig.client
import miyucomics.scryglass.ScryglassMain.Companion.DIMENSIONS_CHANNEL
import miyucomics.scryglass.state.ClientManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.client.MinecraftClient

object ScryglassClient : ClientModInitializer {
	var previousWidth = 0
	var previousHeight = 0
	var previousFov = 0

	override fun onInitializeClient() {
		ClientManager.init()
		HudRenderCallback.EVENT.register { drawContext, tickDelta -> ClientManager.render(drawContext, tickDelta) }

		ClientPlayConnectionEvents.JOIN.register { _, _, client -> sendDimensions(client) }

		ClientTickEvents.END_CLIENT_TICK.register { client ->
			if (client.world == null)
				return@register
			if (client.window.scaledWidth == previousWidth && client.window.scaledHeight == previousHeight && client.options.fov.value == previousFov)
				return@register
			sendDimensions(client)
			previousWidth = client.window.scaledWidth
			previousHeight = client.window.scaledHeight
			previousFov = client.options.fov.value
		}
	}

	private fun sendDimensions(client: MinecraftClient) {
		ClientPlayNetworking.send(DIMENSIONS_CHANNEL, PacketByteBufs.create().apply {
			writeInt(client.window.scaledWidth)
			writeInt(client.window.scaledHeight)
			writeInt(client.options.fov.value)
		})
	}
}