package com.bing.lan.invest.domain;

import com.bing.lan.invest.domain.message.HuabaoMessage;
import com.bing.lan.invest.domain.message.WeiboMessage;

import java.util.*;

/**
 * Created by oopcoder at 2023/3/17 22:41 .
 */

public class MessageList {

    private long period;

    private List<HuabaoMessage> huabaoMessages;

    private List<WeiboMessage> weiboMessages;

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public List<HuabaoMessage> getHuabaoMessages() {
        return huabaoMessages;
    }

    public void setHuabaoMessages(List<HuabaoMessage> huabaoMessages) {
        this.huabaoMessages = huabaoMessages;
    }

    public List<WeiboMessage> getWeiboMessages() {
        return weiboMessages;
    }

    public void setWeiboMessages(List<WeiboMessage> weiboMessages) {
        this.weiboMessages = weiboMessages;
    }
}
