package net.studymongolian.chimee;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.text.format.DateFormat;
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
import java.util.Date;
import java.util.List;

class FileUtils {

    // TODO change these to mongolian names
    private static final String APP_PUBLIC_FOLDER_NAME = "Chimee";
    private static final String IMAGE_FOLDER_NAME = "suuder";
    private static final String EXPORT_FOLDER_NAME = "database";
    private static final String TEXT_FOLDER_NAME = "text";
    private static final String TEXT_FILE_EXTENSION = ".txt";
    private static final String HISTORY_EXPORT_FILE_NAME = "yabuulsan_chimee.txt";
    private static final String WORDS_EXPORT_FILE_NAME = "minii_uges.kbd";
    private static final String RESERVED_CHARS= "|\\?*<\":>/";
    private static final String TAG = "Chimee FileUtils";

    public static final String TEMP_CACHE_SUBDIR = "images";
    public static final String TEMP_CACHE_FILENAME = "image.png";
    public static final String FILE_PROVIDER_AUTHORITY = "net.studymongolian.chimee.fileprovider";


    private static List<String> getTextFileNames(Context context) {
        String path = getAppDocumentFolder();
        File directory = new File(path);
        File[] files = getFilesInDirectorySortedByLastModified(context, directory);
        List<String> list = new ArrayList<>();
        if (files == null)
            return list;
        for (File file : files) {
            list.add(file.getName());
        }
        return list;
    }

    private static File[] getFilesInDirectorySortedByLastModified(Context context, File directory) {
        makeSureFolderExists(context, directory);
        File[] files = directory.listFiles();
        if (files == null) return null;
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

    /**
     *
     * @param context needed for getting save location
     * @param text String to save to file
     * @return whether file was successfully saved
     */
    public static boolean saveHistoryMessageFile(Context context, String text) {
        File destFolder = new File(getAppExportFolder());
        makeSureFolderExists(context, destFolder);

        try {
            copyTextFileOver(context, destFolder, HISTORY_EXPORT_FILE_NAME, text);
        } catch (IOException e) {
            Log.e(TAG, "saveHistoryMessageFile: copyTextFileOver failed");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     *
     * @param context needed for getting save location
     * @param text String to save to file
     * @return whether file was successfully saved
     */
    public static boolean saveExportedWordsFile(Context context, String text) {
        File destFolder = new File(getAppExportFolder());
        makeSureFolderExists(context, destFolder);

        try {
            copyTextFileOver(context, destFolder, WORDS_EXPORT_FILE_NAME, text);
        } catch (IOException e) {
            Log.e(TAG, "saveExportedWordsFile: copyTextFileOver failed");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void makeSureFolderExists(Context context, File destFolder) {
        if (!destFolder.exists()) {
            boolean created = destFolder.mkdirs();
            if (created)
                scanFile(context, destFolder);
        }
    }

    static String saveOverlayPhoto(Context context, Bitmap bitmap) {
        File destFolder = new File(getAppImageFolder());
        makeSureFolderExists(context, destFolder);
        String filename = convertCurrentTimeToString() + ".png";
        String pathName = destFolder + File.separator + filename;
        try {
            FileOutputStream stream = new FileOutputStream(pathName);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return getSavedPhotoDisplayFilePath(filename);
    }

    private static String convertCurrentTimeToString() {
        Date date = new Date(System.currentTimeMillis());
        return DateFormat.format("yyyy-MM-dd_kk-mm-ss", date).toString();
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
    static List<String> getTextFileNamesWithoutExtension(Context context) {
        List<String> list = new ArrayList<>();
        List<String> listWithExtensions = getTextFileNames(context);
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
        String fullFilePath = getAppDocumentFolder() + File.separator
                + shortFilenameWithoutExtension + TEXT_FILE_EXTENSION;

        return getStringFromFile(fullFilePath);
    }

    private static String getStringFromFile (String filePath) throws Exception {
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);
        String text = convertStreamToString(inputStream);
        inputStream.close();
        return text;
    }

    public static ArrayList<CharSequence> convertStreamToStringArray(InputStream inputStream) throws Exception  {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        ArrayList<CharSequence> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        return lines;
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

        File destFolder = new File(getAppDocumentFolder());
        makeSureFolderExists(appContext, destFolder);

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

    private static String getAppPublicFolder() {
        return Environment.getExternalStorageDirectory() + File.separator + APP_PUBLIC_FOLDER_NAME;
    }

    static String getAppDocumentFolder() {
        return getAppPublicFolder() + File.separator + TEXT_FOLDER_NAME;
    }

    private static String getAppExportFolder() {
        return getAppPublicFolder() + File.separator + EXPORT_FOLDER_NAME;
    }

    private static String getAppImageFolder() {
        return getAppPublicFolder() + File.separator + IMAGE_FOLDER_NAME;
    }

    public static String getExportedHistoryFileDisplayPath() {
        return APP_PUBLIC_FOLDER_NAME +
                 File.separator + EXPORT_FOLDER_NAME +
                File.separator + HISTORY_EXPORT_FILE_NAME;
    }

    public static String getExportedWordsFileDisplayPath() {
        return APP_PUBLIC_FOLDER_NAME +
                File.separator + EXPORT_FOLDER_NAME +
                File.separator + WORDS_EXPORT_FILE_NAME;
    }

    public static String getSavedPhotoDisplayFilePath(String filename) {
        return APP_PUBLIC_FOLDER_NAME +
                File.separator + IMAGE_FOLDER_NAME +
                File.separator + filename;
    }

    private static void scanFile(Context context, File file) {
        // this registers the file so that file explorers can find it more quickly
        new MediaScanner(context, file);
//        MediaScannerConnection
//                .scanFile(context, new String[]{file.getAbsolutePath()},
//                        null, null);
//        Uri contentUri = Uri.fromFile(file);
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        mediaScanIntent.setData(contentUri);
//        context.sendBroadcast(mediaScanIntent);
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

    public static boolean textFileExists(Context context, String filename) {
        String nameToTest = filename;
        if (!filename.endsWith(TEXT_FILE_EXTENSION))
            nameToTest += TEXT_FILE_EXTENSION;
        List<String> fileNames = getTextFileNames(context);
        for (String file : fileNames) {
            if (file.equals(nameToTest))
                return true;
        }
        return false;
    }

    static Intent getShareImageIntent(Context context, Bitmap bitmap) {
        boolean successfullySaved = saveBitmapToCacheDir(context, bitmap);
        if (!successfullySaved) return null;
        Uri imageUri = getUriForSavedImage(context);
        if (imageUri == null) return null;
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setDataAndType(imageUri, context.getContentResolver().getType(imageUri));
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        return shareIntent;
    }

    private static boolean saveBitmapToCacheDir(Context context, Bitmap bitmap) {
        try {
            File cachePath = new File(context.getCacheDir(), TEMP_CACHE_SUBDIR);
            //noinspection ResultOfMethodCallIgnored
            cachePath.mkdirs();
            FileOutputStream stream =
                    new FileOutputStream(cachePath + File.separator + TEMP_CACHE_FILENAME);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static Uri getUriForSavedImage(Context context) {
        File imagePath = new File(context.getCacheDir(), TEMP_CACHE_SUBDIR);
        File newFile = new File(imagePath, TEMP_CACHE_FILENAME);
        return FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, newFile);
    }
}
