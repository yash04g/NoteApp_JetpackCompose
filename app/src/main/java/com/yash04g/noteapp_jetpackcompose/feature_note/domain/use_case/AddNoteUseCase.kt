package com.yash04g.noteapp_jetpackcompose.feature_note.domain.use_case

import com.yash04g.noteapp_jetpackcompose.feature_note.domain.model.InvalidNoteException
import com.yash04g.noteapp_jetpackcompose.feature_note.domain.model.Note
import com.yash04g.noteapp_jetpackcompose.feature_note.domain.repository.NoteRepository

class AddNoteUseCase(
    private val repository: NoteRepository
) {

    @Throws(InvalidNoteException::class)
    suspend operator fun invoke(note: Note) {
        if(note.title.isEmpty()) {
            throw InvalidNoteException("The title of the note is empty!")
        }
        if(note.title.length < 10) {
            throw InvalidNoteException("The length of title of the note is short!")
        }
        if(note.content.isEmpty()) {
            throw InvalidNoteException("The content of the note is empty!")
        }
        repository.insertNote(note)
    }
}