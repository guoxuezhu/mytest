package com.zhqz.faces.utils.faceUtil;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LicenseUtils {
    /**
     * 从assets目录拷贝license文件到应用私有目录<br>
     * copy license file from assets to application-specific directories on the
     * primary shared/external storage
     *
     * @param context 应用上下文<br>
     *                application or activity context
     * @return 本地license文件路径<br>
     * local license file path
     * @throws IOException 文件操作过程的IO异常<br>
     *                     the IO exception of file operation
     */
    public static String copyLicenseFile(Context context) throws IOException {
        String path = null;
        String licenseFileName = null;
        licenseFileName = getLicenseFileName(context);
        if (licenseFileName == null) {
            throw new FileNotFoundException("No suitable License File ends with .lic in assets dir");
        }
        path = "/sdcard/sensetime" + File.separator + licenseFileName;
        InputStream in = null;
        OutputStream out = null;
        File licenseFile = new File(path);
        try {
            if (!licenseFile.exists()) {
                licenseFile.createNewFile();
                in = context.getApplicationContext().getAssets().open(licenseFileName);
                out = new FileOutputStream(licenseFile);
                byte[] buffer = new byte[4096];
                int n;
                while ((n = in.read(buffer)) > 0) {
                    out.write(buffer, 0, n);
                }
            }
        } catch (IOException e) {
            licenseFile.delete();
            throw e;
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
        return path;
    }

    public static String getLicenseFilePath(Context context) {
        String path = null;
        path = context.getExternalFilesDir(null).getAbsolutePath();
        return path;
    }

    /**
     * 获取assets目录下的license文件名<br>
     * get license file from assets directory
     *
     * @param context 应用上下文<br>
     *                application or activity context
     * @return 授权文件名<br>
     * license name
     * @throws IOException 文件操作过程的IO异常<br>
     *                     the IO exception of file operation
     */
    private static String getLicenseFileName(Context context) throws IOException {
        String licenseFileName = null;
        String[] fileList = context.getAssets().list("");
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].endsWith(".lic")) {
                licenseFileName = fileList[i];
                break;
            }
        }
        return licenseFileName;
    }

    /**
     * 读取assets下license文件的内容<br>
     * read license content to string
     *
     * @param context 应用上下文<br>
     *                application or activity context
     * @return license字符串<br>
     * the string license
     * @throws IOException 文件操作过程的IO异常<br>
     *                     the IO exception of file operation
     */
    public static String readLicenseFromAssets(Context context) throws IOException {
        String res = "";
        String licenseFileName = null;
        licenseFileName = getLicenseFileName(context);
        if (licenseFileName == null) {
            throw new FileNotFoundException("No suitable License File ends with .lic  in assets dir");
        }
        InputStream in = context.getResources().getAssets().open(licenseFileName);
        int length = in.available();
        byte[] buffer = new byte[length];
        try {
            in.read(buffer);
        } catch (IOException e) {
            throw e;
        } finally {
            if (in != null) {
                in.close();
            }
        }
        res = new String(buffer, "UTF-8");
        return res;
    }
}
