package net.studymongolian.chimee;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import net.studymongolian.mongollibrary.MongolAlertDialog;

public class PermissionsHelper {


    static final int WRITE_EXTERNAL_STORAGE_REQUEST = 100;

    static boolean getWriteExternalStoragePermission(Activity activity) {
        // get permission to write to external storage
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST);
            return false;
        }
        return true;
    }

    static boolean isWritePermissionRequestGranted(int requestCode, int[] grantResults) {
        return requestCode == WRITE_EXTERNAL_STORAGE_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    static void notifyUserThatTheyCantSaveFileWithoutWritePermission(Context context) {
        MongolAlertDialog.Builder builder = new MongolAlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.no_write_file_permission));
        builder.setPositiveButton(context.getString(R.string.dialog_got_it), null);
        MongolAlertDialog dialog = builder.create();
        dialog.show();
    }
}