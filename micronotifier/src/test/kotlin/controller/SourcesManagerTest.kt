package controller

import model.LifeParameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * Created by Matteo Gabellini on 12/02/18.
 */
class SourcesManagerTest {

    lateinit var subjCont: SourcesManager<String, Any>

    @Before
    fun setUp() {
        subjCont = NotifierSourcesManager()
    }

    @Test
    fun createNewSubjectForAndGet() {
        val subj = LifeParameters.TEMPERATURE.toString()
        val observable = io.reactivex.subjects.PublishSubject.create<String>().publish().autoConnect()
        subjCont.addNewObservableSource(subj, observable)
        assertEquals(subjCont.getObservableSourceOf<String>(subj)!!::class.java , observable::class.java)
    }

    @Test
    fun getSubjectsOfNotPresent() {
        val subj = LifeParameters.HEART_RATE.toString()
        val res = subjCont.getObservableSourceOf<String>(subj)
        assertNull(res)
    }
}