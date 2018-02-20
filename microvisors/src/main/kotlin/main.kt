import config.ConfigLoader
import config.Services
import controllers.RouteController
import model.Bootstrapper
import utils.PathGetter
import java.awt.Desktop
import java.io.File
import java.net.URI
import java.net.URLEncoder

fun main(args: Array<String>) {
    ConfigLoader().load(args)

    File(PathGetter.getRootPath()+"microvisors/src/view/js/portLoader.js").bufferedWriter().use { out -> out.write("var sessionExchange = " + Bootstrapper(Services.VISORS.port).port.toString()) }
    if (Desktop.isDesktopSupported()) {
        var rootProjectPath = PathGetter.getRootPath()
        if (System.getProperty("os.name").contains("Mac OS X")) {
            rootProjectPath = URLEncoder.encode(rootProjectPath, "UTF-8").replace("%2F", "/").replace("+", " ")
        } else if (System.getProperty("os.name").contains("Windows")) {
            rootProjectPath = rootProjectPath.replace("\\", "/").replace(" ", "%20")
        }
        val usrDir = rootProjectPath.replace("\\", "/").replace(" ", "%20")
        val separator = "/"
        var path = "file:" + separator + separator + separator + usrDir + "microvisors" + separator + "src" + separator + "index.html"
        var uri = URI(path)
        println(uri)
        Desktop.getDesktop().browse(uri)
    }
    RouteController.initRoutes()
}