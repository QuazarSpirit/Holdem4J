package org.quazarspirit.Utils;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONException;
import org.json.JSONObject;
import org.quazarspirit.Utils.Logger.SimpleLogger;
import org.quazarspirit.Utils.Logger.TCPLogger;
import org.quazarspirit.Utils.PubSub.Publisher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Utils {
    public enum EVENT {
        LOG
    }

    public enum LOG_LEVEL {
        MESSAGE, INFO, DEBUG, WARNING, ERROR
    }

    private static LogSource logSource = new LogSource();

    private static boolean envLoaded = false;

    public static Class<?> GetCallerClass() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String clazzName = stackTrace[4].getClassName();
        try {
            return Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String ReadFileAsString(String filePath) throws IOException {
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

    public static boolean IsTesting() {
        return (System.getProperty("TEST") != null && System.getProperty("TEST").equals("true"));
    }

    public static class CircularArrayList<E> extends ArrayList<E> {
        public CircularArrayList(ArrayList<E> arrayList) {
            super(arrayList);
        }

        public CircularArrayList() {
            super();
        }

        public E get(int index) {
            int i = index % size();
            if (i == -1) {
                i = size() - 1;
            }

            return super.get(i);
        }
    }

    static class LogSource extends Publisher {

        LogSource() {
            createLoggers();
        }

        private void createLoggers() {
            SimpleLogger simpleLogger = new SimpleLogger();
            this.addSubscriber(simpleLogger);
            TCPLogger wssLogger = TCPLogger.GetSingleton();
            this.addSubscriber(wssLogger);
        }

        public void log(Object message) {
            if (message instanceof JSONObject json) {
                if (json.has("type")) {
                    publish(json);
                    return;
                }
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", EVENT.LOG);
            jsonObject.put("message", message);
            publish(jsonObject);
        }
    }

    public static void Log(Object arg) {
        logSource.log(arg);
    }

    public static void Log(String message, Map.Entry<String, Object>... args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", message);
        Arrays.stream(args).forEach(arg -> {
            jsonObject.put(arg.getKey(), arg.getValue());
        });
        logSource.log(jsonObject);
    }

    public static void Log(Object arg, LOG_LEVEL log_level) {
        if (!IsTesting() && log_level == LOG_LEVEL.DEBUG) {
            return;
        }

        logSource.log(arg);
    }

    private static void LoadEnv() {
        if (!envLoaded) {
            Dotenv.configure().systemProperties().load();
            envLoaded = true;
        }
    }

    public static Object GetEnv(String key, Object defaultValue) {
        LoadEnv();
        Object property = System.getProperty(key);
        if (property != null) {
            return property;
        }

        return defaultValue;
    }
}
