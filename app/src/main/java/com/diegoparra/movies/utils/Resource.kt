package com.diegoparra.movies.utils

/**
 * Used as a wrapper to set state on data going from viewModel to Ui.
 */
sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    class Success<T>(val data: T) : Resource<T>()
    class Error(val failure: Exception) : Resource<Nothing>()

    override fun toString(): String {
        return when(this) {
            Loading -> "Loading"
            is Success -> "Success[data=${this.data}]"
            is Error -> "Error[exception=${this.failure}]"
        }
    }

    /*
        Generic equals method was not working, it was previously returning false, for example,
        when having Success("123") == Success("123)
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if(this is Success && other is Success<*>) {
            return this.data == other.data
        }
        if(this is Error && other is Error) {
            return this.failure == other.failure
        }
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

}