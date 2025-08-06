package com.droidconlisbon.sp2ds.model

typealias ProtoUser = com.droidconlisbon.sp2ds.proto.User

data class User(
    val firstName: String = "",
    val lastName: String = "",
    val picUri: String = ""
) {
    fun isValid() = firstName.isNotBlank() &&
            lastName.isNotBlank() &&
            picUri.isNotBlank()
}

// From domain User to ProtoUser
fun User.toProto(): ProtoUser {
    return ProtoUser.newBuilder()
        .setFirstName(firstName)
        .setLastName(lastName)
        .setPicUri(picUri)
        .build()
}

// From ProtoUser to domain User
fun ProtoUser.toUser(): User {
    return User(
        firstName = firstName,
        lastName = lastName,
        picUri = picUri
    )
}