package com.demo.jdk_18;

import java.util.HashMap;
import java.util.Map;

public class Test {
	public static void main(String[] args) {
		Map<Integer, Integer> map = new HashMap<>(2);
		map.put(1, 1);
		map.put(2, 1);
		map.put(3, 1);
		map.put(4, 1);

		System.out.println(map.keySet());
		map.computeIfAbsent(5, (key) -> {
			return key;
		});
		System.out.println(map.keySet());
		
		
	}
}
