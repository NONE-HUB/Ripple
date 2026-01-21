package com.example.ripple.viewmodel

data class ReportModel(
    val id: String = "",
    val userId: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)

data class FeedbackModel(
    val reportId: String = "",
    val userId: String = "",
    val message: String = "",
    val feedback: String = ""   // feedback field
)

