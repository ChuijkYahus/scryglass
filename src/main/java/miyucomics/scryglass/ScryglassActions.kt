package miyucomics.scryglass

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexActions
import miyucomics.scryglass.actions.alter.OpRemoveVision
import miyucomics.scryglass.actions.alter.OpRotateVision
import miyucomics.scryglass.actions.alter.OpScaleVision
import miyucomics.scryglass.actions.meta.OpGetVisions
import miyucomics.scryglass.actions.meta.OpGetWindowSize
import miyucomics.scryglass.actions.visions.OpDrawLine
import miyucomics.scryglass.actions.visions.OpDrawRect
import miyucomics.scryglass.actions.visions.OpDrawText
import net.minecraft.registry.Registry

object ScryglassActions {
	fun init() {
		register("get_window_size", "aawawaa", HexDir.NORTH_EAST, OpGetWindowSize())
		register("get_visions", "dwdwd", HexDir.EAST, OpGetVisions())
		register("remove_vision", "awawa", HexDir.WEST, OpRemoveVision())

		register("rotate_vision", "aaqdwdwdedd", HexDir.NORTH_EAST, OpRotateVision())
		register("scale_vision", "aaqwdwwwdwwwdweede", HexDir.NORTH_EAST, OpScaleVision())

		register("draw_text", "aaqdwdwd", HexDir.NORTH_EAST, OpDrawText())
		register("draw_rect", "aaqdwdwdewaq", HexDir.NORTH_EAST, OpDrawRect())
		register("draw_line", "aaqdwdwdeww", HexDir.NORTH_EAST, OpDrawLine())
	}

	private fun register(name: String, signature: String, startDir: HexDir, action: Action) =
		Registry.register(
			HexActions.REGISTRY, ScryglassMain.id(name),
			ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), action)
		)
}