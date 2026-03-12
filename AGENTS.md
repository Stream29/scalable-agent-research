# Repository Guidelines

## Project Structure & Module Organization
This repository centers on [`scalable-agent-demo/`](./scalable-agent-demo), a single-module Kotlin/JVM app built with Gradle. Production code lives in `scalable-agent-demo/src/main/kotlin`, resources in `scalable-agent-demo/src/main/resources`, tests in `scalable-agent-demo/src/test/kotlin`, and test fixtures in `scalable-agent-demo/src/test/resources`. Keep new code under the existing `ai.dify.stream` package tree.

`reference-repository/koog` is a Git submodule that provides upstream examples and API references. Do not add feature work there unless you are intentionally updating the submodule. Root-level notes such as `FirstQuestions.md` are research artifacts, not runtime code.

## Build, Test, and Development Commands
Run commands from `scalable-agent-demo/`:

- `./gradlew classes` compiles the main source set without packaging.
- `./gradlew test` runs the Kotlin test suite.
- `./gradlew build` compiles, tests, and packages the app.
- `./gradlew check` runs verification tasks, including ABI checks when configured.
- `./gradlew clean` removes generated output.
- `git submodule update --init --recursive` fetches the `koog` reference code after clone.

Run `src/main/kotlin/Main.kt` from IntelliJ IDEA for local execution; a Gradle `run` task is not configured in this project.

## Coding Style & Naming Conventions
Use 4-space indentation and Kotlin defaults from IntelliJ. Keep packages lowercase (`ai.dify.stream...`), classes and objects in `PascalCase`, functions and properties in `camelCase`, and constants in `UPPER_SNAKE_CASE`. Prefer small files with one primary responsibility. Name files after the main type they contain, for example `AgentRunner.kt`.

No formatter or linter is wired into this demo project yet, so run the IDE Kotlin formatter before committing.

## Testing Guidelines
Use `kotlin.test` for unit tests. Place tests under `src/test/kotlin` with the same package structure as the code they cover and name files `*Test.kt`. Add or update tests for every behavior change; there is no numeric coverage gate, but new logic should cover success paths, failures, and integration boundaries.

## Commit & Pull Request Guidelines
Recent history uses short Conventional Commit-style subjects such as `feat: add scalable-agent-demo project` and `chore: don't include koog as build`. Follow `type: imperative summary`, keeping the subject lowercase and specific.

Pull requests should describe the behavior change, list the commands run (`test`, `build`, or `check`), and link the relevant issue or note. Include console output only when it clarifies a runtime change. Flag any submodule pointer updates explicitly.
