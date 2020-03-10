package com.yocto.wetodo.auto_backup;

import android.app.ActivityManager;
import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.content.Context;
import android.os.ParcelFileDescriptor;

import java.io.IOException;
import java.util.List;

import static com.yocto.wetodo.Constants.PACKAGE_NAME;

public class CustomBackupAgent extends BackupAgent {
    private Boolean isRestoreFinished = false;

    @Override
    public void onBackup(ParcelFileDescriptor parcelFileDescriptor, BackupDataOutput backupDataOutput, ParcelFileDescriptor parcelFileDescriptor1) throws IOException {

    }

    @Override
    public void onRestore(BackupDataInput backupDataInput, int i, ParcelFileDescriptor parcelFileDescriptor) throws IOException {

    }

    @Override
    public void onRestoreFinished() {
        super.onRestoreFinished();

        isRestoreFinished = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isRestoreFinished) {
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

            if (activityManager != null) {
                final List<ActivityManager.RunningAppProcessInfo> runningServices = activityManager.getRunningAppProcesses();

                if (runningServices != null &&
                        runningServices.size() > 0 &&
                        PACKAGE_NAME.equals(runningServices.get(0).processName)
                ) {
                    android.os.Process.killProcess(runningServices.get(0).pid);
                }
            }
        }
    }
}
