package net.studymongolian.chimee;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class FileUtils {

    private static final String APP_PUBLIC_FOLDER_NAME = "Chimee";
    private static final String TEXT_FILE_FOLDER_NAME = "doc";
    private static final String TEXT_FILE_EXTENSION = ".txt";
    private static final String RESERVED_CHARS= "|\\?*<\":>/";
    private static final String TAG = "Chimee FileUtils";

    private static List<String> getTextFileNames() {
        String path = getAppDocumentDirectory();
        File directory = new File(path);
        File[] files = getFilesInDirectorySortedByLastModified(directory);
        List<String> list = new ArrayList<>();
        if (files == null)
            return list;
        for (File file : files) {
            list.add(file.getName());
        }
        return list;
    }

    private static File[] getFilesInDirectorySortedByLastModified(File directory) {
        File[] files = directory.listFiles();
        Pair[] pairs = new Pair[files.length];
        for (int i = 0; i < files.length; i++)
            pairs[i] = new Pair(files[i]);

        // Sort by timestamp.
        Arrays.sort(pairs);

        // put newest first, dropping timestamp
        int length = files.length;
        for (int i = 0; i < length; i++)
            files[i] = pairs[length - i - 1].file;

        return files;
    }

    private static class Pair implements Comparable {
        public long time;
        public File file;

        Pair(File file) {
            this.file = file;
            time = file.lastModified();
        }

        public int compareTo(@NonNull Object o) {
            long u = ((Pair) o).time;
            //noinspection UseCompareMethod // Requires API 19
            return time < u ? -1 : time == u ? 0 : 1;
        }
    }

    static List<String> getTextFileNamesWithoutExtension() {
        List<String> list = new ArrayList<>();
        List<String> listWithExtensions = getTextFileNames();
        int extLength = TEXT_FILE_EXTENSION.length();
        for (String name : listWithExtensions) {
            int nameLength = name.length();
            if (name.endsWith(TEXT_FILE_EXTENSION)
                    && nameLength > extLength) {
                String shortened = name.substring(0, nameLength - extLength);
                list.add(shortened);
            }
        }
        return list;
    }

    public static String openFile(String shortFilenameWithoutExtension) throws Exception {
        String fullFilePath = getAppDocumentDirectory() + File.separator
                + shortFilenameWithoutExtension + TEXT_FILE_EXTENSION;

        return getStringFromFile(fullFilePath);
    }

    public static String getStringFromFile (String filePath) throws Exception {
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);
        String text = convertStreamToString(inputStream);
        inputStream.close();
        return text;
    }

    static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static boolean saveTextFile(Context appContext, String filename, String text) {

        // make sure the directory exists
        File destFolder = new File(getAppDocumentDirectory());
        if (!destFolder.exists()) {
            boolean created = destFolder.mkdirs();
            if (created)
                scanFile(appContext, destFolder);
        }

        String sanitizedFileName = sanitizeFileName(filename);
        if (TextUtils.isEmpty(sanitizedFileName)) return false;

        String nameWithExtension = sanitizedFileName + TEXT_FILE_EXTENSION;
        try {
            copyTextFileOver(appContext, destFolder, nameWithExtension, text);
        } catch (IOException e) {
            Log.e(TAG, "saveTextFile: copyTextFileOver failed");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static String sanitizeFileName(String filename) {
        StringBuilder sanitized = new StringBuilder();
        for (char c : filename.toCharArray()) {
            if (!RESERVED_CHARS.contains(String.valueOf(c))) {
                sanitized.append(c);
            }
        }
        return sanitized.toString().trim();
    }

    private static String getAppPublicStorageDirectory() {
        return Environment.getExternalStorageDirectory() + File.separator
                + APP_PUBLIC_FOLDER_NAME;
    }

    static String getAppDocumentDirectory() {
        return getAppPublicStorageDirectory() + File.separator
                + TEXT_FILE_FOLDER_NAME;
    }

    private static void scanFile(Context context, File file) {
        // this registers the file so that file explorers can find it more quickly
        MediaScannerConnection
                .scanFile(context, new String[]{file.getAbsolutePath()},
                        null, null);
    }

    private static void copyTextFileOver
            (Context context, File destFolder, String fileName, String text)
            throws IOException {
        File csvFile = new File(destFolder, fileName);
        FileOutputStream fileOutput = new FileOutputStream(csvFile);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutput);
        outputStreamWriter.write(text);
        outputStreamWriter.flush();
        fileOutput.getFD().sync();
        outputStreamWriter.close();
        scanFile(context, csvFile);
    }

    public static boolean textFileExists(String filename) {
        String nameToTest = filename;
        if (!filename.endsWith(TEXT_FILE_EXTENSION))
            nameToTest += TEXT_FILE_EXTENSION;
        List<String> fileNames = getTextFileNames();
        for (String file : fileNames) {
            if (file.equals(nameToTest))
                return true;
        }
        return false;
    }
}
