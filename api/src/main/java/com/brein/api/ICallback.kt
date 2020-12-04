package com.brein.api

import com.brein.domain.BreinResult

interface ICallback<T : BreinResult?> {
    fun callback(data: T)
}