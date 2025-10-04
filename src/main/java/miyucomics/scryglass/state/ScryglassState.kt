package miyucomics.scryglass.state

import at.petrak.hexcasting.api.utils.asCompound
import miyucomics.scryglass.ScryglassMain
import miyucomics.scryglass.ScryglassMain.Companion.VISION_REGISTRY
import miyucomics.scryglass.visions.Vision
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtInt
import net.minecraft.nbt.NbtList
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class ScryglassState {
	private val frame: MutableMap<Int, Vision>
	private val additions: MutableMap<Int, Vision> = mutableMapOf()
	private val removals: MutableList<Int> = mutableListOf()

	constructor() : this(mutableMapOf())

	constructor(frame: Map<Int, Vision>) {
		this.frame = frame.toMutableMap()
	}

	fun peek(): MutableMap<Int, Vision> {
		return frame
	}

	fun get(index: Int): Vision? {
		return frame[index]
	}

	fun setVision(index: Int, vision: Vision) {
		frame[index] = vision
		additions[index] = vision
		removals.remove(index)
	}

	fun removeVision(index: Int) {
		frame.remove(index)
		additions.remove(index)
		removals.add(index)
	}

	fun prime(player: ServerPlayerEntity) {
		val buf = PacketByteBufs.create()
		buf.writeNbt(this.serialize())
		ServerPlayNetworking.send(player, ScryglassMain.PRIMER_CHANNEL, buf)
	}

	fun push(player: ServerPlayerEntity) {
		if (additions.isEmpty() && removals.isEmpty())
			return

		val deltaNbt = NbtCompound()

		val addedList = NbtList()
		for ((index, vision) in additions) {
			val visionNbt = vision.toNBT()
			visionNbt.putInt("index", index)
			visionNbt.putString("type", VISION_REGISTRY.getId(vision.type)!!.toString())
			addedList.add(visionNbt)
		}
		deltaNbt.put("added", addedList)

		val removedList = NbtList()
		for (index in removals) {
			removedList.add(NbtInt.of(index))
		}
		deltaNbt.put("removed", removedList)

		val buf = PacketByteBufs.create()
		val wrapper = NbtCompound()
		wrapper.put("deltas", deltaNbt)
		buf.writeNbt(wrapper)
		ServerPlayNetworking.send(player, ScryglassMain.UPDATE_CHANNEL, buf)

		additions.clear()
		removals.clear()
	}

	fun serialize(): NbtCompound {
		val compound = NbtCompound()
		val list = NbtList()
		for ((index, vision) in frame) {
			val visionNbt = vision.toNBT()
			visionNbt.putInt("index", index)
			visionNbt.putString("type", VISION_REGISTRY.getId(vision.type)!!.toString())
			list.add(visionNbt)
		}
		compound.put("visions", list)
		return compound
	}

	companion object {
		@JvmStatic
		fun deserialize(compound: NbtCompound): ScryglassState {
			val frame = mutableMapOf<Int, Vision>()
			val visionNbt = compound.getList("visions", NbtElement.COMPOUND_TYPE.toInt())
			for (element in visionNbt) {
				val visionNbt = element.asCompound
				val typeId = Identifier(visionNbt.getString("type"))
				val type = VISION_REGISTRY.get(typeId) ?: continue
				val index = visionNbt.getInt("index")
				frame[index] = type.fromNBT(visionNbt)
			}
			return ScryglassState(frame)
		}
	}
}