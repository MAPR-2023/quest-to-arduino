package com.hoho.android.usbserial.util;

// see source file: https://github.com/cvasquez-github/unity-android-serial-arduino/blob/main/MyUnityPlayerActivity.java

import android.app.Activity;
import android.content.Intent;

import android.app.PendingIntent;

import java.util.List;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.nio.charset.StandardCharsets;



public class UnityUsbSerial implements SerialInputOutputManager.Listener
{
    private static final int WRITE_WAIT_MILLIS = 200; //timeout timer
    private static final int READ_WAIT_MILLIS = 200; //timeout timer
    private static final String INTENT_ACTION_GRANT_USB = "com.cvs.pinball.USB_PERMISSION";

    private static Activity unityActivity;

//    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code
    private SerialInputOutputManager ioManager;
    private UsbSerialPort port=null;

    protected String updateUnityCommandLineArguments(String cmdLine)
    {
        return cmdLine;
    }

    public void Write(String line) {
        try
        {
            System.out.println("Writing to Robot Arm");
            port.write((line+"\n").getBytes(), WRITE_WAIT_MILLIS);
            port.setBreak(true);
            Thread.sleep(100);
            port.setBreak(false);
        }
        catch (Exception e)
        {
            System.out.println("Exception Writing line to Robot Arm");
        }
    }

    public void receiveUnityActivity(Activity activity)
    {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        super.onCreate(savedInstanceState);
//        String cmdLine = updateUnityCommandLineArguments(getIntent().getStringExtra("unity"));
//        getIntent().putExtra("unity", cmdLine);
//        mUnityPlayer = new UnityPlayer(this, this);
//        setContentView(mUnityPlayer);
//        mUnityPlayer.requestFocus();

        unityActivity = activity;

        try
        {
            System.out.println("Initializing Arduino USB Connection");
            UsbManager manager = (UsbManager) unityActivity.getSystemService(Activity.USB_SERVICE);
            List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
            if (availableDrivers.isEmpty()) {
                return;
            }
            UsbSerialDriver driver = availableDrivers.get(0);
            UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
            if (connection == null) {
                System.out.println("Can't open Arduino USB Serial Connection");
                PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(unityActivity, 0, new Intent(INTENT_ACTION_GRANT_USB), PendingIntent.FLAG_IMMUTABLE);
                manager.requestPermission(driver.getDevice(), usbPermissionIntent);
                return;
            }
            port = driver.getPorts().get(0);
            port.open(connection);
            port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            port.setDTR(true);
            port.setRTS(true);
            System.out.println("Initializing USB Serial Manager");
            ioManager = new SerialInputOutputManager(port, this);
            ioManager.start();

            Write("W10"); // testing to see if it works, reset to normal position

        }
        catch(UnsupportedOperationException ignored)
        {
            System.out.println("USB UnsupportedOperationException");
        }
        catch(Exception e)
        {
            System.out.println("USB General exception: "+e.getMessage());
        }
    }

    @Override
    public void onNewData(byte[] data) {
        String s = new String(data, StandardCharsets.UTF_8);
        System.out.println("Data from Robot Arm: "+s);
    }

    @Override
    public void onRunError(Exception e) {
        System.out.println("On Run Error: "+e.getMessage());
    }

//    @Override public void onUnityPlayerUnloaded() {
//        moveTaskToBack(true);
//    }
//
//    @Override public void onUnityPlayerQuitted() {
//    }

//    @Override protected void onNewIntent(Intent intent)
//    {
//        setIntent(intent);
//        mUnityPlayer.newIntent(intent);
//    }
//
//    @Override protected void onDestroy ()
//    {
//        mUnityPlayer.destroy();
//        if (port != null)
//        {
//            try {
//                port.close();
//            }
//            catch (Exception e)
//            {
//            }
//
//        }
//        super.onDestroy();
//    }
//
//    @Override protected void onStop()
//    {
//        super.onStop();
//
//        if (!MultiWindowSupport.getAllowResizableWindow(this))
//            return;
//
//        mUnityPlayer.pause();
//    }
//
//    @Override protected void onStart()
//    {
//        super.onStart();
//
//        if (!MultiWindowSupport.getAllowResizableWindow(this))
//            return;
//
//        mUnityPlayer.resume();
//    }
//
//    @Override protected void onPause()
//    {
//        super.onPause();
//
//        if (MultiWindowSupport.getAllowResizableWindow(this))
//            return;
//
//        mUnityPlayer.pause();
//    }
//
//    @Override protected void onResume()
//    {
//        super.onResume();
//
//        if (MultiWindowSupport.getAllowResizableWindow(this))
//            return;
//
//        mUnityPlayer.resume();
//    }
//
//    @Override public void onLowMemory()
//    {
//        super.onLowMemory();
//        mUnityPlayer.lowMemory();
//    }
//
//    @Override public void onTrimMemory(int level)
//    {
//        super.onTrimMemory(level);
//        if (level == TRIM_MEMORY_RUNNING_CRITICAL)
//        {
//            mUnityPlayer.lowMemory();
//        }
//    }
//
//    @Override public void onConfigurationChanged(Configuration newConfig)
//    {
//        super.onConfigurationChanged(newConfig);
//        mUnityPlayer.configurationChanged(newConfig);
//    }
//
//    @Override public void onWindowFocusChanged(boolean hasFocus)
//    {
//        super.onWindowFocusChanged(hasFocus);
//        mUnityPlayer.windowFocusChanged(hasFocus);
//    }
//
//    @Override public boolean dispatchKeyEvent(KeyEvent event)
//    {
//        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
//            return mUnityPlayer.injectEvent(event);
//        return super.dispatchKeyEvent(event);
//    }
//
//    @Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
//    @Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
//    @Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
//    /*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }
}
