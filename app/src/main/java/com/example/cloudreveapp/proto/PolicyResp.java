package com.example.cloudreveapp.proto;

public class PolicyResp {
    public  String Parent  ;

    public  Object []Objects;
    public Policy Policy;

    public PolicyResp(String Parent, Object []Objects, Policy Policy) {
        this.Parent = Parent;
        this.Objects = Objects;
        this.Policy = Policy;
    }

}
