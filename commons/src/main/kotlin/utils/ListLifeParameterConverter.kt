package utils

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonValue
import com.beust.klaxon.KlaxonException
import model.LifeParameters

@Target(AnnotationTarget.FIELD)
annotation class KlaxonListLifeParameter

val listLifeParameterConverter = object : Converter<List<LifeParameters>> {

    override fun fromJson(jv: JsonValue): List<LifeParameters> {
        if (jv.array != null) {
            val list = (jv.array as JsonArray<String>).map { elem: String ->
                LifeParameters.Utils.getByLongName(elem)!!
            }.toList()
            return list
        } else {
            throw KlaxonException("Couldn't parse date: ${jv.string}")
        }
    }

    override fun toJson(value: List<LifeParameters>): String? {
        return value.toString()
    }
}