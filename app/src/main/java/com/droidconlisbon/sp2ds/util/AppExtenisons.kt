package com.droidconlisbon.sp2ds.util

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.droidconlisbon.sp2ds.storage.Constants.DEFAULT_APP_DATE_FORMAT
import com.droidconlisbon.sp2ds.storage.Constants.DEFAULT_TIMEOUT_TIME_IN_MIN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun String.toProtoStoreName() = "${lowercase()}.pb"

/**
 * Calculate the difference between now and given date and return as: ${days}d ${hours}h ${minutes}m
 */
fun String.calculateDateDifference(
    pattern: String = DEFAULT_APP_DATE_FORMAT,
    timeoutTimeInMin: Int = DEFAULT_TIMEOUT_TIME_IN_MIN
): String {
    if (isEmpty()) {
        return ""
    }
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val inputDate = LocalDateTime.parse(this, formatter).plusMinutes(timeoutTimeInMin.toLong())
    val now = LocalDateTime.now()
    if (now.isAfter(inputDate)) return "0m 0s"

    val duration = Duration.between(now, inputDate)
    val minutes = duration.toMinutes()
    val seconds = duration.seconds % 60

    return "${minutes}m ${seconds}s"
}

fun String.hasMinutesPassed(
    minutes: Int = DEFAULT_TIMEOUT_TIME_IN_MIN,
    format: String = DEFAULT_APP_DATE_FORMAT
): Boolean {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    val savedDate = sdf.parse(this) ?: return false
    val now = Date()

    val diffMillis = now.time - savedDate.time
    val minutesInMillis = minutes * 60 * 1000

    return diffMillis >= minutesInMillis
}

fun Long.toFormattedDate(format: String = DEFAULT_APP_DATE_FORMAT): String =
    SimpleDateFormat(format, Locale.getDefault()).format(Date(this))


suspend fun startCountdown(
    targetDate: String,
    pattern: String = DEFAULT_APP_DATE_FORMAT,
    onTick: (String) -> Unit
) {
    while (true) {
        val remainingTime = targetDate.calculateDateDifference(pattern)
        if (remainingTime.isEmpty()) break
        onTick(remainingTime)
        if (remainingTime.trim() == "0s") {
            break
        }
        delay(1000)
    }
}

fun Context.requestPermissionAndLaunch(
    permission: String,
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    onGranted: () -> Unit
) {
    if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
        onGranted()
    } else {
        permissionLauncher.launch(permission)
    }
}

suspend fun Context.createUriForImage() = withContext(Dispatchers.IO) {
    try {
        val file = async {
            createJPGFile()
        }
        return@withContext FileProvider.getUriForFile(
            this@createUriForImage,
            "$packageName.provider",
            file.await()
        )
    } catch (io: IOException) {
        io.printStackTrace()
        return@withContext null
    }
}

private fun Context.createJPGFile(): File = File.createTempFile(
    "JPEG_${System.currentTimeMillis()}_",
    ".jpg",
    filesDir
)

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, duration).show()

fun List<String>.toCommaSeparatedString() = toString().replace("]", "").replace("[", "")