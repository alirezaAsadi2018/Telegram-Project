package com.telegram.controllers;

public interface SharedDataExchanger {
    public Object[] getData();

    public void setData(Object... data);
}
