package com.arraybase.shell;

import java.util.Collection;
import java.util.Set;

public interface MultiMap<K, V> {

	void put(K key, V value);

	void putAll(MultiMap<K, V> map);

	Collection<V> get(K key);

	Set<K> keySet();

	void remove(K key, V value);

	void removeAll(K key);

	/**
	 * @return total size of all value collections in the MultiMap.
	 */
	int size();

}