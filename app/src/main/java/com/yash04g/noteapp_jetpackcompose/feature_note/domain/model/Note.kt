package com.yash04g.noteapp_jetpackcompose.feature_note.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yash04g.noteapp_jetpackcompose.ui.theme.*

@Entity
data class Note(
    val title: String,
    val content: String,
    val timeStamp: Long,
    val color: Int,
    @PrimaryKey val id: Int? = null,
) {
    companion object {
        val noteColors = listOf(RedOrange, RedPink, LightGreen, Violet, BabyBlue) // Without creating instance of Note we can access as Note.noteColors
    }
}

