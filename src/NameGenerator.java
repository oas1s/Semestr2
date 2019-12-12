import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class NameGenerator {
    private static void readFileToList(ArrayList<String> list, String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));

        String line;
        while ((line = br.readLine()) != null) {
            list.add(line);
        }
    }

    public static String generateName() throws IOException {
        ArrayList<String> nouns = new ArrayList<>();
        readFileToList(nouns, "nouns.txt");

        ArrayList<String> adjectives = new ArrayList<>();
        readFileToList(adjectives, "adjectives.txt");

        Random random = new Random();

        return adjectives.get(random.nextInt(adjectives.size())) + " " + nouns.get(random.nextInt(nouns.size()));
    }
}
