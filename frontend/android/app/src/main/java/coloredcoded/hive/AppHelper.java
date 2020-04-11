package coloredcoded.hive;

import android.app.Activity;
import android.app.AlertDialog;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import coloredcoded.hive.client.HiveLocation;
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

    static HiveLocation getTestUserLocation() {
        return new HiveLocation("47.608013", "-122.335167",
                new HiveLocation.Area("47.60", "-122.33",
                        "Seattle", "WA", "United States"));
    }

    static String getTestUser() {
        return "user1";
    }

    public static void showPermanentAlert(Activity activity, String message) {
        showPermanentAlert(activity, "", message);
    }

    public static void showPermanentAlert(final Activity activity, final String title,
                                          final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        activity);
                builder.setTitle(title);
                builder.setMessage(message);
                builder.setCancelable(false);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public static void showInternalServerErrorAlert(Activity activity) {
        showAlert(activity, "Um... Yikes", "Some server error.");
    }

    public static void showAlert(Activity activity, String message) {
        showAlert(activity, "", message);
    }

    public static void showAlert(final Activity activity, final String title,
                                 final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        activity);
                builder.setTitle(title);
                builder.setMessage(message);
                builder.setCancelable(true);
                builder.setPositiveButton("Ok", null);
                final AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

}
