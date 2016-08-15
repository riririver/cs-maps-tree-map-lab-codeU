/**
 * 
 */
package com.flatironschool.javacs;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of a Map using a binary search tree.
 * 
 * @param <K>
 * @param <V>
 *
 */
public class MyTreeMap<K, V> implements Map<K, V> {

	private int size = 0;
	private Node root = null;

	/**
	 * Represents a node in the tree.
	 *
	 */
	protected class Node {
		public K key;
		public V value;
		public Node left = null;
		public Node right = null;

		/**
		 * @param key
		 * @param value
		 * @param left
		 * @param right
		 */
		public Node(K key, V value) {
			this.key = key;
			this.value = value;
		}
	}

	@Override
	public void clear() {
		size = 0;
		root = null;
	}

	@Override
	public boolean containsKey(Object target) {
		return findNode(target) != null;
	}

	/**
	 * Returns the entry that contains the target key, or null if there is none.
	 * 
	 * @param target
	 */
	private Node findNode(Object target) {
		// some implementations can handle null as a key, but not this one
		if (target == null) {
			throw new NullPointerException();
		}

		// something to make the compiler happy
		@SuppressWarnings("unchecked")
		Comparable<? super K> k = (Comparable<? super K>) target;

		// the actual search
		// TODO: Fill this in.
		return findNode(root, target);

	}

	private Node findNode(Node parent, Object target) {
		if (target == null) {
			throw new NullPointerException();
		}

		Node foundNode;

		// something to make the compiler happy
		@SuppressWarnings("unchecked")
		Comparable<? super K> k = (Comparable<? super K>) target;

		int cmp = k.compareTo(parent.key);
		if (cmp == 0)
			return parent;
		else if (cmp < 0 && parent.left != null)
			foundNode = findNode(parent.left, target);
		else if (cmp > 0 && parent.right != null)
			foundNode = findNode(parent.right, target);
		else
			foundNode = null;

		return foundNode;

	}

	/**
	 * Compares two keys or two values, handling null correctly.
	 * 
	 * @param target
	 * @param obj
	 * @return
	 */
	private boolean equals(Object target, Object obj) {
		if (target == null) {
			return obj == null;
		}
		return target.equals(obj);
	}

	@Override
	public boolean containsValue(Object target) {
		Collection<V> values = this.values();
		if (values == null || values.size() == 0)
			return false;

		for (V v : values) {
			if (equals(target, v))
				return true;
		}

		return false;
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public V get(Object key) {
		Node node = findNode(key);
		if (node == null) {
			return null;
		}
		return node.value;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Set<K> keySet() {
		Set<K> set = new LinkedHashSet<K>();

		addKeys(root, set);

		return set;

	}

	private void addKeys(Node node, Set<K> sortedKeys) {
		if (node == null)
			return;

		// add left
		if (node.left != null)
			addKeys(node.left, sortedKeys);

		// add self
		sortedKeys.add(node.key);

		// add right
		if (node.right != null)
			addKeys(node.right, sortedKeys);

	}

	@Override
	public V put(K key, V value) {
		if (key == null) {
			throw new NullPointerException();
		}
		if (root == null) {
			root = new Node(key, value);
			size++;
			return null;
		}
		return putHelper(root, key, value);
	}

	private V putHelper(Node node, K key, V value) {

		V oldValue = null;
		Deque<Node> stack = new LinkedList<Node>();
		stack.push(node);

		Node newNode = new Node(key, value);

		@SuppressWarnings("unchecked")
		Comparable<? super K> k = (Comparable<? super K>) key;

		while (!stack.isEmpty()) {
			Node nextNode = stack.pop();

			int cmp = k.compareTo(nextNode.key);
			if (cmp == 0) {
				oldValue = nextNode.value;
				nextNode.value = value;
				break;
			} else if (cmp < 0) {
				if (nextNode.left == null) {
					nextNode.left = newNode;
					size++;
					break;
				} else
					stack.push(nextNode.left);
			} else {
				if (nextNode.right == null) {
					nextNode.right = newNode;
					size++;
					break;
				}
				stack.push(nextNode.right);
			}

		}
		return oldValue;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public V remove(Object key) {
		if (root == null)
			return null;
		V val = remove (key, root, root, null);
		return val;
	}

	@SuppressWarnings("unchecked")
	private V remove(Object target, Node current, Node parent, V val) {

		//V value = null;
		Comparable<? super K> k = (Comparable<? super K>) target;
		int cmp = k.compareTo(current.key);
		if (cmp < 0) {
			if (parent.left != null)
				val = remove(target, current.left, current, val);
			else
				return null;
		} else if (cmp > 0) {
			if (current.right != null)
				val =remove(target, current.right, current, val);
			else
				return null;
		} else {
			if (current.right != null && current.left != null) {
                   Node minValNode = minValue (current.right);
                   val = current.value;
                   current.value = minValNode.value;
                   current.key = minValNode.key;
                 
                   val = remove (minValNode.key, current.right, current, val);
			} else if (parent.left == current) {
				parent.left = current.left != null ? current.left : current.right;
			} else if (parent.right == current) {
				parent.right = current.left != null ? current.left : current.right;
			}

		}
		
		return val;
	}
	
	public Node minValue(Node node) {
        if (node.left == null)
              return node;
        else
              return minValue(node.left);
    }

	@Override
	public int size() {
		return size;
	}

	@Override
	public Collection<V> values() {
		Set<V> set = new HashSet<V>();
		Deque<Node> stack = new LinkedList<Node>();
		stack.push(root);
		while (!stack.isEmpty()) {
			Node node = stack.pop();
			if (node == null)
				continue;
			set.add(node.value);
			stack.push(node.left);
			stack.push(node.right);
		}
		return set;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map<String, Integer> map = new MyTreeMap<String, Integer>();
		map.put("Word1", 1);
		map.put("Word2", 2);
		Integer value = map.get("Word1");
		System.out.println(value);

		for (String key : map.keySet()) {
			System.out.println(key + ", " + map.get(key));
		}
	}

	/**
	 * Makes a node.
	 * 
	 * This is only here for testing purposes. Should not be used otherwise.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public MyTreeMap<K, V>.Node makeNode(K key, V value) {
		return new Node(key, value);
	}

	/**
	 * Sets the instance variables.
	 * 
	 * This is only here for testing purposes. Should not be used otherwise.
	 * 
	 * @param node
	 * @param size
	 */
	public void setTree(Node node, int size) {
		this.root = node;
		this.size = size;
	}

	/**
	 * Returns the height of the tree.
	 * 
	 * This is only here for testing purposes. Should not be used otherwise.
	 * 
	 * @return
	 */
	public int height() {
		return heightHelper(root);
	}

	private int heightHelper(Node node) {
		if (node == null) {
			return 0;
		}
		int left = heightHelper(node.left);
		int right = heightHelper(node.right);
		return Math.max(left, right) + 1;
	}
}
