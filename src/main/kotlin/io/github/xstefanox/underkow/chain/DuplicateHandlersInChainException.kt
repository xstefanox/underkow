package io.github.xstefanox.underkow.chain

/**
 * Thrown when trying to configure the chain with duplicate handlers.
 */
class DuplicateHandlersInChainException : IllegalArgumentException("handler chain must not contain duplicates")
