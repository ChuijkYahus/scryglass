package miyucomics.scryglass.misc

import at.petrak.hexcasting.api.utils.asCompound
import miyucomics.scryglass.ScryglassMain
import miyucomics.scryglass.ScryglassMain.Companion.VISION_REGISTRY
import miyucomics.scryglass.visions.AbstractVision
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.server.network.ServerPlayerEntity

class ScryglassState(val frame: MutableMap<Int, AbstractVision>) {
	private val additions: MutableMap<Int, AbstractVision> = mutableMapOf()
	private val removals: MutableList<Int> = mutableListOf()

	constructor() : this(mutableMapOf())

	fun setVision(index: Int, vision: AbstractVision) {
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
		buf.writeInt(frame.size)
		frame.forEach { (index, vision) ->
			buf.writeInt(index)
			vision.writeToBuf(buf)
		}
		ServerPlayNetworking.send(player, ScryglassMain.PRIMER_CHANNEL, buf)
	}

	fun push(player: ServerPlayerEntity) {
		if (additions.isEmpty() && removals.isEmpty())
			return

		ServerPlayNetworking.send(player, ScryglassMain.UPDATE_CHANNEL, PacketByteBufs.create().apply {
			writeInt(removals.size)
			removals.forEach(::writeInt)

			writeInt(additions.size)
			additions.forEach { (index, vision) ->
				writeInt(index)
				vision.writeToBuf(this)
			}
		})

		additions.clear()
		removals.clear()
	}

	fun serialize(): NbtList {
		val list = NbtList()
		for ((index, vision) in frame) {
			list.add(NbtCompound().apply {
				putInt("index", index)
				putString("type", VISION_REGISTRY.getId(vision.type)!!.toString())
				vision.writeToCompound(this)
			})
		}
		return list
	}

	companion object {
		@JvmStatic
		fun deserialize(list: NbtList): ScryglassState {
			return ScryglassState(list.map { it.asCompound }.associate { it.getInt("index") to AbstractVision.createFromNbt(it) }.toMutableMap())
		}
	}
}