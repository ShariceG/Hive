package coloredcoded.hive;

import android.app.Activity;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import coloredcoded.hive.client.Location;
import coloredcoded.hive.client.ServerClient;
import coloredcoded.hive.client.ServerClientImp;

public class AppHelper {

    public static void deleteFromInternalStorage(Activity activity, String key) {
        if (!activity.deleteFile(key + ".txt")) {
            throw new RuntimeException("Unable to delete file: " + key);
        }
        System.out.println("Delete file: " + key);
    }

    public static boolean internalStorageContainsKey(Activity activity, String key) {
        try {
            new FileReader(new File(fullInternalStoragePath(activity, key))).close();
        } catch (FileNotFoundException e) {
            return false;
        } catch (Exception p) {
            p.printStackTrace();
            return false;
        }
        return true;
    }

    public static void writeToInternalStorage(Activity activity, String key, String data) {
        try {
            String fullPath = fullInternalStoragePath(activity, key);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(fullPath)));
            bufferedWriter.write(data);
            System.out.println("Wrote data: " + data + " to " + fullPath);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static org.json.simple.JSONObject readFromInternalStorageToJSONObject(
            Activity activity, String key) {
        try {
            String fullPath = fullInternalStoragePath(activity, key);
            FileReader reader = new FileReader(new File(fullPath));
            JSONParser parser = new JSONParser();
            JSONObject json = (org.json.simple.JSONObject) parser.parse(reader);
            System.out.println("Read data: " + json.toJSONString() + " from " + fullPath);
            reader.close();
            return json;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException p) {
            p.printStackTrace();
        }
        return null;
    }

    private static String fullInternalStoragePath(Activity activity, String filename) {
        return String.format("%s%s%s", activity.getFilesDir(), File.separator,
                String.format("%s.txt", filename));
    }

    static ServerClient serverClient() {
        return new ServerClientImp();
    }

    static Location getTestUserLocation() {
        return new Location("47.608013", "-122.335167",
                new Location.Area("47.60", "-122.33",
                        "Seattle", "WA", "United States"));
    }

    static String getTestUser() {
        return "user1";
    }

}
