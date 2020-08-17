package com.example.mqttdemo;

import android.util.Log;
import org.eclipse.paho.client.mqttv3.*;

public class PushCallback implements MqttCallback {

    @Override
    public void connectionLost(Throwable cause) {

        Log.e(getClass().getSimpleName(), "客户端与mqtt服务端断开");

    }

    @Override
    public void messageArrived(String topic, final MqttMessage message) throws Exception {

        String msg=new String(message.getPayload());

        Log.e(getClass().getSimpleName(), "回调方法:收到新消息");

        if ("掉线重连".equals(String.valueOf(message.getPayload()))){
            Log.e("订阅主题topic", topic);
            Log.e("消息内容msg", msg);
            Log.e("消息级别qos", message.getQos() + "");
        }else {
            Log.e("订阅主题topic", topic);
            Log.e("消息内容msg", msg);
            Log.e("消息级别qos", message.getQos() + "");
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

        Log.e(getClass().getSimpleName(), "回调方法:成功发出了消息!");

    }








}
