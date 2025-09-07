package botwithus.state

enum class BotState(val description: String) {
    DOOGLE_PICKING("Doogle Picking"),
    BANKING("Banking");

    override fun toString(): String = description
}
