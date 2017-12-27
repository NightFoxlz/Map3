package com.example.liav.map3.Model;

/**
 * Created by Liav on 11/15/2017.
 */

public class User {
    private String email,status;

    public User () {

    }

    public User (String email , String status) {
        this.email = email;
        this.status = status;
    }

    public String getEmail(){
        return email;
    }

    public String getStatus(){
        return status;
    }

}
