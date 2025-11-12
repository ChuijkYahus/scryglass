package miyucomics.scryglass.visions

import miyucomics.scryglass.ScryglassMain.Companion.id
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import org.joml.Vector3f

class RectVision(visionType: VisionType<RectVision>) : AbstractVision(visionType) {
	private lateinit var position: Vector3f
	private lateinit var size: Vector3f
	private var color: Int = 0

	constructor(position: Vector3f, size: Vector3f, color: Int) : this(TYPE) {
		this.position = position
		this.size = size
		this.color = color
	}

	override fun renderCustom(matrices: MatrixStack, drawContext: DrawContext, deltaTime: Float) {
		val buffer = drawContext.vertexConsumers.getBuffer(RenderLayer.getGuiOverlay())
		val matrix = matrices.peek().positionMatrix

		buffer.vertex(matrix, position.x + size.x, position.y, 0f).color(color).next()
		buffer.vertex(matrix, position.x + size.x, position.y - size.y, 0f).color(color).next()
		buffer.vertex(matrix, position.x, position.y - size.y, 0f).color(color).next()
		buffer.vertex(matrix, position.x, position.y, 0f).color(color).next()

		drawContext.draw()
	}

	override fun writeNBTCustom(compound: NbtCompound) {
		compound.putFloat("x", position.x)
		compound.putFloat("y", position.y)
		compound.putFloat("z", position.z)
		compound.putFloat("width", size.x)
		compound.putFloat("height", size.y)
		compound.putFloat("depth", size.z)
		compound.putInt("color", color)
	}

	override fun readNBTCustom(compound: NbtCompound) {
		position = Vector3f(compound.getFloat("x"), compound.getFloat("y"), compound.getFloat("z"))
		size = Vector3f(compound.getFloat("width"), compound.getFloat("height"), compound.getFloat("depth"))
		color = compound.getInt("color")
	}

	override fun writeBufCustom(buf: PacketByteBuf) {
		buf.writeVector3f(position)
		buf.writeVector3f(size)
		buf.writeInt(color)
	}

	override fun readBufCustom(buf: PacketByteBuf) {
		position = buf.readVector3f()
		size = buf.readVector3f()
		color = buf.readInt()
	}

	companion object {
		val TYPE = visionType(::RectVision, id("rect"))
	}
}