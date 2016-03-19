For this assignment you are required to implement a linked list in java.
Additionally the linked list must support both serial and parallel merge sort.

Before you begin, in the terminal:

run "./sbt compile". If you reviece any compile errors you should alert the TA.
run "./sbt test". This will execute the JUnit tests under src/test/java. 
  - That these tests pass is the BARE MINIMUM for a passing grade on the assignment.
  - Additionally, 
    - Your implementations must be thread safe => all state mutations must be gaurded by a mutex / lock from the java.util.concurrent package
    - Your parallel sort implementation must make use of the ExecutorService interface, The Future class and may NOT refrence the java Thread class directly.

Part 1 of the assignment is to complete the scaffolded code under src/main/java.

Part 2 is to profile the serial and parallel implementations. 
  How long does it take to sort a list of N elements for values of N = 1e5, 2e5, 4e5, 8e5, 16e5 ?
  Provide a graph (pdf or jpeg) comparing the serial and parallel algorithms where the parallel algorithm uses a thread pool of size 4.
  Provide a graph (pdf or jpeg) comparing the runtime of the parallel algorthm for N=16e5, and a thread pool of size = 2, 4, 6, 8, 16, 32, 64, 1024.
  
In your README file:

    - document: 
      - java version
      - linux kernel version
      - number of physical cores / processors
      - size of RAM
      - size of java heapspace
      - number of processes visable on the system
      (NOTE: if you are unsure how to aquire any of the above information,
             ask google before you ask your TA)
    - explain your results (the two graphs).



