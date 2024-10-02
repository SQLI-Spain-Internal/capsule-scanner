package com.sqli.capsulescanner.utilities

open class ResourceState<out T> {
    object Loading : ResourceState<Nothing>()
    data class Success<T>(val data: T) : ResourceState<T>()
    data class Error(val error: String) : ResourceState<Nothing>()
}