package com.bing.lan.invest.action;

import com.bing.lan.invest.domain.BaseIDEAMessage;
import com.bing.lan.invest.domain.MessageList;
import com.bing.lan.invest.domain.message.HuabaoMessage;
import com.bing.lan.invest.domain.message.WeiboMessage;
import com.google.gson.Gson;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by lanbing at 2023/3/20 9:28
 */
public class NotificationMonitoringAction extends AnAction {

    private ScheduledExecutorService scheduledExecutorService;

    private volatile boolean runFlag = false;
    private volatile long period = 10;
    private static Gson gson = new Gson();
    private static HttpClient httpClient = HttpClients.createDefault();

    private static List<Long> exist = new ArrayList<>();
    int times = 0;

    @Override
    public void actionPerformed(AnActionEvent e) {
        if (runFlag) {
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
            runFlag = false;
            return;
        }

        times = 0;
        runFlag = true;
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                HttpGet request = new HttpGet("http://localhost:6666/invest/api/idea/messages?times=" + times);
                HttpResponse response = httpClient.execute(request);
                String responseBody = EntityUtils.toString(response.getEntity());
                MessageList bean = gson.fromJson(responseBody, MessageList.class);
                period = bean.getPeriod();
                if (period <= 0) {
                    period = 10;
                }
                List<HuabaoMessage> huabaoMessages = bean.getHuabaoMessages();
                for (int i = 0; i < huabaoMessages.size(); i++) {
                    HuabaoMessage huabaoMessage = huabaoMessages.get(i);
                    notifyMe(huabaoMessage, huabaoMessage.getText());
                }
                List<WeiboMessage> etfWeiboMessages = bean.getWeiboMessages();
                etfWeiboMessages.stream().filter(weiboMessage -> {
                    if (exist.contains(weiboMessage.getId())) {
                        return false;
                    }
                    exist.add(weiboMessage.getId());
                    return true;
                }).forEach(weiboMessage -> notifyMe(weiboMessage, weiboMessage.getText()));

                times++;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }, 0, period, TimeUnit.SECONDS);
    }

    public static void notifyMe(BaseIDEAMessage baseIDEAMessage, String text) {

        Notification holdingMonitoringGroup = NotificationGroupManager.getInstance()
                .getNotificationGroup(baseIDEAMessage.getNotificationGroupId())
                .createNotification(baseIDEAMessage.getTitle(), text, NotificationType.INFORMATION);
        Notifications.Bus.notify(holdingMonitoringGroup);
    }
}
