package miyucomics.scryglass.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getInt
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import miyucomics.scryglass.misc.PlayerEntityMinterface
import miyucomics.scryglass.visions.AbstractVision

class OpModifyVision(val modifier: (AbstractVision, List<Iota>) -> Unit) : ConstMediaAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val scryglassState = (env.castingEntity as? PlayerEntityMinterface)?.getScryglassState() ?: throw MishapBadCaster()
		val index = args.getInt(0, argc)
		val vision = scryglassState.frame[index] ?: return emptyList()
		modifier(vision, args)
		scryglassState.setVision(index, vision)
		return emptyList()
	}
}