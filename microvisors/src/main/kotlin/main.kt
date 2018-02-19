import config.ConfigLoader
import config.Services
import controllers.RouteController
import model.Bootstrapper
import utils.PathGetter
import java.awt.Desktop
import java.io.File

fun main(args: Array<String>) {
    ConfigLoader().load(args)

    File(PathGetter.getRootPath()+"microvisors/src/view/js/portLoader.js").bufferedWriter().use { out -> out.write("var sessionExchange = " + Bootstrapper(Services.VISORS.port).port.toString()) }
    if (Desktop.isDesktopSupported()) {
        val usrDir = PathGetter.getRootPath().replace("\\", "/").replace(" ", "%20")
        val separator = "/"
//        Desktop.getDesktop().browse(URI("file:" + separator + separator + separator + usrDir + "microvisors" + separator + "src" + separator + "index.html"))
    }
    RouteController.initRoutes()
}