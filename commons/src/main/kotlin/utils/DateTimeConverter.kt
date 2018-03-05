package utils

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import com.beust.klaxon.KlaxonException
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Target(AnnotationTarget.FIELD)
annotation class KlaxonDate

val dateConverter = object : Converter<Timestamp> {
    val javaSDF = SimpleDateFormat("MMM d, yyyy hh:mm:ss aaa", Locale.ENGLISH)
    val androidSDF = SimpleDateFormat("MMM d, yyyy hh:mm:ss", Locale.ENGLISH)
    val defaultSDF = SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS", Locale.ENGLISH)
    val sdfList: List<SimpleDateFormat> = listOf(defaultSDF,androidSDF,javaSDF)
    lateinit var date:Timestamp

    override fun fromJson(jv: JsonValue): Timestamp{
        if (jv.string != null) {
            sdfList.forEach{
                try{
                    date = Timestamp(it.parse(jv.string).time)
                }catch(e:ParseException){}
            }
        } else {
            throw KlaxonException("Couldn't parse date: ${jv.string}")
        }
        return date
    }

    override fun toJson(value: Timestamp) = """ { "date" : $value } """
}