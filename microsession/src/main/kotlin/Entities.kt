import java.sql.Timestamp
import java.util.*

data class SessionDNS(val sessionId: Int, val patId: String, val microTaskAddress: String)

data class Session @JvmOverloads constructor(val id: Int = 0, val cf: String, @KlaxonDate val date: Timestamp = Timestamp(Date().time))