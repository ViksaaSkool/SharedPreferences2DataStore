package com.droidconlisbon.sp2ds.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext


interface CoroutineProvider {
    fun createJob(): Job = SupervisorJob()
    fun createCoroutineScope(): CoroutineScope
    fun mainContext(): CoroutineContext = Dispatchers.Main.immediate
    fun defaultContext(): CoroutineContext = Dispatchers.Default
    fun ioContext(): CoroutineContext = Dispatchers.IO
}