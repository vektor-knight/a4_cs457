package cpsc457;

import cpsc457.doNOTmodify.Pair;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Node<T> {
	Node<T> current;
	Node<T> next;
	T data;

	Node(T data) {
		current = null;
		next = null;
		this.data = data;
	}
}
