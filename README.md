# Underkow [![Build Status](https://travis-ci.com/xstefanox/underkow.svg?branch=master)](https://travis-ci.com/xstefanox/underkow) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

_A Kotlin declarative DSL for Undertow routing configuration_

## Why should I use Underkow

You should use Underkow if you need to build a REST HTTP service using [Undertow](http://undertow.io/) and you want to
write code that is simple and effective without sacrificing best practices like dependency injection. 

## Features

* A simple and clear DSL that can be used to configure Underkow routing using a declarative syntax (inspired by [Ktor](https://ktor.io/))
* Coroutine based: every request is handled by a [Kotlin coroutine](https://kotlinlang.org/docs/reference/coroutines-overview.html), no need to mess with pools
* Completely asynchronous: every request is automatically dispatched to a coroutine and marked as asynchronous, thus making the I/O thread immediately ready to receive a new request
* Composable: request handlers and filters can be declared independently and then chained together to create a filter chain
* Do not mess your classpath: Kotlin and Undertow are the only dependencies

## Example

The following is a simple example showing a minimal application. See the example directory for a more complete application.

```kotlin
fun main() {

    // declare an Undertow server listening on TCP port 8080
    undertow(8080) {

        // declare a GET request requiring a path parameter
        get("/users/{id}") { exchange ->
            val id = exchange.getAttachment<PathTemplateMatch>(PathTemplateMatch.ATTACHMENT_KEY).parameters["id"]
            exchange.responseSender.send("Hello world, this is user $id")
        }

        // declare a POST request that reads the full request body into a variable
        post("/echo") { exchange ->
            exchange.requestReceiver.receiveFullString { _, body ->
                exchange.responseSender.send("Received body: $body")
            }
        }
        
        // declare or inject a filter that will check for client credentials
        val accessControlFilter : SuspendingHttpHandler = ...
        
        // declare a group of paths prefixed by "/some/protected/path": each incoming request will be passed to the
        // access control filter before being dispatched to its handler
        path("/some/protected/path", accessControlFilter) {
            
            get("/{id}") {
                
            }
            
            post("/{id}") {
                
            }
            
            put("/{id}") {
                
            }
            
            delete("/{id}") {
                
            }
        }
    }
}
   

```