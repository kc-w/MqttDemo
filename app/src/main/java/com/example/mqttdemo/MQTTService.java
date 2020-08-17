package com.example.mqttdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTService extends Service {
    //消息服务器的URL
    public static final String BROKER_URL = "tcp://192.168.1.126:1883";
    //客户端ID，用来标识一个客户，可以根据不同的策略来生成
    public static final String clientId = "android-clientID1";
    //订阅的主题名
    public static final String TOPIC = "主题3";
    //mqtt客户端类
    private static MqttClient mqttClient;
    //mqtt连接配置类
    private MqttConnectOptions options;

    private String userName = "admin";
    private String passWord = "public";


    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        try {
            //在服务开始时new一个mqttClient实例，客户端ID为clientId，第三个参数说明是持久化客户端，如果是null则是非持久化
            mqttClient = new MqttClient(BROKER_URL, clientId, new MemoryPersistence());

            // MQTT的连接设置
            options = new MqttConnectOptions();

            //设置自动重连
            options.setAutomaticReconnect(true);
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录
            //设置为true表示每次连接到服务器都以新的身份连接
            //设置为false时可以客户端可以接受离线消息
            options.setCleanSession(false);
            // 设置连接的用户名
            options.setUserName(userName);
            // 设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(10);

            // 设置会话心跳时间 单位为秒 服务器会每隔5秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(5);
            // 设置回调  回调类的说明看后面
            mqttClient.setCallback(new PushCallback());
            MqttTopic topic = mqttClient.getTopic(TOPIC);
            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
            options.setWill(topic, "掉线重连".getBytes(), 2, true);
            //mqtt客户端连接服务器
            mqttClient.connect(options);

            //mqtt客户端订阅主题
            //在mqtt中用QoS来标识服务质量
            //QoS=0时，报文最多发送一次，有可能丢失
            //QoS=1时，报文至少发送一次，有可能重复
            //QoS=2时，报文只发送一次，并且确保消息只到达一次。
            int[] Qos  = {2};
            String[] topic1 = {TOPIC};
            mqttClient.subscribe(topic1, Qos);





        } catch (MqttException e) {
            Toast.makeText(getApplicationContext(), "错误1!" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


        return START_STICKY;
    }

    public static void sendMsg() throws MqttException {

        // 消息发布所需参数
        MqttMessage message = new MqttMessage("发布的消息!".getBytes());
        message.setQos(2);
        mqttClient.publish(TOPIC, message);

    }



    @Override
    public void onDestroy() {
//        try {
//            mqttClient.disconnect(0);
//            mqttClient.close();
//        } catch (MqttException e) {
//            Toast.makeText(getApplicationContext(), "错误2!", Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }
    }


}

