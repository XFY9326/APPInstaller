package tool.xfy9326.appinstaller.Methods;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;

import tool.xfy9326.appinstaller.R;

public class InstallMethod {

    public static void installApk(final Activity activity, final Dialog installDialog, final String apkPath, final String apkName, final Drawable apkIcon, final String pkgName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = IOMethod.prepareApk(activity, apkPath);
                if (!path.equals(IOMethod.FAILED)) {
                    try {
                        String cmd;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            int user_id = Process.myUserHandle().describeContents();
                            cmd = "pm install -r --user " + user_id + " ";
                        } else {
                            cmd = "pm install -r ";
                        }
                        String result = CommandMethod.runCommand(cmd + path.trim());
                        installDialog.cancel();
                        if (result.contains("\n")) {
                            String temp[] = result.split("\n");
                            result = temp[temp.length - 1];
                        }
                        String showText;
                        String showDetail = null;
                        String showPkg = null;
                        if (result.contains("Success")) {
                            showText = activity.getString(R.string.install_success);
                            showPkg = pkgName;
                        } else if (result.contains("Failure")) {
                            showText = activity.getString(R.string.install_failed);
                            showDetail = result.substring(result.indexOf("[") + 1, result.lastIndexOf("]"));
                        } else {
                            showText = activity.getString(R.string.install_failed);
                            showDetail = result;
                        }
                        showStatus(activity, installDialog, showText, showDetail, apkName, apkIcon, showPkg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showStatus(activity, installDialog, activity.getString(R.string.install_failed), ExceptionToString(e), apkName, apkIcon, null);
                    }
                    IOMethod.cleanInstallTemp(activity);
                }
            }
        }).start();
    }

    private static void showStatus(final Activity activity, Dialog install, final String text, final String detail, final String apkName, final Drawable apkIcon, final String pkgName) {
        install.cancel();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showStatusDialog(activity, text, detail, apkName, apkIcon, pkgName);
            }
        });
    }

    private static String ExceptionToString(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        e.printStackTrace(printWriter);
        printWriter.flush();
        stringWriter.flush();
        return stringWriter.toString();
    }

    private static void showStatusDialog(final Activity activity, String text, String detail, String apkName, Drawable apkIcon, final String pkgName) {
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_content_install_status, (ViewGroup) activity.findViewById(R.id.dialog_layout_install_status));
        TextView textView_status = view.findViewById(R.id.textView_install_status);
        textView_status.setText(text);
        if (detail != null) {
            TextView textView_detail = view.findViewById(R.id.textView_install_detail);
            textView_detail.setText(detail);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(apkName);
        builder.setIcon(apkIcon);
        if (pkgName != null) {
            PackageManager packageManager = activity.getPackageManager();
            final Intent intent = packageManager.getLaunchIntentForPackage(pkgName);
            if (intent != null) {
                builder.setPositiveButton(R.string.open, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.startActivity(intent);
                        activity.finish();
                    }
                });
            }
        }
        builder.setNegativeButton(R.string.complete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        builder.setNeutralButton(R.string.donate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse("https://QR.ALIPAY.COM/FKX04268ELODAA2RIXEW54");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                activity.startActivity(intent);
                activity.finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                activity.finish();
            }
        });
        builder.setView(view);
        builder.show();
    }
}
