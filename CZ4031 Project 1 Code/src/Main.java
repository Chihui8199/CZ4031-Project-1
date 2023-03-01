import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Scanner;

import utils.Parser;

public class Main {
    private static final int DEFAULT_MAX_DISK_CAPACITY = 500 * (int) (Math.pow(10, 6));


    public static void main(String[] args) throws Exception {
        int diskSize = getDiskInput();
        String separator = System.getProperty("file.separator");
        String filePath = new File("").getAbsolutePath();
        filePath = filePath.concat(separator + "data.tsv");
        System.out.print(filePath + "\n");
        File file = new File(String.valueOf(filePath));
        if (file.exists()) {
            System.out.print("Yes File Exist\nStarting to read data...\n");
            Parser.readTSVFile(String.valueOf(filePath), diskSize);
        } else {
            throw new FileNotFoundException("File does not exist!");
        }
    }

    private static int getDiskInput() {
        int n = 0;
        Scanner sc = new Scanner(System.in);
        while (n < 3) {
            try {
                System.out.print("Disk Size must be between 200-500MB: ");
                int diskSize = sc.nextInt();
                if (diskSize < 200 || diskSize > 500) {
                    n++;
                } else {
                    return diskSize * (int) (Math.pow(10, 6));
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.printf("No argument detected, falling back to default disk size: %d " + DEFAULT_MAX_DISK_CAPACITY);
                break;
            } catch (NumberFormatException e) {
                System.out.printf("Invalid disk size input detected, falling back to default disk size: %d " + DEFAULT_MAX_DISK_CAPACITY);
                break;
            } catch (Exception e) {
                System.out.printf("Something went wrong, falling back to default disk size: %d" + DEFAULT_MAX_DISK_CAPACITY);
                break;
            }
        }
        System.out.println("Invalid disk size input detected, falling back to default disk size: " + DEFAULT_MAX_DISK_CAPACITY);
        return DEFAULT_MAX_DISK_CAPACITY;
    }
}