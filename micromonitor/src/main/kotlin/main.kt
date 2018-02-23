fun main(argv: Array<String>) {
    MicroMonitorBootstrap.main(argv)
    waitInitAndNotifyToMicroSession(argv[0].toInt())
}
