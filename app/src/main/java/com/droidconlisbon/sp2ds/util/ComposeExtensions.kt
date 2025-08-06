package com.droidconlisbon.sp2ds.util

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import timber.log.Timber


@Composable
fun Int.getPainter(): Painter = painterResource(id = this)

@Composable
fun Int.getVector(): ImageVector = ImageVector.vectorResource(id = this)

@Composable
fun Int.getString(): String = stringResource(id = this)

@Composable
fun Int.getColor(): Color = colorResource(id = this)

@Composable
fun Int.getDimen(): Dp = dimensionResource(id = this)

fun Modifier.hideSoftKeyboardOnOutsideClick(focusManager: FocusManager) =
    this.pointerInput(Unit) {
        detectTapGestures(onTap = {
            try {
                focusManager.clearFocus()
            } catch (e: Exception) {
                Timber.e("hideSoftKeyboardOnOutsideClick() | e = ${e.message}")
            }
        })
    }



@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun getScreenHeight() = LocalConfiguration.current.screenHeightDp.dp

