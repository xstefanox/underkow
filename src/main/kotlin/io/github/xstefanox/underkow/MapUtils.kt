@file:JvmName("MapUtils")

package io.github.xstefanox.underkow

internal fun <K, V> MutableMap<K, V>.putAllIfAbsent(other: Map<K, V>) {
    other.forEach { k, v ->
        putIfAbsent(k, v)
    }
}
