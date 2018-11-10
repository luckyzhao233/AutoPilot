package com.example.hello_world;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    public boolean Send = false;

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

    String sLatitude;
    String sLongtitude;
    String time;

    /**
     * 发送消息到服务器 变量
     */
    // 输出流对象
    OutputStream outputStream;


    // 显示接收服务器消息 按钮
    private TextView receive_message;


    public SendLocation() throws UnsupportedEncodingException {
    }

    protected double[] onCreate(double Latitude,double Longitude,short FlowNum) {

        /**
         * 初始化操作
         */

        final MsgHandling mH = new MsgHandling();

        sLatitude = Latitude + "";
        sLongtitude = Longitude + "";


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
                        socket = new Socket("47.100.44.138", 8801);
                        while (true) {
                            sleep(100000);
                        }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        //接收服务器消息的线程
        class Thread2 implements Runnable{
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

                        response = br.readLine();
//                        response = Integer.toHexString(bytes[0]);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        //发送位置信息的线程
        class Thread3 implements Runnable{
            @Override
            public void run() {
                    try {
                        time = mH.insertTime(time);
                        String sLocationMsg = mH.start + sLatitude + "," + sLongtitude + "," + "1000"  + ">\\r\\n";
                        outputStream = socket.getOutputStream();
                        outputStream.write(sLocationMsg.getBytes("utf-8"));
                        outputStream.flush();
                        sleep(5000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }

        Thread thread1 = new Thread(new Thread1());
        thread1.start();
        sleep(1000);
        Thread thread3 = new Thread(new Thread3());
        thread3.start();
        Thread thread2 = new Thread(new Thread2());
        thread2.start();
        try {
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double[] cararray = {0,0,00,00,0,0,0,0,00,0,0,0,00,0,0,0,0,0,0,0,00,0,0,0,00};
        if(response != null) {      //如果收到了平台位置应答
            String[] strarray=response.split(",|>");//得到一个List数组
            int z = 2;
            cararray[0] = Double.parseDouble(strarray[1]);//最近站点的经纬度
            cararray[1] = Double.parseDouble(strarray[2]);
            for(int i = 0; ((3*i)+ 6) < strarray.length;i++){
                cararray[z] =  Double.parseDouble(strarray[3*i + 5]);//纬度
                cararray[z+1] =  Double.parseDouble(strarray[3*i+6]);//经度
                z = z+2;
            }
            Send = true;
            return cararray;
        }
        return cararray;
    }

}
