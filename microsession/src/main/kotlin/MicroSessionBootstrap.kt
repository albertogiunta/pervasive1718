object MicroSessionBootstrap {
    fun init(localPort: Int) {
        RouteController.initRoutes(localPort)

        // TODO all'avvio questo deve caricare tutte le sessioni aperte che non sono state chiuse
    }
}