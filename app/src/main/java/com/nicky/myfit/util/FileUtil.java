package com.nicky.myfit.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Created by nicholas on 1/18/15.
 */
public class FileUtil {

    Uri contentUri;

    boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    public FileUtil() {

    }

    public void setContentUri(Uri uri) {
        this.contentUri = uri;
    }

    public Uri getContentUri() {
        return contentUri;
    }

    public String getPath(Context context, Uri uri) {
        Log.v(" File -",
                "Authority: " + uri.getAuthority() +
                        ", Fragment: " + uri.getFragment() +
                        ", Port: " + uri.getPort() +
                        ", Query: " + uri.getQuery() +
                        ", Scheme: " + uri.getScheme() +
                        ", Host: " + uri.getHost() +
                        ", Segments: " + uri.getPathSegments().toString()
        );

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            return getRealPathFromUriKitKatPlus(context, uri);
        } else {
            return getRealPathFromUriMinusKitKat(context, uri);
        }
    }



    private String getRealPathFromUriKitKatPlus(Context context, Uri uri) {
        Cursor cursor = null;
        String wholeId = DocumentsContract.getDocumentId(uri);
        String id = wholeId.split(":")[1];

        try {
            String proj[] = {MediaStore.Images.Media.DATA};
            String sel = MediaStore.Images.Media._ID + "=?";
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    proj, sel, new String[]{id}, null);
            int column_index = cursor.getColumnIndexOrThrow(proj[0]);
            String filePath = "";
            if (cursor.moveToFirst()){
                filePath = cursor.getString(column_index);
            }
            cursor.close();
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    private String getRealPathFromUriMinusKitKat(Context context, Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        System.out.println(picturePath);
        return picturePath;
    }
}
