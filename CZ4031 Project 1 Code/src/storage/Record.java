package storage;

/*
 * Class representing a single record in storage
 */
public class Record {
    private String tconst;
    private float averageRating;
    private int numVotes;

    public Record(String tconst, float averageRating, int numVotes) {
        this.tconst = tconst;
        this.averageRating = averageRating;
        this.numVotes = numVotes;
    }

    public String getTconst() {
        return tconst;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public int getNumVotes() {
        return numVotes;
    }

    // Java: one char 2 Bytes
    // tConst is fixed-size 10 char x 2Bytes = 20 Bytes, rating is float so 4 bytes and votes is integer so 4 bytes also
    public static int getRecordSize(){
        return 20 + 4 + 4;
    }

    @Override
    public String toString(){
        return String.format("Record Info: Tconst: %s, aveRating: %f, numVotes %d ", this.getTconst(), this.getAverageRating(),this.getNumVotes());

    }
}