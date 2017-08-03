package com.example.cpufreuencyformiui;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by 秦聪 on 2017/8/2.
 */

public class FxService extends Service {
    LinearLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    WindowManager mWindowManager;
    private MyReceiver myReceiver;


    private static final String TAG = "FxService";

    @Override
    public void onCreate() {
        super.onCreate();
        myReceiver=new MyReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("close_window");
        registerReceiver(myReceiver,intentFilter);
        createFloatView();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void removeFloatView() {
        if(mWindowManager!=null && mFloatLayout!=null) {
            mWindowManager.removeView(mFloatLayout);
        }

    }
    private void createFloatView() {
        Log.w("qc1", "creatFloatView");
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        wmParams.format = PixelFormat.RGBA_8888;
        //wmParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 300;
        wmParams.y = 0;

        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        mWindowManager.addView(mFloatLayout, wmParams);

        final TextView mFloatTextView = (TextView) mFloatLayout.findViewById(R.id.text1);
        mFloatTextView.setText("qincong");

        final Handler mHandler = new Handler() {
            public void handleMessage(Message msg) {
                Bundle b = msg.getData();
                if (b.getString("cpu0") != null && b.getString("cpu2") != null) {
                    String s = b.getString("cpu0");
                    Integer cpu0 = (Integer.parseInt(s)) / 1000;
                    s = b.getString("cpu2");
                    Integer cpu2 = (Integer.parseInt(s)) / 1000;

                    mFloatTextView.setText(cpu0.toString());
                    ((TextView) mFloatLayout.findViewById(R.id.text2)).setText(cpu2.toString());
                }
            }
        };


        new Thread(new Runnable() {
            @Override
            public void run() {

                java.lang.Process suProcess;
                try {
                    suProcess = Runtime.getRuntime().exec("su");

                    DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
                    DataInputStream osRes = new DataInputStream(suProcess.getInputStream());
                    Bundle b = new Bundle();
                    if (null != os && null != osRes) {
                        // Getting the id of the current user to check if this is root
                        String res;
                        while (true) {
                            try{
                            os.writeBytes("cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq\n");
                            os.flush();
                            res = osRes.readLine();
                            b.putString("cpu0", res);

                            os.writeBytes("cat /sys/devices/system/cpu/cpu2/cpufreq/scaling_cur_freq\n");
                            os.flush();
                            res = osRes.readLine();
                            b.putString("cpu2", res);
                            Message msg = mHandler.obtainMessage();
                            msg.setData(b);
                            mHandler.sendMessage(msg);

                            Thread.sleep(1000);}
                            catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();


        mFloatLayout.setOnTouchListener(new View.OnTouchListener()

        {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                wmParams.x = (int) event.getRawX() - mFloatLayout.getMeasuredWidth() / 2;
                Log.i(TAG, "RawX" + event.getRawX());
                Log.i(TAG, "X" + event.getX());
                wmParams.y = (int) event.getRawY() - mFloatLayout.getMeasuredHeight() / 2;
                Log.i(TAG, "RawY" + event.getRawY());
                Log.i(TAG, "Y" + event.getY());
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                return false;
            }
        });

    }
    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent) {
            removeFloatView();
        }
    }
}

//        try {
//            suProcess = Runtime.getRuntime().exec("su");
//
//            DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
//            DataInputStream osRes = new DataInputStream(suProcess.getInputStream());
//
//            if (null != os && null != osRes) {
//                // Getting the id of the current user to check if this is root
//                //os.writeBytes("cat /sys/devices/system/cpu/cpu2/cpufreq/scaling_cur_freq\n");
//                os.writeBytes("pwd\n");
//                os.flush();
//
//                String currUid = osRes.readLine();
//
//                int i=3;
//                i=4;
//                boolean exitSu = false;
//            }
//        }catch (Exception e) {
//            int i;
//            i=1;
//        }


//                String res = "null";
//                File file = new File(path);//创建一个文件对象
//                try {
//                    FileInputStream inStream = new FileInputStream(file);//读文件
//                    byte[] buffer = new byte[1024];//缓存
//                    int len = 0;
//                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//                    while ((len = inStream.read(buffer)) != -1) {//直到读到文件结束
//                        outStream.write(buffer, 0, len);
//                    }
//                    byte[] data = outStream.toByteArray();//得到文件的二进制数据
//                    outStream.close();
//                    inStream.close();
//                    Log.i(TAG, new String(data));
//                    res = new String(data);
//                } catch (Exception e) {
//                    int i = 1;
//                    i = 3;
//                }
//                //Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
//                return res;
//
//            }
//
//            private String getCpu2() {
//                java.lang.Process suProcess;
//                String res = "null";
//                try {
//                    suProcess = Runtime.getRuntime().exec("su");
//
//                    DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
//                    DataInputStream osRes = new DataInputStream(suProcess.getInputStream());
//
//                    if (null != os && null != osRes) {
//                        // Getting the id of the current user to check if this is root
//                        os.writeBytes("cat /sys/devices/system/cpu/cpu2/cpufreq/scaling_cur_freq\n");
//                        os.flush();
//
//                        res = osRes.readLine();
//                        os.writeBytes("exit\n");
//                        os.flush();
//                    }
//                    suProcess.destroy();
//                } catch (Exception e) {
//                }
//                return res;
//            }
