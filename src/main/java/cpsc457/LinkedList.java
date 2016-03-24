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
	public Node<T> head;
		//Tail
	public Node<T> tail;
		//Size (not required)
	public int size;
		//Critical Section
	public Lock lock;

 
	//Constructor
    public LinkedList() {
		//Set head and tail to null
	    head = tail = null;
		//Set size to zero
		size = 0;
		//Create new instance for the critical section
		lock = new ReentrantLock();
    }

	//Returns the size of the list
    public int size() {
        lock.lock();
        if (head == null) {
			lock.unlock();
            return 0;
        }           //either iterate through all the list and count
					//or create an attribute that stores the size and changes
					//every time we add or remove a node
	    else {
			int size = 1;
			Node<T> current = head;
			while (current.next != null) {
				size++;
				current = current.next;
			}
			lock.unlock();
			return size;
		}
    }
	
	//Checks if the list is empty
	// Don't need locks
	public boolean isEmpty() {
		Boolean empty;
		return (empty = size() == 0);
    }
	
	//Deletes all the nodes in the list
	public void clear() {
		//just set the head and tail to null (the garbage collector takes care of the rest)
			//cpp developers: be careful, you have to destroy them first
		
		//What if the merge sort is running now in a thread
			//I should not be able to delete the nodes (and vice versa)
			//Thus run this and everything else in a critical section
		lock.lock();
		head = tail = null;
		lock.unlock();
    }
	
	//Adds a new node to the list at the end (tail)
    public LinkedList<T> append(T t) {
        lock.lock();
		Node<T> newNode = new Node<T>(t, null);

		//Check if it is empty 
			//head = tail = t
		if (t == null) {
			throw new NullPointerException();
		}

		//Else add to the tail and move the tail to the end
			//tail.next = t    then		tail = t
		else if (tail != null) {
			tail.next = newNode;
			tail = newNode;
			size++;
		} else {
			head = newNode;
			tail = newNode;
			size++; // Do not forget to increment the size by 1 (if you have it as an attribute)
		}
		lock.unlock();
        return this;		
    }

	//Gets a node's value at a specific index
    public T get(int index) {
		lock.lock();

		//Create a new pointer that starts at the head
		Node<T> indexical = head;
		//Iterate through the list
		for (int i = 0; i < size(); i++) {
			if (indexical == null) {
				throw new IndexOutOfBoundsException();
			}
			//Keeps moving forward (pt = pt.next) for index times
			indexical = indexical.next;
		}
		T data = indexical.data;
		
		lock.unlock();
		//then return that object
		return data;
    }
	
	@Override
    public Iterator<T> iterator() {
		lock.lock();
		 try {
		    Iterator<T> iterator = new Iterator<T>() {
		        Node<T> pointer = head;

				@Override
				public void remove() {
					pointer = null;
				}
		        
		        @Override
		        public boolean hasNext() {
		            if (head == null) {
						return false;
					}
					return (pointer.next != null);
		        }
		        
		        @Override
		        public T next() {
		            if (!hasNext()) {
						throw new NoSuchElementException();
					}
					pointer = pointer.next;
					return pointer.data;
		        }
		    };
			return iterator;
		} finally {
            lock.unlock();
        }
    }
	
	//The next two functions, are being called by the static functions at the top of this page
	//These functions are just wrappers to prevent the static function from deciding which
	//sorting algorithm should it use.
	//This function will decide which sorting algorithm it should use 
	//(we only have merge sort in this assignment)
	
	//Sorts the link list in serial
    private void sort(Comparator<T> comp) {
	
		//Run this within the critical section (as discussed before)
		new MergeSort<T>(comp).sort(this); //Create a final pointer = this then use that pointer
		//It might not allow you to use this inside critical
		// Didn't have to use final pointer. "sort" will inherit all lock/unlock behaviour
		// from the methods that are being called within this context.
    }

	//Sorts the link list in parallel (using multiple threads)
    private void par_sort(Comparator<T> comp) {
		//Run this within the critical section (as discussed before)
		new MergeSort<T>(comp).parallel_sort(this); 
    }

	//Merge sort
    static class MergeSort<T> {
	
		//Variables (attributes)
			//ExecutorService
		ExecutorService threadPool;
			//Depth limit
		int depth;	

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
		    LinkedList<T> result = mergeSort(list);
			list.head = result.head;
		}

		public void parallel_sort(LinkedList<T> list) {
		    if (list.head == null) {
				return;			
		}
		
		// We changed this variable for specified
		// thread pool sizes: {2, 4, 6, 8, 32, 64, 1028}
		// Tried to create an iterator so that this wasn't
		// a hardcoded value. Got a bit more complicated to
		// try to automate this for testing.
		int threadCount = 1024;	
		depth = (int) Math.floor(Math.log10(threadCount) / Math.log10(2));

			try {
				threadPool = Executors.newFixedThreadPool(threadCount);
				LinkedList<T> sorted = parallel_mergesort(list, depth);
				list.head = sorted.head;
				threadPool.shutdown();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			    }
		    }
		
		//#########
		//# Steps #
		//#########
		
		    //The main merge sort function (parallel_msort and msort)
			//Split the list to two parts
			//Merge sort each part
			//Merge the two sorted parts together
			
			public LinkedList<T> parallel_mergesort(LinkedList<T> list, int depth)
				throws InterruptedException, ExecutionException {

			if (depth == 1) {
				return mergeSort(list);
			}

			Pair<LinkedList<T>, LinkedList<T>> splitLists = split(list);
			LinkedList<T> listOne = splitLists.fst();
			LinkedList<T> listTwo = splitLists.snd();

			Future<LinkedList<T>> futureOne = threadPool.submit(new Callable<LinkedList<T>>() {
				public LinkedList<T> call() throws Exception {
					return parallel_mergesort(listOne, depth - 1);
				}
			});

			Future<LinkedList<T>> futureTwo = threadPool.submit(new Callable<LinkedList<T>>() {
				public LinkedList<T> call() throws Exception {
					return parallel_mergesort(listTwo, depth - 1);
				}
			});

			LinkedList<T> firstSortedList = futureOne.get();
			LinkedList<T> secondSortedList = futureTwo.get();
			return merge(firstSortedList, secondSortedList);
		}
		
			public LinkedList<T> mergeSort(LinkedList<T> list) {
			if (list.head == null || list.head.next == null) {
				return list;
			}
			Pair<LinkedList<T>, LinkedList<T>> splitLists = split(list);
			LinkedList<T> firstHalf = splitLists.fst();
			LinkedList<T> listTwo = splitLists.snd();

			return merge(mergeSort(firstHalf), mergeSort(listTwo));
		}
		
		    //Splitting function
			//Create two new lists (and break the link between them)
			//It should return pair (the two new lists)
			public Pair<LinkedList<T>, LinkedList<T>> split(LinkedList<T> list) {
				//Run two pointers and find the middle of the a specific list
				Node<T> firstPointer = list.head;
				Node<T> secondPointer = firstPointer.next;

				if (list.head == null) {
					return null;
				}

				while (secondPointer != null && secondPointer.next != null) {
					firstPointer = firstPointer.next;
					secondPointer = secondPointer.next.next;
				}

				LinkedList<T> listTwo = new LinkedList<>();
				listTwo.head = firstPointer.next; // Creates new list
				firstPointer.next = null; // Cuts off backend of front list

				return new Pair<LinkedList<T>, LinkedList<T>>(list, listTwo);
		    }
		
		    //Merging function
			//1- Keep comparing the head of the two link lists
			//2- Move the smallest node to the new merged link list
			//3- Move the head on the list that lost this node
			
			//4- Once one of the two lists is done, append the rest of the 
			//	 second list to the tail of the new merged link list
			public LinkedList<T> merge(LinkedList<T> firstList, LinkedList<T> secondList) {

			Node<T> compareTemp, current, firstHead, secondHead;

			compareTemp = new Node<T>(null, null);
			current = compareTemp;
			firstHead = firstList.head;
			secondHead = secondList.head;

			while (firstHead != null && secondHead != null) {
				if ((comp.compare(firstHead.data, secondHead.data)) == -1) {
					current.next = firstHead;
					firstHead = firstHead.next;
				} else {
					current.next = secondHead;
					secondHead = secondHead.next;
				}
				current = current.next;
			}

			current.next = (firstHead == null) ? secondHead : firstHead;
			LinkedList<T> sortedList = new LinkedList<>();
			sortedList.head = compareTemp.next;

			return sortedList;
		    }
	    }
}
