package queues;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] items;
    private int size;

    public RandomizedQueue() {
        items = (Item[]) new Object[1];
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (size == items.length) {
            resize(2*items.length);
        }
        items[size++] = item;
    }

    private void resize(int capacity) {
        Item[] copy = (Item[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            copy[i] = items[i];
        }
        items = copy;
    }

    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("No elements in the randomized queue");
        }
        int random = StdRandom.uniformInt(size);
        Item item = items[random];
        items[random] = items[--size];
        items[size] = null;
        if (size > 0 && size == items.length / 4) {
            resize(items.length / 2);
        }
        return item;
    }

    public Item sample() {
        if (isEmpty()) {
            throw new NoSuchElementException("No elements in the randomized queue");
        }
        int random = StdRandom.uniformInt(size);
        Item item = items[random];
        return item;
    }

    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    private class RandomizedQueueIterator implements Iterator<Item> {
        private Item[] randomItems;
        private int i = size;

        public RandomizedQueueIterator() {
            randomItems = (Item[]) new Object[size];
            for (int i = 0; i < size; i++) {
                randomItems[i] = items[i];
            }
            StdRandom.shuffle(randomItems);
        }
        public boolean hasNext() {
            return i > 0;
        }
        public void remove() {
            throw new UnsupportedOperationException("Remove operation is not supported");
        }
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements to iterate");
            }
            return randomItems[--i];
        }
    }

    public static void main(String[] args) {
        RandomizedQueue<Integer> randomizedQueue = new RandomizedQueue<>();

        StdOut.println(randomizedQueue.size());
        StdOut.println(randomizedQueue.isEmpty());

        for (int i = 0; i < 10; i++) {
            randomizedQueue.enqueue(i);
        }

        StdOut.println(randomizedQueue.size());
        StdOut.println(randomizedQueue.isEmpty());

        for (int i = 0; i < 5; i++) {
            StdOut.println(randomizedQueue.sample());
        }
        for (int i = 0; i < 5; i++) {
            StdOut.println(randomizedQueue.dequeue() + " Current size: " + randomizedQueue.size());
        }

        for (Integer i : randomizedQueue) {
            StdOut.println(i);
        }

        Iterator<Integer> iterator = randomizedQueue.iterator();
        StdOut.println(iterator.next());
        StdOut.println(iterator.next());
        StdOut.println(iterator.hasNext());

        Iterator<Integer> iterator2 = randomizedQueue.iterator();
        StdOut.println(iterator2.next());
        StdOut.println(iterator2.next());
        StdOut.println(iterator2.hasNext());
    }
}
