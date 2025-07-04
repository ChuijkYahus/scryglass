package miyucomics.scryglass.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getInt
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import miyucomics.scryglass.ScryglassMain.Companion.floatVector
import miyucomics.scryglass.ScryglassMain.Companion.reflectY
import miyucomics.scryglass.icons.TextIcon
import miyucomics.scryglass.state.PlayerEntityMinterface
import net.minecraft.server.network.ServerPlayerEntity

class OpDrawText : ConstMediaAction {
	override val argc = 3
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		if (env.caster !is ServerPlayerEntity)
			throw MishapBadCaster()

		val index = args.getInt(0, argc)
		val position = reflectY(args.getVec3(1, argc))
		val text = args[2].display()

		val scryglassState = (env.caster!! as PlayerEntityMinterface).getScryglassState()
		scryglassState.setIcon(index, TextIcon(text, floatVector(position)))
		return emptyList()
	}
}