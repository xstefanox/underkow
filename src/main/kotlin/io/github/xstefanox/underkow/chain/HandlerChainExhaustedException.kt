package io.github.xstefanox.underkow.chain

/**
 * Thrown when trying to delegate the request handling to the next handler, but the request has already reached the last
 * handler.
 */
class HandlerChainExhaustedException : IllegalStateException("no more handlers in chain")
