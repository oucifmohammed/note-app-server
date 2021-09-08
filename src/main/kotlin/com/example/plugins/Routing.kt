package com.example.plugins

import com.example.authentication.JwtService
import com.example.data.models.UserEntity
import com.example.repository.Repository
import com.example.routes.noteRoutes
import com.example.routes.userRoutes
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting(repository: Repository, jwtService: JwtService, hashFunction: (String) -> String) {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/token") {
            val email = call.request.queryParameters["email"]!!
            val password = call.request.queryParameters["password"]!!
            val username = call.request.queryParameters["username"]!!

            val user = UserEntity(email, username, password)

            call.respond(jwtService.generateToken(user))
        }

        userRoutes(
            repository,
            jwtService,
            hashFunction
        )

        noteRoutes(
            repository,
        )
    }
}
