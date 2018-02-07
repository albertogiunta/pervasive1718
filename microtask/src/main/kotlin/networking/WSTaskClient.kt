package networking

import URIFactory
import WSClient
import WSClientInitializer
import java.net.URI

class WSTaskClient(uri: URI) : WSClient(uri)

fun main(args: Array<String>) {
    WSClientInitializer.init(WSTaskClient(URIFactory.getTaskURI()))
}