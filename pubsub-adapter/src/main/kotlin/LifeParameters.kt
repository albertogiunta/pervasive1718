/**
 * Created by Matteo Gabellini on 25/01/2018.
 */
enum class LifeParameters(val longName: String, val acronym: String) {
    SYSTOLIC_BLOOD_PRESSURE("Pressione Arteriosa Sistolica", "SYS"),
    DIASTOLIC_BLOOD_PRESSURE("Pressione Arteriosa Diastolica", "DIA"),
    HEART_RATE("Frequenza Cardiaca", "HR"),
    TEMPERATURE("Temperatura", "T"),
    OXYGEN_SATURATION("Saturazione Ossigeno", "SpO2"),
    END_TIDAL_CARBON_DIOXIDE("End Tidal Anidride Carbonica", "EtCO2")

}

object LifeParametersUtils {
    fun getByAcronym(acr: String) = LifeParameters.values().first { it.acronym == acr }
}