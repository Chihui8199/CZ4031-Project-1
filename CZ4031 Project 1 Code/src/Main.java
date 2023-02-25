import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Scanner;
import index.testBplusTree;

import utils.Parser;

public class Main {
    private static final int DEFAULT_MAX_DISK_CAPACITY = 500 * (int)(Math.pow(10,6));


    public static void main(String[] args) throws Exception{
        int diskSize = getDiskInput();
        // Path filePath = Path.of("data.tsv");
        // String filePath = "/Users/chihui/Desktop/CZ4031 Project/CZ4031 Project 1 Code/src/data.tsv";
        String filePath = "/Github/CZ4031-Project-1/CZ4031 Project 1 Code/src/data.tsv";
        File file = new File(String.valueOf(filePath));
        if (file.exists()) {
            System.out.print("Yes File Exist\nStarting to read data...\n");
            Parser.readTSVFile(String.valueOf(filePath), diskSize);
        } else {
				throw new FileNotFoundException("File does not exist!");
        }
    }

    private static int getDiskInput(){
        int n = 0;
        while (n < 3) {
            try {
                Scanner sc = new Scanner(System.in);
                System.out.print("Enter disk size between 200-500MB: ");
                int diskSize = sc.nextInt();
                if (diskSize < 200 || diskSize > 500){
                    System.out.print("Disk Size must be between 200-500MB: ");
                    n++;
                } else {
                    return diskSize * (int)(Math.pow(10,6));
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.println("No argument detected, falling back to default disk size: " + DEFAULT_MAX_DISK_CAPACITY);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid disk size input detected, falling back to default disk size: " + DEFAULT_MAX_DISK_CAPACITY);
                break;
            } catch (Exception e){
                System.out.println("Something went wrong, falling back to default disk size!" + DEFAULT_MAX_DISK_CAPACITY);
                break;
            }
        }
        System.out.println("Invalid disk size input detected, falling back to default disk size: " + DEFAULT_MAX_DISK_CAPACITY);
        return DEFAULT_MAX_DISK_CAPACITY;
    }
}