@file:JvmName("Constants")

package io.github.xstefanox.underkow.dsl

import io.github.xstefanox.underkow.dispatcher.XnioTaskThreadExchangeDispatcher
import io.github.xstefanox.underkow.exception.UnhandledExceptionHandler

/**
 * The exception handler used internally to handle exceptions thrown in each call.
 */
internal val UNHANDLED_EXCEPTION_HANDLER = UnhandledExceptionHandler()

/**
 * The error message used when a blank path prefix is configured.
 */
internal const val BLANK_PREFIX_REQUIREMENT_MESSAGE = "prefix must not be blank"

/**
 * Default is to listen to requests directed to any host.
 */
internal const val DEFAULT_HOST = "0.0.0.0"

/**
 * Default is to not apply any prefix to the configured routes.
 */
internal const val DEFAULT_PREFIX = ""

/**
 * Default port used by the server.
 */
internal const val DEFAULT_PORT = 8080

/**
 * Default dispatcher used to dispatch the exchange.
 */
internal val DEFAULT_DISPATCHER = XnioTaskThreadExchangeDispatcher()
