# Kotlin DSL for Apache HTTP Client

Library provide DSL for simple building and executing HTTP requests over Apache HTTP client.  
You are welcome to use the library and contribute into it.

### Examples
Simply execute request:
```kotlin
get("http://google.ru")
```

Add headers or body to request:
```kotlin
post("https://yourapp.com/api/v1/users") {
    header("content-type", "application/json")
    entity("""{"user":"admin"}""")
}
```

Work with response:
```kotlin
val response: Response = get("https://randomuser.me/api/?gender=female")

println("Request url ${response.url} done with code ${response.code} and body ${response.body}")
```

Simply parse response to object:
```kotlin
val response: Response = post("https://example.com/api/create-something")

val result: ResultModel = response.parse()
```