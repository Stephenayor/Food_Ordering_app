package com.example.yummy.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : Resource<T>(data), Flow<Resource<Any>> {
        override suspend fun collect(collector: FlowCollector<Resource<Any>>) {

        }
    }

    class Error<T>(message: String?, data: T? = null) : Resource<T>(data, message),
        Flow<Resource<Any>> {
        override suspend fun collect(collector: FlowCollector<Resource<Any>>) {

        }
    }

    class Loading<T> : Resource<T>(null, null), Flow<Resource<Any>> {
        override suspend fun collect(collector: FlowCollector<Resource<Any>>) {

        }
    }

    class J<T>: Resource<T>()
}

fun <T> Resource<T>.successOr(fallback: T): T {
    return (this as? Resource.Success<T>)?.data ?: fallback
}


sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

fun <T> Result<T>.successOr(fallback: T): T {
    return (this as? Result.Success<T>)?.data ?: fallback
}


open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}