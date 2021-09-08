package com.example.plugins

import io.ktor.sessions.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import com.example.authentication.JwtService
import com.example.repository.Repository
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureSecurity(jwtService: JwtService, repository: Repository) {
    data class MySession(val count: Int = 0)
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    install(Authentication) {
        jwt {
            verifier(jwtService.verifier)
            realm = "Note Server"
            validate {
                val playLoad = it.payload
                val email = playLoad.getClaim("email").asString()
                val user = repository.findUserByEmail(email)
                user
            }
        }
    }

    routing {
        get("/session/increment") {
            val session = call.sessions.get<MySession>() ?: MySession()
            call.sessions.set(session.copy(count = session.count + 1))
            call.respondText("Counter is ${session.count}. Refresh to increment.")
        }
    }
}
