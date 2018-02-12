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
class NotifierSubjectsController private constructor() : SubjectsController<String, Any>{

    private val publishSubjects = mutableMapOf<String, Subject<out Any>>()

    init {
    }

    @Synchronized
    override fun <N : Any> createNewSubjectFor(identifier: String): Subject<N> {
        if (!publishSubjects.containsKey(identifier)) {
            publishSubjects[identifier] = PublishSubject.create<N>()
        }

        return publishSubjects[identifier]!! as Subject<N>
    }

    @Synchronized
    override fun <N : Any> getSubjectsOf(identifier: String): Subject<N>? = publishSubjects[identifier] as? Subject<N>

    companion object {

        private var instance: SubjectsController<String, Any> = NotifierSubjectsController()

        fun singleton(): SubjectsController<String, Any> = instance
    }
}

fun main(args: Array<String>) {

    //NotifierSubjectsController.init()

    val dumb = NotifierSessionsController.singleton()
    val dumber = NotifierTopicsController.singleton(LifeParameters.values().toSet())

    NotifierSubjectsController.singleton().createNewSubjectFor<String>(dumb.toString())
    NotifierSubjectsController.singleton().createNewSubjectFor<String>(dumber.toString())
    NotifierSubjectsController.singleton().createNewSubjectFor<String>(LifeParameters.TEMPERATURE.toString())
    NotifierSubjectsController.singleton().createNewSubjectFor<String>(LifeParameters.OXYGEN_SATURATION.toString())
    NotifierSubjectsController.singleton().createNewSubjectFor<String>(LifeParameters.HEART_RATE.toString())
    NotifierSubjectsController.singleton().createNewSubjectFor<String>(LifeParameters.DIASTOLIC_BLOOD_PRESSURE.toString())
    NotifierSubjectsController.singleton().createNewSubjectFor<String>(LifeParameters.SYSTOLIC_BLOOD_PRESSURE.toString())
    NotifierSubjectsController.singleton().createNewSubjectFor<String>(LifeParameters.END_TIDAL_CARBON_DIOXIDE.toString())

    Logger.info(NotifierSubjectsController.singleton().getSubjectsOf<String>(dumb.toString()).toString())
    Logger.info(NotifierSubjectsController.singleton().getSubjectsOf<String>(dumber.toString()).toString())
    Logger.info(NotifierSubjectsController.singleton().getSubjectsOf<String>(LifeParameters.TEMPERATURE.toString()).toString())
    Logger.info(NotifierSubjectsController.singleton().getSubjectsOf<String>(LifeParameters.OXYGEN_SATURATION.toString()).toString())
    Logger.info(NotifierSubjectsController.singleton().getSubjectsOf<String>(LifeParameters.HEART_RATE.toString()).toString())
    Logger.info(NotifierSubjectsController.singleton().getSubjectsOf<String>(LifeParameters.DIASTOLIC_BLOOD_PRESSURE.toString()).toString())
    Logger.info(NotifierSubjectsController.singleton().getSubjectsOf<String>(LifeParameters.SYSTOLIC_BLOOD_PRESSURE.toString()).toString())
    Logger.info(NotifierSubjectsController.singleton().getSubjectsOf<String>(LifeParameters.END_TIDAL_CARBON_DIOXIDE.toString()).toString())
}