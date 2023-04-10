package br.com.conclusaoandroid.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class ShoppingItem(
    val description: String? = null,
    val value: Double? = null,
){

    companion object {

        const val FIELD_DESCRIPTION = "description"
        const val FIELD_VALUE = "value"
    }
}