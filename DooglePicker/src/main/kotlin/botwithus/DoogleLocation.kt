package botwithus

import net.botwithus.rs3.world.Area
import net.botwithus.rs3.world.Coordinate

enum class DoogleLocation(
    val displayName: String,
    val spawnCount: Int,
    val doogleArea: Area,
    val bankArea: Area,
    val isMembers: Boolean
) {
    FELDIP_HILLS(
        displayName = "Feldip Hills",
        spawnCount = 4,
        doogleArea = Area.Rectangular(
            Coordinate(2559, 2977, 0),
            Coordinate(2565, 2971, 0)

        ),
        bankArea = Area.Rectangular(
            Coordinate(2609, 3096, 0),
            Coordinate(2613, 3089, 0)
        ),
        isMembers = true
    ),

    SWAYING_TREE(
        displayName = "Swaying Tree (Rellekka)",
        spawnCount = 13,
        doogleArea = Area.Rectangular(
            Coordinate(2735, 3644, 0),
            Coordinate(2743, 3634, 0)
        ),
        bankArea = Area.Rectangular(
            Coordinate(2722, 3492, 0),
            Coordinate(2727, 3490, 0)
        ),
        isMembers = true
    ),

    GERTRUDES_HOUSE(
        displayName = "Gertrude's House (Varrock)",
        spawnCount = 5,
        doogleArea = Area.Rectangular(
            Coordinate(3150, 3400, 0),
            Coordinate(3160, 3390, 0)
        ),
        bankArea = Area.Rectangular(
            Coordinate(3184, 3445, 0),
            Coordinate(3187, 3435, 0)
        ),
        isMembers = false
    );

    override fun toString(): String = displayName
}
