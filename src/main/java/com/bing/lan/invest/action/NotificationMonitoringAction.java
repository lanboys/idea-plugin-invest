package com.bing.lan.invest.action;

import com.bing.lan.invest.domain.BaseIDEAMessage;
import com.bing.lan.invest.domain.MessageList;
import com.bing.lan.invest.domain.message.WeiboMessage;
import com.bing.lan.invest.util.DateTimeUtil;
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

    private volatile ScheduledExecutorService scheduledExecutorService;
    private volatile boolean runFlag = false;
    private volatile long period = 10;
    private Gson gson = new Gson();
    private HttpClient httpClient = HttpClients.createDefault();

    private List<Long> exist = new ArrayList<>();
    private int times = 0;
    private String displayGroupId = "displayGroup";

    private String noDisplayGroupId = "noDisplayGroup";

    private Integer[] monitoringPeriod = new Integer[]{8, 50, 15, 30};

    @Override
    public void actionPerformed(AnActionEvent e) {
        if (!DateTimeUtil.currentTimeIn(monitoringPeriod)) {
            notifyMe(displayGroupId, "Warn",
                    "The monitoring can only be enabled" + getMonitoringPeriodString(), NotificationType.WARNING);
            return;
        }

        // close
        if (runFlag) {
            notifyMe(displayGroupId, "Success", "Monitoring disabled successfully", NotificationType.INFORMATION);
            if (scheduledExecutorService != null) {
                scheduledExecutorService.shutdown();
            }
            scheduledExecutorService = null;
            runFlag = false;
            return;
        }

        // open
        times = 0;
        runFlag = true;
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
        }
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                if (!DateTimeUtil.currentTimeIn(monitoringPeriod)) {
                    notifyMe(displayGroupId, "Warn",
                            "The monitoring has stopped automatically not" + getMonitoringPeriodString(),
                            NotificationType.WARNING);
                    if (scheduledExecutorService != null) {
                        scheduledExecutorService.shutdown();
                    }
                    scheduledExecutorService = null;
                    runFlag = false;
                    return;
                }

                HttpGet request = new HttpGet("http://localhost:6666/invest/api/idea/messages?times=" + times);
                HttpResponse response = httpClient.execute(request);
                String responseBody = EntityUtils.toString(response.getEntity());
                MessageList bean = gson.fromJson(responseBody, MessageList.class);
                period = bean.getPeriod();
                if (period <= 0) {
                    period = 10;
                }
                List<BaseIDEAMessage> messages = bean.getMessages();
                if (messages.isEmpty()) {
                    notifyMe(noDisplayGroupId, "Warn",
                            "No data is returned from the server", NotificationType.WARNING);
                }
                for (BaseIDEAMessage ideaMessage : messages) {
                    notifyMe(ideaMessage.getNotificationGroupId(), ideaMessage.getTitle(), ideaMessage.getText(),
                            NotificationType.INFORMATION);
                }
                List<WeiboMessage> weiboMessages = bean.getWeiboMessages();
                weiboMessages.stream().filter(weiboMessage -> {
                    if (exist.contains(weiboMessage.getId())) {
                        return false;
                    }
                    exist.add(weiboMessage.getId());
                    return true;
                }).forEach(weiboMessage ->
                        notifyMe(weiboMessage.getNotificationGroupId(),
                                weiboMessage.getTitle(),
                                weiboMessage.getText(),
                                NotificationType.INFORMATION));
                times++;
            } catch (Exception ex) {
                ex.printStackTrace();
                notifyMe(displayGroupId, "Warn",
                        "Throwing exception , please check your program, the exception is: " + ex.getLocalizedMessage(),
                        NotificationType.WARNING);
            }

        }, 0, period, TimeUnit.SECONDS);

        notifyMe(displayGroupId, "Success", "Monitoring enabled successfully", NotificationType.INFORMATION);
    }

    private static void notifyMe(String groupId, String title, String text, NotificationType information) {
        Notification holdingMonitoringGroup = NotificationGroupManager.getInstance()
                .getNotificationGroup(groupId)
                .createNotification(title, text, information);
        Notifications.Bus.notify(holdingMonitoringGroup);
    }

    private String getMonitoringPeriodString() {
        if (monitoringPeriod == null || monitoringPeriod.length != 4) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(" between ")
                .append(monitoringPeriod[0])
                .append(":")
                .append(monitoringPeriod[1])
                .append(" and ")
                .append(monitoringPeriod[2])
                .append(":")
                .append(monitoringPeriod[3]);
        return builder.toString();
    }
}
