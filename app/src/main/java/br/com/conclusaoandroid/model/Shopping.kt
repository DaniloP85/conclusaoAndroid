package br.com.conclusaoandroid.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Shopping (
    var date:  String? = null,
    var marketplace:  String? = null,
    var items:  List<ShoppingItem>? = null
){
    companion object {
        const val FIELD_DATE = "date"
        const val FIELD_MARKPLACE = "marketplace"
        const val FIELD_ITEMS = "items"
    }
}