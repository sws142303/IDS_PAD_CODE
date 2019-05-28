package com.juphoon.lemon.ui;

import java.io.File;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.juphoon.lemon.MtcVer;

public class MtcCrashHandler implements UncaughtExceptionHandler {

    private static String mLogDir;
    private static String mVersion;

    public MtcCrashHandler(String logDir, String version) {
        mLogDir = logDir;
        mVersion = version;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        PrintStream printStream = null;

        try {
            printStream = new PrintStream(getCrashFile());
            ex.printStackTrace(printStream);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            if (printStream != null) {
                printStream.close();
            }
        }

        exit();
    }

    private static File getCrashFile() {
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH.mm.ss", Locale.getDefault()).format(new Date());
        String version = "_Version_" + mVersion;
        String lemonVersion = "_LemonVersion_" + MtcVer.Mtc_GetVersion();
        String avatarVersion = "_AvatarVersion_" + MtcVer.Mtc_GetAvatarVersion();
        return new File(mLogDir + File.separator + timeStamp + version + lemonVersion + avatarVersion + "_crash.txt");
    }

    private static void exit() {
        // android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
