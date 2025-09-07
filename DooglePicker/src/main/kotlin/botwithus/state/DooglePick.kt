package botwithus.state

import botwithus.DooglePicker
import botwithus.extensions.leafNode
import net.botwithus.rs3.entities.LocalPlayer
import net.botwithus.rs3.entities.SceneObject
import net.botwithus.rs3.world.Distance
import net.botwithus.util.Rand
import net.botwithus.xapi.game.inventory.Backpack
import net.botwithus.xapi.query.SceneObjectQuery
import net.botwithus.xapi.script.permissive.Interlock
import net.botwithus.xapi.script.permissive.Permissive
import net.botwithus.xapi.script.permissive.base.PermissiveScript
import net.botwithus.xapi.script.permissive.node.Branch
import net.botwithus.xapi.script.permissive.node.LeafNode

class DooglePick(val script: DooglePicker, name: String) : PermissiveScript.State(name) {

    private lateinit var isBackpackFull: Branch
    private lateinit var shouldPickDoogle: Branch
    private lateinit var toBankStateLeaf: LeafNode
    private lateinit var pickDoogleLeaf: LeafNode
    private lateinit var traverseToDoogleAreaLeaf: LeafNode

    private var doogleObj: SceneObject? = null

    private companion object {
        const val INTERACT_DISTANCE = 10
    }

    init {
        initializeNodes()
    }

    override fun initializeNodes() {
        isBackpackFull = Branch(script, "isBackpackFull", Interlock("isBackpackFull",
            Permissive("backpackFull", Backpack::isFull)
        ))

        shouldPickDoogle = Branch(script, "shouldPickDoogle", Interlock("isDoogleObjNearby",
            Permissive("doogleObjExists") {
                doogleObj = SceneObjectQuery.newQuery().name("Doogle bush").results().nearest()
                doogleObj != null
            },
            Permissive("doogleNearby") { Distance.to(script.selectedLocation.doogleArea) < INTERACT_DISTANCE },
            Permissive("playerIdleAnim") { script.player.animationId == -1 }
        ))

        toBankStateLeaf = script.toBankStateLeaf

        pickDoogleLeaf = leafNode("pickDoogleLeaf",script) {
            script.status = "Picking Doogle Leaf"
            val currentBackpackCount = Backpack.getItems().size
            if (currentBackpackCount < 28 && (doogleObj?.interact("Pick-leaf") ?: 0) > 0) {
                script.delayUntil({
                    val newCount = Backpack.getItems().size
                    !script.player.isMoving && script.player.animationId == -1 && currentBackpackCount != newCount
                }, Rand.nextInt(6, 9))
            } else {
                script.warn("Failed to interact with doogle object")
                script.delay(5)
            }
        }

        traverseToDoogleAreaLeaf = leafNode("traverseToDoogleAreaLeaf",script) {
            script.status = if (script.selectedLocation.doogleArea.contains(script.player)) {
                "Already in doogle area, waiting for pick process to complete."
            } else {
                "Traversing to Doogle leaves area"
            }
            if (!script.selectedLocation.doogleArea.contains(script.player)) {
                moveToDoogleArea()
            }
        }

        isBackpackFull.setChildrenNodes(toBankStateLeaf, shouldPickDoogle)
        shouldPickDoogle.setChildrenNodes(pickDoogleLeaf, traverseToDoogleAreaLeaf)

        node = isBackpackFull

        script.println("DooglePickState Initialized")
    }


    private fun moveToDoogleArea(): Boolean {
        val player = LocalPlayer.self()

        if (script.selectedLocation.doogleArea.contains(player)) {
            script.movement.takeIf { it.hasActiveNavPath() }?.let {
                println("Reached Doogle Leaves!")
                it.resetNavPath()
            }
            return false
        }

        if (script.movement.processNavPath()) return true

        println("Creating path to Doogle Leaves...")
        script.movement.navigateToTile(script.selectedLocation.doogleArea.randomCoordinate, true, false, true)
        return true
    }

}
