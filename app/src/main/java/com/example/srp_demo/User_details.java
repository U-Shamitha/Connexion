package com.example.srp_demo;

import java.util.ArrayList;

public class User_details {
    public String user_pno, user_pwd, user_name;

    public User_details(){}

    public User_details(String user_name, String user_pwd, String user_pno) {
        this.user_pno = user_pno;
        this.user_pwd = user_pwd;
        this.user_name = user_name;
    }
}
