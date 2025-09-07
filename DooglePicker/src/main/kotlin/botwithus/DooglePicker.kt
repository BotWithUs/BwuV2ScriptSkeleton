package botwithus

import botwithus.extensions.leafNode
import botwithus.state.Banking
import botwithus.state.BotState
import botwithus.state.DooglePick
import botwithus.ui.DooglePickerUI
import com.google.gson.JsonObject
import net.botwithus.rs3.inventories.events.InventoryEvent
import net.botwithus.scripts.Info
import net.botwithus.ui.workspace.Workspace
import net.botwithus.xapi.script.BwuScript
import net.botwithus.xapi.script.ui.interfaces.BuildableUI
import net.botwithus.xapi.util.BwuMath

@Info(name = "DooglePicker", description = "Picks Doogle leaves", version = "1.0.0", author = "Mark")
class DooglePicker : BwuScript() {

    private var ui: DooglePickerUI? = null
    var movement = Movement()
    
    var selectedLocation: DoogleLocation = DoogleLocation.GERTRUDES_HOUSE

    private var doogleLeavesPicked = 0
    private var lifetimePicked = 0

    val toPickingStateLeaf = leafNode("toPickingStateLeaf",this) {
        status = "Switching to Picking State"
        setCurrentState(BotState.DOOGLE_PICKING.description)
    }

    val toBankStateLeaf = leafNode("toBankStateLeaf",this) {
        status = "Switching to Bank State"
        setCurrentState(BotState.BANKING.description)
    }

    init {
        isDebugMode = true
    }

    override fun onDrawConfig(workspace: Workspace) {
        if (isActive && player != null && !player.isMember && selectedLocation.isMembers) {
            selectedLocation = DoogleLocation.entries.first { !it.isMembers }
        }
        ui = ui ?: DooglePickerUI(this)
        ui?.buildUI()
    }

    override fun onInitialize() {
        super.onInitialize()

    }
    
    fun initializeScript() {
        runCatching {
            botStatInfo.displayInfoMap["Doogle leaves Picked"] = 
                " $doogleLeavesPicked (${BwuMath.getUnitsPerHour(STOPWATCH, doogleLeavesPicked)} / hr"

            val bankState = Banking(this, BotState.BANKING.description)
            val dooglePickState = DooglePick(this, BotState.DOOGLE_PICKING.description)
            
            initStates(bankState, dooglePickState)
            status = "Script initialized with state: ${currentState.name}"
        }.onFailure { e ->
            logger.error(e.message, e)
        }
    }

    override fun getBuildableUI(): BuildableUI? = null

    override fun loadPersistentData(jsonObject: JsonObject) {
        lifetimePicked = jsonObject.get("lifetimePicked")?.asInt ?: 0
    }

    override fun savePersistentData(jsonObject: JsonObject) {
        jsonObject.addProperty("lifetimePicked", lifetimePicked)
    }

    override fun onItemAcquired(event: InventoryEvent) {
        event.newItem().takeIf { it.name == "Doogle leaves" }?.let { item ->
            doogleLeavesPicked += item.quantity
            lifetimePicked += item.quantity
        }
    }
}
