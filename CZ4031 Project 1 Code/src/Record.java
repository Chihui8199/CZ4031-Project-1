
class Record {
    private String key;
    private String[] data;

    public Record(String key, String[] data) {
        this.key = key;
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    public String[] getData() {
        return data;
    }
}
