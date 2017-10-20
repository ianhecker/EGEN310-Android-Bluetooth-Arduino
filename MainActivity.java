package egen310.rccar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

    private cBluetooth bl = null;

    private int motorLeft = 0;
    private int motorRight = 0;
    private String address;
    private int pwmBtnMotorLeft;
    private int pwmBtnMotorRight;
    private String commandLeft;
    private String commandRight;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        address = "SOME STRING OF MAC ADDRESS HERE";
        pwmBtnMotorLeft = 20;
        pwmBtnMotorRight = 20;
        commandLeft = "L";
        commandRight = "R";

        bl = new cBluetooth(this, mHandler);
        bl.checkBTState();

        Button buttonForward = (Button)findViewById(R.id.forward);
            buttonForward.setOnClickListener(this);

        Button buttonBackward = (Button)findViewById(R.id.backward);
            buttonBackward.setOnClickListener(this);

        Button buttonRight = (Button)findViewById(R.id.right);
            buttonRight.setOnClickListener(this);

        Button buttonLeft = (Button)findViewById(R.id.left);
            buttonLeft.setOnClickListener(this);


        buttonForward.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE) {
                    motorLeft = pwmBtnMotorLeft;
                    motorRight = pwmBtnMotorRight;
                    bl.sendData(String.valueOf(commandLeft+motorLeft+"\r"+commandRight+motorRight+"\r"));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    motorLeft = 0;
                    motorRight = 0;
                    bl.sendData(String.valueOf(commandLeft+motorLeft+"\r"+commandRight+motorRight+"\r"));
                }
                return false;
            }
        });

        buttonLeft.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE) {
                    motorLeft = -pwmBtnMotorLeft;
                    motorRight = pwmBtnMotorRight;
                    bl.sendData(String.valueOf(commandLeft+motorLeft+"\r"+commandRight+motorRight+"\r"));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    motorLeft = 0;
                    motorRight = 0;
                    bl.sendData(String.valueOf(commandLeft+motorLeft+"\r"+commandRight+motorRight+"\r"));
                }
                return false;
            }
        });

        buttonRight.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE) {
                    motorLeft = pwmBtnMotorLeft;
                    motorRight = -pwmBtnMotorRight;
                    bl.sendData(String.valueOf(commandLeft+motorLeft+"\r"+commandRight+motorRight+"\r"));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    motorLeft = 0;
                    motorRight = 0;
                    bl.sendData(String.valueOf(commandLeft+motorLeft+"\r"+commandRight+motorRight+"\r"));
                }
                return false;
            }
        });

        buttonBackward.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE) {
                    motorLeft = -pwmBtnMotorLeft;
                    motorRight = -pwmBtnMotorRight;
                    bl.sendData(String.valueOf(commandLeft+motorLeft+"\r"+commandRight+motorRight+"\r"));
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    motorLeft = 0;
                    motorRight = 0;
                    bl.sendData(String.valueOf(commandLeft+motorLeft+"\r"+commandRight+motorRight+"\r"));
                }
                return false;
            }
        });

    }

    private final Handler mHandler =  new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case cBluetooth.BL_NOT_AVAILABLE:
                    Log.d(cBluetooth.TAG, "Bluetooth is not available. Exit");
                    Toast.makeText(getBaseContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case cBluetooth.BL_INCORRECT_ADDRESS:
                    Log.d(cBluetooth.TAG, "Incorrect MAC address");
                    Toast.makeText(getBaseContext(), "Incorrect Bluetooth address", Toast.LENGTH_SHORT).show();
                    break;
                case cBluetooth.BL_REQUEST_ENABLE:
                    Log.d(cBluetooth.TAG, "Request Bluetooth Enable");
                    BluetoothAdapter.getDefaultAdapter();
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                    break;
                case cBluetooth.BL_SOCKET_FAILED:
                    Toast.makeText(getBaseContext(), "Socket failed", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        };
    };

    @Override
    protected void onResume() {
        super.onResume();
        bl.BT_Connect(address);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bl.BT_onPause();
    }

    @Override
    public void onClick(View v) {

    }
}
