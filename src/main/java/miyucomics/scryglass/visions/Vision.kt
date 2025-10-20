package miyucomics.scryglass.visions

import miyucomics.scryglass.ScryglassMain
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.RotationAxis

abstract class Vision(visionType: VisionType<out Vision>) {
	var scale: Float = 1f
	var rotation: Float = 0f
	val type: VisionType<out Vision> = visionType

	fun render(drawContext: DrawContext, deltaTime: Float) {
		val matrices = drawContext.matrices
		matrices.push()
		matrices.translate(MinecraftClient.getInstance().window.scaledWidth / 2.0, MinecraftClient.getInstance().window.scaledHeight / 2.0, 0.0)
		matrices.scale(scale, scale, scale)
		matrices.multiply(RotationAxis.NEGATIVE_Z.rotation(rotation * 6.283f))
		renderCustom(matrices, drawContext, deltaTime)
		matrices.pop()
	}

	fun toNBT(): NbtCompound {
		val compound = NbtCompound().apply {
			putFloat("scale", scale)
			putFloat("rotation", rotation)
		}
		writeCustomNBT(compound)
		return compound
	}

	open fun readNBT(compound: NbtCompound) {
		scale = compound.getFloat("scale")
		rotation = compound.getFloat("rotation")
		readCustomNBT(compound)
	}

	fun writeToBuf(buf: PacketByteBuf) {
		buf.writeIdentifier(this.type.identifier)
		buf.writeNbt(NbtCompound().apply(::writeCustomNBT))
		buf.writeFloat(this.scale)
		buf.writeFloat(this.rotation)
	}

	protected abstract fun writeCustomNBT(compound: NbtCompound)
	protected abstract fun readCustomNBT(compound: NbtCompound)
	protected abstract fun renderCustom(matrices: MatrixStack, drawContext: DrawContext, deltaTime: Float)

	companion object {
		fun createFromBuf(buf: PacketByteBuf): Vision {
			val type = ScryglassMain.VISION_REGISTRY.get(buf.readIdentifier())
			val instance = type!!.fromNBT(buf.readNbt()!!)
			instance.scale = buf.readFloat()
			instance.rotation = buf.readFloat()
			return instance
		}
	}
}

fun <T> visionType(factory: (VisionType<T>) -> T, identifier: Identifier): VisionType<T> where T : Vision {
	return object : VisionType<T> {
		override val identifier = identifier
		override fun fromNBT(compound: NbtCompound): T {
			val instance = factory(this)
			instance.readNBT(compound)
			return instance
		}
	}
}

interface VisionType<T : Vision> {
	val identifier: Identifier
	fun fromNBT(compound: NbtCompound): T
}