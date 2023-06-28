package it.polimi.ingsw;

/**
 * This class is used to create a tuple of two elements
 */
public class Tuple <T1, T2>{
    private final T1 first;
    private final T2 second;

    /**
     * This method returns the first element of the tuple
     *
     * @return the first element of the tuple
     */
    public T1 getFirst() {
        return first;
    }

    /**
     * This method returns the second element of the tuple
     *
     * @return the second element of the tuple
     */
    public T2 getSecond() {
        return second;
    }

    /**
     * This method creates a new {@code Tuple} of two elements
     *
     * @param first the first element of the tuple
     * @param second the second element of the tuple
     */
    public Tuple(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }
}
