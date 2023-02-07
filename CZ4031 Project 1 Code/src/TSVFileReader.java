import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TSVFileReader {

    public static List<String[]> readTSVFile(String filePath) {
        List<String[]> data = new ArrayList<>();
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                data.add(line.split("\t"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void main(String[] args) {
        List<String[]> tsvData = readTSVFile("sample.tsv");
        // Prepare data for B+ tree creation
        // ...
    }
}
