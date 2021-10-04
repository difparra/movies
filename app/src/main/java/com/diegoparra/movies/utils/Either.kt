package com.diegoparra.movies.utils

/**
 * Represents a value of one of two possible types (a disjoint union).
 * Instances of [Either] are either an instance of [Left] or [Right].
 * FP Convention dictates that [Left] is used for "failure"
 * and [Right] is used for "success".
 *
 * @see Left
 * @see Right
 */
sealed class Either<out L, out R> {
    //  In order to define static functions
    companion object {}

    /** * Represents the left side of [Either] class which by convention is a "Failure". */
    data class Left<out L>(val a: L) : Either<L, Nothing>()

    /** * Represents the right side of [Either] class which by convention is a "Success". */
    data class Right<out R>(val b: R) : Either<Nothing, R>()


    fun getOrNull(): R? =
        when (this) {
            is Left -> null
            is Right -> b
        }

    fun leftOrNull(): L? =
        when (this) {
            is Left -> a
            is Right -> null
        }
}

/*
    ------------------------------------------------------------------------------------------------
        BASIC FUNCTIONS
    ------------------------------------------------------------------------------------------------
 */

inline fun <L, R> Either<L, R>.getOrElse(onFailure: (failure: L) -> R): R =
    when (this) {
        is Either.Left -> onFailure(a)
        is Either.Right -> b
    }

inline fun <L,R,T> Either<L,R>.fold(onSuccess: (R) -> T, onFailure: (L) -> T): T =
    when (this) {
        is Either.Left -> onFailure(a)
        is Either.Right -> onSuccess(b)
    }



/*
    ------------------------------------------------------------------------------------------------
        CHAINING EITHER RETURNING ORIGINAL VALUE
    ------------------------------------------------------------------------------------------------
    Perform given function on the encapsulated value
    Returns the original `Either` unchanged.
 */

inline fun <L, R> Either<L, R>.onSuccess(fnR: (success: R) -> Unit): Either<L, R> {
    if (this is Either.Right) fnR(b)
    return this
}

inline fun <L, R> Either<L, R>.onFailure(fnL: (failure: L) -> Unit): Either<L, R> {
    if (this is Either.Left) fnL(a)
    return this
}


/*
    ------------------------------------------------------------------------------------------------
        TRANSFORMATIONS
    ------------------------------------------------------------------------------------------------
    Right-biased map() convention which means that Right is assumed to be the default case
    to operate on. If it is Left, operations like map, flatMap, ... return the Left value unchanged.
 */

inline fun <T, L, R> Either<L, R>.map(fn: (R) -> (T)): Either<L, T> =
    when (this) {
        is Either.Left -> this
        is Either.Right -> Either.Right(fn(b))
    }

inline fun <T, L, R> Either<L, R>.flatMap(fn: (R) -> Either<L, T>): Either<L, T> =
    when (this) {
        is Either.Left -> this
        is Either.Right -> fn(b)
    }


/*
    ------------------------------------------------------------------------------------------------
        CUSTOM FUNCTIONS HANDLING LIST OF EITHER
    ------------------------------------------------------------------------------------------------
 */

/**
 * Transform a List of Either<> into a Either<_,List>. (Left-Biased)
 * The returned value will be:
 *  - Failure:  If any of the elements in the list is Failure (Left)
 *  - Success:  If every item in the list is Success (Right) or original list is empty
 */
fun <L, R> List<Either<L, R>>.getFailuresOrRight(): Either<List<L>, List<R>> {
    val failures = this.filterIsInstance<Either.Left<L>>().map { it.a }
    return if (failures.isNotEmpty()) {
        Either.Left(failures)
    } else {
        Either.Right(this.map { (it as Either.Right).b })
    }
}

/**
 * Transform a List of Either<> into a Either<_,List>. (Left-Biased)
 * The returned value will be:
 *  - Failure:  If any of the elements in the list is Failure(Left).
 *              Returned Failure will be the first Failure encountered in the list.
 *  - Success:  If every item in the list is Success (Right).
 *              Returned Success will now represent the list<R> without failures.
 */
inline fun <L, R> List<Either<L, R>>.reduceFailuresOrRight(reduceFailure: (List<L>) -> L = { it.first() }): Either<L, List<R>> {
    return when (val mappedList = this.getFailuresOrRight()) {
        is Either.Left -> Either.Left(reduceFailure(mappedList.a))
        is Either.Right -> mappedList
    }
}


/*
    ------------------------------------------------------------------------------------------------
        CUSTOM FUNCTIONS FOR SUCCESS/FAILURE DATA HANDLING
    ------------------------------------------------------------------------------------------------
 */

/**
 *  runCatching -> Similar to kotlin.runCatching from Result class
 *  It will catch any exception and save as Either.Left
 */
inline fun <R> Either.Companion.runCatching(block: () -> R): Either<Exception, R> =
    try {
        Either.Right(block())
    } catch (e: Exception) {
        Either.Left(e)
    }

/**
 *  Transform to Resource
 */
fun <T> Either<Exception, T>.toResource(): Resource<T> {
    return when (this) {
        is Either.Left -> Resource.Error(this.a)
        is Either.Right -> Resource.Success(this.b)
    }
}