package com.demo.jdk_18;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Test {
	public static void main(String[] args) {
		Map<Integer, Integer> map = new HashMap<>(2);
		map.put(1, 1);
		map.put(2, 1);
		map.put(3, 1);
		map.put(4, 1);

		TreeMap<Integer, Integer> treeMap = new TreeMap<>(new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				if (o1 == null || o2 == null) {
					return 1;
				}
				return 0;
			}
		});
		treeMap.put(null, 1);
		System.out.println(treeMap);

	}
}
