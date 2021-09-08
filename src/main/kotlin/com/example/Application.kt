package com.example

import com.example.authentication.JwtService
import com.example.authentication.hashPassword
import io.ktor.application.*
import com.example.plugins.*
import com.example.repository.DatabaseFactory
import com.example.repository.Repository
import io.ktor.locations.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {

    DatabaseFactory.init()
    val repository = Repository()
    val jwtService = JwtService()
    val hashFunction = { password: String -> hashPassword(password) }

    install(Locations)
    configureSerialization()
    configureSecurity(jwtService,repository)
    configureRouting(repository,jwtService,hashFunction)
}
