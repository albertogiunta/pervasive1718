package controller

import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.ConcurrentHashMap

interface SubjectsManager <I, T> {

    fun <N : T> createNewSubjectFor(identifier: I): Subject<N>

    fun <N : T> getSubjectsOf(identifier: I): Subject<N>?

}

@Suppress("UNCHECKED_CAST")
class NotifierSubjectsManager : SubjectsManager<String, Any>{

    private val publishSubjects = ConcurrentHashMap<String, Subject<out Any>>()

    init { }

    override fun <N : Any> createNewSubjectFor(identifier: String): Subject<N> {
        if (!publishSubjects.containsKey(identifier)) {
            publishSubjects[identifier] = PublishSubject.create<N>()
        }

        return publishSubjects[identifier]!! as Subject<N>
    }

    override fun <N : Any> getSubjectsOf(identifier: String): Subject<N>? = publishSubjects[identifier] as? Subject<N>
}