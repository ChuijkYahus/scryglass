package miyucomics.scryglass.visions

import miyucomics.scryglass.ScryglassMain.Companion.VISION_REGISTRY
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis

abstract class Vision(val type: VisionType<out Vision>) {
	var scale: Float = 1f
	var rotation: Float = 0f

	fun render(drawContext: DrawContext, deltaTime: Float) {
		val matrices = drawContext.matrices
		matrices.push()
		matrices.translate(MinecraftClient.getInstance().window.scaledWidth / 2.0, MinecraftClient.getInstance().window.scaledHeight / 2.0, 0.0)
		matrices.scale(scale, scale, scale)
		matrices.multiply(RotationAxis.NEGATIVE_Z.rotation(rotation * 6.283f))
		renderCustom(matrices, drawContext, deltaTime)
		matrices.pop()
	}

	fun writeToCompound(compound: NbtCompound) {
		compound.putFloat("scale", this.scale)
		compound.putFloat("rotation", this.rotation)
		writeNBTCustom(compound)
	}

	fun writeToBuf(buf: PacketByteBuf) {
		buf.writeIdentifier(this.type.identifier)
		buf.writeFloat(this.scale)
		buf.writeFloat(this.rotation)
		writeBufCustom(buf)
	}

	abstract fun renderCustom(matrices: MatrixStack, drawContext: DrawContext, deltaTime: Float)

	abstract fun writeNBTCustom(compound: NbtCompound)
	abstract fun readNBTCustom(compound: NbtCompound)

	abstract fun writeBufCustom(buf: PacketByteBuf)
	abstract fun readBufCustom(buf: PacketByteBuf)

	companion object {
		fun createFromNbt(compound: NbtCompound) = VISION_REGISTRY.get(Identifier(compound.getString("type")))!!.create().apply {
			this.scale = compound.getFloat("scale")
			this.rotation = compound.getFloat("rotation")
			this.readNBTCustom(compound)
		}

		fun createFromBuf(buf: PacketByteBuf) = VISION_REGISTRY.get(buf.readIdentifier())!!.create().apply {
			this.scale = buf.readFloat()
			this.rotation = buf.readFloat()
			this.readBufCustom(buf)
		}
	}
}

fun <T> visionType(factory: (VisionType<T>) -> T, identifier: Identifier): VisionType<T> where T : Vision {
	return object : VisionType<T> {
		override val identifier = identifier
		override fun create() = factory(this)
	}
}

interface VisionType<T : Vision> {
	val identifier: Identifier
	fun create(): T
}