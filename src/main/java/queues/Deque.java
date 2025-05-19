package queues;

import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private Node first;
    private Node last;
    private int size;

    public Deque() {
        first = null;
        last = null;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null");
        }
         Node oldFirst = first;
         first = new Node();
         first.item = item;
         first.next = oldFirst;
         if (oldFirst != null) {
             oldFirst.previous = first;
         }
         if (isEmpty()) {
             last = first;
         }
         size++;
    }

    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null");
        }
        Node oldLast = last;
        last = new Node();
        last.item = item;
        last.next = null;
        if (isEmpty()) {
            first = last;
        } else {
            last.previous = oldLast;
            oldLast.next = last;
        }
        size++;
    }

    public Item removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("No elements in the deque");
        }
        size--;
        Item item = first.item;
        first = first.next;
        if (first != null) {
            first.previous = null;
        }
        if (isEmpty()) {
            last = null;
        }
        return item;
    }

    public Item removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("No elements in the deque");
        }
        size--;
        Item item = last.item;
        last = last.previous;
        if (last != null) {
            last.next = null;
        }
        if (isEmpty()) {
            first = null;
        }
        return item;
    }

    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private class Node {
        Item item;
        Node next;
        Node previous;
    }

    private class DequeIterator implements Iterator<Item> {
        private Node current = first;

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove operation is not supported");
        }

        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements to iterate");
            }
            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    public static void main(String[] args) {
        Deque<Integer> deque = new Deque<>();
        deque.addFirst(3);
        deque.addLast(2);
        deque.addLast(1);
        deque.addFirst(4);

        StdOut.println(deque.isEmpty());
        StdOut.println(deque.size());

        StdOut.println(deque.removeLast());
        StdOut.println(deque.removeLast());
        StdOut.println(deque.removeLast());
        StdOut.println(deque.removeFirst());

        StdOut.println(deque.isEmpty());

        deque.addFirst(3);
        deque.addLast(2);
        deque.addLast(1);
        deque.addFirst(4);

        for (Integer i : deque) {
            StdOut.println(i);
        }

        Iterator<Integer> iterator = deque.iterator();
        StdOut.println(iterator.next());
        StdOut.println(iterator.next());
        StdOut.println(iterator.hasNext());
    }
}
