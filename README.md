# Kotlin DSL for Apache HTTP Client

Library provide DSL for building simple HTTP requests over Apache HTTP client.

### Examples
Building request:
```kotlin
val response: Response = buildHttpRequest {
    get("http://google.ru")
    header("content-type", "text/html")
    param("q", "kotlin dsl")
}
```

Working with response:
```kotlin
val response: Response = request {
    get("https://randomuser.me/api/?gender=female")
}

println("Request url ${response.url} done with code ${response.code} and body ${response.body}")
```

Parsing results:
```kotlin
val response: Response = request {
    post("https://example.com/api/create-something")
}

val result: ResultModel = response.parse()
```