package ai.dify.stream.tool.kotlinscript

import ai.dify.stream.tool.kotlinscript.contract.ScriptContext
import org.jetbrains.kotlin.mainKts.CompilerOptions
import org.jetbrains.kotlin.mainKts.Import
import org.jetbrains.kotlin.mainKts.MainKtsConfigurator
import org.jetbrains.kotlin.mainKts.MainKtsScriptDefinition
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.implicitReceivers
import kotlin.script.experimental.api.refineConfiguration
import kotlin.script.experimental.dependencies.DependsOn
import kotlin.script.experimental.dependencies.Repository
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm

internal val <T : ScriptContext> T.compilationConfiguration: ScriptCompilationConfiguration.Builder.() -> Unit
    get() = {
        defaultImports.invoke(
            this@compilationConfiguration::class,
            DependsOn::class,
            Repository::class,
            Import::class,
            CompilerOptions::class,
        )
        defaultImports.append(this@compilationConfiguration.defaultImports)
        jvm {
            dependenciesFromClassContext(
                MainKtsScriptDefinition::class,
                "kotlin-main-kts",
                "kotlin-stdlib",
                "kotlin-reflect",
                wholeClasspath = true,
            )
        }
        refineConfiguration {
            onAnnotations(
                DependsOn::class,
                Repository::class,
                Import::class,
                CompilerOptions::class,
                handler = MainKtsConfigurator()
            )
        }
        implicitReceivers(this@compilationConfiguration::class)
    }