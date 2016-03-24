package cpsc457;

public class Node<T> {
	T value;
	Node<T> next;
	Node<T> prev;
        
        public Node()
        {
            value = null;
            next = null;
            prev = null;
        }
        
        public Node(T v)
        {
            value = v;
        }
    }
 
