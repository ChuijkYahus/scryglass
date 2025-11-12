package miyucomics.scryglass.actions.spawners

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getInt
import at.petrak.hexcasting.api.casting.getPositiveIntUnderInclusive
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import miyucomics.scryglass.ScryglassMain.Companion.floatVector
import miyucomics.scryglass.misc.PlayerEntityMinterface
import miyucomics.scryglass.visions.TextJustification
import miyucomics.scryglass.visions.TextVision

object OpDrawText : ConstMediaAction {
	override val argc = 4
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val index = args.getInt(0, argc)
		val position = args.getVec3(1, argc)
		val justify = args.getPositiveIntUnderInclusive(2, 2, argc)
		val text = args[3].display()

		val scryglassState = (env.castingEntity as? PlayerEntityMinterface)?.getScryglassState() ?: throw MishapBadCaster()
		scryglassState.setVision(index, TextVision(text, floatVector(position), enumValues<TextJustification>()[justify]))
		return emptyList()
	}
}