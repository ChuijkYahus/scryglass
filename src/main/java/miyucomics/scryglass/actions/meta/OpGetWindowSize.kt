package miyucomics.scryglass.actions.meta

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import miyucomics.scryglass.state.PlayerEntityMinterface

class OpGetWindowSize : ConstMediaAction {
	override val argc = 0
	override fun execute(args: List<Iota>, env: CastingEnvironment) = (env.castingEntity as? PlayerEntityMinterface ?: throw MishapBadCaster()).getWindowSize().asActionResult
}