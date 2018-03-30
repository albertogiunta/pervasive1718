package model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * Created by Matteo Gabellini on 12/02/18.
 */
class SourcesContainerTest {

    lateinit var sourcesContainer: SourcesContainer<String, Any>

    @Before
    fun setUp() {
        sourcesContainer = NotifierSourcesContainer()
    }

    @Test
    fun createNewSubjectForAndGet() {
        val subj = LifeParameters.TEMPERATURE.toString()
        val observable = io.reactivex.subjects.PublishSubject.create<String>().publish().autoConnect()
        sourcesContainer.addNewObservableSource(subj, observable)
        assertEquals(sourcesContainer.getObservableSourceOf<String>(subj)!!::class.java , observable::class.java)
    }

    @Test
    fun getSubjectsOfNotPresent() {
        val subj = LifeParameters.HEART_RATE.toString()
        val res = sourcesContainer.getObservableSourceOf<String>(subj)
        assertNull(res)
    }
}