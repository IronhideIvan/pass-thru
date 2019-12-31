package com.passthru.android.util

import com.passthru.android.ui.notifications.DebuggerFragment
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

object Globals {
    var debuggerFragment: AtomicReference<DebuggerFragment?> = AtomicReference(null)
    var debugMode: AtomicBoolean = AtomicBoolean(false)
}