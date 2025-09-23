package miyucomics.scryglass.state

import net.minecraft.util.math.Vec3d

interface PlayerEntityMinterface {
	fun getWindowSize(): Vec3d
	fun setWindowSize(size: Vec3d)
	fun getScryglassState(): ScryglassState
}