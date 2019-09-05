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
	 * ͷ���
	 */
	transient Node<E> first;

	/**
	 * β�ڵ�
	 */
	transient Node<E> last;

	/**
	 * Constructs an empty list.
	 */
	public LinkedList() {
	}

	/**
	 * ���ݴ��뼯�Ϲ���һ���б�
	 * 
	 * @throws NullPointerException
	 *             if the specified collection is null
	 */
	public LinkedList(Collection<? extends E> c) {
		this();
		addAll(c);
	}

	/**
	 * ��Ԫ����ӵ�ͷ���
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
	 * ��Ԫ����ӵ�β��
	 */
	void linkLast(E e) {
		final Node<E> l = last;
		final Node<E> newNode = new Node<>(l, e, null);
		last = newNode;
		if (l == null)
			// ������������
			first = newNode;
		else
			// �������ڵ�������
			l.next = newNode;
		size++;
		modCount++;
	}

	/**
	 * ��null�ڵ�ǰ�������
	 */
	void linkBefore(E e, Node<E> succ) {
		// assert succ != null;
		final Node<E> pred = succ.prev;
		final Node<E> newNode = new Node<>(pred, e, succ);
		succ.prev = newNode;
		if (pred == null)
			// ͷ����ӽڵ�
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
	 * ɾ���ڵ�x
	 */
	E unlink(Node<E> x) {
		// assert x != null;
		final E element = x.item;
		final Node<E> next = x.next;
		final Node<E> prev = x.prev;

		if (prev == null) {
			// �h������ͷ���,ͷ����Ϊ��һ���ڵ�
			first = next;
		} else {
			// ���ǰһ���ڵ��nextָ��x.next
			prev.next = next;
			// ��һ��Ӧ��ûɶӰ��(��ֹ����x�õ�prev��������޸ģ���)
			x.prev = null;
		}

		if (next == null) {
			// ɾ������β�ڵ�
			// ����β�ڵ�Ϊǰһ���ڵ�
			last = prev;
		} else {
			// ɾ���Ĳ���β�ڵ�
			// ������һ���ڵ��prevָ��x.prev
			next.prev = prev;
			// ͬ��x.prev = null;
			x.next = null;
		}
		// ûɶӰ��ɣ�
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
	 * ׷�Ӽ���Ԫ��
	 *
	 * @param c
	 *            ��Ҫ��ӵļ���
	 * @return {@code true} if this list changed as a result of the call
	 * @throws NullPointerException
	 *             if the specified collection is null
	 */
	public boolean addAll(Collection<? extends E> c) {
		return addAll(size, c);
	}

	/**
	 * ��ָ��λ����Ӽ���Ԫ��
	 *
	 * @param index
	 *            �������ݵ�λ��
	 * @param c
	 *            ��Ҫ��ӵ�Ԫ�ؼ���
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
			// ���뼯���ǿռ���
			return false;

		Node<E> pred, succ;// index��ǰһ���ڵ��index���ڵĽڵ�
		if (index == size) {
			succ = null;
			pred = last;
		} else {
			succ = node(index);
			pred = succ.prev;
		}
		// ������������
		for (Object o : a) {
			@SuppressWarnings("unchecked")
			E e = (E) o;
			// �½�һ���ڵ�
			Node<E> newNode = new Node<>(pred, e, null);
			if (pred == null)
				// �����ǰ��ӵ���ͷ���
				first = newNode;
			else
				// ����һ���ڵ�������
				pred.next = newNode;
			pred = newNode;
		}

		if (succ == null) {
			// ���ʵ��β���������,���������β�ڵ㣬pred�����������֮������һ���ڵ�
			last = pred;
		} else {
			// ������Ϊβ�ڵ��������,��������ݺ���Ҫ��index�Լ�֮�������������
			pred.next = succ;
			succ.prev = pred;
		}
		// �����С�仯
		size += numNew;
		// ������������һ
		modCount++;
		return true;
	}

	/**
	 * ɾ�����������Ԫ��
	 */
	public void clear() {
		// Clearing all of the links between nodes is "unnecessary", but:
		// - helps a generational GC if the discarded nodes inhabit
		// more than one generation
		// - is sure to free memory even if there is a reachable Iterator
		// ��һ�����Ǳ���ģ���ΪֻҪfirst,last����Ϊnull,�ͻᱻGC��
		// Ӣ��ע��:�Ҳ��Ǻ���������������Ҫ��������
		for (Node<E> x = first; x != null;) {
			Node<E> next = x.next;
			x.item = null;
			x.next = null;
			x.prev = null;
			x = next;
		}
		// ��һ�������ܱ�֤GC������
		first = last = null;
		size = 0;
		modCount++;
	}

	// Positional Access Operations

	/**
	 * ��ѯָ��λ�õ�Ԫ�أ���ò�Ҫʹ�����ַ�ʽ,��Ϊ�Ǵ�ͷ���ߴ�β����ʼ��index���ڵ�Ԫ�صģ�
	 * 
	 * @param index
	 *            ָ��λ�õ�����
	 * @return the element at the specified position in this list
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public E get(int index) {
		checkElementIndex(index);
		return node(index).item;
	}

	/**
	 * �޸�ָ��λ�õ�ֵ
	 *
	 * @param index
	 *            ָ��λ�õ��±�
	 * @param element
	 *            index����ֵ
	 * @return the element previously at the specified position
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public E set(int index, E element) {
		checkElementIndex(index);
		// �ҵ�index���ڵĽڵ�
		Node<E> x = node(index);
		E oldVal = x.item;
		// ����Ϊ��ֵ
		x.item = element;
		return oldVal;
	}

	/**
	 * ��ָ��λ�����Ԫ��
	 *
	 * @param index
	 *            �±�
	 * @param element
	 *            ��ӵ�Ԫ��
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public void add(int index, E element) {
		checkPositionIndex(index);

		if (index == size)
			// ��β�����
			linkLast(element);
		else
			// ������β���������
			linkBefore(element, node(index));
	}

	/**
	 * ɾ��ָ��λ�õ�Ԫ��
	 * 
	 * @param index
	 *            Ԫ���±�
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
	 * ������λ���Ƿ�Խ��
	 * 
	 * @param index
	 */
	private void checkPositionIndex(int index) {
		if (!isPositionIndex(index))
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	/**
	 * �ҵ�index���ڵĽڵ�
	 */
	Node<E> node(int index) {
		// assert isElementIndex(index);

		if (index < (size >> 1)) {
			// index�������ǰһ����
			Node<E> x = first;
			for (int i = 0; i < index; i++)
				x = x.next;
			return x;
		} else {
			// index������ĺ�һ����
			Node<E> x = last;
			for (int i = size - 1; i > index; i--)
				x = x.prev;
			return x;
		}
	}

	// Search Operations

	/**
	 * ���ݸ���Ԫ�ز�ѯ��֮��ȵĵ�һ��Ԫ�����ڵ�λ��
	 *
	 * @param o
	 *            ������Ԫ��
	 * @return ���Ԫ�ص�λ��,-1=�����ڸ�Ԫ��
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
	 * ���������������������ȵ����һ��Ԫ��λ��
	 * 
	 * @param o
	 *            �o���Ķ���
	 * @return ���Ԫ�ص�λ��,-1=�����ڸ�Ԫ��
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
	 * �õ�ͷ���Ԫ��,֧��null
	 */
	public E peek() {
		final Node<E> f = first;
		return (f == null) ? null : f.item;
	}

	/**
	 * �õ�ͷ���,��֧��null
	 */
	public E element() {
		return getFirst();
	}

	/**
	 * ��ѯͷ��㲢ɾ����֧��null
	 */
	public E poll() {
		final Node<E> f = first;
		return (f == null) ? null : unlinkFirst(f);
	}

	/**
	 *
	 * ɾ��ͷ���,��֧��null
	 */
	public E remove() {
		return removeFirst();
	}

	/**
	 * ��β�����Ԫ��
	 * 
	 * @since 1.5
	 */
	public boolean offer(E e) {
		return add(e);
	}

	// Deque operations
	/**
	 * ͷ�����Ԫ��
	 * 
	 */
	public boolean offerFirst(E e) {
		addFirst(e);
		return true;
	}

	/**
	 * β�����Ԫ��
	 */
	public boolean offerLast(E e) {
		addLast(e);
		return true;
	}

	/**
	 * ��ѯͷ���Ԫ�أ�֧��null
	 */
	public E peekFirst() {
		final Node<E> f = first;
		return (f == null) ? null : f.item;
	}

	/**
	 * ����β�ڵ�Ԫ��,֧��null
	 */
	public E peekLast() {
		final Node<E> l = last;
		return (l == null) ? null : l.item;
	}

	/**
	 * ɾ��ͷ���Ԫ�أ������ڷ���null
	 */
	public E pollFirst() {
		final Node<E> f = first;
		return (f == null) ? null : unlinkFirst(f);
	}

	/**
	 * ɾ��β�ڵ�,�����ڷ���null
	 */
	public E pollLast() {
		final Node<E> l = last;
		return (l == null) ? null : unlinkLast(l);
	}

	/**
	 * ���Ԫ�ص�ͷ���
	 */
	public void push(E e) {
		addFirst(e);
	}

	/**
	 * ɾ��ͷ���,���������쳣
	 */
	public E pop() {
		return removeFirst();
	}

	/**
	 * ɾ����һ�����ֵ�Ԫ��
	 */
	public boolean removeFirstOccurrence(Object o) {
		return remove(o);
	}

	/**
	 *
	 * ɾ�����һ�����ֵ�Ԫ��
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
	 * �������������
	 */
	public ListIterator<E> listIterator(int index) {
		checkPositionIndex(index);
		return new ListItr(index);
	}

	/**
	 * �����������֧�ֵ���ǰ��Ԫ���Լ���ɾ��(��ArrayList����)
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
	 * ������ݵĽڵ�
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
	 * ��ǰ�����ĵ�������ʹ�õľ���ListItr�ĵ�����ʽ��
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
	 * ǳ����
	 *
	 */
	public Object clone() {
		// �����������(Ӧ�ú�new LinkedListû����)
		LinkedList<E> clone = superClone();

		// Put clone into "virgin" state
		clone.first = clone.last = null;
		clone.size = 0;
		clone.modCount = 0;

		// �����������ʹ���µ����������������ﲢ����������������������еĶ�Ӧ��item����ָ����ͬ�Ķ���
		for (Node<E> x = first; x != null; x = x.next)
			clone.add(x.item);

		return clone;
	}

	/**
	 * ������ת��Ϊobject����
	 *
	 */
	public Object[] toArray() {
		// ����һ�������С������
		Object[] result = new Object[size];
		int i = 0;
		// ��������
		for (Node<E> x = first; x != null; x = x.next)
			result[i++] = x.item;
		return result;
	}

	/**
	 * ����һ��ָ�����͵�����
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		if (a.length < size)
			// ����һ��T���͵Ĵ�СΪsize������
			a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
		int i = 0;
		// ��һ��Ӧ���Ƿ�ֹʹ��a[i++] = x.item�״�,����ʹ��result[i++] = x.item
		Object[] result = a;
		for (Node<E> x = first; x != null; x = x.next)
			result[i++] = x.item;
		if (a.length > size)
			// ����������С�������(���Ǻ����,һ�㴫���a��С����0,����������)
			a[size] = null;

		return a;
	}

	/**
	 * ���л��汾��
	 */
	private static final long serialVersionUID = 876323262645176354L;

	/**
	 * ���л�
	 * 
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		// Write out any hidden serialization magic
		// ���л��Ǿ�̬�ͷ�traisent�ֶ�
		s.defaultWriteObject();

		// Write out size
		// ���л���С
		s.writeInt(size);

		// Write out all elements in the proper order.
		// ���л������е�ÿһ��Ԫ��
		for (Node<E> x = first; x != null; x = x.next)
			s.writeObject(x.item);
	}

	/**
	 * �����л�
	 */
	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		// Read in any hidden serialization magic
		// �����л�defaultWriteObjectд�������
		s.defaultReadObject();

		// Read in size
		// �����л���С
		int size = s.readInt();

		// Read in all elements in the proper order.
		// �����л�ÿ��Ԫ��
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
