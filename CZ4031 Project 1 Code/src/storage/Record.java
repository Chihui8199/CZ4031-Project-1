package storage;

/*
 * Class representing a single record in storage
 */
public class Record {
    private String tconst;
    private float averageRating;
    private int numVotes;

    /**
     * Constructs a Record object with the given unique identifier, average rating, and number of votes.
     *
     * @param tconst        The unique identifier of the record.
     * @param averageRating The average rating of the record.
     * @param numVotes      The number of votes the record has received.
     */
    public Record(String tconst, float averageRating, int numVotes) {
        this.tconst = tconst;
        this.averageRating = averageRating;
        this.numVotes = numVotes;
    }

    /**
     * Returns Tconst of the record.
     *
     * @return The Tconst of the record.
     */
    public String getTconst() {
        return tconst;
    }

    /**
     * Returns the average rating of the record.
     *
     * @return The average rating of the record.
     */
    public float getAverageRating() {
        return averageRating;
    }

    /**
     * Returns the number of votes the record has received.
     *
     * @return The number of votes the record has received.
     */
    public int getNumVotes() {
        return numVotes;
    }

    /**
     * Retrieves Size of record which is fixed due to unspanned implementation
     * - tconst is fixed with 10 chars -> Java one char = 2 Bytes --> 20 Bytes
     * - avgRating is float -> 4B
     * - numVote is int -> 4B
     */
    public static int getRecordSize() {
        return 20 + 4 + 4;
    }


    /**
     * Returns a formatted string representation of the Record object with information about the Tconst, average rating and number of votes.
     *
     * @return A formatted string with the Tconst, average rating and number of votes of the Record.
     */
    @Override
    public String toString() {
        return String.format("Record Info: Tconst: %s, aveRating: %f, numVotes %d ", this.getTconst(), this.getAverageRating(), this.getNumVotes());

    }
}