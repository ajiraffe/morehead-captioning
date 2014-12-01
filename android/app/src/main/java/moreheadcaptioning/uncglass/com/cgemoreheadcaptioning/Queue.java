package moreheadcaptioning.uncglass.com.cgemoreheadcaptioning;

/**
 * Created by adam on 11/14/14.
 */
public interface Queue<T> {
    public int size();
    public boolean isEmpty();
    public void enqueue(T d);
    public T dequeue() throws IndexOutOfBoundsException;
}
