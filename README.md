# Kotlin DSL for Apache HTTP Client

Library provide DSL for building simple HTTP requests over Apache client.

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
    get("https://randomuser.me/api/")
}

println("Request url ${response.url} done with code ${response.code} and body ${response.body}")
```

Parsing results:
```kotlin
val response: Response = request {
    get("https://randomuser.me/api/?gender=female")
}

val results: RandomUserResults = response.parse()
```

### Plan
- ~~Extend return value~~
- ~~Parse response body to models~~
- Support most common request types: GET, POST, PUT, DELETE