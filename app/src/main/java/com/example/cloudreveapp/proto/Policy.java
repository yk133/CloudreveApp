package com.example.cloudreveapp.proto;

public class Policy {
    String Id;
    String Name;
    String Type;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public int getMaxSize() {
        return MaxSize;
    }

    public void setMaxSize(int maxSize) {
        MaxSize = maxSize;
    }

    int MaxSize;

    public Policy(String Id, String Name, String type, int MaxSize) {
        this.Id = Id;
        this.Name = Name;
        this.Type = Type;
        this.MaxSize = MaxSize;
    }
}

