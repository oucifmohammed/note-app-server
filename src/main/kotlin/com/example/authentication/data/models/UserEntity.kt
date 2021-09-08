package com.example.data.models

import io.ktor.auth.*

data class UserEntity(
    val email: String,
    val userName: String,
    val hashPassword: String
): Principal