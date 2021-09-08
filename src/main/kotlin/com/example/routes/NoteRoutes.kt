package com.example.routes

import com.example.data.models.Note
import com.example.data.models.Response
import com.example.data.models.UserEntity
import com.example.repository.Repository
import com.example.util.Constants.CREATE_NOTES
import com.example.util.Constants.DELETE_NOTES
import com.example.util.Constants.NOTES
import com.example.util.Constants.UPDATE_NOTES
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

@KtorExperimentalLocationsAPI
@Location(NOTES)
class NoteGetRoute

@KtorExperimentalLocationsAPI
@Location(CREATE_NOTES)
class NoteCreateRoute

@KtorExperimentalLocationsAPI
@Location(UPDATE_NOTES)
class NoteUpdateRoute

@KtorExperimentalLocationsAPI
@Location(DELETE_NOTES)
class NoteDeleteRoute

@KtorExperimentalLocationsAPI
fun Route.noteRoutes(
    repository: Repository,
) {
    authenticate {
        post<NoteCreateRoute> {
            val note = try {
                call.receive<Note>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, Response(success = false, "Missing Fields"))
                return@post
            }

            try {
                val email = call.principal<UserEntity>()!!.email
                repository.addNote(note, email)
                call.respond(HttpStatusCode.OK, Response(success = true, "Note Added Successfully!"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, Response(success = false, e.message ?: "Some problem occurred"))
            }
        }

        get<NoteGetRoute> {

            try {
                val email = call.principal<UserEntity>()!!.email
                val notes = repository.getAllNotes(email)
                call.respond(HttpStatusCode.OK, notes)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, e.message ?: "Some problem occurred")
            }
        }

        post<NoteUpdateRoute> {
            val note = try {
                call.receive<Note>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, Response(success = false, "Missing Fields"))
                return@post
            }

            try {
                val email = call.principal<UserEntity>()!!.email
                repository.updateNote(note, email)
                call.respond(HttpStatusCode.OK, Response(success = true, "Note Updated Successfully!"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, Response(success = false, e.message ?: "Some problem occurred"))
            }
        }

        delete<NoteDeleteRoute> {
            val noteId = try {
                call.request.queryParameters["id"]!!
            }catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest,Response(success = false,"id parameter is not present"))
                return@delete
            }

            try {
                val email = call.principal<UserEntity>()!!.email
                repository.deleteNote(noteId,email)

                call.respond(HttpStatusCode.OK,Response(success = true,"Note Deleted successfully"))
            }catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest,Response(success = false,"id parameter is not present"))
            }
        }
    }
}