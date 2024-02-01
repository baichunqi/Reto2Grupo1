package com.example.reto2grupo1.utils

data class Resource<out T>(val status: Status, val data: T?, val message: String?, val code: Int?) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data, null, null)
        }

        fun <T> success(): Resource<T> {
            return Resource(Status.SUCCESS, null, null, null)
        }

        fun <T> error(message: String, data: T? = null, code: Int? = null): Resource<T> {
            return Resource(Status.ERROR, data, message, code)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null, null)
        }
    }
}
