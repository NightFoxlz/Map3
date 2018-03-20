package com.example.liav.map3.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liav on 11/15/2017.
 */

public class User {
    private String email,uid;
    private List<String> friendList, uidList;
    private List<Challange> sentChallanges, recivedChallanges;

    public User () {
        this.friendList = new ArrayList<String>();
        this.uidList = new ArrayList<String>();
        this.sentChallanges = new ArrayList<Challange>();
        this.recivedChallanges = new ArrayList<Challange>();
    }

    public User (String email , String uid) {
        this.email = email;
        this.uid = uid;
        this.friendList = new ArrayList<String>();
        this.uidList = new ArrayList<String>();
        this.sentChallanges = new ArrayList<Challange>();
        this.recivedChallanges = new ArrayList<Challange>();
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

    public List<Challange> getSentChallanges() {return this.sentChallanges; }

    public void setSentChallanges(List<Challange> sentChallanges) {this.sentChallanges = sentChallanges; }

    public List<Challange> getRecivedChallanges() {return this.recivedChallanges; }

    public void setRecivedChallanges(List<Challange> recivedChallanges) {this.recivedChallanges = recivedChallanges; }

    public boolean addFriend (String email, String uid){
        if (this.friendList.contains(email)) return false;
        this.friendList.add(email);
        this.uidList.add(uid);
        return true;
    }

    public boolean addRecivedChallange (Challange c){
        for (int i=0; i< this.recivedChallanges.size(); i++){
            if (this.recivedChallanges.get(i).getRouteUID().equals(c.getRouteUID())) return false;
        }
        this.recivedChallanges.add(c);
        return true;
    }

    public boolean addSentChallange (Challange c){
        for (int i=0; i< this.sentChallanges.size(); i++){
            if (this.sentChallanges.get(i).getRouteUID().equals(c.getRouteUID()) &&
                    this.sentChallanges.get(i).getChallengeMail().equals(c.getChallengeMail())) return false;
        }
        this.sentChallanges.add(c);
        return true;
    }

}
