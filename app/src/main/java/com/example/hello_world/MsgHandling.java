package com.example.hello_world;

import android.text.format.Time;

public class MsgHandling {      //将消息进行校验转义处理

    public byte[] RegisterMsg = {0x7e,0x01,0x00,0x00,0x36,0x01,(byte)0x86,0x55,0x03,0x72,0x59,
            0x00,0x02,//流水号
            //消息体------------------------------------------
            0x00,0x00,//省域ID
            0x00,0x00,//市域ID
            0x00,0x00,0x00,0x00,0x00,//制造商ID
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,//终端型号
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,//终端ID
            0x00,//车牌颜色
            0x30,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,//车牌标识，string类型的17个字节
            //消息体-------------------------------------------
            0x00,0x7e,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00//预留的多余长度，为了放多出来的转义字符
    };

    public byte[] JQMsg = {0x7e,0x01,0x02,0x00,0x09,
            0x01,(byte)0x86,0x55,0x03,0x72,0x59,
            0x00,0x01,
            //消息体------------------------------------------
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            //消息体------------------------------------------
            0x00,0x7e,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00//预留的多余长度，为了放多出来的转义字符
    };

    public byte[] LocationMsg = {0x7e,0x02,0x00,
            0x00,0x28,//消息体属性，中包含消息体长度
            0x01,(byte)0x86,0x55,0x03,0x72,0x59,
            0x00,0x33,//流水号
            //消息体------------------------------------------
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x00,(byte) 0x00,(byte)0x00,0x00,//纬度
            0x00,(byte)0x00,(byte)0x00,(byte)0x00,//经度
            0x00,0x00,0x00,0x00,0x00,0x00,0x18,0x07,0x02,0x19,0x25,0x07,//前面的高度，速度，方向，后面是时间
            0x20,0x0A,//附加id,长度
            0x00,0x00,0x00,0x01,0x00,0x00,0x00,0x00,0x00,0x00,//附加信息
            //消息体-------------------------------------------
            0x00,0x7e,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00//预留的多余长度，为了放多出来的转义字符
    };

    public byte[] HeartMsg = {0x7e,0x00,0x02,0x00,0x00,0x01,(byte)0x86,0x55,0x03,0x72,0x59,0x00,0x00,
            //消息体-为空-----------------------------------------

            //消息体-------------------------------------------
            0x00,0x7e,
            0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00//预留的多余长度，为了放多出来的转义字符
    };

    public final byte[] operate(byte[] M){

        //预留的用于放多出来的转义字符的大小
        int temp = 12;
        /*/
            求校验码
         */
        byte Check = 0x00;
        for (int i = 1; i < M.length - 1 - temp; i++) {
            Check ^= M[i];
            if (i == M.length - 2 - temp)
                M[i] = Check;
        }
        /*/
            转义
         */
        for (int i = 1; i < M.length - 1 - temp; ++i) {//在两个标志位中间找0x7e
            if (M[i] == 0x7e) {
                M[i] = 0x7d;
                for (int j = M.length - 1; j > i; j--) {
                    M[j] = M[j - 1]; //"7e"所在下标开始的元素后移一个位置
                }
                M[i + 1] = 0x02;
            }
        }
        /*/
            将预留位去除
         */
        int z, j = 0;
        for (z = 0; z < M.length - 1; ++z) {
            if (M[z] == 0x7e) {
                ++j;
                if (j == 2)
                    break;
            }
        }
        byte[] Msg = new byte[z + 1];//新建的用于存储截取后的数组的数组
        System.arraycopy(M, 0, Msg, 0, z + 1);
        return Msg;
    }

    //将服务器返回的鉴权码插入到鉴权消息中
    public final void JQ_operate(char[] M){
        JQMsg[13] = (byte)M[16];
        JQMsg[14] = (byte)M[17];
        JQMsg[15] = (byte)M[18];
        JQMsg[16] = (byte)M[19];
        JQMsg[17] = (byte)M[20];
        JQMsg[18] = (byte)M[21];
        JQMsg[19] = (byte)M[22];
        JQMsg[20] = (byte)M[23];
        JQMsg[21] = (byte)M[24];

    }

    //添加时间
    public void insertTime(byte[] LMsg){
        Time t=new Time();
        t.setToNow(); // 取得系统时间。
        int year = t.year;
        year = (year-2000)+((year-2000)/10)*6;
        int month = t.month+1;
        month=month+(month/10)*6;
        int day = t.monthDay;
        day = day + (day/10)*6;
        int hour = t.hour; // 0-23
        hour = hour + (hour/10)*6;
        int minute = t.minute;
        minute = minute + (minute/10)*6;
        int second = t.second;
        second = second + (second/10)*6;

        LMsg[35] = (byte)year;
        LMsg[36] = (byte)month;
        LMsg[37] = (byte)day;
        LMsg[38] = (byte)hour;
        LMsg[39] = (byte)minute;
        LMsg[40] = (byte)second;
    }
}
