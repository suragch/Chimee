package net.studymongolian.chimee;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

class FileUtils {

    private static final String APP_PUBLIC_FOLDER_NAME = "Chimee";
    private static final String TEXT_FILE_FOLDER_NAME = "doc";
    private static final String TEXT_FILE_EXTENSION = ".txt";
    private static final String RESERVED_CHARS= "|\\?*<\":>/";
    private static final String TAG = "Chimee FileUtils";

    static List<String> getTextFileNames() {
        String path = getAppDocumentDirectory();
        File directory = new File(path);
        List<String> list = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files == null)
            return list;
        for (File file : directory.listFiles()) {
            list.add(file.getName());
        }
        return list;
    }

    public static String openFile(String fileName) {
        return "This is dummy text from " + fileName;
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

    private static String getAppDocumentDirectory() {
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
