package utils

import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import java.io.Reader
import java.io.StringReader
import java.util.*

inline fun <reified A> handlingGetResponse(triplet: Triple<Request, Response, Result<String, FuelError>>): List<A> {
    lateinit var listResult: List<A>
    triplet.third.fold(success = {
        val klaxon = Klaxon().fieldConverter(KlaxonDate::class, dateConverter)
        JsonReader(StringReader(it) as Reader).use { reader ->
            listResult = arrayListOf()
            reader.beginArray {
                while (reader.hasNext()) {
                    val data = klaxon.parse<A>(reader)!!
                    (listResult as ArrayList<A>).add(data)
                }
            }
        }
    }, failure = {
        println(String(it.errorData))
    })
    return listResult
}