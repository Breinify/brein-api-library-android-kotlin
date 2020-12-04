package com.brein.domain

/**
 * The type of the activity collected, i.e., one of search, login, logout, addToCart, removeFromCart, checkOut,
 * selectProduct, or other.
 */
object BreinActivityType {
    //  pre-defined activity types
    const val SEARCH = "search"
    const val LOGIN = "login"
    const val LOGOUT = "logout"
    const val ADD_TO_CART = "addToCart"
    const val REMOVE_FROM_CART = "removeFromCart"
    const val SELECT_PRODUCT = "selectProduct"
    const val CHECKOUT = "checkOut"
    const val PAGEVISIT = "pageVisit"
    const val OTHER = "other"
}