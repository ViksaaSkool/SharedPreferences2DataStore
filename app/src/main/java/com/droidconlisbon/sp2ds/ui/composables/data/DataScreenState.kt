package com.droidconlisbon.sp2ds.ui.composables.data

import com.droidconlisbon.sp2ds.model.User
import com.droidconlisbon.sp2ds.storage.Constants.DEFAULT_EXPERIENCE_LEVEL_VALUE

data class DataScreenState(
    val user: User = User(),
    val description: List<String> = emptyList(),
    val androidRate: Float = 5.0f,
    val canClear: Boolean = false,
    val canSave: Boolean = false,
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
