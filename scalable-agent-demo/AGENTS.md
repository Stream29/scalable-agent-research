# Cowork Requirements

Unless specified, always respond in Chinese.

Never use `gradlew` without `--no-daemon`.

# Kode Development SOPs

This file should be updated when essential. (For example, new module being added)
## Coding Standards

### General Principles

1. Follow Kotlin coding conventions
2. Use meaningful variable and function names
3. Keep functions small and focused
4. Write self-documenting code with clear intent
5. Avoid using default parameters
6. Use named parameters when it's better

### Suppress Scope

- Avoid file-level `@file:Suppress` whenever possible.
- Prefer the narrowest suppression scope (statement/property/function/class) to keep warnings visible elsewhere.

## Dependency Management

### Adding Dependencies

1. Add dependencies to `gradle/libs.versions.toml` (version catalog)
2. Reference them in module `build.gradle.kts` files
3. Run `./gradlew build --refresh-dependencies` to update

### Time Dependency Compatibility

- Keep `kotlinx-datetime` on `0.7.1-0.6.x-compat` until Koog public APIs fully migrate away from
  `kotlinx.datetime.Instant/Clock`.
- Do not switch to plain `0.7.1` while Koog artifacts in use still require old ABI classes at runtime.
