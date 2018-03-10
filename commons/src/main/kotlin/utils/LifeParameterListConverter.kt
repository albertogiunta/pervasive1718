package utils

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonValue
import com.beust.klaxon.KlaxonException
import model.LifeParameters

@Target(AnnotationTarget.FIELD)
annotation class KlaxonLifeParameterList

val lifeParameterListConverter = object : Converter<List<LifeParameters>> {

    @Suppress("UNCHECKED_CAST")
    override fun fromJson(jv: JsonValue): List<LifeParameters> {
        if (jv.array != null) {
            return (jv.array as JsonArray<String>).map { elem: String ->
                LifeParameters.Utils.getByEnumName(elem)!!
            }.toList()
        } else {
            throw KlaxonException("Couldn't parse life parameter list: ${jv.string}")
        }
    }

    override fun toJson(value: List<LifeParameters>): String? {
        return value.toString()
    }
}

@Target(AnnotationTarget.FIELD)
annotation class KlaxonLifeParameter

val lifeParameterConverter = object : Converter<LifeParameters> {

    @Suppress("UNCHECKED_CAST")
    override fun fromJson(jv: JsonValue): LifeParameters {
        if (jv.string != null) {
            return LifeParameters.Utils.getByEnumName(jv.string!!)!!

        } else {
            throw KlaxonException("Couldn't parse life parameter list: ${jv.string}")
        }
    }

    override fun toJson(value: LifeParameters): String? {
        return value.toString()
    }
}