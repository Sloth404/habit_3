# Kotlin Coding Conventions for App Development

This document outlines the coding conventions to follow when developing an app in Kotlin. These conventions are designed to ensure code readability, maintainability, and consistency across the codebase.

---

## Project Structure

- **Use feature-based modularization**  
  Organize your project by feature (e.g., `login`, `dashboard`, `settings`) rather than by layer (e.g., `views`, `models`).

- **Recommended module layers**:
  ```
  - data/
  - domain/
  - presentation/
  - common/
  ```

---

## Naming Conventions

- **Classes and Interfaces**: `PascalCase`  
  ```kotlin
  class LoginViewModel
  interface UserRepository
  ```

- **Functions and Variables**: `camelCase`  
  ```kotlin
  val userName = "Alice"
  fun fetchUserData() { ... }
  ```

- **Constants**: `UPPER_SNAKE_CASE` (inside companion objects or objects)  
  ```kotlin
  const val MAX_RETRIES = 3
  ```

- **Packages**: `lowercase.without_underscores`  
  ```kotlin
  com.example.myapp.login
  ```

---

## Code Style

- **Use 4 spaces for indentation** (no tabs)
- **Line length**: Limit to 100 characters
- **Use trailing commas** in multi-line collections and function parameters
- **Prefer expression bodies** for simple functions:
  ```kotlin
  fun isLoggedIn() = user != null
  ```

---

## Control Flow

- Use `when` instead of complex `if-else` chains
- Avoid deeply nested blocks by using early returns
  ```kotlin
  if (user == null) return
  ```

---

## Best Practices

- **Immutable by default**: Use `val` instead of `var` unless reassignment is necessary.
- **Use data classes** for models:
  ```kotlin
  data class User(val id: String, val name: String)
  ```

- **Use Kotlin's null safety features** (`?`, `!!`, `?:`) thoughtfully.
  Avoid `!!` unless absolutely certain the value is non-null.

- **Leverage Kotlin extensions** and **higher-order functions** for reusable logic.

---

## Dependency Injection

- Use [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) or [Koin](https://insert-koin.io/) for DI.
- Annotate constructors with `@Inject` where possible.

---

## Testing

- Use JUnit and MockK for unit tests.
- Test naming convention: `methodName_condition_expectedResult()`
  ```kotlin
  fun loginUser_validCredentials_returnsSuccess()
  ```

- Place tests in `src/test/java/` following the same package structure.

---

## Android-Specific

- Use `ViewModel` and `LiveData` or `StateFlow` for state management.
- Prefer `sealed classes` for representing UI states and events.
- Use Jetpack Compose (if applicable) and follow Compose best practices.
- Don't place business logic in Activities or Fragments — use ViewModels.

---

## Resources

- [Kotlin Official Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Android Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)
- [Effective Kotlin](https://www.amazon.com/Effective-Kotlin-best-practices-Android/dp/B08G1K3T7L)

---

By adhering to these conventions, we ensure a clean, collaborative, and scalable Kotlin codebase.
