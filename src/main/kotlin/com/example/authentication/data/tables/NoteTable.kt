package com.example.data.tables

import org.jetbrains.exposed.sql.Table

object NoteTable: Table(){

    val id = varchar("id",512)
    val userEmail = varchar("email",512).references(UserTable.email)
    val noteTitle = text("noteTitle")
    val description = text("description")
    val date = long("date")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}