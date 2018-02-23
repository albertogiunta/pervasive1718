fun main(argv: Array<String>) {
    MicroMonitorBootstrap.main(argv)
    waitInitAndNotifiyToMicroSession(argv[0].toInt())
}
