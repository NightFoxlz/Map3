package com.example.liav.map3.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liav on 11/15/2017.
 */

public class User {
    private String email,uid;
    private List<String> friendList, uidList;

    public User () {
        this.friendList = new ArrayList<String>();
        this.uidList = new ArrayList<String>();
    }

    public User (String email , String uid) {
        this.email = email;
        this.uid = uid;
        this.friendList = new ArrayList<String>();
        this.uidList = new ArrayList<String>();
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email) {this.email = email; }

    public String getUid(){
        return uid;
    }

    public void setUid(String uid) {this.uid = uid; }

    public List<String> getFriendList() {return this.friendList; }

    public void setFriendList(List<String> friendList) {this.friendList = friendList; }

    public List<String> getUidList() {return this.uidList; }

    public void setUidList(List<String> uidList) {this.uidList = uidList; }

    public boolean addFriend (String email, String uid){
        if (this.friendList.contains(email)) return false;
        this.friendList.add(email);
        this.uidList.add(uid);
        return true;
    }

}
