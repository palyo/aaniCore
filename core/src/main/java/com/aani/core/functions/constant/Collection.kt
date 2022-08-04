package com.aani.core.functions.constant

import android.os.Build
import android.util.SparseArray
import java.util.*
import kotlin.collections.associateBy

import android.content.res.TypedArray
import androidx.collection.LongSparseArray
import androidx.collection.SparseArrayCompat
import androidx.collection.forEach
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by hristijan on 2/22/19 to long live and prosper !
 */


/**
 * like map, but multithreaded. It uses the number of cores + 2 threads.
 */
inline fun <T, V> List<T>.pmap(operation: (T) -> V): List<V> {
    val threads = ArrayList<Thread>()
    val cores = Runtime.getRuntime().availableProcessors()
    // run each thread on a partitioned block to minimize thread setup/teardown
    val partitioned = this.split(cores + 2)
    val partitionedResult = partitioned.map { partition ->
        val thread = Thread()
        threads.add(thread)
        thread.run {
            partition.map(operation)
        }
    }
    // wait for threads to finish
    for (thread in threads) {
        thread.join()
    }

    return partitionedResult.flatten()
}

fun <T> MutableList<T>.move(item: T, newIndex: Int) {
    val currentIndex = indexOf(item)
    if (currentIndex < 0) return
    removeAt(currentIndex)
    add(newIndex, item)
}

fun <T> MutableList<T>.moveAt(oldIndex: Int, newIndex: Int) {
    val item = this[oldIndex]
    removeAt(oldIndex)
    if (oldIndex > newIndex)
        add(newIndex, item)
    else
        add(newIndex - 1, item)
}

fun <T> MutableList<T>.moveAll(newIndex: Int, predicate: (T) -> Boolean) {
    check(newIndex in 0 until size)
    val split = partition(predicate)
    clear()
    addAll(split.second)
    addAll(if (newIndex >= size) size else newIndex, split.first)
}

fun <T> MutableList<T>.moveUpAt(index: Int) {
    if (index == 0) return
    if (index < 0 || index >= size) throw Exception("Invalid index $index for MutableList of size $size")
    val newIndex = index + 1
    val item = this[index]
    removeAt(index)
    add(newIndex, item)
}

fun <T> MutableList<T>.moveDownAt(index: Int) {
    if (index == size - 1) return
    if (index < 0 || index >= size) throw Exception("Invalid index $index for MutableList of size $size")
    val newIndex = index - 1
    val item = this[index]
    removeAt(index)
    add(newIndex, item)
}

fun <T> MutableList<T>.moveUp(item: T): Boolean {
    val currentIndex = indexOf(item)
    if (currentIndex == -1) return false
    val newIndex = (currentIndex - 1)
    if (currentIndex <= 0) return false
    remove(item)
    add(newIndex, item)
    return true
}

fun <T> MutableList<T>.moveDown(item: T): Boolean {
    val currentIndex = indexOf(item)
    if (currentIndex == -1) return false
    val newIndex = (currentIndex + 1)
    if (newIndex >= size) return false
    remove(item)
    add(newIndex, item)
    return true
}

inline fun <T> MutableList<T>.moveUp(crossinline predicate: (T) -> Boolean) = find(predicate)?.let { moveUp(it) }

inline fun <T> MutableList<T>.moveDown(crossinline predicate: (T) -> Boolean) = find(predicate)?.let { moveDown(it) }

inline fun <T> MutableList<T>.moveUpAll(crossinline predicate: (T) -> Boolean) = asSequence().withIndex()
    .filter { predicate.invoke(it.value) }
    .forEach { moveUpAt(it.index) }

inline fun <T> MutableList<T>.moveDownAll(crossinline predicate: (T) -> Boolean) = asSequence().withIndex()
    .filter { predicate.invoke(it.value) }
    .forEach { moveDownAt(it.index) }

fun <T> MutableList<T>.swap(itemOne: T, itemTwo: T) = swap(indexOf(itemOne), indexOf(itemTwo))

@JvmName("combinationsExtension")
fun <A, B> List<A>.combinations(listB: List<B>): List<Pair<A, B>> =
    combinations<A, B>(this, listB)

@JvmName("combinations")
fun <A, B> combinations(listA: List<A>, listB: List<B>): List<Pair<A, B>> =
    listA.flatMap { first -> listB.map { second -> first to second } }

@JvmName("combinationsExtension")
fun <A, B, C> List<A>.combinations(listB: List<B>, listC: List<C>): List<Triple<A, B, C>> =
    combinations<A, B, C>(this, listB, listC)

@JvmName("combinations")
fun <A, B, C> combinations(listA: List<A>, listB: List<B>, listC: List<C>): List<Triple<A, B, C>> =
    listA.flatMap { first ->
        listB.flatMap { second ->
            listC.map { third ->
                Triple(first, second, third)
            }
        }
    }

fun <T> combinations(vararg listOfList: List<T>): List<List<T>> {
    return listOfList.fold(
        initial = listOf(emptyList<T>()),
        operation = { accumulateListOfList: List<List<T>>, elementList: List<T> ->
            accumulateListOfList.flatMap { list: List<T> ->
                elementList.map { element: T -> list + element }
            }
        }
    )
}

fun <T> Collection<T>.doIfContained(t: T, func: T.() -> Unit): Boolean {
    if (contains(t)) {
        t.func()
        return true
    }
    return false
}

infix fun <T> T.appendTo(list: List<T>) = list + listOf(this)
infix fun <T> T.prependTo(list: List<T>) = listOf(this) + list

fun <T> List<T>.swap(i: Int, j: Int): List<T> {
    if (isInBounds(i) && isInBounds(j)) {
        Collections.swap(this, i, j)
    }
    return this
}

private fun <T> List<T>.isInBounds(index: Int): Boolean {
    return index in 0..lastIndex
}


fun <T> List<T>.startWith(item: T): List<T> {
    val list = this.toMutableList()
    list.add(0, item)
    return list
}

fun <T> List<T>.startWith(data: List<T>): List<T> {
    val list = this.toMutableList()
    list.addAll(0, data)
    return list
}

fun <T> List<T>.startWithIfNotEmpty(item: T): List<T> {
    if (this.isNotEmpty()) {
        return startWith(item)
    }
    return this
}

fun <K, V> MutableMap<K, MutableList<V>>.deepCopy(): MutableMap<K, MutableList<V>> {
    val result = mutableMapOf<K, MutableList<V>>()
    for ((key, value) in this) {
        result[key] = value.toMutableList()
    }
    return result
}

fun <T> MutableList<T>.doIf(predicate: Boolean, action: MutableList<T>.() -> Any): MutableList<T> {
    if (predicate) {
        this.action()
    }
    return this
}

fun <T> MutableList<T>.removeFirst(predicate: (T) -> Boolean): Boolean {
    for (t in this) {
        if (predicate(t)) {
            this.remove(t)
            return true
        }
    }
    return false
}

fun <T> LongSparseArray<T>.toList(): List<T> {
    val list = mutableListOf<T>()


    this.forEach { longValue, value -> list.add(value) }

    return list
}

fun <T> LongSparseArray<T>.toggle(key: Long, item: T) {
    val current = this.get(key)
    if (current == null) {
        this.append(key, item)
    } else {
        this.remove(key)
    }
}

inline fun <T> Iterable<T>.toSparseArray(getKey: Function1<T, Int?>): SparseArrayCompat<T> {
    val array = SparseArrayCompat<T>()
    for (item in this) {
        array.append(getKey(item) ?: 0, item)
    }
    return array
}

inline fun <T> Iterable<T>.toLongSparseArray(getKey: Function1<T, Long?>): LongSparseArray<T> {
    val array = LongSparseArray<T>()
    for (item in this) {
        array.append(getKey(item) ?: 0L, item)
    }
    return array
}

inline fun <T> Iterable<T>.toLongSparseArrayGrouped(getKey: Function1<T, Long?>): LongSparseArray<MutableList<T>> {
    val array = LongSparseArray<MutableList<T>>()
    for (item in this) {
        val key: Long = getKey(item) ?: 0L
        var vList = array.get(key)
        if (vList == null) {
            vList = java.util.ArrayList()
            array.put(key, vList)
        }
        vList.add(item)
    }
    return array
}

inline fun <T, K> Iterable<T>.toHashMap(getKey: Function1<T, K>) = toHashMap(getKey, { it })

inline fun <T, K, V> Iterable<T>.toHashMap(getKey: Function1<T, K>, getValue: Function1<T, V>): HashMap<K, V> {
    val map = HashMap<K, V>()
    for (item in this) {
        map[getKey(item)] = getValue(item)
    }
    return map
}

inline fun <T, K> Iterable<T?>.toHashMapNullable(getKey: Function1<T?, K>) = toHashMapNullable(getKey, { it })

inline fun <T, K, V> Iterable<T?>.toHashMapNullable(getKey: Function1<T?, K>, getValue: Function1<T?, V?>): HashMap<K, V?> {
    val map = HashMap<K, V?>()
    for (item in this) {
        map[getKey(item)] = getValue(item)
    }
    return map
}

inline fun <T : TypedArray?, R> T.use(block: (T) -> R): R {
    var recycled = false
    try {
        return block(this)
    } catch (e: Exception) {
        recycled = true
        try {
            this?.recycle()
        } catch (exception: Exception) {
        }
        throw e
    } finally {
        if (!recycled) {
            this?.recycle()
        }
    }
}

fun generateRandomIntegerList(size: Int, range: IntRange): MutableList<Int> {
    val resultList = ArrayList<Int>(size)
    for (i in 1..size) {
        resultList.add(kotlin.random.Random.nextInt(range.first, range.last))
    }
    return resultList
}

fun generateRandomStringList(size: Int): MutableList<String> {
    val resultList = ArrayList<String>(size)
    for (i in 1..size) {
        resultList.add(randomUUIDString)
    }
    return resultList
}

fun <T> List<T>.getStringRepresentation(maxElements: Int = Integer.MAX_VALUE): String {
    if (size > maxElements) return " The list is too long to output!"
    var string = ""
    forEach { string += "$it, " }
    string = string.trimEnd(' ')
    string = string.trimEnd(',')
    return string
}

fun <T : Comparable<T>> List<T>.isListSorted(): Boolean {
    return this == this.sorted()
}

val List<Int>.isBinarySearchable: Boolean
    get() {
        return (this == this.distinct().sorted())
    }

fun <K, V> Map<K, V?>.filterNotNullValues(): Map<K, V> {
    return mapNotNull { (key, nullableValue) ->
        nullableValue?.let { key to it }
    }.toMap()
}

fun <T, S> MutableMap<T, S>.replaceWith(map: Map<T, S>) {
    if (map === this) return
    clear()
    putAll(map)
}

fun <T> MutableCollection<T>.addOrRemove(item: T): Boolean {
    return if (contains(item)) {
        remove(item)
        false
    } else {
        add(item)
        true
    }
}

inline fun <T> Iterable<T>.contains(predicate: (T) -> Boolean): Boolean {
    for (element in this) if (predicate(element)) return true
    return false
}

fun <T> MutableCollection<T>.replaceWith(collection: Collection<T>) {
    if (collection === this) return
    clear()
    addAll(collection)
}

fun <T> MutableList<T>.addOrReplace(item: T, predicate: (T) -> Boolean): Boolean {
    return this.indexOfFirst { predicate.invoke(it) }
        .takeIf { it >= 0 }
        ?.let { this[it] = item }
        ?.let { true }
        ?: this.add(item)
            .let { false }
}

fun <S : MutableList<T>, T> S.addAnd(index: Int, item: T): S {
    add(index, item)
    return this
}

fun <S : MutableCollection<T>, T> S.addAnd(item: T): S {
    add(item)
    return this
}

fun <T> MutableCollection<T>.addAll(vararg items: T) {
    addAll(items)
}

val <T> Collection<T>?.isNotNullOrEmpty: Boolean get() = this != null && this.isNotEmpty()

fun Array<String>.containsIgnoreCase(element: String): Boolean = any { it.equals(element, true) }

fun <E> Collection<E>.isLast(position: Int) = position == size - 1


inline fun <reified T> T.addToList(list: MutableList<T>): T = this.apply {
    list.add(this)
}

fun Collection<*>?.isIndexOutOfBounds(index: Int): Boolean {
    return this == null || index < 0 || index > this.size - 1
}

inline fun <K, V> MutableMap<K, V>.putAllIfNotNull(
    vararg data: Pair<K, V?>,
    predicate: (V) -> Boolean = { true }
) {
    for ((key, value) in data) {
        value?.also { if (predicate.invoke(it)) put(key, it) }
    }
}

fun <K, V : CharSequence> MutableMap<K, V>.putAllIfNotNullOrEmpty(vararg data: Pair<K, V?>) =
    putAllIfNotNull(*data) { it.isNotEmpty() }

fun <K, V : CharSequence> MutableMap<K, V>.putAllIfNotNullOrBlank(vararg data: Pair<K, V?>) =
    putAllIfNotNull(*data) { it.isNotBlank() }

fun <E> List<E>?.safeMutable(default: MutableList<E> = mutableListOf()): MutableList<E> =
    (if (this is MutableList<E>) this else this?.toMutableList()) ?: default

fun <K, V> Map<K, V>?.safeMutable(default: MutableMap<K, V> = mutableMapOf()): MutableMap<K, V> =
    (if (this is MutableMap<K, V>) this else this?.toMutableMap()) ?: default

inline fun <T> Array<T>.resize(newSize: Int, creator: (Int) -> T): Array<T?> {
    val copiedArray = this.copyOf(newSize)
    for (i in size until newSize) {
        copiedArray[i] = creator(i)
    }
    return copiedArray
}

fun <T, E> Map<T, E>.keySet(): Set<T> {
    return this
        .map { (key, _) -> key }
        .toSet()
}

fun <T, E> Map<T, E>.valueSet(): Set<E> {
    return this
        .map { (_, values) -> values }
        .toSet()
}

fun <T, E> List<Pair<T, E>>.keySet(): Set<T> {
    return this
        .map { (key, _) -> key }
        .toSet()
}

fun <T, E> List<Pair<T, E>>.valueSet(): Set<E> {
    return this
        .map { (_, values) -> values }
        .toSet()
}

inline fun <reified T> Collection<T>.removeDuplicates(): MutableList<T> {
    return this.toSet().toMutableList()
}

inline fun <reified T> Collection<T>.onDuplicatesRemoved(noDups: (collection: Set<T>) -> Unit = {}) {
    noDups(toSet())
}


inline fun <T> Collection<T>?.onNullOrEmpty(onNotNullOrEmpty: () -> Unit = {}, function: () -> Unit) {
    if (this.isNullOrEmpty()) function() else onNotNullOrEmpty()
}

inline fun <T> Collection<T>?.onNull(onNotNull: () -> Unit = {}, function: () -> Unit) {
    if (this == null) function() else onNotNull()
}

inline fun <T> Collection<T>?.onNotNullOrEmpty(onNullOrEmpty: () -> Unit = {}, function: Collection<T>.() -> Unit) {
    if (this.isNullOrEmpty()) onNullOrEmpty() else this.function()
}

inline fun <T> Collection<T>?.onNotNull(onNull: () -> Unit = {}, function: Collection<T>.() -> Unit) {
    if (this == null) onNull() else this.function()
}


inline fun <T, R> T.isListAndNullOrEmpty(actionFalse: () -> R, actionTrue: () -> R): R =
    if (this is List<*> && this.isNullOrEmpty()) actionTrue() else actionFalse()


inline fun <T, R> T.isListAndNotNullOrEmpty(actionFalse: () -> R, actionTrue: () -> R): R =
    if (this is List<*> && !this.isNullOrEmpty()) actionTrue() else actionFalse()



fun sumFromOne(n: Int) = n * (n + 1) / 2
fun sumFromOne(n: Float) = n * (n + 1) / 2
fun sumFromOne(n: Double) = n * (n + 1) / 2
fun sumFromOne(n: Short) = n * (n + 1) / 2
fun sumFromOne(n: Long) = n * (n + 1) / 2

inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
    var sum: Long = 0
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

inline fun <T> Iterable<T>.sumByFloat(selector: (T) -> Long): Float {
    var sum = 0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

operator fun List<Int>.times(by: Int): List<Int> {
    return this.map { it * by }
}

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

fun <T> MutableList<T>.swapAsList(index1: Int, index2: Int): MutableList<T> {
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
    return this
}

fun <T> List<T>.midElement(): T? {
    return if (isEmpty())
        null
    else
        this[size / 2]
}

fun <T> ArrayList<T>.midElement(): T? {
    return if (isEmpty())
        null
    else
        this[size / 2]
}

inline fun <T> List<T>.forEachReversed(f: (T) -> Unit) {
    var i = size - 1
    while (i >= 0) {
        f(get(i))
        i--
    }
}

inline fun <T> List<T>.forEachReversedIndexed(f: (Int, T) -> Unit) {
    var i = size - 1
    while (i >= 0) {
        f(i, get(i))
        i--
    }
}

inline fun <T> ArrayList<T>.forEachReversed(f: (T) -> Unit) {
    var i = size - 1
    while (i >= 0) {
        f(get(i))
        i--
    }
}

inline fun <T> ArrayList<T>.forEachReversedIndexed(f: (Int, T) -> Unit) {
    var i = size - 1
    while (i >= 0) {
        f(i, get(i))
        i--
    }
}

val <T> List<T>.midIndex: Int
    get() = if (size == 0) 0 else size / 2

val <T> ArrayList<T>.midIndex: Int
    get() = if (size == 0) 0 else size / 2

infix fun <E> ArrayList<E>.addIfNotExist(obj: E) = if (!this.contains(obj)) add(obj) else false

infix fun <E> MutableList<E>.addIfNotExist(obj: E) = if (!this.contains(obj)) add(obj) else false

fun <T> List<List<T>>.concat(): List<T> = fold(listOf()) { acc, l -> acc + l }
fun <T> List<List<T>>.concatToMutableList(): MutableList<T> = concat().toMutableList()

fun <K, E> java.util.HashMap<K, E>.addIfNotExist(key: K, obj: E): E? = if (!this.containsKey(key)) put(key, obj) else {
    null
}

fun <K, E> MutableMap<K, E>.addIfNotExist(key: K, obj: E): E? = if (!this.containsKey(key)) put(key, obj) else {
    null
}

fun <T> List<T>.randomItem(): T {
    return this[Random().nextInt(size)]
}

fun <T> ArrayList<T>.randomItem(): T {
    return this[Random().nextInt(size)]
}

val <T> SparseArray<T>.values: List<T>
    get() = ArrayList<T>().apply {
        for (i in 0 until size()) {
            add(valueAt(i))
        }
    }

operator fun <T> SparseArray<T>.set(key: Int, value: T?) {
    put(key, value)
}

operator fun <T> SparseArray<T>.plusAssign(array: SparseArray<T?>) {
    for (i in 0 until array.size()) {
        put(array.keyAt(i), array.valueAt(i))
    }
}

inline fun <V> Iterable<V>.associateBy(keySelector: (V) -> Int): SparseArray<V> =
    associateByTo(SparseArray(), keySelector)

inline fun <T, V> Iterable<T>.associateBy(keySelector: (T) -> Int,
                                          valueTransform: (T) -> V): SparseArray<V> =
    associateByTo(SparseArray(), keySelector, valueTransform)

inline fun <V, M : SparseArray<in V>> Iterable<V>.associateByTo(destination: M,
                                                                keySelector: (V) -> Int): M {
    for (element in this) destination.put(keySelector(element), element)
    return destination
}

inline fun <T, V, M : SparseArray<in V>> Iterable<T>.associateByTo(destination: M,
                                                                   keySelector: (T) -> Int,
                                                                   valueTransform: (T) -> V): M {
    for (element in this) {
        destination.put(keySelector(element), valueTransform(element))
    }
    return destination
}

inline fun <V> SparseArray<V>.forEach(action: (Int, V) -> Unit) {
    for (i in 0 until size()) action(keyAt(i), valueAt(i))
}

inline fun <V> SparseArray<V>.filterTo(destination: SparseArray<V>,
                                       predicate: (Int, V) -> Boolean): SparseArray<V> {
    forEach { k, v -> if (predicate(k, v)) destination[k] = v }
    return destination
}

inline fun <V> SparseArray<V>.filter(predicate: (Int, V) -> Boolean): SparseArray<V> =
    filterTo(SparseArray(), predicate)

inline fun <V> SparseArray<V>.getOrPut(key: Int, defaultValue: () -> V): V {
    this[key]?.let { return it }
    return defaultValue().apply { put(key, this) }
}

fun <T> Iterable<T>.toDeque(): Deque<T> {
    return if (this is Collection<T>) this.toDeque()
    else toCollection(java.util.ArrayDeque<T>())
}

fun <T> Collection<T>.toDeque(): Deque<T> = java.util.ArrayDeque<T>(this)

fun <T> dequeOf(): Deque<T> = arrayDequeOf()

fun <T> arrayDequeOf(): java.util.ArrayDeque<T> = java.util.ArrayDeque()

fun <T> dequeOf(vararg elements: T): Deque<T> {
    return if (elements.isEmpty()) java.util.ArrayDeque()
    else elements.toCollection(java.util.ArrayDeque<T>())
}

fun <T> arrayDequeOf(vararg elements: T): java.util.ArrayDeque<T> {
    return if (elements.isEmpty()) java.util.ArrayDeque()
    else elements.toCollection(java.util.ArrayDeque())
}

fun <T : Comparable<T>> indexOfMin(arr: Array<T>): Int {
    var minIndex = 0
    val minItem = arr[0]

    for (i in 1..arr.size) {
        if (arr[minIndex] < minItem)
            minIndex = i
    }

    return minIndex
}

inline fun <T> count(start: T, step: T, crossinline add: (T, T) -> T): Sequence<T> = sequence {
    var n = start

    while (true) {
        yield(n)
        n = add(n, step)
    }
}

fun count(start: Byte, step: Byte = 1): Sequence<Byte> = count(start, step) { n, s -> (n + s).toByte() }

fun count(start: Short, step: Short = 1): Sequence<Short> = count(start, step) { n, s -> (n + s).toShort() }

fun count(start: Int, step: Int = 1): Sequence<Int> = count(start, step) { n, s -> n + s }

fun count(start: Long, step: Long = 1L): Sequence<Long> = count(start, step) { n, s -> n + s }

fun count(start: Float, step: Float = 1f): Sequence<Float> = count(start, step) { n, s -> n + s }

fun count(start: Double, step: Double = 1.0): Sequence<Double> = count(start, step) { n, s -> n + s }

fun <T> Iterable<T>.cycle(): Sequence<T> = sequence {
    val saved = mutableListOf<T>()

    for (item in this@cycle) {
        yield(item)
        saved.add(item)
    }
    if (saved.isNotEmpty()) {
        while (true) {
            for (item in saved) {
                yield(item)
            }
        }
    }
}

fun <T> T.repeat(): Sequence<T> = sequence {
    while (true) {
        yield(this@repeat)
    }
}

fun <T> T.repeat(count: Int): Sequence<T> = sequence {
    for (i in 1..count) {
        yield(this@repeat)
    }
}

inline fun <T> Iterable<T>.firstOrElse(predicate: (T) -> Boolean, defaultValue: () -> T): T {
    for (element in this) if (predicate(element)) return element

    return defaultValue()
}

fun <T> Iterable<T?>.firstNotNull(): T = this.first { it != null } as T

fun <T> List<T?>.firstNotNull(): T = this.first { it != null } as T

fun <T> ArrayList<T?>.firstNotNull(): T = this.first { it != null } as T

inline fun <T> Iterable<T?>.firstNotNullOrElse(defaultValue: () -> T): T = firstOrElse({ it != null }, defaultValue) as T

inline fun <T> List<T?>.firstNotNullOrElse(defaultValue: () -> T): T = firstOrElse({ it != null }, defaultValue) as T

inline fun <T> Iterable<T>.lastOrElse(predicate: (T) -> Boolean, defaultValue: () -> T): T {
    var last: T? = null
    var found = false
    for (element in this) {
        if (predicate(element)) {
            last = element
            found = true
        }
    }
    if (!found) {
        return defaultValue()
    }
    return last as T
}

inline fun <T> List<T>.lastOrElse(predicate: (T) -> Boolean, defaultValue: () -> T): T {
    val iterator = this.listIterator(size)
    while (iterator.hasPrevious()) {
        val element = iterator.previous()
        if (predicate(element)) return element
    }

    return defaultValue()
}

fun <T> Iterable<T?>.lastNotNull(): T = this.last { it != null } as T

fun <T> List<T?>.lastNotNull(): T = this.last { it != null } as T

inline fun <T> Iterable<T?>.lastNotNullOrElse(defaultValue: () -> T): T = lastOrElse({ it != null }, defaultValue) as T

inline fun <T> List<T?>.lastNotNullOrElse(defaultValue: () -> T): T = lastOrElse({ it != null }, defaultValue) as T

fun <T> List<T>.split(partitionSize: Int): List<List<T>> {
    if (this.isEmpty()) return emptyList()
    if (partitionSize < 1) throw IllegalArgumentException("partitionSize must be positive")

    val result = ArrayList<List<T>>()
    var entry = ArrayList<T>(partitionSize)
    for (item in this) {
        if (entry.size == partitionSize) {
            result.add(entry)
            entry = ArrayList<T>()
        }
        entry.add(item)
    }
    result.add(entry)
    return result
}

fun <E> MutableList<E>.removeInRange(position: Int, count: Int) {
    this.removeAll(drop(position).take(count))
}

fun <E> ArrayList<E>.removeInRange(position: Int, count: Int) {
    this.removeAll(drop(position).take(count))
}

fun <T> List<T>?.sizeOrZero(): Int {
    return this?.size ?: 0
}

fun <T> List<T>?.orEmptyString(string: String): String {
    return if (this?.isEmpty() == true) "" else string
}

fun <T> flatten(vararg elements: List<T>): List<T> =
    elements.flatMap { it }

fun <T> flatten(list: List<List<T>>): List<T> =
    list.flatMap { it }

inline fun <reified T> union(l: List<T>, vararg elements: T): List<T> =
    flatten(l, elements.toList())

inline fun <reified T> union(l1: List<T>, l2: List<T>, vararg elements: T): List<T> =
    flatten(l1, l2, elements.toList())

inline fun <reified T> union(l1: List<T>, l2: List<T>, l3: List<T>, vararg elements: T): List<T> =
    flatten(l1, l2, l3, elements.toList())

inline fun <reified T> union(l1: List<T>, l2: List<T>, l3: List<T>, l4: List<T>, vararg elements: T): List<T> =
    flatten(l1, l2, l3, l4, elements.toList())

inline fun <reified T> union(l1: List<T>, l2: List<T>, l3: List<T>, l4: List<T>, l5: List<T>, vararg elements: T): List<T> =
    flatten(l1, l2, l3, l4, l5, elements.toList())

inline fun <T, R> Iterable<T>.mapToSet(transform: (T) -> R) =
    this.map(transform).toSet()

inline fun <T, R : Any> Iterable<T>.mapNotNullToSet(transform: (T) -> R?) =
    this.mapNotNull(transform).toSet()


fun <T> java.util.Comparator<T>.sort(list: MutableList<T>) {
    list.sortWith(this)
}


fun <T> listEqualsIgnoreOrder(list1: List<T>, list2: List<T>): Boolean {
    return HashSet(list1) == HashSet(list2)
}

inline fun <T> Array<T>.firstIndexed(
    filter: (index: Int, T) -> Boolean,
    onNext: (index: Int, T) -> Unit = { _, _ -> },
    onSuccess: () -> Unit = {},
    onFail: () -> Unit = {},
    onComplete: () -> Unit = {}
) {
    var isNull = true
    for ((index, element) in this.withIndex()) {
        if (filter(index, element)) {
            isNull = false
            onNext(index, element)
            break
        }
    }
    if (isNull) onFail() else onSuccess()
    onComplete()
}

inline fun <reified T> initArrayOf(size: Int, predicate: (index: Int) -> T): Array<T> {
    return arrayListOf<T>().also {
        for (index in 0 until size) {
            it.add(predicate(index))
        }
    }.toTypedArray()
}

inline fun MutableMap<String, Any>.transform(key: String, block: (Any?) -> String) {
    val t = get(key)
    this[key] = block.invoke(t)
}

inline fun <reified T : Any> MutableMap<String, Any>.makeTo(fromKey: String, toKey: String, block: (from: Any?) -> T) {
    val from = get(fromKey)
    this[toKey] = block.invoke(from)
}

fun <T, S> MutableList<T>.compose(
    mixList: List<S>,
    mixFilter: (T, S) -> Boolean,
    mixOperate: (T?, S) -> T,
    removeCannotMix: Boolean = true
) {
    val list1 = this
    val list2 = mutableListOf<S>().apply {
        addAll(mixList)
    }
    for (i in list1.size - 1 downTo 0) {
        run {
            val item1 = list1[i]
            for (j in list2.size - 1 downTo 0) {
                val item2 = list2[j]
                val mix = mixFilter(item1, item2)
                if (mix) {
                    list1[i] = mixOperate(item1, item2)
                    list2.removeAt(j)
                    return@run
                }
            }
            if (removeCannotMix) {
                list1.remove(item1)
            }
        }
    }
    list1.addAll(0, list2.map {
        mixOperate(null, it)
    })
}

inline fun <T> mutableListCreate(size: Int, create: (index: Int) -> T): MutableList<T> {
    val list = ArrayList<T>()
    for (index in 0 until size) {
        list.add(create(index))
    }
    return list
}

infix operator fun ByteArray.plus(byte: Byte) = ByteArray(this@plus.size + 1) { index ->
    when {
        index < this@plus.size -> this[index]
        index == this@plus.size -> byte
        else -> throw ArrayIndexOutOfBoundsException("ByteArray.plus return error with index:$index")
    }
}


infix operator fun ByteArray.plus(int: Int): ByteArray {
    if (int > 0XFF) {
        throw IndexOutOfBoundsException("An error occurred while int converting Byte, $int > 0XFF,please check index!!!")
    } else return this@plus + int.toByte()
}


fun ByteArray.toInt(isHighFront: Boolean = true): Int {
    val array = if (isHighFront) {
        this.reversedArray()
    } else {
        this
    }
    var temp = (array[0].toInt() and 0xff)
    val rangeSize = if (array.size <= 4) array.size else 4
    for (index in 1 until rangeSize) {
        temp = temp or (array[index].toInt() and 0xff shl 8 * index)
    }
    return temp
}

fun <T> LinkedList<T>.safePop(): T? {
    return if (size > 0) pop()
    else null
}


inline fun <E> Iterable<E>.forEachApply(action: E.() -> Unit) {
    for (element in this) element.action()
}

inline fun <E> Iterable<E>.forEachApplyIndexed(action: E.(index: Int) -> Unit) {
    var index = 0
    for (item in this) item.action(index++)
}

fun <T> Set<T>?.isEmpty(): Boolean = this?.isEmpty() ?: true


fun <K, V> Map<K, List<V>>.flatten(): List<Any?> =
    map { (k, v) ->
        mutableListOf<Any?>().apply {
            add(k)
            addAll(v)
        }
    }.flatten()

fun <K, V> Map<K, List<V>>.indexInSection(element: V): Int {
    forEach { (_, list) -> if (list.contains(element)) return list.indexOf(element) }
    return -1
}

fun <K, V> Map<K, List<V>>.keyIndex(key: K?): Int {
    key ?: return -1
    var index = 0
    forEach { (k, _) -> if (key == k) return index else index++ }
    return -1
}

fun <K, V> Map<K, List<V>>.keyAtIndex(index: Int): K? {
    keys.forEachIndexed { ind, k -> if (ind == index) return k }
    return null
}

fun <K, V> Map<K, List<V>>.sectionKey(element: V): K? {
    forEach { (k, list) -> if (list.contains(element)) return k }
    return null
}


inline fun <E, R : Comparable<R>> MutableList<E>.sortedSelf(crossinline selector: (E) -> R?): MutableList<E> =
    also { sortWith(compareBy(selector)) }

fun <E> List<E>.indexOf(item: E?): Int = if (item == null) -1 else indexOf(item)

fun <E> List<E>.isLast(element: E): Boolean = indexOf(element) == size - 1

fun <E> List<E>.nextAfter(index: Int): E? = if (index == count() - 1) null else this[index + 1]

fun <E> List<E>.nextAfter(element: E): E? =
    when (val index = indexOf(element)) {
        -1 -> throw IllegalAccessException("List doesn't contains $element")
        size - 1 -> null
        else -> this[index + 1]
    }


fun <E> List<E>.prevBefore(index: Int): E? = if (index == 0) null else this[index - 1]

fun <E> List<E>.prevBefore(element: E): E? =
    when (val index = indexOf(element)) {
        -1 -> throw IllegalAccessException("List doesn't contains $element")
        0 -> null
        else -> this[index - 1]
    }

fun <T> List<T>.random(): T = shuffled().first()


fun <T> List<T>?.isLast(index: Int): Boolean {
    return index == this?.lastIndex
}


fun <E> MutableList<E>.removeElement(element: E): E? {
    val index = indexOfOrNull(element) ?: return null
    return removeAt(index)
}

private fun <E> MutableList<E>.indexOfOrNull(element: E): Int? {
    val indexOf = indexOf(element)
    return if (indexOf != -1) indexOf else null
}

fun <T> Collection<T>?.isBlank(): Boolean = this == null || isEmpty()

fun <T> Collection<T?>.anyNull(): Boolean = any { it == null }

fun <T> Collection<T?>.allNull(): Boolean = all { it == null }

fun <T> Collection<T?>.countNulls(): Int = count { it == null }
fun <T> Collection<T?>.countNonNulls(): Int = size - countNulls()

fun <T : Any> Iterable<T?>.trimNulls(): List<T> = filterNotNull()
fun <T : Any> Iterable<T?>.trimNullsToMutableList(): MutableList<T> = filterNotNullTo(mutableListOf())

fun Iterable<String?>.trim(): List<String> = trimNulls().filterNot { it.isBlank() }
fun Iterable<String?>.trimToMutableList(): MutableList<String> = trimNulls().filterNotTo(mutableListOf()) { it.isBlank() }

fun <T1, T2> Iterable<T1>.combine(other: Iterable<T2>): List<Pair<T1, T2>> = combine(other) { thisItem: T1, otherItem: T2 -> Pair(thisItem, otherItem) }
fun <T1, T2> Iterable<T1>.combineToMutableList(other: Iterable<T2>): MutableList<Pair<T1, T2>> = combineToMutableList(other) { thisItem: T1, otherItem: T2 -> Pair(thisItem, otherItem) }

inline fun <T1, T2, R> Iterable<T1>.combine(other: Iterable<T2>, transform: (thisItem: T1, otherItem: T2) -> R): List<R> = flatMap { thisItem -> other.map { otherItem -> transform(thisItem, otherItem) } }

inline fun <T1, T2, R> Iterable<T1>.combineToMutableList(other: Iterable<T2>, transform: (thisItem: T1, otherItem: T2) -> R): MutableList<R> = flatMapTo(mutableListOf()) { thisItem -> other.map { otherItem -> transform(thisItem, otherItem) } }

inline fun <T, R> Iterable<T>.mapToMutableList(transform: (T) -> R): MutableList<R> = mapTo(mutableListOf(), transform)
inline fun <T, R> Iterable<T>.flatMapToMutableList(transform: (T) -> Iterable<R>): MutableList<R> = flatMapTo(mutableListOf(), transform)

inline fun <T> Int.timesToListOf(predicate: (Int) -> T): List<T> = (0 until this).map { predicate(it) }
inline fun <T> Int.timesToMutableListOf(predicate: (Int) -> T): MutableList<T> = (0 until this).mapToMutableList { predicate(it) }

fun <T> MutableList<T>.swapMutable(i: Int, j: Int): MutableList<T> {
    return apply {
        val aux = this[i]
        this[i] = this[j]
        this[j] = aux
    }
}

fun <T> List<T>.swapped(i: Int, j: Int): List<T> = toMutableList().swapMutable(i, j)

fun <T> List<T>.getRandom(generator: Random = Random()): T = get(generator.nextInt(size))

fun <T> MutableList<T>.shuffle(generator: Random = Random()): MutableList<T> = apply { Collections.shuffle(this, generator) }

fun <T> List<T>.shuffled(): List<T> = toMutableList().shuffle()

fun randomIntList(size: Int, generator: Random = Random()) = size.timesToListOf { generator.nextInt() }
fun randomIntList(size: Int, bound: Int, generator: Random = Random()) = size.timesToListOf { generator.nextInt(bound) }
fun randomFloatList(size: Int, generator: Random = Random()) = size.timesToListOf { generator.nextFloat() }
fun randomDoubleList(size: Int, generator: Random = Random()) = size.timesToListOf { generator.nextDouble() }
fun randomBooleanList(size: Int, generator: Random = Random()) = size.timesToListOf { generator.nextBoolean() }

fun <T> List<T>.encapsulate(): List<List<T>> = map { listOf(it) }
fun <T> List<T>.encapsulateToMutableList(): MutableList<MutableList<T>> = mapToMutableList { mutableListOf(it) }

fun <T> Collection<T>.init(): List<T> = take(size - 1)

inline val Collection<*>.half: Int get() = size / 2

fun <T> Collection<T>.firstHalf(): List<T> = take(half)
fun <T> Collection<T>.secondHalf(): List<T> = drop(half)

fun <T> Collection<T>.split(index: Int): Pair<List<T>, List<T>> = take(index) to drop(index)
fun <T> Collection<T>.split(): Pair<List<T>, List<T>> = split(half)

fun <K, V> Map<K, V>.keyAt(value: V): K? {
    val entry = entries.find { it.value == value }
    return entry?.key
}

fun <K, V> Map<K, V>.valueAt(key: V): V? {
    val entry = entries.find { it.key == key }
    return entry?.value
}

inline fun <reified T> MutableList<T>.removeIfCompat(noinline filter: (T) -> Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) removeIf(filter) else {
        for (i in size - 1 downTo 0) {
            if (filter(this[i])) {
                removeAt(i)
            }
        }
    }
}

fun <T> List<T>.second(): T? {
    return when {
        isEmpty() -> null
        size < 2 -> null
        else -> this[1]
    }
}

fun <T> ArrayList<T>.second(): T? {
    return when {
        isEmpty() -> null
        size < 2 -> null
        else -> this[1]
    }
}

inline fun <reified T> List<T>.unmodifiable(): List<T> =
    Collections.unmodifiableList(this)

inline fun <reified T> Set<T>.unmodifiable(): Set<T> =
    Collections.unmodifiableSet(this)

fun <T> List<T>.mostCommon(): T? {
    return groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
}

fun <T> List<T>.leastCommon(): T? {
    return groupingBy { it }.eachCount().minByOrNull { it.value }?.key
}

fun <T> ArrayList<T>.mostCommon(): T? {
    return groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
}

fun <T> ArrayList<T>.leastCommon(): T? {
    return groupingBy { it }.eachCount().minByOrNull { it.value }?.key
}

fun <T> Collection<T>.mostCommon(): T? {
    return groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
}

fun <T> Collection<T>.leastCommon(): T? {
    return groupingBy { it }.eachCount().minByOrNull { it.value }?.key
}

val <E> List<E>?.arrayList: ArrayList<E>
    get() = if (this is ArrayList) this else ArrayList(emptyList())

fun <T> Array<T>?.isNullOrEmpty() = this == null || isEmpty()

fun BooleanArray?.isNullOrEmpty() = this == null || isEmpty()

fun CharArray?.isNullOrEmpty() = this == null || isEmpty()

fun ByteArray?.isNullOrEmpty() = this == null || isEmpty()

fun ShortArray?.isNullOrEmpty() = this == null || isEmpty()

fun IntArray?.isNullOrEmpty() = this == null || isEmpty()

fun LongArray?.isNullOrEmpty() = this == null || isEmpty()

fun FloatArray?.isNullOrEmpty() = this == null || isEmpty()

fun DoubleArray?.isNullOrEmpty() = this == null || isEmpty()

fun <T> Array<T>?.isNotNullOrEmpty() = this != null && isNotEmpty()

fun BooleanArray?.isNotNullOrEmpty() = this != null && isNotEmpty()

fun CharArray?.isNotNullOrEmpty() = this != null && isNotEmpty()

fun ByteArray?.isNotNullOrEmpty() = this != null && isNotEmpty()

fun ShortArray?.isNotNullOrEmpty() = this != null && isNotEmpty()

fun IntArray?.isNotNullOrEmpty() = this != null && isNotEmpty()

fun LongArray?.isNotNullOrEmpty() = this != null && isNotEmpty()

fun FloatArray?.isNotNullOrEmpty() = this != null && isNotEmpty()

fun DoubleArray?.isNotNullOrEmpty() = this != null && isNotEmpty()

fun <T> Set<T>?.isNullOrEmpty(): Boolean = this == null || isEmpty()

fun <T> T.mergeWith(items: Iterable<T>) =
    mutableListOf(this).apply { addAll(items) }

fun <T> T.mergeWith(vararg items: T) =
    mutableListOf(this).apply { addAll(items) }

fun <E : Comparable<E>> MutableList<E>.sortedSelf(): MutableList<E> =
    also { sortWith(Comparator { o1, o2 -> o1.compareTo(o2) }) }

fun <T> Iterable<T>.skip(n: Int): List<T> {
    require(n >= 0) { "Requested skip count $n is less than zero." }
    if (n == 0) return this.toList()
    if (this is Collection<T>) {
        if (n >= size) return emptyList()
    }
    val list = ArrayList<T>()
    for ((index, item) in this.withIndex()) {
        if (index < n) continue
        list.add(item)
    }
    return list
}

fun <FIRST_LIST_TYPE, SECOND_LIST_TYPE, FIRST_LIST_TRANSFORMER, SECOND_LIST_TRANSFORMER, RETURN_TYPE>
        Iterable<FIRST_LIST_TYPE>.zipBy(
    other: Iterable<SECOND_LIST_TYPE>,
    firstListTransformer: (FIRST_LIST_TYPE) -> FIRST_LIST_TRANSFORMER,
    secondListTransformer: (SECOND_LIST_TYPE) -> SECOND_LIST_TRANSFORMER,
    pairPredicate: (first: FIRST_LIST_TYPE?, second: SECOND_LIST_TYPE?) -> RETURN_TYPE?
): List<RETURN_TYPE> {
    val firstByKey: Map<FIRST_LIST_TRANSFORMER, FIRST_LIST_TYPE> = associateBy {
        firstListTransformer(it)
    }
    val secondByKey: Map<SECOND_LIST_TRANSFORMER, SECOND_LIST_TYPE> = other.associateBy {
        secondListTransformer(it)
    }
    val resultList = (firstByKey.keys + secondByKey.keys).map { (firstByKey[it]) to (secondByKey[it]) }
    return resultList.asSequence().mapNotNull {
        val firstPair = it.first
        val secondPair = it.second
        pairPredicate(firstPair, secondPair)
    }.toList()
}