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

import java.util.function.Consumer;

public class LinkedList<E> extends AbstractSequentialList<E>
		implements List<E>, Deque<E>, Cloneable, java.io.Serializable {
	transient int size = 0;

	/**
	 * 头结点
	 */
	transient Node<E> first;

	/**
	 * 尾节点
	 */
	transient Node<E> last;

	/**
	 * Constructs an empty list.
	 */
	public LinkedList() {
	}

	/**
	 * 根据传入集合构造一个列表
	 * 
	 * @throws NullPointerException
	 *             if the specified collection is null
	 */
	public LinkedList(Collection<? extends E> c) {
		this();
		addAll(c);
	}

	/**
	 * 将元素添加到头结点
	 */
	private void linkFirst(E e) {
		final Node<E> f = first;
		final Node<E> newNode = new Node<>(null, e, f);
		first = newNode;
		if (f == null)
			last = newNode;
		else
			f.prev = newNode;
		size++;
		modCount++;
	}

	/**
	 * 将元素添加到尾部
	 */
	void linkLast(E e) {
		final Node<E> l = last;
		final Node<E> newNode = new Node<>(l, e, null);
		last = newNode;
		if (l == null)
			// 链表不存在数据
			first = newNode;
		else
			// 将新增节点连起来
			l.next = newNode;
		size++;
		modCount++;
	}

	/**
	 * 非null节点前添加数据
	 */
	void linkBefore(E e, Node<E> succ) {
		// assert succ != null;
		final Node<E> pred = succ.prev;
		final Node<E> newNode = new Node<>(pred, e, succ);
		succ.prev = newNode;
		if (pred == null)
			// 头部添加节点
			first = newNode;
		else
			pred.next = newNode;
		size++;
		modCount++;
	}

	/**
	 * Unlinks non-null first node f.
	 */
	private E unlinkFirst(Node<E> f) {
		// assert f == first && f != null;
		final E element = f.item;
		final Node<E> next = f.next;
		f.item = null;
		f.next = null; // help GC
		first = next;
		if (next == null)
			last = null;
		else
			next.prev = null;
		size--;
		modCount++;
		return element;
	}

	/**
	 * Unlinks non-null last node l.
	 */
	private E unlinkLast(Node<E> l) {
		// assert l == last && l != null;
		final E element = l.item;
		final Node<E> prev = l.prev;
		l.item = null;
		l.prev = null; // help GC
		last = prev;
		if (prev == null)
			first = null;
		else
			prev.next = null;
		size--;
		modCount++;
		return element;
	}

	/**
	 * 删除节点x
	 */
	E unlink(Node<E> x) {
		// assert x != null;
		final E element = x.item;
		final Node<E> next = x.next;
		final Node<E> prev = x.prev;

		if (prev == null) {
			// 刪除的是头结点,头结点变为下一个节点
			first = next;
		} else {
			// 变更前一个节点的next指向x.next
			prev.next = next;
			// 这一步应该没啥影响(防止根据x得到prev对齐进行修改？？)
			x.prev = null;
		}

		if (next == null) {
			// 删除的是尾节点
			// 更新尾节点为前一个节点
			last = prev;
		} else {
			// 删除的不是尾节点
			// 更新下一个节点的prev指向x.prev
			next.prev = prev;
			// 同上x.prev = null;
			x.next = null;
		}
		// 没啥影响吧？
		x.item = null;
		size--;
		modCount++;
		return element;
	}

	/**
	 * Returns the first element in this list.
	 *
	 * @return the first element in this list
	 * @throws NoSuchElementException
	 *             if this list is empty
	 */
	public E getFirst() {
		final Node<E> f = first;
		if (f == null)
			throw new NoSuchElementException();
		return f.item;
	}

	/**
	 * Returns the last element in this list.
	 *
	 * @return the last element in this list
	 * @throws NoSuchElementException
	 *             if this list is empty
	 */
	public E getLast() {
		final Node<E> l = last;
		if (l == null)
			throw new NoSuchElementException();
		return l.item;
	}

	/**
	 * Removes and returns the first element from this list.
	 *
	 * @return the first element from this list
	 * @throws NoSuchElementException
	 *             if this list is empty
	 */
	public E removeFirst() {
		final Node<E> f = first;
		if (f == null)
			throw new NoSuchElementException();
		return unlinkFirst(f);
	}

	/**
	 * Removes and returns the last element from this list.
	 *
	 * @return the last element from this list
	 * @throws NoSuchElementException
	 *             if this list is empty
	 */
	public E removeLast() {
		final Node<E> l = last;
		if (l == null)
			throw new NoSuchElementException();
		return unlinkLast(l);
	}

	/**
	 * Inserts the specified element at the beginning of this list.
	 *
	 * @param e
	 *            the element to add
	 */
	public void addFirst(E e) {
		linkFirst(e);
	}

	/**
	 * Appends the specified element to the end of this list.
	 *
	 * <p>
	 * This method is equivalent to {@link #add}.
	 *
	 * @param e
	 *            the element to add
	 */
	public void addLast(E e) {
		linkLast(e);
	}

	/**
	 * Returns {@code true} if this list contains the specified element. More
	 * formally, returns {@code true} if and only if this list contains at least
	 * one element {@code e} such that
	 * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
	 *
	 * @param o
	 *            element whose presence in this list is to be tested
	 * @return {@code true} if this list contains the specified element
	 */
	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}

	/**
	 * Returns the number of elements in this list.
	 *
	 * @return the number of elements in this list
	 */
	public int size() {
		return size;
	}

	/**
	 * Appends the specified element to the end of this list.
	 *
	 * <p>
	 * This method is equivalent to {@link #addLast}.
	 *
	 * @param e
	 *            element to be appended to this list
	 * @return {@code true} (as specified by {@link Collection#add})
	 */
	public boolean add(E e) {
		linkLast(e);
		return true;
	}

	/**
	 * Removes the first occurrence of the specified element from this list, if
	 * it is present. If this list does not contain the element, it is
	 * unchanged. More formally, removes the element with the lowest index
	 * {@code i} such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
	 * (if such an element exists). Returns {@code true} if this list contained
	 * the specified element (or equivalently, if this list changed as a result
	 * of the call).
	 *
	 * @param o
	 *            element to be removed from this list, if present
	 * @return {@code true} if this list contained the specified element
	 */
	public boolean remove(Object o) {
		if (o == null) {
			for (Node<E> x = first; x != null; x = x.next) {
				if (x.item == null) {
					unlink(x);
					return true;
				}
			}
		} else {
			for (Node<E> x = first; x != null; x = x.next) {
				if (o.equals(x.item)) {
					unlink(x);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 追加集合元素
	 *
	 * @param c
	 *            需要添加的集合
	 * @return {@code true} if this list changed as a result of the call
	 * @throws NullPointerException
	 *             if the specified collection is null
	 */
	public boolean addAll(Collection<? extends E> c) {
		return addAll(size, c);
	}

	/**
	 * 在指定位置添加集合元素
	 *
	 * @param index
	 *            插入数据的位置
	 * @param c
	 *            需要添加的元素集合
	 * @return {@code true} if this list changed as a result of the call
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if the specified collection is null
	 */
	public boolean addAll(int index, Collection<? extends E> c) {
		checkPositionIndex(index);

		Object[] a = c.toArray();
		int numNew = a.length;
		if (numNew == 0)
			// 传入集合是空集合
			return false;

		Node<E> pred, succ;// index的前一个节点和index所在的节点
		if (index == size) {
			succ = null;
			pred = last;
		} else {
			succ = node(index);
			pred = succ.prev;
		}
		// 遍历参数集合
		for (Object o : a) {
			@SuppressWarnings("unchecked")
			E e = (E) o;
			// 新建一个节点
			Node<E> newNode = new Node<>(pred, e, null);
			if (pred == null)
				// 如果当前添加的是头结点
				first = newNode;
			else
				// 将下一个节点连起来
				pred.next = newNode;
			pred = newNode;
		}

		if (succ == null) {
			// 如果实在尾部添加数据,更新链表的尾节点，pred是添加完数据之后的最后一个节点
			last = pred;
		} else {
			// 不是在为尾节点添加数据,添加完数据后需要将index以及之后的链表连起来
			pred.next = succ;
			succ.prev = pred;
		}
		// 链表大小变化
		size += numNew;
		// 操作计数器加一
		modCount++;
		return true;
	}

	/**
	 * 删除链表的所有元素
	 */
	public void clear() {
		// Clearing all of the links between nodes is "unnecessary", but:
		// - helps a generational GC if the discarded nodes inhabit
		// more than one generation
		// - is sure to free memory even if there is a reachable Iterator
		// 这一步不是必须的，因为只要first,last被置为null,就会被GC。
		// 英文注释:我不是很明白那种条件需要这样做。
		for (Node<E> x = first; x != null;) {
			Node<E> next = x.next;
			x.item = null;
			x.next = null;
			x.prev = null;
			x = next;
		}
		// 这一步就能能保证GC回收了
		first = last = null;
		size = 0;
		modCount++;
	}

	// Positional Access Operations

	/**
	 * 查询指定位置的元素（最好不要使用这种方式,因为是从头或者从尾部开始找index所在的元素的）
	 * 
	 * @param index
	 *            指定位置的索引
	 * @return the element at the specified position in this list
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public E get(int index) {
		checkElementIndex(index);
		return node(index).item;
	}

	/**
	 * 修改指定位置的值
	 *
	 * @param index
	 *            指定位置的下标
	 * @param element
	 *            index的新值
	 * @return the element previously at the specified position
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public E set(int index, E element) {
		checkElementIndex(index);
		// 找到index所在的节点
		Node<E> x = node(index);
		E oldVal = x.item;
		// 更新为新值
		x.item = element;
		return oldVal;
	}

	/**
	 * 在指定位置添加元素
	 *
	 * @param index
	 *            下标
	 * @param element
	 *            添加的元素
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public void add(int index, E element) {
		checkPositionIndex(index);

		if (index == size)
			// 在尾部添加
			linkLast(element);
		else
			// 不是在尾部添加数据
			linkBefore(element, node(index));
	}

	/**
	 * 删除指定位置的元素
	 * 
	 * @param index
	 *            元素下标
	 * @return the element previously at the specified position
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public E remove(int index) {
		checkElementIndex(index);
		return unlink(node(index));
	}

	/**
	 * Tells if the argument is the index of an existing element.
	 */
	private boolean isElementIndex(int index) {
		return index >= 0 && index < size;
	}

	/**
	 * Tells if the argument is the index of a valid position for an iterator or
	 * an add operation.
	 */
	private boolean isPositionIndex(int index) {
		return index >= 0 && index <= size;
	}

	/**
	 * Constructs an IndexOutOfBoundsException detail message. Of the many
	 * possible refactorings of the error handling code, this "outlining"
	 * performs best with both server and client VMs.
	 */
	private String outOfBoundsMsg(int index) {
		return "Index: " + index + ", Size: " + size;
	}

	private void checkElementIndex(int index) {
		if (!isElementIndex(index))
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	/**
	 * 检查给定位置是否越界
	 * 
	 * @param index
	 */
	private void checkPositionIndex(int index) {
		if (!isPositionIndex(index))
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	/**
	 * 找到index所在的节点
	 */
	Node<E> node(int index) {
		// assert isElementIndex(index);

		if (index < (size >> 1)) {
			// index在链表的前一半内
			Node<E> x = first;
			for (int i = 0; i < index; i++)
				x = x.next;
			return x;
		} else {
			// index在链表的后一半内
			Node<E> x = last;
			for (int i = size - 1; i > index; i--)
				x = x.prev;
			return x;
		}
	}

	// Search Operations

	/**
	 * 根据给定元素查询与之相等的第一个元素所在的位置
	 *
	 * @param o
	 *            给定的元素
	 * @return 相等元素的位置,-1=不存在该元素
	 */
	public int indexOf(Object o) {
		int index = 0;
		if (o == null) {
			for (Node<E> x = first; x != null; x = x.next) {
				if (x.item == null)
					return index;
				index++;
			}
		} else {
			for (Node<E> x = first; x != null; x = x.next) {
				if (o.equals(x.item))
					return index;
				index++;
			}
		}
		return -1;
	}

	/**
	 * 返回链表中与给定数据相等的最后一个元素位置
	 * 
	 * @param o
	 *            給定的对象
	 * @return 相等元素的位置,-1=不存在该元素
	 */
	public int lastIndexOf(Object o) {
		int index = size;
		if (o == null) {
			for (Node<E> x = last; x != null; x = x.prev) {
				index--;
				if (x.item == null)
					return index;
			}
		} else {
			for (Node<E> x = last; x != null; x = x.prev) {
				index--;
				if (o.equals(x.item))
					return index;
			}
		}
		return -1;
	}

	// Queue operations.

	/**
	 * 得到头结点元素,支持null
	 */
	public E peek() {
		final Node<E> f = first;
		return (f == null) ? null : f.item;
	}

	/**
	 * 得到头结点,不支持null
	 */
	public E element() {
		return getFirst();
	}

	/**
	 * 查询头结点并删除，支持null
	 */
	public E poll() {
		final Node<E> f = first;
		return (f == null) ? null : unlinkFirst(f);
	}

	/**
	 *
	 * 删除头结点,不支持null
	 */
	public E remove() {
		return removeFirst();
	}

	/**
	 * 在尾部添加元素
	 * 
	 * @since 1.5
	 */
	public boolean offer(E e) {
		return add(e);
	}

	// Deque operations
	/**
	 * 头部添加元素
	 * 
	 */
	public boolean offerFirst(E e) {
		addFirst(e);
		return true;
	}

	/**
	 * 尾部添加元素
	 */
	public boolean offerLast(E e) {
		addLast(e);
		return true;
	}

	/**
	 * 查询头结点元素，支持null
	 */
	public E peekFirst() {
		final Node<E> f = first;
		return (f == null) ? null : f.item;
	}

	/**
	 * 返回尾节点元素,支持null
	 */
	public E peekLast() {
		final Node<E> l = last;
		return (l == null) ? null : l.item;
	}

	/**
	 * 删除头结点元素，不存在返回null
	 */
	public E pollFirst() {
		final Node<E> f = first;
		return (f == null) ? null : unlinkFirst(f);
	}

	/**
	 * 删除尾节点,不存在返回null
	 */
	public E pollLast() {
		final Node<E> l = last;
		return (l == null) ? null : unlinkLast(l);
	}

	/**
	 * 添加元素到头结点
	 */
	public void push(E e) {
		addFirst(e);
	}

	/**
	 * 删除头结点,不存在抛异常
	 */
	public E pop() {
		return removeFirst();
	}

	/**
	 * 删除第一个出现的元素
	 */
	public boolean removeFirstOccurrence(Object o) {
		return remove(o);
	}

	/**
	 *
	 * 删除最后一个出现的元素
	 */
	public boolean removeLastOccurrence(Object o) {
		if (o == null) {
			for (Node<E> x = last; x != null; x = x.prev) {
				if (x.item == null) {
					unlink(x);
					return true;
				}
			}
		} else {
			for (Node<E> x = last; x != null; x = x.prev) {
				if (o.equals(x.item)) {
					unlink(x);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 返回链表迭代器
	 */
	public ListIterator<E> listIterator(int index) {
		checkPositionIndex(index);
		return new ListItr(index);
	}

	/**
	 * 链表迭代器，支持迭代前后元素以及增删改(和ArrayList类似)
	 *
	 */
	private class ListItr implements ListIterator<E> {
		private Node<E> lastReturned;
		private Node<E> next;
		private int nextIndex;
		private int expectedModCount = modCount;

		ListItr(int index) {
			// assert isPositionIndex(index);
			next = (index == size) ? null : node(index);
			nextIndex = index;
		}

		public boolean hasNext() {
			return nextIndex < size;
		}

		public E next() {
			checkForComodification();
			if (!hasNext())
				throw new NoSuchElementException();

			lastReturned = next;
			next = next.next;
			nextIndex++;
			return lastReturned.item;
		}

		public boolean hasPrevious() {
			return nextIndex > 0;
		}

		public E previous() {
			checkForComodification();
			if (!hasPrevious())
				throw new NoSuchElementException();

			lastReturned = next = (next == null) ? last : next.prev;
			nextIndex--;
			return lastReturned.item;
		}

		public int nextIndex() {
			return nextIndex;
		}

		public int previousIndex() {
			return nextIndex - 1;
		}

		public void remove() {
			checkForComodification();
			if (lastReturned == null)
				throw new IllegalStateException();

			Node<E> lastNext = lastReturned.next;
			unlink(lastReturned);
			if (next == lastReturned)
				next = lastNext;
			else
				nextIndex--;
			lastReturned = null;
			expectedModCount++;
		}

		public void set(E e) {
			if (lastReturned == null)
				throw new IllegalStateException();
			checkForComodification();
			lastReturned.item = e;
		}

		public void add(E e) {
			checkForComodification();
			lastReturned = null;
			if (next == null)
				linkLast(e);
			else
				linkBefore(e, next);
			nextIndex++;
			expectedModCount++;
		}

		public void forEachRemaining(Consumer<? super E> action) {
			Objects.requireNonNull(action);
			while (modCount == expectedModCount && nextIndex < size) {
				action.accept(next.item);
				lastReturned = next;
				next = next.next;
				nextIndex++;
			}
			checkForComodification();
		}

		final void checkForComodification() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
		}
	}

	/**
	 * 存放数据的节点
	 *
	 */
	private static class Node<E> {
		E item;
		Node<E> next;
		Node<E> prev;

		Node(Node<E> prev, E element, Node<E> next) {
			this.item = element;
			this.next = next;
			this.prev = prev;
		}
	}

	/**
	 * 
	 */
	public Iterator<E> descendingIterator() {
		return new DescendingIterator();
	}

	/**
	 * 向前迭代的迭代器（使用的就是ListItr的迭代方式）
	 */
	private class DescendingIterator implements Iterator<E> {
		private final ListItr itr = new ListItr(size());

		public boolean hasNext() {
			return itr.hasPrevious();
		}

		public E next() {
			return itr.previous();
		}

		public void remove() {
			itr.remove();
		}
	}

	@SuppressWarnings("unchecked")
	private LinkedList<E> superClone() {
		try {
			return (LinkedList<E>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}

	/**
	 * 浅拷贝
	 *
	 */
	public Object clone() {
		// 拷贝链表变量(应该和new LinkedList没区别)
		LinkedList<E> clone = superClone();

		// Put clone into "virgin" state
		clone.first = clone.last = null;
		clone.size = 0;
		clone.modCount = 0;

		// 将链表的数据使用新的链表连起来（这里并不是深拷贝，所以两个链表中的对应的item都是指向相同的对象）
		for (Node<E> x = first; x != null; x = x.next)
			clone.add(x.item);

		return clone;
	}

	/**
	 * 将链表转化为object数组
	 *
	 */
	public Object[] toArray() {
		// 创建一个链表大小的数组
		Object[] result = new Object[size];
		int i = 0;
		// 遍历链表
		for (Node<E> x = first; x != null; x = x.next)
			result[i++] = x.item;
		return result;
	}

	/**
	 * 返回一个指定类型的数组
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		if (a.length < size)
			// 创建一个T类型的大小为size的数组
			a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
		int i = 0;
		// 这一步应该是防止使用a[i++] = x.item抛错,所以使用result[i++] = x.item
		Object[] result = a;
		for (Node<E> x = first; x != null; x = x.next)
			result[i++] = x.item;
		if (a.length > size)
			// 传入的数组大小比链表大(不是很理解,一般传入的a大小都是0,不会走这里)
			a[size] = null;

		return a;
	}

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 876323262645176354L;

	/**
	 * 序列化
	 * 
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		// Write out any hidden serialization magic
		// 序列化非静态和非traisent字段
		s.defaultWriteObject();

		// Write out size
		// 序列化大小
		s.writeInt(size);

		// Write out all elements in the proper order.
		// 序列化链表中的每一个元素
		for (Node<E> x = first; x != null; x = x.next)
			s.writeObject(x.item);
	}

	/**
	 * 反序列化
	 */
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		// Read in any hidden serialization magic
		// 反序列化defaultWriteObject写入的数据
		s.defaultReadObject();

		// Read in size
		// 反序列化大小
		int size = s.readInt();

		// Read in all elements in the proper order.
		// 反序列化每个元素
		for (int i = 0; i < size; i++)
			linkLast((E) s.readObject());
	}

	/**
	 * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
	 * and <em>fail-fast</em> {@link Spliterator} over the elements in this
	 * list.
	 *
	 * <p>
	 * The {@code Spliterator} reports {@link Spliterator#SIZED} and
	 * {@link Spliterator#ORDERED}. Overriding implementations should document
	 * the reporting of additional characteristic values.
	 *
	 * @implNote The {@code Spliterator} additionally reports
	 *           {@link Spliterator#SUBSIZED} and implements {@code trySplit} to
	 *           permit limited parallelism..
	 *
	 * @return a {@code Spliterator} over the elements in this list
	 * @since 1.8
	 */
	@Override
	public Spliterator<E> spliterator() {
		return new LLSpliterator<E>(this, -1, 0);
	}

	/** A customized variant of Spliterators.IteratorSpliterator */
	static final class LLSpliterator<E> implements Spliterator<E> {
		static final int BATCH_UNIT = 1 << 10; // batch array size increment
		static final int MAX_BATCH = 1 << 25; // max batch array size;
		final LinkedList<E> list; // null OK unless traversed
		Node<E> current; // current node; null until initialized
		int est; // size estimate; -1 until first needed
		int expectedModCount; // initialized when est set
		int batch; // batch size for splits

		LLSpliterator(LinkedList<E> list, int est, int expectedModCount) {
			this.list = list;
			this.est = est;
			this.expectedModCount = expectedModCount;
		}

		final int getEst() {
			int s; // force initialization
			final LinkedList<E> lst;
			if ((s = est) < 0) {
				if ((lst = list) == null)
					s = est = 0;
				else {
					expectedModCount = lst.modCount;
					current = lst.first;
					s = est = lst.size;
				}
			}
			return s;
		}

		public long estimateSize() {
			return (long) getEst();
		}

		public Spliterator<E> trySplit() {
			Node<E> p;
			int s = getEst();
			if (s > 1 && (p = current) != null) {
				int n = batch + BATCH_UNIT;
				if (n > s)
					n = s;
				if (n > MAX_BATCH)
					n = MAX_BATCH;
				Object[] a = new Object[n];
				int j = 0;
				do {
					a[j++] = p.item;
				} while ((p = p.next) != null && j < n);
				current = p;
				batch = j;
				est = s - j;
				return Spliterators.spliterator(a, 0, j, Spliterator.ORDERED);
			}
			return null;
		}

		public void forEachRemaining(Consumer<? super E> action) {
			Node<E> p;
			int n;
			if (action == null)
				throw new NullPointerException();
			if ((n = getEst()) > 0 && (p = current) != null) {
				current = null;
				est = 0;
				do {
					E e = p.item;
					p = p.next;
					action.accept(e);
				} while (p != null && --n > 0);
			}
			if (list.modCount != expectedModCount)
				throw new ConcurrentModificationException();
		}

		public boolean tryAdvance(Consumer<? super E> action) {
			Node<E> p;
			if (action == null)
				throw new NullPointerException();
			if (getEst() > 0 && (p = current) != null) {
				--est;
				E e = p.item;
				current = p.next;
				action.accept(e);
				if (list.modCount != expectedModCount)
					throw new ConcurrentModificationException();
				return true;
			}
			return false;
		}

		public int characteristics() {
			return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
		}
	}

}
