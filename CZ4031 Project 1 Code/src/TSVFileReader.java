import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TSVFileReader {

    public static List<Record> readTSVFile(String filePath) {

        List<Record> data = new ArrayList<>();
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                String[] fields = line.split("\t");
                String key = fields[0];
                String[] recordData = new String[fields.length - 1];
                System.arraycopy(fields, 1, recordData, 0, fields.length - 1);
                data.add(new Record(key, recordData));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;

    }

}
