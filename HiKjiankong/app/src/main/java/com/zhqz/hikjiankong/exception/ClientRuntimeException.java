package com.zhqz.hikjiankong.exception;

public class ClientRuntimeException extends RuntimeException {
    String code;
    String message;

    public String getErrMessage() {
        return message;
    }

    public ClientRuntimeException(String errCode, String errMessage) {
        super(String.format("%s: %s", errCode, errMessage));
        this.code = errCode;
        this.message = errMessage;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", code, message);
    }

}
