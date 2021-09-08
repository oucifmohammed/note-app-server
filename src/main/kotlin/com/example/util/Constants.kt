package com.example.util

object Constants {

    private const val API_VERSION = "/v1"
    private const val USERS = "$API_VERSION/users"
    const val REGISTER_REQUEST = "$USERS/register"
    const val LOGIN_REQUEST = "$USERS/login"

    const val NOTES = "$API_VERSION/notes"
    const val CREATE_NOTES = "$NOTES/create"
    const val UPDATE_NOTES = "$NOTES/update"
    const val DELETE_NOTES = "$NOTES/delete"
}