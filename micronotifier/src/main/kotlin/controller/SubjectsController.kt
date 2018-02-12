package controller

import LifeParameters
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import utils.Logger
import java.util.concurrent.ConcurrentHashMap

@Suppress("UNCHECKED_CAST")
class SubjectsController<I, T> private constructor() {

    private val publishSubjects = ConcurrentHashMap<I, Subject<out T>>()

    init {
    }

    @Synchronized
    fun <N : T> createNewSubjectFor(identifier: I): Subject<N> {
        if (!publishSubjects.containsKey(identifier)) {
            publishSubjects[identifier] = PublishSubject.create<N>()
        }

        return publishSubjects[identifier]!! as Subject<N>
    }

    @Synchronized
    fun <N : T> getSubjectsOf(identifier: I): Subject<N>? = publishSubjects[identifier] as? Subject<N>

    companion object {

        private var instance: SubjectsController<String, Any> = SubjectsController()

        fun singleton(): SubjectsController<String, Any> = instance
    }
}

fun main(args: Array<String>) {

    //SubjectsController.init()

    val dumb = NotifierSessionsController.singleton()
    val dumber = NotifierTopicsController.singleton(LifeParameters.values().toSet())

    SubjectsController.singleton().createNewSubjectFor<String>(dumb.toString())
    SubjectsController.singleton().createNewSubjectFor<String>(dumber.toString())
    SubjectsController.singleton().createNewSubjectFor<String>(LifeParameters.TEMPERATURE.toString())
    SubjectsController.singleton().createNewSubjectFor<String>(LifeParameters.OXYGEN_SATURATION.toString())
    SubjectsController.singleton().createNewSubjectFor<String>(LifeParameters.HEART_RATE.toString())
    SubjectsController.singleton().createNewSubjectFor<String>(LifeParameters.DIASTOLIC_BLOOD_PRESSURE.toString())
    SubjectsController.singleton().createNewSubjectFor<String>(LifeParameters.SYSTOLIC_BLOOD_PRESSURE.toString())
    SubjectsController.singleton().createNewSubjectFor<String>(LifeParameters.END_TIDAL_CARBON_DIOXIDE.toString())

    Logger.info(SubjectsController.singleton().getSubjectsOf<String>(dumb.toString()).toString())
    Logger.info(SubjectsController.singleton().getSubjectsOf<String>(dumber.toString()).toString())
    Logger.info(SubjectsController.singleton().getSubjectsOf<String>(LifeParameters.TEMPERATURE.toString()).toString())
    Logger.info(SubjectsController.singleton().getSubjectsOf<String>(LifeParameters.OXYGEN_SATURATION.toString()).toString())
    Logger.info(SubjectsController.singleton().getSubjectsOf<String>(LifeParameters.HEART_RATE.toString()).toString())
    Logger.info(SubjectsController.singleton().getSubjectsOf<String>(LifeParameters.DIASTOLIC_BLOOD_PRESSURE.toString()).toString())
    Logger.info(SubjectsController.singleton().getSubjectsOf<String>(LifeParameters.SYSTOLIC_BLOOD_PRESSURE.toString()).toString())
    Logger.info(SubjectsController.singleton().getSubjectsOf<String>(LifeParameters.END_TIDAL_CARBON_DIOXIDE.toString()).toString())
}