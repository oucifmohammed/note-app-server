package com.example.repository

import com.example.data.models.Note
import com.example.data.models.UserEntity
import com.example.data.tables.NoteTable
import com.example.data.tables.UserTable
import com.example.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class Repository {

    suspend fun addUser(user: UserEntity) {

        dbQuery {
            UserTable.insert { userTable ->
                userTable[email] = user.email
                userTable[name] = user.userName
                userTable[hashPassword] = user.hashPassword
            }
        }
    }

    suspend fun findUserByEmail(email: String) = dbQuery {
        UserTable.select {
            UserTable.email eq email
        }.map { rowToUser(it) }.singleOrNull()
    }

    private fun rowToUser(row: ResultRow?): UserEntity? {
        if (row == null) {
            return null
        }

        return UserEntity(
            email = row[UserTable.email],
            hashPassword = row[UserTable.hashPassword],
            userName = row[UserTable.name]
        )
    }

    //========Notes========

    suspend fun addNote(note: Note,email: String) {

        dbQuery {
            NoteTable.insert {noteTable ->
                noteTable[id] = note.id
                noteTable[userEmail] = email
                noteTable[noteTitle] = note.noteTitle
                noteTable[description] = note.description
                noteTable[date] = note.date
            }
        }
    }

    suspend fun getAllNotes(email: String): List<Note> = dbQuery {
        NoteTable.select {
            NoteTable.userEmail eq email
        }.mapNotNull { rowToNote(it) }
    }

    suspend fun updateNote(note: Note,email: String) = dbQuery {
        NoteTable.update(
            where = {
                NoteTable.userEmail.eq(email) and NoteTable.id.eq(note.id)
            }
        ) {noteTable ->
            noteTable[noteTitle] = note.noteTitle
            noteTable[description] = note.description
            noteTable[date] = note.date
        }
    }

    suspend fun deleteNote(id: String,email: String) = dbQuery {
        NoteTable.deleteWhere {
            NoteTable.userEmail.eq(email) and NoteTable.id.eq(id)
        }
    }

    private fun rowToNote(row: ResultRow?): Note? {
        if(row == null) {
            return null
        }

        return Note(
            id = row[NoteTable.id],
            email = row[NoteTable.userEmail],
            noteTitle = row[NoteTable.noteTitle],
            description = row[NoteTable.description],
            date = row[NoteTable.date]
        )
    }
}