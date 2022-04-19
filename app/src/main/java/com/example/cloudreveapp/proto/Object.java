package com.example.cloudreveapp.proto;

public class Object {
    public String Id;
    public String Name;
    public String Path;
    public String Pic;
    public int Size;
    public String Type;
    public String Date;
    public boolean SourceEnabled;
    public String Md5;

    public Object(String Id, String Name, String Path, String Pic, int Size,
                  String Type, String Date, boolean SourceEnabled, String Md5) {
        this.Id = Id;
        this.Name = Name;
        this.Path = Path;
        this.Pic = Pic;
        this.Size = Size;
        this.Type = Type;
        this.Date = Date;
        this.SourceEnabled = SourceEnabled;
        this.Md5 = Md5;
    }
}
