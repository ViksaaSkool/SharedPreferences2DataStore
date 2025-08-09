package com.droidconlisbon.sp2ds.ui.composables.data


import com.droidconlisbon.sp2ds.proto.User
import com.droidconlisbon.sp2ds.storage.Constants.DEFAULT_EXPERIENCE_LEVEL_VALUE

data class DataScreenState(
    var isInitialized: Boolean = false,
    var user: User = User.newBuilder()
        .setFirstName("")
        .setLastName("")
        .setPicUri("")
        .build(),
    var description: List<String> = emptyList(),
    var androidRate: Float = 5.0f,
    var canClear: Boolean = false,
    var canSave: Boolean = false,
) {
    fun isDataValid() =
        user.picUri.isNotEmpty()
                && user.firstName.isNotEmpty()
                && user.lastName.isNotEmpty()
                && description.isNotEmpty()

    fun hasDataChangedFromDefault() = user.picUri.isNotEmpty()
            || user.firstName.isNotEmpty()
            || user.lastName.isNotEmpty()
            || description.isNotEmpty()
            || androidRate != DEFAULT_EXPERIENCE_LEVEL_VALUE
}

fun DataScreenState.hasDataBeenChanged(
    u: User,
    desc: List<String>,
    rate: Float
) = user.picUri != u.picUri
        || user.firstName != u.firstName
        || user.lastName != u.lastName
        || description.toString() != desc.toString()
        || androidRate != rate
