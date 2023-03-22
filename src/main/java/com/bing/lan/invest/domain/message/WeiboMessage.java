package com.bing.lan.invest.domain.message;

import com.bing.lan.invest.domain.BaseIDEAMessage;

/**
 * Created by lanbing at 2023/3/22 9:56
 */

public class WeiboMessage extends BaseIDEAMessage {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}