package com.droidconlisbon.sp2ds.model


data class User(
    val firstName: String = "",
    val lastName: String = "",
    val picUri: String = ""
) {
    fun isValid() = firstName.isNotBlank() &&
            lastName.isNotBlank() &&
            picUri.isNotBlank()
}