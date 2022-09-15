package com.brein.api

import com.brein.domain.BreinResult

interface IAsyncExecutable<R : BreinResult?> {
    /**
     * Method to execute the request asynchronous with a callback.
     *
     * @param callback the callback containing the response of the request, can be `null`
     */
    fun execute(callback: ICallback<R>?)
}