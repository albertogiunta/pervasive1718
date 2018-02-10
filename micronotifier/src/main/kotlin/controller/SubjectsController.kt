package controller

import LifeParameters
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import utils.Logger
import java.util.concurrent.atomic.AtomicBoolean

class SubjectsController<I, T> private constructor() {

    private val publishSubjects =
            mutableMapOf<I, Subject<T>>()

    init {
    }

    fun createNewSubjectFor(identifier: I): Subject<T> {
        if (!publishSubjects.containsKey(identifier)) {
            publishSubjects[identifier] = PublishSubject.create()
        }

        return publishSubjects[identifier]!!
    }

    fun getSubjectsOf(identifier: I): Subject<T> = publishSubjects[identifier]!!

    companion object {

        private var instance: SubjectsController<String, String> = SubjectsController()

        private var isInitialized = AtomicBoolean(false)

        fun singleton(): SubjectsController<String, String> = instance
    }
}

fun main(args: Array<String>) {

    //SubjectsController.init()

    val dumb = NotifierSessionsController.singleton()
    val dumber = NotifierTopicsController.singleton(LifeParameters.values().toSet())

    SubjectsController.singleton().createNewSubjectFor(dumb.toString())
    SubjectsController.singleton().createNewSubjectFor(dumber.toString())
    SubjectsController.singleton().createNewSubjectFor(LifeParameters.TEMPERATURE.toString())
    SubjectsController.singleton().createNewSubjectFor(LifeParameters.OXYGEN_SATURATION.toString())
    SubjectsController.singleton().createNewSubjectFor(LifeParameters.HEART_RATE.toString())
    SubjectsController.singleton().createNewSubjectFor(LifeParameters.DIASTOLIC_BLOOD_PRESSURE.toString())
    SubjectsController.singleton().createNewSubjectFor(LifeParameters.SYSTOLIC_BLOOD_PRESSURE.toString())
    SubjectsController.singleton().createNewSubjectFor(LifeParameters.END_TIDAL_CARBON_DIOXIDE.toString())

    Logger.info(SubjectsController.singleton().getSubjectsOf(dumb.toString()).toString())
    Logger.info(SubjectsController.singleton().getSubjectsOf(dumber.toString()).toString())
    Logger.info(SubjectsController.singleton().getSubjectsOf(LifeParameters.TEMPERATURE.toString()).toString())
    Logger.info(SubjectsController.singleton().getSubjectsOf(LifeParameters.OXYGEN_SATURATION.toString()).toString())
    Logger.info(SubjectsController.singleton().getSubjectsOf(LifeParameters.HEART_RATE.toString()).toString())
    Logger.info(SubjectsController.singleton().getSubjectsOf(LifeParameters.DIASTOLIC_BLOOD_PRESSURE.toString()).toString())
    Logger.info(SubjectsController.singleton().getSubjectsOf(LifeParameters.SYSTOLIC_BLOOD_PRESSURE.toString()).toString())
    Logger.info(SubjectsController.singleton().getSubjectsOf(LifeParameters.END_TIDAL_CARBON_DIOXIDE.toString()).toString())
}