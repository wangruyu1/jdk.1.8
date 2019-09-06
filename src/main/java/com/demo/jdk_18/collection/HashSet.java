/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.demo.jdk_18.collection;

import java.io.InvalidObjectException;

/**
 * 其实就是对hashmap的key的操作
 *
 */
public class HashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, java.io.Serializable {
	static final long serialVersionUID = -5024744406713321676L;

	private transient HashMap<E, Object> map;

	// Dummy value to associate with an Object in the backing Map
	private static final Object PRESENT = new Object();

	/**
	 * 调用默认的hashmap构造方法初始化属性map
	 */
	public HashSet() {
		map = new HashMap<>();
	}

	/**
	 * 根据传入的集合构造set
	 *
	 * @param c
	 *            传入的集合
	 * @throws NullPointerException
	 *             if the specified collection is null
	 */
	public HashSet(Collection<? extends E> c) {
		// 根据c计算map的初始化大小
		map = new HashMap<>(Math.max((int) (c.size() / .75f) + 1, 16));
		addAll(c);
	}

	/**
	 * 创建set集合指定容量和扩容因子
	 *
	 * @param initialCapacity
	 *            the initial capacity of the hash map
	 * @param loadFactor
	 *            the load factor of the hash map
	 * @throws IllegalArgumentException
	 *             if the initial capacity is less than zero, or if the load
	 *             factor is nonpositive
	 */
	public HashSet(int initialCapacity, float loadFactor) {
		// 直接调用hashmap的构造方法
		map = new HashMap<>(initialCapacity, loadFactor);
	}

	/**
	 * 创建set使用容量
	 *
	 * @param initialCapacity
	 *            the initial capacity of the hash table
	 * @throws IllegalArgumentException
	 *             if the initial capacity is less than zero
	 */
	public HashSet(int initialCapacity) {
		map = new HashMap<>(initialCapacity);
	}

	/**
	 * 创建一个有序的set(包可见?)
	 *
	 */
	HashSet(int initialCapacity, float loadFactor, boolean dummy) {
		map = new LinkedHashMap<>(initialCapacity, loadFactor);
	}

	/**
	 * 返回map的key迭代器
	 */
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	/**
	 *
	 * @return the number of elements in this set (its cardinality)
	 */
	public int size() {
		return map.size();
	}

	/**
	 * Returns <tt>true</tt> if this set contains no elements.
	 *
	 * @return <tt>true</tt> if this set contains no elements
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Returns <tt>true</tt> if this set contains the specified element. More
	 * formally, returns <tt>true</tt> if and only if this set contains an
	 * element <tt>e</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
	 *
	 * @param o
	 *            element whose presence in this set is to be tested
	 * @return <tt>true</tt> if this set contains the specified element
	 */
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	/**
	 * 添加数据数据(其实会更新map的value,只是value值不变)
	 *
	 * @param e
	 *            element to be added to this set
	 * @return <tt>true</tt> if this set did not already contain the specified
	 *         element
	 */
	public boolean add(E e) {
		// 调用hashmap的put方法,null表示添加数据
		return map.put(e, PRESENT) == null;
	}

	/**
	 * 删除元素
	 * 
	 * @param o
	 *            object to be removed from this set, if present
	 * @return <tt>true</tt> if the set contained the specified element
	 */
	public boolean remove(Object o) {
		return map.remove(o) == PRESENT;
	}

	/**
	 * Removes all of the elements from this set. The set will be empty after
	 * this call returns.
	 */
	public void clear() {
		map.clear();
	}

	/**
	 * Returns a shallow copy of this <tt>HashSet</tt> instance: the elements
	 * themselves are not cloned.
	 *
	 * @return a shallow copy of this set
	 */
	@SuppressWarnings("unchecked")
	public Object clone() {
		try {
			HashSet<E> newSet = (HashSet<E>) super.clone();
			newSet.map = (HashMap<E, Object>) map.clone();
			return newSet;
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}

	/**
	 * Save the state of this <tt>HashSet</tt> instance to a stream (that is,
	 * serialize it).
	 *
	 * @serialData The capacity of the backing <tt>HashMap</tt> instance (int),
	 *             and its load factor (float) are emitted, followed by the size
	 *             of the set (the number of elements it contains) (int),
	 *             followed by all of its elements (each an Object) in no
	 *             particular order.
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		// Write out any hidden serialization magic
		s.defaultWriteObject();

		// Write out HashMap capacity and load factor
		s.writeInt(map.capacity());
		s.writeFloat(map.loadFactor());

		// Write out size
		s.writeInt(map.size());

		// Write out all elements in the proper order.
		for (E e : map.keySet())
			s.writeObject(e);
	}

	/**
	 * Reconstitute the <tt>HashSet</tt> instance from a stream (that is,
	 * deserialize it).
	 */
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		// Read in any hidden serialization magic
		s.defaultReadObject();

		// Read capacity and verify non-negative.
		int capacity = s.readInt();
		if (capacity < 0) {
			throw new InvalidObjectException("Illegal capacity: " + capacity);
		}

		// Read load factor and verify positive and non NaN.
		float loadFactor = s.readFloat();
		if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
			throw new InvalidObjectException("Illegal load factor: " + loadFactor);
		}

		// Read size and verify non-negative.
		int size = s.readInt();
		if (size < 0) {
			throw new InvalidObjectException("Illegal size: " + size);
		}

		// Set the capacity according to the size and load factor ensuring that
		// the HashMap is at least 25% full but clamping to maximum capacity.
		capacity = (int) Math.min(size * Math.min(1 / loadFactor, 4.0f), HashMap.MAXIMUM_CAPACITY);

		// Create backing HashMap
		map = (((HashSet<?>) this) instanceof LinkedHashSet ? new LinkedHashMap<E, Object>(capacity, loadFactor)
				: new HashMap<E, Object>(capacity, loadFactor));

		// Read in all elements in the proper order.
		for (int i = 0; i < size; i++) {
			@SuppressWarnings("unchecked")
			E e = (E) s.readObject();
			map.put(e, PRESENT);
		}
	}

	/**
	 * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
	 * and <em>fail-fast</em> {@link Spliterator} over the elements in this set.
	 *
	 * <p>
	 * The {@code Spliterator} reports {@link Spliterator#SIZED} and
	 * {@link Spliterator#DISTINCT}. Overriding implementations should document
	 * the reporting of additional characteristic values.
	 *
	 * @return a {@code Spliterator} over the elements in this set
	 * @since 1.8
	 */
	public Spliterator<E> spliterator() {
		return new HashMap.KeySpliterator<E, Object>(map, 0, -1, 0, 0);
	}
}
