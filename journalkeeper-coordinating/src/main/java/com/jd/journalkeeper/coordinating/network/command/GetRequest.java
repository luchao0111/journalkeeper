package com.jd.journalkeeper.coordinating.network.command;

import com.jd.journalkeeper.coordinating.network.CoordinatingCommands;

/**
 * GetRequest
 * author: gaohaoxiang
 * email: gaohaoxiang@jd.com
 * date: 2019/6/4
 */
public class GetRequest implements CoordinatingPayload, KeyPayload {

    private byte[] key;

    public GetRequest() {

    }

    public GetRequest(byte[] key) {
        this.key = key;
    }

    @Override
    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    @Override
    public int type() {
        return CoordinatingCommands.GET_REQUEST.getType();
    }
}