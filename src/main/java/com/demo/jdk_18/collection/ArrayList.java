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
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
	private static final long serialVersionUID = 8683452581122892189L;

	/**
	 * 默认的初始化大小(初始化时不传集合容量使用)
	 */
	private static final int DEFAULT_CAPACITY = 10;

	/**
	 * 表示空的数组
	 */
	private static final Object[] EMPTY_ELEMENTDATA = {};

	/**
	 * 空数组,和DEFAULT_CAPACITY一起使用
	 */
	private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

	/**
	 * 保存集合元素的数组
	 * (不使用默认的序列化方式,因为可能elementData后面有很多还没使用到的数据,会导致被序列化,使用writeObject进行elementData的序列化)
	 */
	transient Object[] elementData; // non-private to simplify nested class
									// access

	/**
	 * 集合中保存的元素的大小
	 *
	 * @serial
	 */
	private int size;

	/**
	 * 指定list的大小
	 *
	 * @param initialCapacity
	 *            list的初始化大小
	 * @throws IllegalArgumentException
	 *             初始化的值小于0，抛出错误的参数异常
	 */
	public ArrayList(int initialCapacity) {
		if (initialCapacity > 0) {
			// 初始化值大于0,创建一个initialCapacity大小的Object数组
			this.elementData = new Object[initialCapacity];
		} else if (initialCapacity == 0) {
			// 初始化大小等于0，将数组赋值为空
			this.elementData = EMPTY_ELEMENTDATA;
		} else {
			// 抛出异常
			throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
		}
	}

	/**
	 * 不指定list大小,将数组赋值为默认的空数组
	 */
	public ArrayList() {
		// 和EMPTY_ELEMENTDATA有区别,在扩容时,如果elementData是DEFAULTCAPACITY_EMPTY_ELEMENTDATA，那么初始化大小为10，否则是1
		this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
	}

	/**
	 * 根据传入的集合构造一个新的数组
	 *
	 * @param c
	 *            传入的集合
	 * @throws NullPointerException
	 *             集合是null,抛出空指针异常
	 */
	public ArrayList(Collection<? extends E> c) {
		// 创建一个新的数组
		elementData = c.toArray();
		if ((size = elementData.length) != 0) {
			// c.toArray might (incorrectly) not return Object[] (see 6260652)
			// elementData数组必须是Object[]类型
			// 如果返回的是Integer[],那么,elementData将不能再添加其他类型的元素了
			// 然而当创建ArrayList时未指定泛型，添加String元素就会有问题
			if (elementData.getClass() != Object[].class)
				// 赋值为Object[]
				elementData = Arrays.copyOf(elementData, size, Object[].class);
		} else {
			// 参数为空,创建空的数组F
			this.elementData = EMPTY_ELEMENTDATA;
		}
	}

	/**
	 * 将数组的长度裁剪为集合大小
	 */
	public void trimToSize() {
		modCount++;
		if (size < elementData.length) {
			elementData = (size == 0) ? EMPTY_ELEMENTDATA : Arrays.copyOf(elementData, size);
		}
	}

	/**
	 * Increases the capacity of this <tt>ArrayList</tt> instance, if necessary,
	 * to ensure that it can hold at least the number of elements specified by
	 * the minimum capacity argument.
	 *
	 * @param minCapacity
	 *            the desired minimum capacity
	 */
	public void ensureCapacity(int minCapacity) {
		int minExpand = (elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA)
				// any size if not default element table
				? 0
				// larger than default for default empty table. It's already
				// supposed to be at default size.
				: DEFAULT_CAPACITY;

		if (minCapacity > minExpand) {
			ensureExplicitCapacity(minCapacity);
		}
	}

	/**
	 * 容量不够进行扩容
	 * 
	 * @param minCapacity
	 */
	private void ensureCapacityInternal(int minCapacity) {
		if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
			// 初始化未指定容量,容量的默认值就是DEFAULT_CAPACITY(10)
			minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
		}

		ensureExplicitCapacity(minCapacity);
	}

	/**
	 * 扩容
	 * 
	 * @param minCapacity
	 */
	private void ensureExplicitCapacity(int minCapacity) {
		// 增删查的计数器+1
		modCount++;

		// overflow-conscious code
		if (minCapacity - elementData.length > 0)
			// 需要的容量比数组大,进行扩容
			grow(minCapacity);
	}

	/**
	 * 集合最大容量 OutOfMemoryError: Requested array size exceeds VM limit
	 */
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	/**
	 * 扩容
	 * 
	 * @param minCapacity
	 *            the desired minimum capacity
	 */
	private void grow(int minCapacity) {
		// overflow-conscious code
		// 旧的容量
		int oldCapacity = elementData.length;
		// 新的容量为为旧的1.5倍
		int newCapacity = oldCapacity + (oldCapacity >> 1);
		if (newCapacity - minCapacity < 0)
			// 新的容量比需要的小,新的容量赋值为需要的容量
			newCapacity = minCapacity;
		if (newCapacity - MAX_ARRAY_SIZE > 0)
			// 新的容量超过最大值
			// 根据需要的容量计算扩容大小
			newCapacity = hugeCapacity(minCapacity);
		// minCapacity is usually close to size, so this is a win:
		// 拷贝旧的数据给数组
		elementData = Arrays.copyOf(elementData, newCapacity);
	}

	private static int hugeCapacity(int minCapacity) {
		if (minCapacity < 0) // overflow
			throw new OutOfMemoryError();
		return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
	}

	/**
	 * 返回集合元素数量
	 *
	 */
	public int size() {
		return size;
	}

	/**
	 *
	 * @return true=集合没有数据,false=有数据
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * 判断集合是否包含元素
	 * 
	 * @param o
	 *            是否包含的元素
	 * @return true=包含,false=不包含
	 */
	public boolean contains(Object o) {
		return indexOf(o) >= 0;
	}

	/**
	 * 查询元素o所在集合中的第一个位置,-1=集合中不存在该元素
	 */
	public int indexOf(Object o) {
		if (o == null) {
			for (int i = 0; i < size; i++)
				if (elementData[i] == null)
					return i;
		} else {
			for (int i = 0; i < size; i++)
				if (o.equals(elementData[i]))
					return i;
		}
		return -1;
	}

	/**
	 * 返回集合中最后一个包含该元素的索引,-1=集合中不包含该元素
	 */
	public int lastIndexOf(Object o) {
		if (o == null) {
			// 从尾部向前遍历
			for (int i = size - 1; i >= 0; i--)
				if (elementData[i] == null)
					return i;
		} else {
			for (int i = size - 1; i >= 0; i--)
				if (o.equals(elementData[i]))
					return i;
		}
		return -1;
	}

	/**
	 * 拷贝数据(浅拷贝)
	 * 
	 * @return a clone of this <tt>ArrayList</tt> instance
	 */
	public Object clone() {
		try {
			ArrayList<?> v = (ArrayList<?>) super.clone();
			v.elementData = Arrays.copyOf(elementData, size);
			v.modCount = 0;
			return v;
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError(e);
		}
	}

	/**
	 * 将集合转化成数组(返回一个集合元素的引用的拷贝)
	 */
	public Object[] toArray() {
		return Arrays.copyOf(elementData, size);
	}

	/**
	 * 返回一个指定类型的数组
	 * 
	 * @param a
	 *            指定的类型
	 * @return 指定类型的数组
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		if (a.length < size)
			// Make a new array of a's runtime type, but my contents:
			return (T[]) Arrays.copyOf(elementData, size, a.getClass());
		System.arraycopy(elementData, 0, a, 0, size);
		if (a.length > size)
			a[size] = null;
		return a;
	}

	// Positional Access Operations

	@SuppressWarnings("unchecked")
	E elementData(int index) {
		return (E) elementData[index];
	}

	/**
	 * 根据位置查询元素
	 * 
	 * @param index
	 *            index of the element to return
	 * @return the element at the specified position in this list
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public E get(int index) {
		rangeCheck(index);

		return elementData(index);
	}

	/**
	 * 替换指定位置的元素
	 * 
	 * @param index
	 *            需要被替换的位置
	 * @param element
	 *            替换的新元素
	 * @return 老元素
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public E set(int index, E element) {
		rangeCheck(index);

		E oldValue = elementData(index);
		elementData[index] = element;
		return oldValue;
	}

	/**
	 * 添加一个元素到尾部
	 *
	 * @param e
	 *            需要添加的元素
	 * @return true=添加成功,false=添加失败
	 */
	public boolean add(E e) {
		ensureCapacityInternal(size + 1); // Increments modCount!!
		elementData[size++] = e;
		return true;
	}

	/**
	 * 在指定位置添加一个元素
	 * 
	 * @param index
	 *            添加元素的位置
	 * @param element
	 *            element to be inserted
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public void add(int index, E element) {
		// 检查指定的索引位置是否在数组之间
		rangeCheckForAdd(index);

		ensureCapacityInternal(size + 1); // Increments modCount!!
		// 将index以及index之后的元素向后移动一格
		System.arraycopy(elementData, index, elementData, index + 1, size - index);
		// index位置重新赋值为新的元素
		elementData[index] = element;
		// 数组大小加一
		size++;
	}

	/**
	 * 删除指定位置的元素
	 * 
	 * @param index
	 *            被删除的位置
	 * @return 被删除的元素
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public E remove(int index) {
		rangeCheck(index);
		// 计数器加一
		modCount++;
		// 记录index位置的元素
		E oldValue = elementData(index);

		int numMoved = size - index - 1;
		if (numMoved > 0)
			// 将index后面的元素向前移动一格
			System.arraycopy(elementData, index + 1, elementData, index, numMoved);
		// 最后一个元素置为null,为了更好的gc
		elementData[--size] = null; // clear to let GC do its work

		return oldValue;
	}

	/**
	 * 删除所有和o相等的元素
	 * 
	 * @param o
	 *            需要删除的元素
	 * @return true=删除成功,false=删除失败
	 */
	public boolean remove(Object o) {
		// 区分null和非null是防止null对象的比较(虽然1.8Objects.equals可以解决,但是需要兼容1.8之前的版本)
		if (o == null) {
			// 需要删除的对象是null
			for (int index = 0; index < size; index++)
				if (elementData[index] == null) {
					fastRemove(index);
					return true;
				}
		} else {
			// 需要删除的对象不是是null
			for (int index = 0; index < size; index++)
				if (o.equals(elementData[index])) {
					fastRemove(index);
					return true;
				}
		}
		return false;
	}

	/*
	 * 删除index位置的元素
	 */
	private void fastRemove(int index) {
		// 计数器加一
		modCount++;
		int numMoved = size - index - 1;
		if (numMoved > 0)
			// 将index位置之后的元素往前移动一格
			System.arraycopy(elementData, index + 1, elementData, index, numMoved);
		elementData[--size] = null; // clear to let GC do its work
	}

	/**
	 * 清空集合的元素(只是将数组的所有数据置为null,并不是删除数组,所以之前的数组大小不变，还在占用内存)
	 */
	public void clear() {
		modCount++;

		// clear to let GC do its work
		for (int i = 0; i < size; i++)
			elementData[i] = null;

		size = 0;
	}

	/**
	 * 追加集合元素到尾部
	 * 
	 * @param c
	 *            需要追加的集合
	 * @return true=添加成功,false=添加元素>0
	 * @throws NullPointerException
	 *             集合是null,抛出空指针异常
	 */
	public boolean addAll(Collection<? extends E> c) {
		// 将参数转化成数组
		Object[] a = c.toArray();
		int numNew = a.length;
		ensureCapacityInternal(size + numNew); // Increments modCount
		// 拷贝新的数组到elementData尾部
		System.arraycopy(a, 0, elementData, size, numNew);
		// 集合大小改变
		size += numNew;
		return numNew != 0;
	}

	/**
	 * 在index位置(包含)插入集合
	 *
	 * @param index
	 *            插入数据的起始位置
	 * @param c
	 *            需要插入的元素集合
	 * @return true=添加成功,false=添加元素<=0
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if the specified collection is null
	 */
	public boolean addAll(int index, Collection<? extends E> c) {
		rangeCheckForAdd(index);

		Object[] a = c.toArray();
		int numNew = a.length;
		ensureCapacityInternal(size + numNew); // Increments modCount

		int numMoved = size - index;
		if (numMoved > 0)
			// 将index以及后面的向后移动数组a的长度
			System.arraycopy(elementData, index, elementData, index + numNew, numMoved);
		// 从index位置拷贝数组a
		System.arraycopy(a, 0, elementData, index, numNew);
		// 改变集合大小
		size += numNew;
		return numNew != 0;
	}

	/**
	 * 删除fromIndex和toIndex之间的元素
	 */
	protected void removeRange(int fromIndex, int toIndex) {
		modCount++;
		int numMoved = size - toIndex;
		System.arraycopy(elementData, toIndex, elementData, fromIndex, numMoved);

		// clear to let GC do its work
		int newSize = size - (toIndex - fromIndex);
		for (int i = newSize; i < size; i++) {
			elementData[i] = null;
		}
		size = newSize;
	}

	/**
	 * 检查指定位置是否超过(包含)list大小
	 */
	private void rangeCheck(int index) {
		if (index >= size)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	/**
	 * A version of rangeCheck used by add and addAll.
	 */
	private void rangeCheckForAdd(int index) {
		if (index > size || index < 0)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	/**
	 * Constructs an IndexOutOfBoundsException detail message. Of the many
	 * possible refactorings of the error handling code, this "outlining"
	 * performs best with both server and client VMs.
	 */
	private String outOfBoundsMsg(int index) {
		return "Index: " + index + ", Size: " + size;
	}

	/**
	 * 删除指定的集合中的元素
	 * 
	 * @param c
	 *            指定的集合
	 * @return {@code true} if this list changed as a result of the call
	 */
	public boolean removeAll(Collection<?> c) {
		Objects.requireNonNull(c);
		return batchRemove(c, false);
	}

	/**
	 * 保留指定集合中的元素
	 *
	 * @param c
	 *            指定的集合
	 * @return {@code true} if this list changed as a result of the call
	 * @see Collection#contains(Object)
	 */
	public boolean retainAll(Collection<?> c) {
		Objects.requireNonNull(c);
		return batchRemove(c, true);
	}

	/**
	 * 批量删除元素
	 * 
	 * @param c
	 *            需要删除的元素集合
	 * @param complement
	 *            false=留下需要集合c不包含的元素,true=相反
	 * @return
	 */
	private boolean batchRemove(Collection<?> c, boolean complement) {
		final Object[] elementData = this.elementData;
		int r = 0, w = 0;
		boolean modified = false;
		try {
			// 遍历集合
			for (; r < size; r++)
				// 参数集合是否包含和complement比较
				if (c.contains(elementData[r]) == complement)
					// 将需要留下的元素在原始数组从0开始复制
					elementData[w++] = elementData[r];
		} finally {
			// Preserve behavioral compatibility with AbstractCollection,
			// even if c.contains() throws.
			if (r != size) {
				// 表示上面出现异常
				System.arraycopy(elementData, r, elementData, w, size - r);
				w += size - r;
			}
			if (w != size) {
				// 表示有删除的元素
				// clear to let GC do its work
				for (int i = w; i < size; i++)
					elementData[i] = null;
				// 计算器加上删除的元素个数
				modCount += size - w;
				// 集合大小变为删除后的元素大小
				size = w;
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * 序列化方法
	 *
	 * @serialData The length of the array backing the <tt>ArrayList</tt>
	 *             instance is emitted (int), followed by all of its elements
	 *             (each an <tt>Object</tt>) in the proper order.
	 */
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		// Write out element count, and any hidden stuff
		int expectedModCount = modCount;
		// 使用默认的序列化方法序列化非静态和未被traisent修饰的变量
		s.defaultWriteObject();
		// 写入集合大小
		s.writeInt(size);

		// 序列化表示集合的数组
		for (int i = 0; i < size; i++) {
			s.writeObject(elementData[i]);
		}

		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}
	}

	/**
	 * 反序列化
	 */
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		elementData = EMPTY_ELEMENTDATA;
		// 读取写入的字段
		s.defaultReadObject();
		// 读取集合大小
		s.readInt(); // ignored

		if (size > 0) {
			// 创建序列化数组同等大小的数组
			ensureCapacityInternal(size);

			Object[] a = elementData;
			// 读取序列化的数组
			for (int i = 0; i < size; i++) {
				a[i] = s.readObject();
			}
		}
	}

	/**
	 * Returns a list iterator over the elements in this list (in proper
	 * sequence), starting at the specified position in the list. The specified
	 * index indicates the first element that would be returned by an initial
	 * call to {@link ListIterator#next next}. An initial call to
	 * {@link ListIterator#previous previous} would return the element with the
	 * specified index minus one.
	 *
	 * <p>
	 * The returned list iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
	 *
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public ListIterator<E> listIterator(int index) {
		if (index < 0 || index > size)
			throw new IndexOutOfBoundsException("Index: " + index);
		return new ListItr(index);
	}

	/**
	 * Returns a list iterator over the elements in this list (in proper
	 * sequence).
	 *
	 * <p>
	 * The returned list iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
	 *
	 * @see #listIterator(int)
	 */
	public ListIterator<E> listIterator() {
		return new ListItr(0);
	}

	/**
	 * Returns an iterator over the elements in this list in proper sequence.
	 *
	 * <p>
	 * The returned iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
	 *
	 * @return an iterator over the elements in this list in proper sequence
	 */
	public Iterator<E> iterator() {
		return new Itr();
	}

	/**
	 * 集合的迭代器实现(只能向后迭代)
	 */
	private class Itr implements Iterator<E> {
		// 记录迭代的下一个元素
		int cursor; // index of next element to return
		// 记录上一次被迭代的元素
		int lastRet = -1; // index of last element returned; -1 if no such
		// 操作计数器,迭代过程中集合不能被修改
		int expectedModCount = modCount;

		/**
		 * 是否包含下一个元素
		 * 
		 * @return
		 */
		public boolean hasNext() {
			return cursor != size;
		}

		/**
		 * 查询当前迭代的元素
		 * 
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public E next() {
			checkForComodification();
			int i = cursor;
			if (i >= size)
				throw new NoSuchElementException();
			Object[] elementData = ArrayList.this.elementData;
			if (i >= elementData.length)
				throw new ConcurrentModificationException();
			// 下一个需要迭代的位置
			cursor = i + 1;
			// 返回当前元素
			return (E) elementData[lastRet = i];
		}

		/**
		 * 删除当前迭代的元素
		 */
		public void remove() {
			// 表示还未开始迭代或者是调用删除后为调用next,导致lastRet=-1
			if (lastRet < 0)
				throw new IllegalStateException();
			checkForComodification();

			try {
				// 删除之前迭代的元素(也就是最后调用next的那个元素)
				ArrayList.this.remove(lastRet);
				// 将下一次迭代的位置置为当前迭代的位置(因为删除当前迭代的元素后，下一个元素是向前移动一格，也就是占用了删除的元素的位置)
				cursor = lastRet;
				lastRet = -1;
				// 更新计数器
				expectedModCount = modCount;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}

		/**
		 * 遍历迭代剩下的元素,对每个元素进行consumer处理
		 * 
		 * @param consumer
		 */
		@Override
		@SuppressWarnings("unchecked")
		public void forEachRemaining(Consumer<? super E> consumer) {
			Objects.requireNonNull(consumer);
			final int size = ArrayList.this.size;
			int i = cursor;
			if (i >= size) {
				return;
			}
			final Object[] elementData = ArrayList.this.elementData;
			if (i >= elementData.length) {
				throw new ConcurrentModificationException();
			}
			while (i != size && modCount == expectedModCount) {
				// 消费数据
				consumer.accept((E) elementData[i++]);
			}
			// update once at end of iteration to reduce heap write traffic
			cursor = i;
			lastRet = i - 1;
			checkForComodification();
		}

		/**
		 * true=集合被修改,false=否
		 */
		final void checkForComodification() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
		}
	}

	/**
	 * 迭代器，可以向前迭代,以及迭代过程中做一些添加，删除，更新的操作
	 */
	private class ListItr extends Itr implements ListIterator<E> {
		ListItr(int index) {
			super();
			cursor = index;
		}

		public boolean hasPrevious() {
			return cursor != 0;
		}

		public int nextIndex() {
			return cursor;
		}

		public int previousIndex() {
			return cursor - 1;
		}

		/**
		 * 返回前一个元素
		 * 
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public E previous() {
			checkForComodification();
			int i = cursor - 1;
			if (i < 0)
				throw new NoSuchElementException();
			Object[] elementData = ArrayList.this.elementData;
			if (i >= elementData.length)
				throw new ConcurrentModificationException();
			cursor = i;
			return (E) elementData[lastRet = i];
		}

		/**
		 * 修改当前迭代的元素
		 * 
		 * @param e
		 */
		public void set(E e) {
			if (lastRet < 0)
				throw new IllegalStateException();
			checkForComodification();

			try {
				ArrayList.this.set(lastRet, e);
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}

		/**
		 * 当前迭代位置添加一个元素
		 * 
		 * @param e
		 */
		public void add(E e) {
			checkForComodification();

			try {
				int i = cursor;
				ArrayList.this.add(i, e);
				cursor = i + 1;
				lastRet = -1;
				expectedModCount = modCount;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}
	}

	/**
	 * Returns a view of the portion of this list between the specified
	 * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive. (If
	 * {@code fromIndex} and {@code toIndex} are equal, the returned list is
	 * empty.) The returned list is backed by this list, so non-structural
	 * changes in the returned list are reflected in this list, and vice-versa.
	 * The returned list supports all of the optional list operations.
	 *
	 * <p>
	 * This method eliminates the need for explicit range operations (of the
	 * sort that commonly exist for arrays). Any operation that expects a list
	 * can be used as a range operation by passing a subList view instead of a
	 * whole list. For example, the following idiom removes a range of elements
	 * from a list:
	 * 
	 * <pre>
	 * list.subList(from, to).clear();
	 * </pre>
	 * 
	 * Similar idioms may be constructed for {@link #indexOf(Object)} and
	 * {@link #lastIndexOf(Object)}, and all of the algorithms in the
	 * {@link Collections} class can be applied to a subList.
	 *
	 * <p>
	 * The semantics of the list returned by this method become undefined if the
	 * backing list (i.e., this list) is <i>structurally modified</i> in any way
	 * other than via the returned list. (Structural modifications are those
	 * that change the size of this list, or otherwise perturb it in such a
	 * fashion that iterations in progress may yield incorrect results.)
	 *
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 * @throws IllegalArgumentException
	 *             {@inheritDoc}
	 */
	public List<E> subList(int fromIndex, int toIndex) {
		subListRangeCheck(fromIndex, toIndex, size);
		return new SubList(this, 0, fromIndex, toIndex);
	}

	static void subListRangeCheck(int fromIndex, int toIndex, int size) {
		if (fromIndex < 0)
			throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
		if (toIndex > size)
			throw new IndexOutOfBoundsException("toIndex = " + toIndex);
		if (fromIndex > toIndex)
			throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
	}

	/**
	 * 
	 * 集合的视图，所有对SubList的操作都是对原始集合的操作
	 *
	 */
	private class SubList extends AbstractList<E> implements RandomAccess {
		private final AbstractList<E> parent;// 表示真实的集合
		private final int parentOffset;// 表示视图是从原始集合那个位置开始
		private final int offset;// 视图从哪个位置开始处理(没看出用途)
		int size;// 从offset开始取数据的数量

		SubList(AbstractList<E> parent, int offset, int fromIndex, int toIndex) {
			this.parent = parent;
			this.parentOffset = fromIndex;
			this.offset = offset + fromIndex;
			this.size = toIndex - fromIndex;
			this.modCount = ArrayList.this.modCount;
		}

		/**
		 * 修改制定位置的元素
		 * 
		 * @param index
		 *            表示视图的位置
		 * @param e
		 * @return
		 */
		public E set(int index, E e) {
			rangeCheck(index);
			checkForComodification();
			// offset + index表示修改的视图元素对应的原始集合的位置
			E oldValue = ArrayList.this.elementData(offset + index);
			ArrayList.this.elementData[offset + index] = e;
			return oldValue;
		}

		/**
		 * 查询视图指定位置的元素
		 * 
		 * @param index
		 * @return
		 */
		public E get(int index) {
			rangeCheck(index);
			checkForComodification();
			return ArrayList.this.elementData(offset + index);
		}

		/**
		 * 返回视图的大小
		 * 
		 * @return
		 */
		public int size() {
			checkForComodification();
			return this.size;
		}

		/**
		 * 在视图的指定位置添加元素
		 * 
		 * @param index
		 * @param e
		 */
		public void add(int index, E e) {
			rangeCheckForAdd(index);
			checkForComodification();
			parent.add(parentOffset + index, e);
			this.modCount = parent.modCount;
			this.size++;
		}

		/**
		 * 在视图的指定位置删除元素
		 * 
		 * @param index
		 * @return
		 */
		public E remove(int index) {
			rangeCheck(index);
			checkForComodification();
			E result = parent.remove(parentOffset + index);
			this.modCount = parent.modCount;
			this.size--;
			return result;
		}

		protected void removeRange(int fromIndex, int toIndex) {
			checkForComodification();
			parent.removeRange(parentOffset + fromIndex, parentOffset + toIndex);
			this.modCount = parent.modCount;
			this.size -= toIndex - fromIndex;
		}

		public boolean addAll(Collection<? extends E> c) {
			return addAll(this.size, c);
		}

		public boolean addAll(int index, Collection<? extends E> c) {
			rangeCheckForAdd(index);
			int cSize = c.size();
			if (cSize == 0)
				return false;

			checkForComodification();
			parent.addAll(parentOffset + index, c);
			this.modCount = parent.modCount;
			this.size += cSize;
			return true;
		}

		public Iterator<E> iterator() {
			return listIterator();
		}

		public ListIterator<E> listIterator(final int index) {
			checkForComodification();
			rangeCheckForAdd(index);
			final int offset = this.offset;

			return new ListIterator<E>() {
				int cursor = index;
				int lastRet = -1;
				int expectedModCount = ArrayList.this.modCount;

				public boolean hasNext() {
					return cursor != SubList.this.size;
				}

				@SuppressWarnings("unchecked")
				public E next() {
					checkForComodification();
					int i = cursor;
					if (i >= SubList.this.size)
						throw new NoSuchElementException();
					Object[] elementData = ArrayList.this.elementData;
					if (offset + i >= elementData.length)
						throw new ConcurrentModificationException();
					cursor = i + 1;
					return (E) elementData[offset + (lastRet = i)];
				}

				public boolean hasPrevious() {
					return cursor != 0;
				}

				@SuppressWarnings("unchecked")
				public E previous() {
					checkForComodification();
					int i = cursor - 1;
					if (i < 0)
						throw new NoSuchElementException();
					Object[] elementData = ArrayList.this.elementData;
					if (offset + i >= elementData.length)
						throw new ConcurrentModificationException();
					cursor = i;
					return (E) elementData[offset + (lastRet = i)];
				}

				@SuppressWarnings("unchecked")
				public void forEachRemaining(Consumer<? super E> consumer) {
					Objects.requireNonNull(consumer);
					final int size = SubList.this.size;
					int i = cursor;
					if (i >= size) {
						return;
					}
					final Object[] elementData = ArrayList.this.elementData;
					if (offset + i >= elementData.length) {
						throw new ConcurrentModificationException();
					}
					while (i != size && modCount == expectedModCount) {
						consumer.accept((E) elementData[offset + (i++)]);
					}
					// update once at end of iteration to reduce heap write
					// traffic
					lastRet = cursor = i;
					checkForComodification();
				}

				public int nextIndex() {
					return cursor;
				}

				public int previousIndex() {
					return cursor - 1;
				}

				public void remove() {
					if (lastRet < 0)
						throw new IllegalStateException();
					checkForComodification();

					try {
						SubList.this.remove(lastRet);
						cursor = lastRet;
						lastRet = -1;
						expectedModCount = ArrayList.this.modCount;
					} catch (IndexOutOfBoundsException ex) {
						throw new ConcurrentModificationException();
					}
				}

				public void set(E e) {
					if (lastRet < 0)
						throw new IllegalStateException();
					checkForComodification();

					try {
						ArrayList.this.set(offset + lastRet, e);
					} catch (IndexOutOfBoundsException ex) {
						throw new ConcurrentModificationException();
					}
				}

				public void add(E e) {
					checkForComodification();

					try {
						int i = cursor;
						SubList.this.add(i, e);
						cursor = i + 1;
						lastRet = -1;
						expectedModCount = ArrayList.this.modCount;
					} catch (IndexOutOfBoundsException ex) {
						throw new ConcurrentModificationException();
					}
				}

				final void checkForComodification() {
					if (expectedModCount != ArrayList.this.modCount)
						throw new ConcurrentModificationException();
				}
			};
		}

		public List<E> subList(int fromIndex, int toIndex) {
			subListRangeCheck(fromIndex, toIndex, size);
			return new SubList(this, offset, fromIndex, toIndex);
		}

		private void rangeCheck(int index) {
			if (index < 0 || index >= this.size)
				throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
		}

		private void rangeCheckForAdd(int index) {
			if (index < 0 || index > this.size)
				throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
		}

		private String outOfBoundsMsg(int index) {
			return "Index: " + index + ", Size: " + this.size;
		}

		private void checkForComodification() {
			if (ArrayList.this.modCount != this.modCount)
				throw new ConcurrentModificationException();
		}

		public Spliterator<E> spliterator() {
			checkForComodification();
			return new ArrayListSpliterator<E>(ArrayList.this, offset, offset + this.size, this.modCount);
		}
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		Objects.requireNonNull(action);
		final int expectedModCount = modCount;
		@SuppressWarnings("unchecked")
		final E[] elementData = (E[]) this.elementData;
		final int size = this.size;
		for (int i = 0; modCount == expectedModCount && i < size; i++) {
			action.accept(elementData[i]);
		}
		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}
	}

	/**
	 * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
	 * and <em>fail-fast</em> {@link Spliterator} over the elements in this
	 * list.
	 *
	 * <p>
	 * The {@code Spliterator} reports {@link Spliterator#SIZED},
	 * {@link Spliterator#SUBSIZED}, and {@link Spliterator#ORDERED}. Overriding
	 * implementations should document the reporting of additional
	 * characteristic values.
	 *
	 * @return a {@code Spliterator} over the elements in this list
	 * @since 1.8
	 */
	@Override
	public Spliterator<E> spliterator() {
		return new ArrayListSpliterator<>(this, 0, -1, 0);
	}

	/** Index-based split-by-two, lazily initialized Spliterator */
	static final class ArrayListSpliterator<E> implements Spliterator<E> {

		/*
		 * If ArrayLists were immutable, or structurally immutable (no adds,
		 * removes, etc), we could implement their spliterators with
		 * Arrays.spliterator. Instead we detect as much interference during
		 * traversal as practical without sacrificing much performance. We rely
		 * primarily on modCounts. These are not guaranteed to detect
		 * concurrency violations, and are sometimes overly conservative about
		 * within-thread interference, but detect enough problems to be
		 * worthwhile in practice. To carry this out, we (1) lazily initialize
		 * fence and expectedModCount until the latest point that we need to
		 * commit to the state we are checking against; thus improving
		 * precision. (This doesn't apply to SubLists, that create spliterators
		 * with current non-lazy values). (2) We perform only a single
		 * ConcurrentModificationException check at the end of forEach (the most
		 * performance-sensitive method). When using forEach (as opposed to
		 * iterators), we can normally only detect interference after actions,
		 * not before. Further CME-triggering checks apply to all other possible
		 * violations of assumptions for example null or too-small elementData
		 * array given its size(), that could only have occurred due to
		 * interference. This allows the inner loop of forEach to run without
		 * any further checks, and simplifies lambda-resolution. While this does
		 * entail a number of checks, note that in the common case of
		 * list.stream().forEach(a), no checks or other computation occur
		 * anywhere other than inside forEach itself. The other less-often-used
		 * methods cannot take advantage of most of these streamlinings.
		 */

		private final ArrayList<E> list;
		private int index; // current index, modified on advance/split
		private int fence; // -1 until used; then one past last index
		private int expectedModCount; // initialized when fence set

		/** Create new spliterator covering the given range */
		ArrayListSpliterator(ArrayList<E> list, int origin, int fence, int expectedModCount) {
			this.list = list; // OK if null unless traversed
			this.index = origin;
			this.fence = fence;
			this.expectedModCount = expectedModCount;
		}

		private int getFence() { // initialize fence to size on first use
			int hi; // (a specialized variant appears in method forEach)
			ArrayList<E> lst;
			if ((hi = fence) < 0) {
				if ((lst = list) == null)
					hi = fence = 0;
				else {
					expectedModCount = lst.modCount;
					hi = fence = lst.size;
				}
			}
			return hi;
		}

		public ArrayListSpliterator<E> trySplit() {
			int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
			return (lo >= mid) ? null : // divide range in half unless too small
					new ArrayListSpliterator<E>(list, lo, index = mid, expectedModCount);
		}

		public boolean tryAdvance(Consumer<? super E> action) {
			if (action == null)
				throw new NullPointerException();
			int hi = getFence(), i = index;
			if (i < hi) {
				index = i + 1;
				@SuppressWarnings("unchecked")
				E e = (E) list.elementData[i];
				action.accept(e);
				if (list.modCount != expectedModCount)
					throw new ConcurrentModificationException();
				return true;
			}
			return false;
		}

		public void forEachRemaining(Consumer<? super E> action) {
			int i, hi, mc; // hoist accesses and checks from loop
			ArrayList<E> lst;
			Object[] a;
			if (action == null)
				throw new NullPointerException();
			if ((lst = list) != null && (a = lst.elementData) != null) {
				if ((hi = fence) < 0) {
					mc = lst.modCount;
					hi = lst.size;
				} else
					mc = expectedModCount;
				if ((i = index) >= 0 && (index = hi) <= a.length) {
					for (; i < hi; ++i) {
						@SuppressWarnings("unchecked")
						E e = (E) a[i];
						action.accept(e);
					}
					if (lst.modCount == mc)
						return;
				}
			}
			throw new ConcurrentModificationException();
		}

		public long estimateSize() {
			return (long) (getFence() - index);
		}

		public int characteristics() {
			return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
		}
	}

	/**
	 * 根据条件删除数据(找出需要删除的数据->将不删除的数据从索引0开始放置)
	 * 
	 * @param filter
	 * @return
	 */
	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		Objects.requireNonNull(filter);
		// figure out which elements are to be removed
		// any exception thrown from the filter predicate at this stage
		// will leave the collection unmodified
		int removeCount = 0;
		final BitSet removeSet = new BitSet(size);
		final int expectedModCount = modCount;
		final int size = this.size;
		// 找出需要删除的数据下标
		for (int i = 0; modCount == expectedModCount && i < size; i++) {
			@SuppressWarnings("unchecked")
			final E element = (E) elementData[i];
			if (filter.test(element)) {
				// 将下标用bit标识
				removeSet.set(i);
				removeCount++;
			}
		}
		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}

		// shift surviving elements left over the spaces left by removed
		// elements
		final boolean anyToRemove = removeCount > 0;
		if (anyToRemove) {
			final int newSize = size - removeCount;
			// 将留下的数据从0开始复制
			for (int i = 0, j = 0; (i < size) && (j < newSize); i++, j++) {
				i = removeSet.nextClearBit(i);
				elementData[j] = elementData[i];
			}
			// 刪除的数据置为null,使得能被gc
			for (int k = newSize; k < size; k++) {
				elementData[k] = null; // Let gc do its work
			}
			this.size = newSize;
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
			modCount++;
		}

		return anyToRemove;
	}

	/**
	 * 对所有元素做operator操作（也就是使用operator操作的结果替换原来的数据）
	 * 
	 * @param operator
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void replaceAll(UnaryOperator<E> operator) {
		Objects.requireNonNull(operator);
		final int expectedModCount = modCount;
		final int size = this.size;
		for (int i = 0; modCount == expectedModCount && i < size; i++) {
			// 替换数据
			elementData[i] = operator.apply((E) elementData[i]);
		}
		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}
		modCount++;
	}

	/**
	 * 排序
	 * 
	 * @param c
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void sort(Comparator<? super E> c) {
		final int expectedModCount = modCount;
		Arrays.sort((E[]) elementData, 0, size, c);
		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}
		modCount++;
	}
}
