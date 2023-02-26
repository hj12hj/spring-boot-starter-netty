package com.hj.message;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class Message implements Serializable {

    private int sequenceId;
    private int messageType;
    private boolean success = true;
    private String code = "200";

}