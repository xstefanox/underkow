package io.github.xstefanox.underkow.chain

class DuplicateHandlersInChainException : IllegalArgumentException("handler chain must not contain duplicates")
