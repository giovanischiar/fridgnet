package io.schiar.fridgnet.library.util

import java.util.IdentityHashMap

class IdentitySet<T> : MutableSet<T> {
    private val map = IdentityHashMap<T, Boolean>()

    override val size: Int
        get() = map.size

    override fun add(element: T): Boolean = map.put(element, true) == null

    override fun addAll(elements: Collection<T>): Boolean {
        var modified = false
        for (element in elements) {
            if (add(element)) modified = true
        }
        return modified
    }

    override fun clear() = map.clear()

    override fun iterator(): MutableIterator<T> = map.keys.iterator()

    override fun remove(element: T): Boolean = map.remove(element) != null

    override fun removeAll(elements: Collection<T>): Boolean {
        var modified = false
        for (element in elements) {
            if (remove(element)) modified = true
        }
        return modified
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        return map.keys.retainAll(elements)
    }

    override fun contains(element: T): Boolean = map.containsKey(element)

    override fun containsAll(elements: Collection<T>): Boolean = map.keys.containsAll(elements)

    override fun isEmpty(): Boolean = map.isEmpty()
}