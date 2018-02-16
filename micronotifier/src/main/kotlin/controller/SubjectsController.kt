package controller

import LifeParameters
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import utils.Logger

interface SubjectsController <I, T> {

    fun <N : T> createNewSubjectFor(identifier: I): Subject<N>

    fun <N : T> getSubjectsOf(identifier: I): Subject<N>?

}

@Suppress("UNCHECKED_CAST")
class NotifierSubjectsController : SubjectsController<String, Any>{

    private val publishSubjects = mutableMapOf<String, Subject<out Any>>()

    init { }

    @Synchronized
    override fun <N : Any> createNewSubjectFor(identifier: String): Subject<N> {
        if (!publishSubjects.containsKey(identifier)) {
            publishSubjects[identifier] = PublishSubject.create<N>()
        }

        return publishSubjects[identifier]!! as Subject<N>
    }

    @Synchronized
    override fun <N : Any> getSubjectsOf(identifier: String): Subject<N>? = publishSubjects[identifier] as? Subject<N>
}

fun main(args: Array<String>) {

    //NotifierSubjectsController.init()

    val dumber = NotifierTopicsController(LifeParameters.values().toSet())

    val dumb = NotifierSubjectsController()

    dumb.createNewSubjectFor<String>(dumb.toString())
    dumb.createNewSubjectFor<String>(dumber.toString())
    dumb.createNewSubjectFor<String>(LifeParameters.TEMPERATURE.toString())
    dumb.createNewSubjectFor<String>(LifeParameters.OXYGEN_SATURATION.toString())
    dumb.createNewSubjectFor<String>(LifeParameters.HEART_RATE.toString())
    dumb.createNewSubjectFor<String>(LifeParameters.DIASTOLIC_BLOOD_PRESSURE.toString())
    dumb.createNewSubjectFor<String>(LifeParameters.SYSTOLIC_BLOOD_PRESSURE.toString())
    dumb.createNewSubjectFor<String>(LifeParameters.END_TIDAL_CARBON_DIOXIDE.toString())

    Logger.info(dumb.getSubjectsOf<String>(dumb.toString()).toString())
    Logger.info(dumb.getSubjectsOf<String>(dumber.toString()).toString())
    Logger.info(dumb.getSubjectsOf<String>(LifeParameters.TEMPERATURE.toString()).toString())
    Logger.info(dumb.getSubjectsOf<String>(LifeParameters.OXYGEN_SATURATION.toString()).toString())
    Logger.info(dumb.getSubjectsOf<String>(LifeParameters.HEART_RATE.toString()).toString())
    Logger.info(dumb.getSubjectsOf<String>(LifeParameters.DIASTOLIC_BLOOD_PRESSURE.toString()).toString())
    Logger.info(dumb.getSubjectsOf<String>(LifeParameters.SYSTOLIC_BLOOD_PRESSURE.toString()).toString())
    Logger.info(dumb.getSubjectsOf<String>(LifeParameters.END_TIDAL_CARBON_DIOXIDE.toString()).toString())
}