# Kotlin DSL for Apache HTTP Client

Library provide DSL for building simple HTTP requests over Apache client.

### Example
```kotlin
val response = buildHttpRequest {
    get("http://google.ru")
    header("content-type", "text/html")
    param("q", "kotlin dsl")
}
```

### Plan
- Extend return value
- Parse response body to models
- Support most common request types: GET, POST, PUT, DELETE