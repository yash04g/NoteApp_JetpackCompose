package com.yash04g.noteapp_jetpackcompose.di

import android.app.Application
import androidx.room.Room
import com.yash04g.noteapp_jetpackcompose.feature_note.data.data_source.NoteDatabase
import com.yash04g.noteapp_jetpackcompose.feature_note.data.repository.NoteRepositoryImpl
import com.yash04g.noteapp_jetpackcompose.feature_note.domain.repository.NoteRepository
import com.yash04g.noteapp_jetpackcompose.feature_note.domain.use_case.DeleteNoteUseCase
import com.yash04g.noteapp_jetpackcompose.feature_note.domain.use_case.GetNotesUseCase
import com.yash04g.noteapp_jetpackcompose.feature_note.domain.use_case.NoteUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(application: Application): NoteDatabase {
        return Room.databaseBuilder(
            application,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(noteDb: NoteDatabase): NoteRepository {
        return NoteRepositoryImpl(noteDb.noteDao)
    }

    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getNotes = GetNotesUseCase(repository),
            deleteNote = DeleteNoteUseCase(repository)
        )
    }
}