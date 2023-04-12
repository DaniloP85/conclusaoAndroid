package br.com.conclusaoandroid.model

data class ShoppingItemFormatted(
    val description: String? = null,
    val value: String? = null,
) {
    companion object {
        const val FIELD_DESCRIPTION = "description"
        const val FIELD_VALUE = "value"
    }
}

