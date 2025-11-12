package miyucomics.scryglass.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getInt
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import miyucomics.scryglass.misc.PlayerEntityMinterface

object OpRemoveVision : ConstMediaAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
		val scryglassState = (env.castingEntity as? PlayerEntityMinterface)?.getScryglassState() ?: throw MishapBadCaster()
		scryglassState.removeVision(args.getInt(0, argc))
		return emptyList()
	}
}