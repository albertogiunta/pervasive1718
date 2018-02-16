import config.ConfigLoader
import config.Services
import controllers.RouteController
import model.Bootstrapper
import java.awt.Desktop
import java.io.File
import java.net.URI

fun main(args: Array<String>) {
    ConfigLoader().load()
    File("microvisors/src/view/js/portLoader.js").bufferedWriter().use { out -> out.write("var sessionExchange = "+Bootstrapper(Services.VISORS.port).port.toString()) }
    if (Desktop.isDesktopSupported()) {
        val usrDir = System.getProperty("user.dir").replace("\\","/").replace(" ","%20")
        val separator = "/"
        Desktop.getDesktop().browse(URI("file:"+separator+separator+separator+usrDir+separator+"microvisors"+separator+"src"+separator+"index.html"))
    }
    RouteController.initRoutes(Services.VISORS.port)
}