package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * An instance is a priority queue of elements of type E implemented as a min-heap.
 */
class InternalMinHeap<E> {

  private int size; // number of elements in the priority queue (and heap)

  /**
   * The heap invariant is given below. Note that / denotes int division.
   * 
   * <p>lst[0..size-1] is viewed as a min-heap, i.e.
   * 1. Each array element in lst[0..size-1] contains a value of the heap.
   * 2. The children of each lst[i] are lst[2i+1] and lst[2i+2].
   * 3. The parent of each lst[i] (except lst[0]) is lst[(i-1)/2].
   * 4. The priority of the parent of each lst[i] is <= the priority of lst[i].
   * 5. Priorities for the lst[i] used for the comparison in point 4
   * are given in map. map contains one entry for each element of
   * the heap, and map and b have the same size.
   * For each element e in the heap, the map entry contains in the
   * Info object the priority of e and its index in b.</p>
   */
  private List<E> lst = new ArrayList<>();
  private Map<E, Info> map = new HashMap<>();

  /**
   * Constructor: an empty heap.
   */
  public InternalMinHeap() {
  }

  /**
   * Return the number of elements in the priority queue.
   * This operation takes constant time.
   */
  public int size() {
    return size;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Add e with priority p to the priority queue.
   * Throw an illegalArgumentException if e is already in the queue.
   * The expected time is O(log N) and the worst-case time is O(N).
   */
  public void add(E e, double p) throws IllegalArgumentException {
    if (map.containsKey(e)) {
      throw new IllegalArgumentException("Cannot insert the same element twice");
    }

    lst.add(e);
    map.put(e, new Info(size, p));
    size++;
    bubbleUp(size - 1);
  }

  /**
   * Return the element of the priority queue with lowest priority, without
   * changing the queue. This operation takes constant time.
   * Throw a PCueException with message "priority queue is empty" if the
   * priority queue is empty.
   */
  public E peek() {
    if (lst.isEmpty()) {
      throw new NoSuchElementException();
    }
    return lst.get(0);
  }

  /**
   * Remove and return the element of the priority queue with lowest priority.
   * The expected time is O(log n) and the worst-case time is O(N).
   * Throw a PCueException with message "priority queue is empty" if the
   * priority queue is empty.
   */
  public E poll() {
    E val = peek();
    map.remove(val);
    size--;
    if (size <= 0) {
      lst.remove(0);
    } else {
      lst.set(0, lst.get(size));
      lst.remove(size);
      bubbleDown(0);
    }
    return val;
  }

  /**
   * Change the priority of element e to p.
   * The expected time is O(log N) and the worst-case is time O(N).
   * Throw an illegalArgumentException if e is not in the priority queue.
   */
  public void changePriority(E e, double p) {
    Info info = map.get(e);
    if (info == null) {
      throw new IllegalArgumentException("No element found: " + e);
    }

    if (p < info.priority) {
      info.priority = p;
      bubbleUp(info.index);
    } else {
      info.priority = p;
      bubbleDown(info.index);
    }
  }


  /**
   * Bubble lst[k] up in heap to its right place.
   * Precondition: Every lst[i] satisfies the heap property except perhaps
   * k's priority < parent's priority
   */
  private void bubbleUp(int k) {
    E val = lst.get(k);
    Info info = map.get(val);

    int i = k;
    while (i > 0) {
      int parentIdx = (i - 1) / 2;
      E parentVal = lst.get(parentIdx);
      Info parentInfo = map.get(parentVal);

      if (parentInfo.priority <= info.priority) {
        break;
      }

      lst.set(i, parentVal);
      parentInfo.index = i;

      i = parentIdx;
    }
    lst.set(i, val);
    info.index = i;
  }

  /**
   * Bubble lst[k] down in heap until it finds the right place.
   * Precondition: Every lst[i] satisfies the heap property except perhaps
   * k's priority > a child's priority.
   */
  private void bubbleDown(int k) {
    E val = lst.get(k);
    Info info = map.get(val);

    int i = k;
    while (2 * i + 1 < size) {
      int childIdx = getSmallerChild(i);
      E childVal = lst.get(childIdx);
      Info childInfo = map.get(childVal);

      if (info.priority <= childInfo.priority) {
        break;
      }

      lst.set(i, childVal);
      childInfo.index = i;

      i = childIdx;
    }
    lst.set(i, val);
    info.index = i;
  }

  /**
   * Return the index of the smaller child of lst[q].
   * Precondition: left child exists: 2q+1 < size of heap.
   */
  private int getSmallerChild(int q) {
    int leftIdx = 2 * q + 1;
    int rightIdx = 2 * q + 2;
    if (size <= rightIdx) {
      return leftIdx;
    }

    Info leftInfo = map.get(lst.get(leftIdx));
    Info rightInfo = map.get(lst.get(rightIdx));
    return (leftInfo.priority <= rightInfo.priority ? leftIdx : rightIdx);
  }

  /**
   * An instance contains the index, value, and priority of an element of the heap.
   */
  private static class Info {
    private int index;  // index of this element in map
    private double priority; // priority of this element

    /**
     * Constructor: an instance in lst[i] with priority p.
     */
    private Info(int i, double p) {
      index = i;
      priority = p;
    }

  }
}
