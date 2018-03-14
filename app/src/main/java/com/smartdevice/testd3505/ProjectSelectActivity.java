package com.smartdevice.testd3505;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.smartdevicesdk.printer.PrinterClassSerialPort3505;
import com.smartdevicesdk.printer.PrinterCommand;

/**
 * Created by lgx on 2018/3/13.
 */

public class ProjectSelectActivity extends Activity {

    private Spinner spinner1, spinner2, spinner3;
    private String spinnerNum1, spinnerNum2, spinnerNum3;
    private Button printerBtn;

    private PrinterClassSerialPort3505 printerClass = null;
    private String device = "/dev/ttyUSB1";
    private int baudrate = 115200;// 38400
    private boolean close_printer = true;
    private String[] arrayString;
    private Thread autoprint_Thread;

    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PrinterCommand.MESSAGE_READ:
                case PrinterCommand.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case PrinterCommand.SUCCESS_CONNECT:
                            printerClass.write(new byte[]{0x1b, 0x2b});
                            Toast.makeText(getApplicationContext(),
                                    "SUCCESS_CONNECT", Toast.LENGTH_SHORT).show();
                            break;
                        case PrinterCommand.FAILED_CONNECT:
                            Toast.makeText(getApplicationContext(),
                                    "FAILED_CONNECT", Toast.LENGTH_SHORT).show();

                            break;
                        case PrinterCommand.LOSE_CONNECT:
                            Toast.makeText(getApplicationContext(), "LOSE_CONNECT",
                                    Toast.LENGTH_SHORT).show();
                    }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_select);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner3 = (Spinner) findViewById(R.id.spinner3);
        printerBtn = (Button) findViewById(R.id.printerBtn);

        setOnClick();

        // 初始化打印类 Initialization print class
        printerClass = new PrinterClassSerialPort3505(device, baudrate, mhandler);
        // 启动自动打印线程 start the thread to auto print
        autoprint_Thread = new AutoPrintThread();
        autoprint_Thread.start();


        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mBatInfoReceiver, filter);

        arrayString = getResources().getStringArray(R.array.spinner_array);

        openAndcloseDevice();
    }


    private void openAndcloseDevice() {
        if (printerClass.mSerialPort.isOpen) {
            printerClass.close();
        } else {
            printerClass.device = device;
            printerClass.baudrate = baudrate;
            printerClass.open();
            printerClass.write(new byte[]{0x1b, 0x76});
        }
    }

    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                printerClass.device = device;
                printerClass.baudrate = baudrate;
                printerClass.open();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                printerClass.close();
            }
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        close_printer = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (close_printer) {
            printerClass.close();
        }
    }

    private void setOnClick() {
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerNum1 = arrayString[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerNum2 = arrayString[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerNum3 = arrayString[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        printerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printer();
            }
        });
    }

    @Override
    public void onBackPressed() {
        printerClass.close();
        super.onBackPressed();
    }

    private void printer() {
        printerClass.printText("茶叶      " + spinnerNum1 + "两");
        printerClass.printText("茶杯     " + spinnerNum2 + "个");
        printerClass.printText("餐巾纸      " + spinnerNum3 + "包");
    }

    class AutoPrintThread extends Thread {
        @Override
        public void run() {
            super.run();
            for (int i = 0; i < 10; i++) {
                printerClass.write(new byte[]{0x1b, 0x76});
            }
            printerClass.printUnicode("H7UJ787-JU78UU6-JJ785J6-J876K76\n");
            printerClass.write(PrinterCommand.CMD_FONTSIZE_DOUBLE);
            printerClass.printUnicode("Quick Lotto (5/11)\n");
            printerClass.printUnicode("Terminal No: 85010002    SN:1\n");
            printerClass.printUnicode("Draw No: 20160503011\n");
            printerClass.printUnicode("Option3\n");
            printerClass.write(PrinterCommand.CMD_FONTSIZE_NORMAL);
            printerClass.printUnicode("A. 02 06 08 ₦200\n");
            printerClass.printUnicode("B. 02 06 08 ₦200\n");
            printerClass.printUnicode("C. 02 06 08 ₦200\n");
            printerClass.printUnicode("D. 02 06 08 ₦200\n");
            printerClass.printUnicode("E. 02 06 08 ₦200\n");
            printerClass.printUnicode("\n\n");
            printerClass.write(PrinterCommand.CMD_FONTSIZE_NORMAL);

        }
    }

}
