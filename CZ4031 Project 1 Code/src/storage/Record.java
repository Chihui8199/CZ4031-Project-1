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

    // tConst is fixed-size 9 char, rating is float so 4 bytes and votes is integer so 4 bytes also
    public static int getRecordSize(){
        return 4 + 4 + 9;
    }

    @Override
    public String toString(){
        return String.format("Record Info: Tconst: %s, aveRating: %f, numVotes %d ", this.getTconst(), this.getAverageRating(),this.getNumVotes());

    }
}