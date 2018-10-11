package com.example.hello_world;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static android.os.SystemClock.sleep;

public class SendLocation extends AppCompatActivity {

    // 主线程Handler
    // 用于将从服务器获取的消息显示出来
    private Handler mMainHandler;

    // Socket变量
    private Socket socket;

    // 线程池
    // 为了方便展示,此处直接采用线程池进行线程管理,而没有一个个开线程
    private ExecutorService mThreadPool;

    /**
     * 接收服务器消息 变量
     */
    // 输入流对象
    InputStream is;

    // 输入流读取器对象
    InputStreamReader isr;
    BufferedReader br;

    // 接收服务器发送过来的消息
    String response;


    /**
     * 发送消息到服务器 变量
     */
    // 输出流对象
    OutputStream outputStream;


    // 显示接收服务器消息 按钮
    private TextView receive_message;


    public SendLocation() throws UnsupportedEncodingException {
    }

    protected void onCreate(double Latitude,double Longitude,short FlowNum) {

        /**
         * 初始化操作
         */

        final MsgHandling mH = new MsgHandling();

        final char[] rcvMsg = new char[1024]; // 用来接收数据的数组
        final boolean isRcved = true;
        int heartThreadNum = 0;
        int WhichMsg = 0;
        long Latitude1 = (long)(Latitude*1000000);
        long Longitude1 =(long)(Longitude*1000000);

        //将经纬度信息写入Msg中
        mH.LocationMsg[21]= (byte) (Latitude1 >> 24);
        mH.LocationMsg[22]= (byte) (Latitude1 >> 16);
        mH.LocationMsg[23]= (byte) (Latitude1 >> 8);
        mH.LocationMsg[24]= (byte) (Latitude1 >> 0);
        mH.LocationMsg[25]= (byte) (Longitude1 >> 24);
        mH.LocationMsg[26]= (byte) (Longitude1 >> 16);
        mH.LocationMsg[27]= (byte) (Longitude1 >> 8);
        mH.LocationMsg[28]= (byte) (Longitude1 >> 0);

        //将流水号写入Msg中
        mH.LocationMsg[11] = (byte) (FlowNum >> 8);
        mH.LocationMsg[12] = (byte) (FlowNum >> 0);

        // 实例化主线程,用于更新接收过来的消息
        mMainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        receive_message.setText(response);
                        break;
                }
            }
        };


        //连接服务器的线程
        class Thread1 implements Runnable{
            @Override
            public void run() {

                try {

//                    sleep(100);
                        // 创建Socket对象 & 指定服务端的IP 及 端口号
                        socket = new Socket("192.168.254.134", 8801);
                        sleep(100000);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        //发送注册信息的线程
        class Thread2 implements Runnable{
            @Override
            public void run() {

                try {

                    byte[] RMsg = mH.operate(mH.RegisterMsg);//注册消息
//                    byte[] JMsg = mH.operate(mH.JQMsg);//鉴权消息
//                    byte[] LMsg = mH.operate(mH.LocationMsg);//位置消息

                    // 步骤1：从Socket 获得输出流对象OutputStream
                    // 该对象作用：发送数据
                    outputStream = socket.getOutputStream();

                    // 步骤2：写入需要发送的数据到输出流对象中
//                            outputStream.write((mEdit.getText().toString() + "\n").getBytes("utf-8"));
                    // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞

                    outputStream.write(RMsg);

                    // 步骤3：发送数据到服务端
                    outputStream.flush();


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }

        //接收服务器消息的线程
        class Thread3 implements Runnable{
            @Override
            public void run() {

                try {

                        // 步骤1：创建输入流对象InputStream
                        is = socket.getInputStream();

                        // 步骤2：创建输入流读取器对象 并传入输入流对象
                        // 该对象作用：获取服务器返回的数据
                        isr = new InputStreamReader(is);
                        br = new BufferedReader(isr);


                        // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
                        br.read(rcvMsg);
//                        response = Integer.toHexString(bytes[0]);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }



        }

        //发送鉴权信息的线程
        class Thread4 implements Runnable{
            @Override
            public void run() {
                try {
                    byte[] JMsg = mH.operate(mH.JQMsg);//鉴权消息
                    outputStream = socket.getOutputStream();
                    outputStream.write(JMsg);
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //发送位置信息的线程
        class Thread5 implements Runnable{
            @Override
            public void run() {
                try {
                    byte[] LMsg = mH.operate(mH.LocationMsg);//鉴权消息
                    outputStream = socket.getOutputStream();
                    outputStream.write(LMsg);
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//        //发送心跳包的线程
//        if(heartThreadNum == 0) {
//            heartThreadNum = 1;
//            class Thread4 implements Runnable{
//                @Override
//                public void run() {
//                    short FlowNum = -1;
//                    while (true) {
//                        try {
//                            //每5s发送一个心跳包
//                            sleep(5000);
//                            FlowNum++;
//                            mH.HeartMsg[11] = (byte) (FlowNum >> 8);
//                            mH.HeartMsg[12] = (byte) (FlowNum >> 0);
//                            byte[] Msg = mH.operate(mH.HeartMsg);
//                            outputStream = socket.getOutputStream();
//                            outputStream.write(Msg);
//                            outputStream.flush();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }

        Thread thread1 = new Thread(new Thread1());
        thread1.start();
        sleep(100);
        Thread thread2 = new Thread(new Thread2());
        thread2.start();
        Thread thread3_0 = new Thread(new Thread3());
        thread3_0.start();
        try {
            thread3_0.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(rcvMsg[28] == 0x7e) {
            Thread thread4 = new Thread(new Thread4());
            thread4.start();
        }
        Thread thread3_1 = new Thread(new Thread3());
        thread3_1.start();
        try {
            thread3_1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(rcvMsg[19] == 0x7e) {
            Thread thread5 = new Thread(new Thread5());
            thread5.start();
        }


        /**
         * 断开客户端 & 服务器的连接
         */
//        btnDisconnect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                try {
//                    // 断开 客户端发送到服务器 的连接，即关闭输出流对象OutputStream
//                    outputStream.close();
//
//                    // 断开 服务器发送到客户端 的连接，即关闭输入流读取器对象BufferedReader
//                    br.close();
//
//                    // 最终关闭整个Socket连接
//                    socket.close();
//
//                    // 判断客户端和服务器是否已经断开连接
//                    System.out.println(socket.isConnected());
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });

    }

}
