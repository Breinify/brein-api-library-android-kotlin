package com.brein.api

/**
 * Exception in case of wrong configuration
 */
class BreinInvalidConfigurationException : RuntimeException {
    constructor(e: Throwable?) : super(e) {}
    constructor(msg: String?) : super(msg) {}
    constructor(msg: String?, cause: Exception?) : super(msg, cause) {}
}