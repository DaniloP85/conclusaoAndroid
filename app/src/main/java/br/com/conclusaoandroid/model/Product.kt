package br.com.conclusaoandroid.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Product (
    val description: String? = null,
    val value: Double? = null,
    var documentId:  String? = null,
)