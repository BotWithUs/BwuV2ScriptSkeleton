package botwithus.state

import botwithus.DooglePicker
import botwithus.extensions.leafNode
import net.botwithus.rs3.entities.LocalPlayer
import net.botwithus.rs3.world.Distance
import net.botwithus.util.Rand
import net.botwithus.xapi.game.inventory.Backpack
import net.botwithus.xapi.game.inventory.Bank
import net.botwithus.xapi.query.SceneObjectQuery
import net.botwithus.xapi.script.permissive.Interlock
import net.botwithus.xapi.script.permissive.Permissive
import net.botwithus.xapi.script.permissive.base.PermissiveScript
import net.botwithus.xapi.script.permissive.node.Branch
import net.botwithus.xapi.script.permissive.node.LeafNode

class Banking(val script: DooglePicker, name: String) : PermissiveScript.State(name) {


    private lateinit var isBackpackFull: Branch
    private lateinit var isBankOpen: Branch
    private lateinit var isNearBank: Branch
    private lateinit var toPickingStateLeaf: LeafNode
    private lateinit var depositBackpackLeaf: LeafNode
    private lateinit var openBankLeaf: LeafNode
    private lateinit var traverseToBankLeaf: LeafNode

    private companion object {
        const val NEARBY_DISTANCE = 20
    }

    init {
        initializeNodes()
    }

    override fun initializeNodes() {
        isBackpackFull = Branch(script, "isBackpackFull", Interlock("isBackpackFull",
            Permissive("backpackFull", Backpack::isFull)
        ))

        isBankOpen = Branch(script, "isBankOpen", Interlock("isBankOpen",
            Permissive("bankOpen", Bank::isOpen)
        ))

        isNearBank = Branch(script, "isNearBank", Interlock("isNearBank",
            Permissive("bankNearby") { Distance.to(script.selectedLocation.bankArea) < NEARBY_DISTANCE }
        ))

        toPickingStateLeaf = script.toPickingStateLeaf

        openBankLeaf = leafNode("openBankLeaf",script) {
            script.status = "Opening bank"
            SceneObjectQuery().name("Bank booth").results().nearest()?.let { bank ->
                bank.interact("Bank")
                script.delayUntil(Bank::isOpen, Rand.nextInt(10, 14))
            } ?: run {
                script.debug("Failed to open bank")
                script.delay(6)
            }
        }

        depositBackpackLeaf = leafNode("depositBackpackLeaf",script) {
            script.status = "Depositing items in bank"
            if (Bank.depositAll()) {
                script.delayUntil(Backpack::isEmpty, Rand.nextInt(10, 14))
            } else {
                script.debug("Failed to deposit items in bank")
            }
        }

        traverseToBankLeaf = leafNode("traverseToBankLeaf",script) {
            script.status = "Traversing to bank"
            moveToBankArea()
        }

        isBackpackFull.setChildrenNodes(isBankOpen, toPickingStateLeaf)
        isBankOpen.setChildrenNodes(depositBackpackLeaf, isNearBank)
        isNearBank.setChildrenNodes(openBankLeaf, traverseToBankLeaf)

        node = isBackpackFull
    }


    private fun moveToBankArea(): Boolean {
        val player = LocalPlayer.self()

        if (script.selectedLocation.bankArea.contains(player)) {
            script.movement.takeIf { it.hasActiveNavPath() }?.let {
                println("Reached Bank!")
                it.resetNavPath()
            }
            return false
        }

        if (script.movement.processNavPath()) return true

        script.movement.navigateToTile(
            script.selectedLocation.bankArea.randomCoordinate,
            enableSurge = true,
            disableTeleports = false,
            enableDive = true
        )
        return true
    }

}
