package com.example.cloudreveapp.proto;

public class SearchFile {
    String FileName;
    String MD5;
    int size;

    public SearchFile(String FileName, String MD5, int size) {
        this.FileName = FileName;
        this.MD5 = MD5;
        this.size = size;
    }
}
