package miyucomics.scryglass.misc

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.getDouble
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexActions
import miyucomics.scryglass.ScryglassMain
import miyucomics.scryglass.actions.OpGetVisions
import miyucomics.scryglass.actions.OpGetWindowSize
import miyucomics.scryglass.actions.OpModifyVision
import miyucomics.scryglass.actions.OpRemoveVision
import miyucomics.scryglass.actions.spawners.OpDrawLine
import miyucomics.scryglass.actions.spawners.OpDrawRect
import miyucomics.scryglass.actions.spawners.OpDrawText
import net.minecraft.registry.Registry

object ScryglassActions {
	fun init() {
		register("get_window_size", "aawawaa", HexDir.NORTH_EAST, OpGetWindowSize)
		register("get_visions", "dwdwd", HexDir.EAST, OpGetVisions)
		register("remove_vision", "awawa", HexDir.WEST, OpRemoveVision)

		register("draw_text", "aaqdwdwd", HexDir.NORTH_EAST, OpDrawText)
		register("draw_rect", "aaqdwdwdewaq", HexDir.NORTH_EAST, OpDrawRect)
		register("draw_line", "aaqdwdwdeww", HexDir.NORTH_EAST, OpDrawLine)

		register("rotate_vision", "aaqdwdwdedd", HexDir.NORTH_EAST, OpModifyVision { vision, args -> vision.rotation = (args.getDouble(1, 2) % 1).toFloat() })
		register("scale_vision", "aaqwdwwwdwwwdweede", HexDir.NORTH_EAST, OpModifyVision { vision, args -> vision.scale = args.getDouble(1, 2).toFloat() })
	}

	private fun register(name: String, signature: String, startDir: HexDir, action: Action) =
		Registry.register(HexActions.REGISTRY, ScryglassMain.id(name), ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), action))
}