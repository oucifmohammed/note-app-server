package com.example.routes

import com.example.authentication.JwtService
import com.example.data.models.LoginRequest
import com.example.data.models.RegisterRequest
import com.example.data.models.Response
import com.example.data.models.UserEntity
import com.example.repository.Repository
import com.example.util.Constants.LOGIN_REQUEST
import com.example.util.Constants.REGISTER_REQUEST
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.mindrot.jbcrypt.BCrypt

@Location(REGISTER_REQUEST)
class UserRegisterRout()

@Location(LOGIN_REQUEST)
class UserLoginRout()

fun Route.userRoutes(
    repository: Repository,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {
    post<UserRegisterRout> {

        val registerRequest = try {
            call.receive<RegisterRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, Response(success = false, message = "Missing fields"))
            return@post
        }

        try {
            val user = UserEntity(registerRequest.email, registerRequest.name, hashFunction(registerRequest.password))
            repository.addUser(user)
            call.respond(HttpStatusCode.OK, Response(success = true, message = jwtService.generateToken(user)))
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                Response(success = false, e.message ?: "Some problem occurred, try again please")
            )
        }
    }

    post<UserLoginRout> {

        val loginRequest = try {
            call.receive<LoginRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, Response(success = false, message = "Missing fields"))
            return@post
        }

        try {
            val user = repository.findUserByEmail(loginRequest.email)

            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, Response(success = false, message = "Wrong Email"))
            } else {

                val doesPasswordMatch = BCrypt.checkpw(loginRequest.password,user.hashPassword)

                if (doesPasswordMatch) {
                    call.respond(HttpStatusCode.OK, Response(success = true, jwtService.generateToken(user)))
                } else {
                    call.respond(HttpStatusCode.BadRequest, Response(success = false, "Wrong credentials"))
                }
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                Response(success = false, e.message ?: "Some problem occurred, try again please")
            )
        }
    }
}