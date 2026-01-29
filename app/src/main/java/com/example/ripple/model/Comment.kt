package com.example.ripple.model

import java.util.UUID

data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val username: String,
    val text: String
)