package botwithus.ui

import botwithus.DoogleLocation
import botwithus.DooglePicker
import net.botwithus.imgui.ImGui
import net.botwithus.xapi.script.ui.interfaces.BuildableUI

class DooglePickerUI(private val script: DooglePicker) : BuildableUI {
    override fun buildUI() {
        ImGui.text("Doogle Picker Settings")
        ImGui.separator()
        
        val player = script.player
        if (!script.isActive) {
            ImGui.textColored("Script not running - Please start the script to configure settings", 1.0f, 0.0f, 0.0f, 1.0f)
        }
        if (player == null) {
            ImGui.textColored("Not logged in - Please log in to configure settings", 1.0f, 0.0f, 0.0f, 1.0f)
        }
        
        if (script.isActive) {
            ImGui.text("Location:")
            var currentLocation = script.selectedLocation.ordinal
            val isMember = player.isMember

            val availableLocations = DoogleLocation.entries.filter { !it.isMembers || isMember }
            val locationNames = availableLocations.map { "${it.displayName} (${it.spawnCount} spawns${if (it.isMembers) " - Members" else " - F2P"})" }.toTypedArray()

            if (!isMember && script.selectedLocation.isMembers) {
                script.selectedLocation = DoogleLocation.entries.first { !it.isMembers }
            }

            val currentIndex = availableLocations.indexOf(script.selectedLocation).coerceAtLeast(0)
            val newSelection = ImGui.combo("##location", currentIndex, locationNames, locationNames.size)

            if (newSelection != currentIndex && newSelection >= 0) {
                script.selectedLocation = availableLocations[newSelection]
            }

            ImGui.separator()
            ImGui.text("Selected: ${script.selectedLocation.displayName}")
            ImGui.text("Spawn Count: ${script.selectedLocation.spawnCount}")
            ImGui.text("Members Area: ${if (script.selectedLocation.isMembers) "Yes" else "No"}")

            if (!isMember) {
                ImGui.textColored("Note: Members-only locations are disabled",1.0f, 0.5f, 0.0f, 1.0f)
            }
        }
        
        ImGui.separator()
        ImGui.text("Script Control:")
        
        if (script.isActive) {
            if (ImGui.button("Stop Script", 120f, 30f)) {
                script.isActive = false
            }
            ImGui.sameLine(0f, 10f)
            ImGui.textColored("Script is running", 0.0f, 1.0f, 0.0f, 1.0f)
        } else {
            if (ImGui.button("Start Script", 120f, 30f)) {
                script.isActive = true
                script.initializeScript()
            }
            ImGui.sameLine(0f, 10f)
            ImGui.textColored("Script is stopped", 1.0f, 0.0f, 0.0f, 1.0f)
        }
    }
}
