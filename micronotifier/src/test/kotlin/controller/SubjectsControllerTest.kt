package controller

import io.reactivex.subjects.PublishSubject
import model.LifeParameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * Created by Matteo Gabellini on 12/02/18.
 */
class SubjectsControllerTest {

    lateinit var subjCont: SubjectsController<String, Any>

    @Before
    fun setUp() {
        subjCont = NotifierSubjectsController()
    }

    @Test
    fun createNewSubjectForAndGet() {
        val subj = LifeParameters.TEMPERATURE.toString()
        subjCont.createNewSubjectFor<String>(subj)
        assertEquals(subjCont.getSubjectsOf<String>(subj)!!.javaClass, PublishSubject::class.java)
    }

    @Test
    fun getSubjectsOfNotPresent() {
        val subj = LifeParameters.HEART_RATE.toString()
        val res = subjCont.getSubjectsOf<String>(subj)
        assertNull(res)
    }
}