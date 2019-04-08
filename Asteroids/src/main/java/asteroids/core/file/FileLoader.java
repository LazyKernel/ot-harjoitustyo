package asteroids.core.file;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileLoader {
    public static String loadFileAsString(String fileName) {
        StringBuilder ret = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ret.append(line).append('\n');
            }
        } catch (FileNotFoundException e) {
            System.err.println("File \"" + fileName + "\" not found.");
        } catch (Exception e) {
            System.err.println("Error while reading file \"" + fileName + "\". \n" + e.getMessage());
        }

        return ret.toString();
    }
}
