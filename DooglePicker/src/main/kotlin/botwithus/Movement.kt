package botwithus

import botwithus.navigation.api.NavPath
import botwithus.navigation.api.State
import net.botwithus.rs3.world.Coordinate

class Movement {
    private var navPath: NavPath? = null

    fun traverse(path: NavPath) {
        navPath ?: run {
            navPath = path
            println("Traversing navPath: from: ${path.getStart()}, to: ${path.getDestination()}")
        }
    }

    fun processNavPath(): Boolean {
        val currentPath = navPath ?: return false
        val state = currentPath.state()
        if (state != State.CONTINUE) {
            navPath = null
            println("Nav path finished with state: $state")
            return true
        }
        currentPath.process()
        return true
    }

    fun resetNavPath() {
        navPath = null
        println("Nav path reset.")
    }

    fun navigateToTile(
        toTile: Coordinate?, 
        enableSurge: Boolean, 
        disableTeleports: Boolean, 
        enableDive: Boolean
    ) {
        val flags = buildFlags(enableSurge, disableTeleports, enableDive)
        NavPath.resolve(toTile, flags).let(::traverse)
    }

    private fun buildFlags(enableSurge: Boolean, disableTeleports: Boolean, enableDive: Boolean): Int {
        var flags = 0
        if (enableSurge) flags = flags or NavPath.ENABLE_SURGE
        if (disableTeleports) flags = flags or NavPath.DISABLE_TELEPORTS
        if (!enableDive) flags = flags or NavPath.DISABLE_DIVE
        return flags
    }

    fun hasActiveNavPath(): Boolean = navPath != null

    private fun println(message: String?) = kotlin.io.println(message)
}