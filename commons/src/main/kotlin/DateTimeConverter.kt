import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import com.beust.klaxon.KlaxonException
import java.sql.Timestamp
import java.text.SimpleDateFormat

@Target(AnnotationTarget.FIELD)
annotation class KlaxonDate

val dateConverter = object : Converter<Timestamp> {
    val sdf: SimpleDateFormat = SimpleDateFormat("MMM d, yyyy hh:mm:ss aaa")
    override fun fromJson(jv: JsonValue) =
        if (jv.string != null) {
            Timestamp(sdf.parse(jv.string).time)
        } else {
            throw KlaxonException("Couldn't parse date: ${jv.string}")
        }

    override fun toJson(o: Timestamp) = """ { "date" : $o } """


}