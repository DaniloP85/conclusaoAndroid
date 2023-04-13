package br.com.conclusaoandroid.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Shopping (
    var date:  Timestamp? = null,
    var marketplace:  String? = null,
    var userId:  String? = null,
    var documentId:  String? = null,
    var total:  Double? = null
)