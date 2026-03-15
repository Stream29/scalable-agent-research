package ai.dify.stream.scripting

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost

internal val host: BasicJvmScriptingHost = BasicJvmScriptingHost()

internal val scriptEvaluationMutex: ReentrantLock = ReentrantLock()

@OptIn(ExperimentalAtomicApi::class)
private val scriptEvaluationCounter: AtomicInt = AtomicInt(0)

@OptIn(ExperimentalAtomicApi::class)
internal val nextScriptEvaluationId: Int
    get() = scriptEvaluationCounter.incrementAndFetch()