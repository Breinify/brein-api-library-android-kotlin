package com.brein.api

import com.brein.domain.BreinConfig

/**
 * Base class for the secret strategy
 */
interface ISecretStrategy {
    /**
     * Creates the appropriate signature that is part of the request to
     * the Breinify Engine.
     *
     * @return creates signature
     */

    fun createSignature(config: BreinConfig): String
}