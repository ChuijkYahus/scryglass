package miyucomics.scryglass.visions

import miyucomics.scryglass.ScryglassMain.Companion.id
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import org.joml.Vector3f

class LineVision(visionType: VisionType<LineVision>) : AbstractVision(visionType) {
	private lateinit var a: Vector3f
	private lateinit var b: Vector3f
	private var color: Int = 0

	constructor(a: Vector3f, b: Vector3f, color: Int) : this(TYPE) {
		this.a = a
		this.b = b
		this.color = color
	}

	override fun renderCustom(matrices: MatrixStack, drawContext: DrawContext, deltaTime: Float) {
		val buffer = drawContext.vertexConsumers.getBuffer(RenderLayer.getGuiOverlay())
		val matrix = matrices.peek().positionMatrix

		val direction = Vector3f(b).sub(a).normalize()
		var perpendicular = Vector3f(direction).cross(Vector3f(0f, 0f, 1f)).normalize()
		if (perpendicular.lengthSquared() < 1e-6f)
			perpendicular = Vector3f(direction).cross(Vector3f(0f, 1f, 0f)).normalize()

		val offset = perpendicular
		val v1 = Vector3f(a).add(offset)
		val v2 = Vector3f(b).add(offset)
		val v3 = Vector3f(b).sub(offset)
		val v4 = Vector3f(a).sub(offset)

		buffer.vertex(matrix, v4.x, v4.y, 0f).color(color).next()
		buffer.vertex(matrix, v3.x, v3.y, 0f).color(color).next()
		buffer.vertex(matrix, v2.x, v2.y, 0f).color(color).next()
		buffer.vertex(matrix, v1.x, v1.y, 0f).color(color).next()

		drawContext.draw()
	}

	override fun writeNBTCustom(compound: NbtCompound) {
		compound.putFloat("ax", a.x)
		compound.putFloat("ay", a.y)
		compound.putFloat("az", a.z)
		compound.putFloat("bx", b.x)
		compound.putFloat("by", b.y)
		compound.putFloat("bz", b.z)
		compound.putInt("color", color)
	}

	override fun readNBTCustom(compound: NbtCompound) {
		a = Vector3f(compound.getFloat("ax"), compound.getFloat("ay"), compound.getFloat("az"))
		b = Vector3f(compound.getFloat("bx"), compound.getFloat("by"), compound.getFloat("bz"))
		color = compound.getInt("color")
	}

	override fun writeBufCustom(buf: PacketByteBuf) {
		buf.writeVector3f(a)
		buf.writeVector3f(b)
		buf.writeInt(color)
	}

	override fun readBufCustom(buf: PacketByteBuf) {
		a = buf.readVector3f()
		b = buf.readVector3f()
		color = buf.readInt()
	}

	companion object {
		val TYPE = visionType(::LineVision, id("line"))
	}
}