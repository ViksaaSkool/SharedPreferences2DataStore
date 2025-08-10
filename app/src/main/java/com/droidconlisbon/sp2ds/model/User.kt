package com.droidconlisbon.sp2ds.model

import com.droidconlisbon.sp2ds.proto.UserProto

data class User(
    val firstName: String = "",
    val lastName: String = "",
    val picUri: String = ""
) {
    fun isValid() = firstName.isNotBlank() &&
            lastName.isNotBlank() &&
            picUri.isNotBlank()
}


fun User.toProto(): UserProto {
    return UserProto.newBuilder()
        .setFirstName(firstName)
        .setLastName(lastName)
        .setPicUri(picUri)
        .build()
}


fun UserProto.toDomain(): User {
    return User(
        firstName = firstName,
        lastName = lastName,
        picUri = picUri
    )
}