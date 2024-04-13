package io.schiar.fridgnet.library.util

import java.util.IdentityHashMap

/**
 * The IdentitySet utilizes an IdentityHashMap internally to act as a Set.
 * It overrides the methods of MutableSet to provide set-like behavior but uses reference equality
 * (`===`) for comparisons instead of object equality (`==`). This means the set checks if objects
 * are the same instance in memory rather than having the same content.
 *
 * This can be useful in scenarios where you want to ensure you only have unique object instances
 * in the set, regardless of their content. For example, if you are working with objects that have
 * unique identifiers but might have mutable properties, using an IdentitySet can help ensure
 * you don't accidentally add duplicate object instances with the same identifier.
 */
class IdentitySet<T> : MutableSet<T> {
    private val map = IdentityHashMap<T, Boolean>()

    /**
     * Returns the size of the set.
     */
    override val size: Int
        get() = map.size

    /**
     * Adds an element to the set.
     *
     * @param element the element to add.
     * @return true if the element was added, false otherwise.
     * @see [IdentityHashMap.put]
     */
    override fun add(element: T): Boolean = map.put(element, true) == null

    /**
     * Adds multiple elements to the set.
     *
     * @param elements elements to add
     * @return true if some element was added, false otherwise.
     */
    override fun addAll(elements: Collection<T>): Boolean {
        var modified = false
        for (element in elements) {
            if (add(element)) modified = true
        }
        return modified
    }

    /**
     * Clears the set.
     */
    override fun clear() = map.clear()

    /**
     * Returns the iterator for the set.
     * @see [IdentityHashMap.iterator]
     */
    override fun iterator(): MutableIterator<T> = map.keys.iterator()

    /**
     * Removes an element from the set.
     *
     * @param element the element to remove.
     * @return true if the element was removed, false otherwise.
     * @see [IdentityHashMap.remove]
     */
    override fun remove(element: T): Boolean = map.remove(element) != null

    /**
     * Removes multiple elements from the set.
     *
     * @param elements elements to remove
     * @return true if some element was removed, false otherwise.
     */
    override fun removeAll(elements: Collection<T>): Boolean {
        var modified = false
        for (element in elements) {
            if (remove(element)) modified = true
        }
        return modified
    }

    /**
     * This method modifies the set by retaining only the elements that are present in both the set
     * and the specified collection. Elements that are not in the collection will be removed from
     * the set.
     *
     * @see [MutableSet.retainAll]
     */
    override fun retainAll(elements: Collection<T>): Boolean {
        return map.keys.retainAll(elements.toSet())
    }

    /**
     * Checks if there's a element.
     * @param element element to check.
     *
     * @return true if contains the element, false otherwise
     */
    override fun contains(element: T): Boolean = map.containsKey(element)

    /**
     * Compares multiple elements.
     * @param elements elements to compare.
     * @return true if the set contains all elements, false otherwise
     */
    override fun containsAll(elements: Collection<T>): Boolean = map.keys.containsAll(elements)

    /**
     * Check if the set is empty.
     * @return true if the set is empty, false otherwise.
     */
    override fun isEmpty(): Boolean = map.isEmpty()
}