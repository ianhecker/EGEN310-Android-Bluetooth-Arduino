package egen310.rccar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;


public class cBluetooth{

    public final static String TAG = "BL_4WD";

    private static BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private ConnectedThread mConnectedThread;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final Handler mHandler;
    public final static int BL_NOT_AVAILABLE = 1;
    public final static int BL_INCORRECT_ADDRESS = 2;
    public final static int BL_REQUEST_ENABLE = 3;
    public final static int BL_SOCKET_FAILED = 4;
    public final static int RECIEVE_MESSAGE = 5;

    cBluetooth(Context context, Handler handler){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;
        if (btAdapter == null) {
            mHandler.sendEmptyMessage(BL_NOT_AVAILABLE);
        }
    }

    public void checkBTState() {
        if(btAdapter == null) {
            mHandler.sendEmptyMessage(BL_NOT_AVAILABLE);
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "Bluetooth ON");
            } else {
                mHandler.sendEmptyMessage(BL_REQUEST_ENABLE);
            }
        }
    }

    public void BT_Connect(String address) {
        Log.d(TAG, "...On Resume...");

        if(!BluetoothAdapter.checkBluetoothAddress(address)){
            mHandler.sendEmptyMessage(BL_INCORRECT_ADDRESS);
        }
        else{
            BluetoothDevice device = btAdapter.getRemoteDevice(address);
            try {
                btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.d(TAG, "In onResume() and socket create failed: " + e.getMessage());
                mHandler.sendEmptyMessage(BL_SOCKET_FAILED);
                return;
            }

            btAdapter.cancelDiscovery();
            Log.d(TAG, "...Connecting...");
            try {
                btSocket.connect();
                Log.d(TAG, "...Connection ok...");
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    Log.d(TAG, "In onResume() and unable to close socket during connection failure" + e2.getMessage());
                    mHandler.sendEmptyMessage(BL_SOCKET_FAILED);
                    return;
                }
            }

            // Create a data stream so we can talk to server.
            Log.d(TAG, "...Create Socket...");

            try {
                outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                Log.d(TAG, "In onResume() and output stream creation failed:" + e.getMessage());
                mHandler.sendEmptyMessage(BL_SOCKET_FAILED);
                return;
            }

            mConnectedThread = new ConnectedThread();
            mConnectedThread.start();
        }
    }

    public void BT_onPause() {
        Log.d(TAG, "...On Pause...");
        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                Log.d(TAG, "In onPause() and failed to flush output stream: " + e.getMessage());
                mHandler.sendEmptyMessage(BL_SOCKET_FAILED);
                return;
            }
        }

        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                Log.d(TAG, "In onPause() and failed to close socket." + e2.getMessage());
                mHandler.sendEmptyMessage(BL_SOCKET_FAILED);
            }
        }
    }

    public void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.i(TAG, "Send data: " + message);

        if (outStream != null) {
            try {
                outStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "In onResume() and an exception occurred during write: " + e.getMessage());
                mHandler.sendEmptyMessage(BL_SOCKET_FAILED);
            }
        } else Log.d(TAG, "Error Send data: outStream is Null");
    }

    private class ConnectedThread extends Thread {

        private final InputStream mmInStream;

        public ConnectedThread() {
            InputStream tmpIn = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = btSocket.getInputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
    }
}
