package com.diegoparra.movies.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class EventTest {

    /*
        --------------------------------------------------------------------------------------------
            GET CONTENT IF NOT HANDLED
        --------------------------------------------------------------------------------------------
    */

    @Test
    fun getContentIfNotHandled_multipleCalls_returnValueOnFirstAndNullOnNextCalls() {
        val event = Event(10)

        val resultFirstCall = event.getContentIfNotHandled()
        val resultSecondCall = event.getContentIfNotHandled()
        val resultThirdCall = event.getContentIfNotHandled()

        assertThat(resultFirstCall).isEqualTo(10)
        assertThat(resultSecondCall).isNull()
        assertThat(resultThirdCall).isNull()
    }

    @Test
    fun getContentIfNotHandled_calledAfterPeekContent_NotAffectedAndReturnValueOnFirstCall() {
        val event = Event(10)

        event.peekContent()
        val result = event.getContentIfNotHandled()

        assertThat(result).isNotNull()
        assertThat(result).isEqualTo(10)
    }


    /*
        --------------------------------------------------------------------------------------------
            HAS BEEN HANDLED
        --------------------------------------------------------------------------------------------
    */

    @Test
    fun hasBeenHandled_getContentIfNotHandledCalled_hasBeenHandledValueChange() {
        val event = Event(10)

        assertThat(event.hasBeenHandled).isFalse()
        event.getContentIfNotHandled()
        assertThat(event.hasBeenHandled).isTrue()
    }

    @Test
    fun hasBeenHandled_peekContentCalled_hasBeenHandledDoesNotChange() {
        val event = Event(10)

        assertThat(event.hasBeenHandled).isFalse()
        event.peekContent()
        assertThat(event.hasBeenHandled).isFalse()
    }


    /*
        --------------------------------------------------------------------------------------------
            PEEK CONTENT
        --------------------------------------------------------------------------------------------
    */

    @Test
    fun peekContent_returnValue() {
        val event = Event(10)
        val result = event.peekContent()
        assertThat(result).isEqualTo(10)
    }

    @Test
    fun peekContent_contentAlreadyHandled_returnResult() {
        val event = Event(10)

        event.getContentIfNotHandled()
        val hasBeenHandled = event.hasBeenHandled
        val contentIfNotHandled = event.getContentIfNotHandled()
        val result = event.peekContent()

        assertThat(hasBeenHandled).isTrue()
        assertThat(contentIfNotHandled).isNull()
        assertThat(result).isNotNull()
        assertThat(result).isEqualTo(10)
    }

}