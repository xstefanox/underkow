package io.github.xstefanox.underkow.chain

/**
 * Thrown when trying to configure a [HandlerChain] with no handlers.
 */
class EmptyHandlerChainException : IllegalArgumentException("handler chain must not be empty")
