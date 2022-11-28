package com.example.cloudreveapp.proto;

public class SearchFile {
    public String FileName;
    public String MD5;
    public int size;

    public SearchFile(String FileName, String MD5, int size) {
        this.FileName = FileName;
        this.MD5 = MD5;
        this.size = size;
    }
}
