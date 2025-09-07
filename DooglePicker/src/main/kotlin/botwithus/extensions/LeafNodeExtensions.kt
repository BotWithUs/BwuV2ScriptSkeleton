package botwithus.extensions

import net.botwithus.xapi.script.BwuScript
import net.botwithus.xapi.script.permissive.node.LeafNode
import java.util.concurrent.Callable

/**
 * Creates a LeafNode with a Runnable action (void return)
 * Usage: leafNode("nodeName", script) { /* action */ }
 */
fun leafNode(desc: String, script: BwuScript, action: () -> Unit): LeafNode =
    LeafNode(script, desc, Runnable { action() })

