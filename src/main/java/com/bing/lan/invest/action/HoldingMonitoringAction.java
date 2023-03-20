package com.bing.lan.invest.action;

import com.bing.lan.invest.domain.MessageList;
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
public class HoldingMonitoringAction extends AnAction {

    private ScheduledExecutorService scheduledExecutorService;

    private volatile boolean runFlag = false;

    private static Gson gson = new Gson();
    private static HttpClient httpClient = HttpClients.createDefault();

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
                List<String> list = bean.getList();
                for (int i = 0; i < list.size(); i++) {
                    notifyMe("attention", list.get(i));
                }
                times++;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }, 0, 10, TimeUnit.SECONDS);
    }

    public static void notifyMe(String title, String text) {
        Notification holdingMonitoringGroup = NotificationGroupManager.getInstance()
                .getNotificationGroup("holdingMonitoringGroup")
                .createNotification(title, text, NotificationType.INFORMATION);
        Notifications.Bus.notify(holdingMonitoringGroup);
    }
}
