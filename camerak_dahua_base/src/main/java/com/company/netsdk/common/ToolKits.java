package com.company.netsdk.common;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.company.NetSDK.FinalVar;
import com.company.NetSDK.INetSDK;
import com.company.netsdk.R;
import com.company.netsdk.activity.MainActivity;
import com.company.netsdk.activity.NetSDKApplication;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 29779 on 2017/4/10.
 */
public class ToolKits {
    public static void showMessage(Context context , String strLog)
    {
        Toast.makeText(context, strLog, Toast.LENGTH_SHORT).show();
    }

    public static void showErrorMessage(Context context , String strLog)
    {
        Toast.makeText(context, strLog + ToolKits.getLastError(),
                      Toast.LENGTH_SHORT).show();
    }

    public static void writeLog(String strLog)
    {
        Log.d("NetSDK Demo", strLog);
    }

    public static void writeErrorLog(String strLog)
    {
        Log.e("NetSDK Demo", strLog + ToolKits.getLastError());
    }

    public static boolean SetDevConfig(String strCmd ,  Object cmdObject , long hHandle , int nChn , int nBufferLen )
    {
        boolean result = false;
        Integer error = new Integer(0);
        Integer restart = new Integer(0);
        char szBuffer[] = new char[nBufferLen];
        for(int i=0; i<nBufferLen; i++)szBuffer[i]=0;

        if(INetSDK.PacketData(strCmd, cmdObject, szBuffer, nBufferLen))
        {
            if( INetSDK.SetNewDevConfig(hHandle,strCmd , nChn , szBuffer, nBufferLen, error, restart, 10000))
            {
                result = true;
            }
            else
            {
                writeErrorLog("Set " + strCmd + " Config Failed!");
                result = false;
            }
        }
        else
        {
            writeErrorLog("Packet " + strCmd + " Config Failed!");
            result = false;
        }

        return result;
    }

    public static boolean GetDevConfig(String strCmd ,  Object cmdObject , long hHandle , int nChn , int nBufferLen)
    {
        boolean result = false;
        Integer error = new Integer(0);
        char szBuffer[] = new char[nBufferLen];

        if(INetSDK.GetNewDevConfig( hHandle, strCmd , nChn, szBuffer,nBufferLen,error,10000) )
        {
            if( INetSDK.ParseData(strCmd ,szBuffer , cmdObject , null ) )
            {
                result = true;
            }
            else
            {
                writeErrorLog("Parse " + strCmd + " Config Failed!");
                result = false;
            }
        }
        else
        {
            writeErrorLog("Get" + strCmd + " Config Failed!");
            result = false;
        }
        return result;
    }

    public static String CharArrayToString(char[] szIn , String strMode){
        try {
            byte[] tmpByte = new byte[FinalVar.SDK_NEW_USER_NAME_LENGTH];
            for( int i=0 ; i<szIn.length ; i++)
            {
                tmpByte[i] = (byte) szIn[i];
            }

            return new String(tmpByte , strMode);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public static char[] StringToCharArray(String strIn, String strMode) {
        try {
            byte[] tempByte = strIn.getBytes(strMode);
            char[] cOut = new char[FinalVar.SDK_NEW_USER_NAME_LENGTH];
            for (int i = 0; i < tempByte.length; i++) {
                cOut[i] = (char)tempByte[i];
            }
            return cOut;
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        return null;
    }

    public static String ByteArrayToString(byte[] szIn){
        int i = 0;
        for (i = 0; i < szIn.length; i++) {
            if (0 == (byte)szIn[i]) {
                break;
            }
        }

        if (i > 0) {
            return new String(szIn, 0, i);
        }

        return null;
    }

    //createFile
    public static boolean createFile(String strPath, String strFile) {
        File path = new File(strPath);
        if (!path.exists()) {
            try {
                if (!path.mkdirs()) {
                    ToolKits.writeLog("App can't create path " + strPath);
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        File file = new File(strPath + strFile);
        if (file.exists()) {
            file.delete();
        }

        try {
            if (!file.createNewFile()) {
                ToolKits.writeLog("App can't crete file " + strPath + strFile);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    // ????????????????????????
    public static void verifyMainPermissions(Activity activity) {
        try {
            List <String> lstPermissions = new ArrayList<>();
            // ???????????????????????????
            int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(permission != PackageManager.PERMISSION_GRANTED) { // ??????????????????
                lstPermissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                lstPermissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            // ???????????????GPS?????????
            if (Build.VERSION.SDK_INT >= 23) { // Android 6.0+
                permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION);
                if (permission != PackageManager.PERMISSION_GRANTED) {  // ??????GPS?????????
                    lstPermissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
                    lstPermissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                }
            }

            if (!lstPermissions.isEmpty()) { // ??????????????????, ??????????????????
                ActivityCompat.requestPermissions(activity, lstPermissions.toArray(new String[lstPermissions.size()]), 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void verifyCameraPermissions(Activity activity) {
        try {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                // ??????????????????
                if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    String[] per = {Manifest.permission.CAMERA};
                    ActivityCompat.requestPermissions(activity, per,123);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void verifyRecordPermissions(Activity activity) {
        try {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                // ??????????????????
                if(ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED){
                    String[] per = {android.Manifest.permission.RECORD_AUDIO};
                    ActivityCompat.requestPermissions(activity, per,123);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class SimpleAsyncTask<T> extends AsyncTask<Void, Integer, T>  {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected T  doInBackground(Void... params) {
            return null;
        }
        @Override
        protected void onPostExecute(T result) {
        }
    };

    public static void alertDisconnected(){
        final Context context = NetSDKApplication.ActivityManager.getManager().getTopActivity();
        if (context == null)
            return;
        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.device_disconnected);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(context,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                Activity activity = NetSDKApplication.ActivityManager.getManager().getTopActivity();

                if (activity != null) {
                    activity.finish();
                }

            }
        });
        builder.show();
    }

    ///??????EditText???????????????
    public static void limitEditTextNumberRange(final EditText editText, final int MIN_NUMBER, final int MAX_NUMBER) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(start > 1) {
                    if(MIN_NUMBER != -1 && MAX_NUMBER != -1) {
                        int number = Integer.parseInt(s.toString());
                        if(number > MAX_NUMBER) {
                            editText.setText(String.valueOf(MAX_NUMBER));
                        }
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s != null && !s.equals("")) {
                    if(MIN_NUMBER != -1 && MAX_NUMBER != -1) {
                        int num = 0;
                        try {
                            num = Integer.valueOf(s.toString());
                        } catch (NumberFormatException e) {
                            num = 0;
                        }
                        if(num > MAX_NUMBER){
                            editText.setText(String.valueOf(MAX_NUMBER));
                        }
                        return;
                    }
                }
            }
        });
    }

    // ???byte?????????????????????byte????????????7???????????????
    public static byte[] getByteArray(byte b){
        byte[] array = new byte[8];
        for(int i = 7; i >= 0; i--) {
            array[i] = (byte)(b & 1);
            b = (byte)(b >> 1);
        }
        return array;
    }

    // ???byte?????????????????????byte????????????0???????????????
    public static byte[] getByteArrayEx(byte b){
        byte[] array = new byte[8];
        for (int i = 0; i < 8; i++) {
            array[i] = (byte) ((b & (1 << i)) > 0 ? 1:0);
        }

        return array;
    }

    // ????????????
    public static void StringToByteArray(String src, byte[] dst) {
        for(int i = 0; i < dst.length; i++) {
            dst[i] = 0;
        }

        // ?????????src????????? ?????? dst??????????????????????????? ?????????src?????????
        // ?????????src????????? ????????? dst??????????????????????????? dst????????? - 1
        if(src.getBytes().length < dst.length) {
            System.arraycopy(src.getBytes(), 0, dst, 0, src.getBytes().length);
        } else {
            System.arraycopy(src.getBytes(), 0, dst, 0, dst.length - 1);
        }
    }

    ///from char[] to byte[]
    ///char????????????byte??????
    public static byte[] getBytes(char[] chars){
        Charset charset = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = charset.encode(cb);
        return bb.array();
    }

    // ???????????????, ???????????????  FinalVar.java
    public static String getLastError() {
        return  "FinalVar.java ?????????????????? ???[0x80000000|" + (INetSDK.GetLastError() & 0x7fffffff) + "]" + "\n" +
                 "????????????????????? ???[" + String.format("%x", INetSDK.GetLastError()) + "]";
    }

    /**
     * ?????????
     * @param context
     * @param message ????????????
     * @param bln  ??????????????????
     * @param cancelListener ????????????
     * @return
     */
    public static ProgressDialog showProgressDialog(Context context, String message, boolean bln, DialogInterface.OnCancelListener cancelListener) {
        ProgressDialog mProgressDialog = ProgressDialog.show(context, null, message, false, bln, cancelListener);
        return mProgressDialog;
    }
}