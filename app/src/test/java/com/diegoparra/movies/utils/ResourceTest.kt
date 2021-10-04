package com.diegoparra.movies.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ResourceTest {

    @Test
    fun equals_resourceSuccess_true() {
        val data = "123"
        val resource1 = Resource.Success(data)
        val resource2 = Resource.Success(data)
        assertThat(resource1).isEqualTo(resource2)
    }

    @Test
    fun equals_resourceSuccess_false() {
        val resource1 = Resource.Success("123")
        val resource2 = Resource.Success("abcd")
        assertThat(resource1).isNotEqualTo(resource2)
    }

    @Test
    fun equals_resourceLoading() {
        val resource1 = Resource.Loading
        val resource2 = Resource.Loading
        assertThat(resource1).isEqualTo(resource2)
    }

    @Test
    fun equals_resourceError_true() {
        val exception = IllegalArgumentException()
        val resource1 = Resource.Error(exception)
        val resource2 = Resource.Error(exception)
        assertThat(resource1).isEqualTo(resource2)
    }

    @Test
    fun equals_resourceError_false() {
        val resource1 = Resource.Error(IllegalArgumentException())
        val resource2 = Resource.Error(NoSuchElementException())
        assertThat(resource1).isNotEqualTo(resource2)
    }

    @Test
    fun equals_resource_differentSubtypes() {
        val resourceSuccess = Resource.Success("123")
        val resourceError = Resource.Error(NoSuchElementException())
        val resourceLoading = Resource.Loading

        assertThat(resourceSuccess).isNotEqualTo(resourceError)
        assertThat(resourceSuccess).isNotEqualTo(resourceLoading)
        assertThat(resourceError).isNotEqualTo(resourceLoading)
    }

}