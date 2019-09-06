package com.demo.jdk_18;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Test {
	public static void main(String[] args) {
		Map<Integer, Integer> map = new HashMap<>(2);
		map.put(1, 1);
		map.put(2, 1);
		map.put(3, 1);
		map.put(4, 1);

		LinkedHashMap< Integer, Integer> linkedHashMap = new LinkedHashMap<>(map);
		System.out.println(linkedHashMap);
		
	}
}
