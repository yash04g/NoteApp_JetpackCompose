package com.yash04g.noteapp_jetpackcompose.feature_note.presentation.add_edit_note

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yash04g.noteapp_jetpackcompose.feature_note.domain.model.InvalidNoteException
import com.yash04g.noteapp_jetpackcompose.feature_note.domain.model.Note
import com.yash04g.noteapp_jetpackcompose.feature_note.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    savedStateHandle: SavedStateHandle // Bundle that contains navigation args
) : ViewModel() {
    private val _noteTitle = mutableStateOf(
        NoteTextFieldState(
            hint = "Enter Title"
        )
    )
    val noteTitle: State<NoteTextFieldState> = _noteTitle

    private val _noteContent = mutableStateOf(
        NoteTextFieldState(
            hint = "Enter Content"
        )
    )
    val noteContent: State<NoteTextFieldState> = _noteContent

    private val _noteColor = mutableStateOf(Note.noteColors.random().toArgb())
    val noteColor: State<Int> = _noteColor

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentNodeId: Int? = null

    init {
        savedStateHandle.get<Int>("noteId")?.let { noteId ->
            if(noteId != -1) {
                viewModelScope.launch {
                    noteUseCases.getNote(id = noteId)?.also { note ->
                        currentNodeId = note.id
                        _noteTitle.value = noteTitle.value.copy(
                            text = note.title,
                            isHintVisible = false
                        )
                        _noteContent.value = noteContent.value.copy(
                            text = note.title,
                            isHintVisible = false
                        )
                        _noteColor.value = note.color
                    }
                }
            }
        }
    }

    fun onEvent(events: AddEditNoteEvent) {
        when (events) {
            is AddEditNoteEvent.ChangeColor -> {
                _noteColor.value = events.color
            }
            is AddEditNoteEvent.ChangeContentFocus -> {
                _noteContent.value.copy(
                    isHintVisible = !events.focusState.isFocused && _noteContent.value.hint.isEmpty()
                )
            }
            is AddEditNoteEvent.ChangeTitleFocus -> {
                _noteTitle.value.copy(
                    isHintVisible = !events.focusState.isFocused && _noteTitle.value.hint.isEmpty()
                )
            }
            is AddEditNoteEvent.EnteredContent -> {
                _noteContent.value.copy(text = events.value)
            }
            is AddEditNoteEvent.EnteredTitle -> {
                _noteTitle.value.copy(text = events.value)
            }
            AddEditNoteEvent.SaveNote -> {
                viewModelScope.launch {
                    try {
                        noteUseCases.addNote(
                            Note(
                                title = noteTitle.value.text,
                                content = noteContent.value.text,
                                timeStamp = System.currentTimeMillis(),
                                color = noteColor.value,
                                id = currentNodeId
                            )
                        )
                        _eventFlow.emit(UiEvent.SaveNote)
                    } catch (e: InvalidNoteException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackBar(
                                e.message ?: "Error: Couldn't save note"
                            )
                        )
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackBar(val message: String) : UiEvent()
        object SaveNote : UiEvent()
    }
}