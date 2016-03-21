package cpsc457;

import cpsc457.doNOTmodify.Pair;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LinkedList<T> implements Iterable<T> {
 
	//####################
	//# Static Functions #
	//####################
	
	//We do not want the testers to have the freedom of specifying the comparison function
	//Thus, we will create wrappers for them that they can use and inside these wrappers
	//we will have the comparison function predefined
		//These two static wrappers, will simply call the sort method in the list passed as parameter,
		//and they pass the comparison function as well
	
	public static <T extends Comparable<T>> void par_sort(LinkedList<T> list) {
		list.par_sort(new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        });
    }

    public static <T extends Comparable<T>> void sort(LinkedList<T> list){
        list.sort(new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        });
    }
 
	//############
	//# LinkList #
	//############
	
	//Variables (attributes)
		//Head
	Node<T> head;
		//Tail
	Node<T> tail;
		//Size (not required)
	int size;
		//Critical Section
	Lock critical; 

	//Constructor
    public LinkedList() {
		//Set head and tail to null
	head = null;
	tail = null;
		//Set size to zero
	size = 0;
		//Create new instance for the critical section
	critical = new ReentrantLock();
    }

	//Returns the size of the list
    public int size() {
        return size; //either iterate through all the list and count
					//or create an attribute that stores the size and changes
					//every time we add or remove a node
    }
	
	//Checks if the list is empty
	public boolean isEmpty() {
		critical.lock();
		try {
			return (head == null || size == 0); // list is empty or there is no head (equal definitions)
		} finally {
			critical.unlock();
		}
        // Cano temove this. Captured by the disjunction above. return true; //size == 0
    }
	
	//Deletes all the nodes in the list
	public void clear() {
		critical.lock(); //just set the head and tail to null (the garbage collector takes care of the rest)
		try {
			head = null;
			tail = null;
			size = 0;
		} finally {	//cpp developers: be careful, you have to destroy them first
			critical.unlock();
		}
		//What if the merge sort is running now in a thread
			//I should not be able to delete the nodes (and vice versa)
			//Thus run this and everything else in a critical section
    	}
	
	//Adds a new node to the list at the end (tail)
    public LinkedList<T> append(T t) {
		critical.lock();
		Node<T> node = new Node<>(t);
		if (size == 0) {
			head = node;
			tail = node;
		}
		size++;
		

        return this;
		//Check if it is empty 
			//head = tail = t
		//Else add to the tail and move the tail to the end
			//tail.next = t    then		tail = t
		
		//Do not forget to increment the size by 1 (if you have it as an attribute)
    }

	//Gets a node's value at a specific index
    public T get(int index) {
		Node<T> current = head;
		if (index >= size || index < 0) {
			return null;
		}

		for (int i = 0; i < index; i++) {
			current = current.next;
		}
		return current.data;


//		return null;
		//Iterate through the list
			//Create a new pointer that starts at the head
			//Keeps moving forward (pt = pt.next) for index times
			//then return that object
		
		//Make sure not to exceed the size of the list (else return null)
    }
	
	@Override
    public Iterator<T> iterator() {
		return null;
    }
	
	//The next two functions, are being called by the static functions at the top of this page
	//These functions are just wrappers to prevent the static function from deciding which
	//sorting algorithm should it use.
	//This function will decide which sorting algorithm it should use 
	//(we only have merge sort in this assignment)
	
	//Sorts the link list in serial
    private void sort(Comparator<T> comp) {
	
		new MergeSort<T>(comp).sort(this); //Run this within the critical section (as discussed before)
		
		//It might not allow you to use this inside critical
			//Create a final pointer = this then use that pointer
    }

	//Sorts the link list in parallel (using multiple threads)
    private void par_sort(Comparator<T> comp) {
		new MergeSort<T>(comp).parallel_sort(this); //Run this within the critical section (as discussed before)
    }

	//Merge sort
    static class MergeSort<T> {
	
		//Variables (attributes)
			//ExecutorService
			//Depth limit
	
		//Comparison function
		final Comparator<T> comp;

		//Constructor
		public MergeSort(Comparator<T> comp) {
			this.comp = comp;
		}

		//#####################
		//# Sorting functions #
		//#####################
		//The next two functions will simply call the correct function 
		//to merge sort the link list and then they will fix its 
		//attributes (head and tail pointers)
		
		public void sort(LinkedList<T> list) {
		}

		public void parallel_sort(LinkedList<T> list) {			
		}
		
		//#########
		//# Steps #
		//#########
		
		//The main merge sort function (parrallel_msort and msort)
			//Split the list to two parts
			//Merge sort each part
			//Merge the two sorted parts together
		
		//Splitting function
			//Run two pointers and find the middle of the a specific list
			//Create two new lists (and break the link between them)
			//It should return pair (the two new lists)
		
		//Merging function
			//1- Keep comparing the head of the two link lists
			//2- Move the smallest node to the new merged link list
			//3- Move the head on the list that lost this node
			
			//4- Once one of the two lists is done, append the rest of the 
			//	 second list to the tail of the new merged link list
	}

 
}
