package com.droidconlisbon.sp2ds.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

class CoroutineProviderImpl @Inject constructor() : CoroutineProvider {
    override fun createJob(): Job = SupervisorJob()

    override fun createCoroutineScope(): CoroutineScope = CoroutineScope(ioContext() + createJob())
}
