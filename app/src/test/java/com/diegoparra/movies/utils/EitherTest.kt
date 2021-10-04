package com.diegoparra.movies.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class EitherTest {

    @Test
    fun either_right_returnCorrectType() {
        val eitherRight: Either<String, Int> = Either.Right(10)
        assertThat(eitherRight).isInstanceOf(Either::class.java)
        assertThat(eitherRight is Either.Right).isTrue()
        assertThat(eitherRight is Either.Left).isFalse()
    }

    @Test
    fun either_left_returnCorrectType() {
        val eitherLeft: Either<String, Int> = Either.Left("left")
        assertThat(eitherLeft).isInstanceOf(Either::class.java)
        assertThat(eitherLeft is Either.Left).isTrue()
        assertThat(eitherLeft is Either.Right).isFalse()
    }

    /*
        --------------------------------------------------------------------------------------------
            BASIC FUNCTIONS
            get & fold
        --------------------------------------------------------------------------------------------
    */

    @Test
    fun getOrNull_right_returnValue() {
        val value = 10
        val right: Either<String, Int> = Either.Right(value)

        val result = right.getOrNull()

        assertThat(result).isEqualTo(value)
    }

    @Test
    fun getOrNull_left_returnNull() {
        val left: Either<String, Int> = Either.Left("left")
        val result = left.getOrNull()
        assertThat(result).isNull()
    }


    @Test
    fun leftOrNull_right_returnNull() {
        val right: Either<String, Int> = Either.Right(10)
        val result = right.leftOrNull()
        assertThat(result).isNull()
    }

    @Test
    fun leftOrNull_left_returnValue() {
        val left: Either<String, Int> = Either.Left("left")
        val result = left.leftOrNull()
        assertThat(result).isEqualTo("left")
    }


    @Test
    fun getOrElse_right_ignoreFunctionAndReturnValue() {
        val right: Either<String, Int> = Either.Right(10)
        val result = right.getOrElse {
            throw Exception("This function shouldn't be invoked")
        }
        assertThat(result).isEqualTo(10)
    }

    @Test
    fun getOrElse_left_returnValueFromFunction() {
        val left: Either<String, Int> = Either.Left("left")
        val result = left.getOrElse {
            assertThat(it).isEqualTo("left")
            15
        }
        assertThat(result).isEqualTo(15)
    }

    @Test
    fun getOrElse_left_invokeFunctionWithLeftValueAndReturnItsResult() {
        val leftValue = "left"
        val left: Either<String, Int> = Either.Left(leftValue)
        val result = left.getOrElse {
            assertThat(it).isEqualTo(leftValue)
            it.length
        }
        assertThat(result).isEqualTo(leftValue.length)
    }

    @Test
    fun fold_right_invokeOnSuccessWithRightValueAndReturnItsResult() {
        val right: Either<String, Int> = Either.Right(10)
        val result = right.fold(
            onSuccess = {
                assertThat(it).isEqualTo(10)
                it + 15
            },
            onFailure = { throw Exception("This function shouldn't be invoked") }
        )
        assertThat(result).isEqualTo(25)
    }

    @Test
    fun fold_left_invokeOnFailureWithLeftValueAndReturnItsResult() {
        val leftValue = "left"
        val left: Either<String, Int> = Either.Left(leftValue)
        val result = left.fold(
            onSuccess = { throw Exception("This function shouldn't be invoked") },
            onFailure = {
                assertThat(it).isEqualTo(leftValue)
                leftValue.length
            }
        )
        assertThat(result).isEqualTo(leftValue.length)
    }


    /*
        --------------------------------------------------------------------------------------------
            CHAINING EITHER RETURNING ORIGINAL VALUE
            onSuccess - onFailure
        --------------------------------------------------------------------------------------------
    */

    @Test
    fun onSuccess_right_invokeFunctionAndReturnOriginalEither() {
        var called = false
        val right: Either<String, Int> = Either.Right(10)
        val result = right.onSuccess {
            assertThat(it).isEqualTo(10)
            called = true
        }
        assertThat(called).isTrue()
        assertThat(result).isEqualTo(right)
    }

    @Test
    fun onSuccess_left_doesNotInvokeFunctionAndReturnOriginalEither() {
        var called = false
        val left: Either<String, Int> = Either.Left("left")
        val result = left.onSuccess {
            called = true
            throw Exception("This function shouldn't be invoked")
        }
        assertThat(called).isFalse()
        assertThat(result).isEqualTo(left)
    }

    @Test
    fun onFailure_right_doesNotInvokeFunctionAndReturnOriginalEither() {
        var called = false
        val right: Either<String, Int> = Either.Right(10)
        val result = right.onFailure {
            called = true
            throw Exception("This function shouldn't be invoked")
        }
        assertThat(called).isFalse()
        assertThat(result).isEqualTo(right)
    }

    @Test
    fun onFailure_left_invokeFunctionAndReturnOriginalEither() {
        var called = false
        val left: Either<String, Int> = Either.Left("left")
        val result = left.onFailure {
            assertThat(it).isEqualTo("left")
            called = true
        }
        assertThat(called).isTrue()
        assertThat(result).isEqualTo(left)
    }


    /*
        --------------------------------------------------------------------------------------------
            TRANSFORMATIONS
            map - flatMap
        --------------------------------------------------------------------------------------------
    */

    @Test
    fun map_right_invokeFunctionWithRightValueAndReturnEitherMapped() {
        val right: Either<String, Int> = Either.Right(10)
        val result = right.map {
            assertThat(it).isEqualTo(10)
            "new"
        }
        assertThat(result).isEqualTo(Either.Right("new"))
    }

    @Test
    fun map_left_doesNotInvokeFunctionAndReturnOriginalEither() {
        val left: Either<String, Int> = Either.Left("left")
        val result = left.map {
            throw Exception("This function shouldn't be invoked")
        }
        assertThat(result).isEqualTo(left)
    }

    @Test
    fun flatMap_right_invokeFunctionWithRightValueAndReturnNewEither() {
        val right: Either<String, Int> = Either.Right(10)
        val result = right.flatMap {
            assertThat(it).isEqualTo(10)
            Either.Left("left")
        }
        assertThat(result).isEqualTo(Either.Left("left"))
    }

    @Test
    fun flatMap_left_doesNotInvokeFunctionAndReturnOriginalEither() {
        val left: Either<String, Int> = Either.Left("left")
        val result = left.flatMap<Unit, String, Int> {
            throw Exception("This function shouldn't be invoked")
        }
        assertThat(result).isEqualTo(left)
    }


    /*
        --------------------------------------------------------------------------------------------
            CUSTOM FUNCTIONS HANDLING LIST OF EITHER
            getFailuresOrRight - reduceFailuresOrRight
        --------------------------------------------------------------------------------------------
    */

    @Test
    fun getFailuresOrRight_listAllRights_returnEitherRightWithRightValues() {
        val list: List<Either<String, Int>> = listOf(
            Either.Right(10),
            Either.Right(15),
            Either.Right(20)
        )
        val result = list.getFailuresOrRight()
        assertThat(result).isEqualTo(Either.Right(listOf(10,15,20)))
    }

    @Test
    fun getFailuresOrRight_listAllLefts_returnEitherLeftWithLeftValues() {
        val list: List<Either<String, Int>> = listOf(
            Either.Left("left1"),
            Either.Left("left2"),
            Either.Left("left3")
        )
        val result = list.getFailuresOrRight()
        assertThat(result).isEqualTo(Either.Left(listOf("left1", "left2", "left3")))
    }

    @Test
    fun getFailuresOrRight_emptyList_returnEitherRightEmptyListOfTypeRight() {
        val list: List<Either<String, Int>> = listOf()
        val result = list.getFailuresOrRight()
        assertThat(result).isEqualTo(Either.Right(listOf<Int>()))
    }

    @Test
    fun getFailuresOrRight_listWithLeftsAndRights_returnEitherLeftWithLeftValues() {
        val list: List<Either<String, Int>> = listOf(
            Either.Left("left1"),
            Either.Right(1),
            Either.Left("left2"),
            Either.Right(2)
        )
        val result = list.getFailuresOrRight()
        assertThat(result).isEqualTo(Either.Left(listOf("left1", "left2")))
    }


    @Test
    fun reduceFailuresOrRight_listAllRights_returnEitherRightWithRightValues() {
        val list: List<Either<String, Int>> = listOf(
            Either.Right(10),
            Either.Right(15),
            Either.Right(20)
        )
        val result = list.reduceFailuresOrRight {
            throw Exception("This function shouldn't be invoked")
        }
        assertThat(result).isEqualTo(Either.Right(listOf(10,15,20)))
    }

    @Test
    fun reduceFailuresOrRight_listAllLefts_invokeFunctionWithLeftsAndReturnEitherLeftWithFunctionResult() {
        val list: List<Either<String, Int>> = listOf(
            Either.Left("left1"),
            Either.Left("left2"),
            Either.Left("left3")
        )
        val result = list.reduceFailuresOrRight {
            assertThat(it).isEqualTo(listOf("left1", "left2", "left3"))
            it.first()
        }
        assertThat(result).isEqualTo(Either.Left("left1"))
    }

    @Test
    fun reduceFailuresOrRight_emptyList_returnEitherRightEmptyListOfTypeRight() {
        val list: List<Either<String, Int>> = listOf()
        val result = list.reduceFailuresOrRight {
            throw Exception("This function shouldn't be invoked")
        }
        assertThat(result).isEqualTo(Either.Right(listOf<Int>()))
    }

    @Test
    fun reduceFailuresOrRight_listWithLeftsAndRights_invokeFunctionOnLeftsAndReturnEitherLeftWithFunctionResult() {
        val list: List<Either<String, Int>> = listOf(
            Either.Left("left1"),
            Either.Right(1),
            Either.Left("left2"),
            Either.Right(2)
        )
        val result = list.reduceFailuresOrRight {
            assertThat(it).isEqualTo(listOf("left1", "left2"))
            it.first()
        }
        assertThat(result).isEqualTo(Either.Left("left1"))
    }


    /*
        --------------------------------------------------------------------------------------------
            CUSTOM FUNCTIONS FOR SUCCESS/FAILURE DATA HANDLING
        --------------------------------------------------------------------------------------------
    */

    @Test
    fun runCatching_noExceptions_returnRightWithFunctionResult() {
        val result = Either.runCatching {
            "success"
        }
        assertThat(result).isInstanceOf(Either.Right::class.java)
        assertThat(result).isEqualTo(Either.Right("success"))
    }

    @Test
    fun runCatching_exception_returnLeftWithException() {
        val exception = IllegalArgumentException()
        val result = Either.runCatching {
            throw exception
        }
        assertThat(result).isInstanceOf(Either.Left::class.java)
        assertThat(result).isEqualTo(Either.Left(exception))
    }

    @Test
    fun toResource_right_returnResourceSuccessWithRightValue() {
        val right: Either<Exception, Int> = Either.Right(10)
        val result = right.toResource()
        assertThat(result).isInstanceOf(Resource.Success::class.java)
        assertThat((result as Resource.Success).data).isEqualTo(10)
    }

    @Test
    fun toResource_left_returnResourceErrorWithExceptionLeftValue() {
        val exception = IllegalArgumentException()
        val left: Either<Exception, Int> = Either.Left(exception)
        val result = left.toResource()
        assertThat(result).isInstanceOf(Resource.Error::class.java)
        assertThat((result as Resource.Error).failure).isEqualTo(exception)
    }

}