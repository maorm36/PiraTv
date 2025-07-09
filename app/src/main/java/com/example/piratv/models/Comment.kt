package com.example.piratv.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Comment(
    val id: String = "",
    val userId: String = "",
    val text: String = "",
    val rating: Int = 0,

    @ServerTimestamp
    val timestamp: Date? = Timestamp.now().toDate()
)
