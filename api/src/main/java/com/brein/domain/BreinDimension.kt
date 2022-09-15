package com.brein.domain

/**
 * Contains the Dimension to ask for
 */
class BreinDimension(private var dimensionFields: List<String>) {

    fun getDimensionFields(): List<String> =
        dimensionFields

    fun setDimensionFields(vararg dimensionFields: String): BreinDimension {
        this.dimensionFields = dimensionFields.toList()
        return this
    }

}