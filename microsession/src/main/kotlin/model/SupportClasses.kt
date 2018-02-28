package model

data class SessionDNS(val sessionId: Int, val patientCF: String, val instanceId: Int = 0, val leaderCF: String)