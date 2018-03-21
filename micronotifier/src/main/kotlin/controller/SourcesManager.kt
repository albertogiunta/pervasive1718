package controller

import io.reactivex.Observable
import java.util.concurrent.ConcurrentHashMap

interface SourcesManager <I, T> {

    fun <N : T> addNewObservableSource(identifier: I, source: Observable<N>): Observable<N>

    fun <N : T> getObservableSourceOf(identifier: I): Observable<N>?
}

@Suppress("UNCHECKED_CAST")
class NotifierSourcesManager : SourcesManager<String, Any>{

    private val observableSources = ConcurrentHashMap<String, Observable<out Any>>()

    init { }

    override fun <N : Any> addNewObservableSource(identifier: String, source: Observable<N>): Observable<N> {
        if (!observableSources.containsKey(identifier)) {
            observableSources[identifier] = source
        }

        return source
    }

    override fun <N : Any> getObservableSourceOf(identifier: String): Observable<N>? = observableSources[identifier] as? Observable<N>
}