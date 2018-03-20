package com.example.liav.map3.Model;

/**
 * Created by Liav on 3/7/2018.
 */

public class Challange {
    private int winner;
    private String routeUID;
    private String challengerEmail;
    private String challangerUID;
    private String challengeEmail;
    private long time;
    private float distance;

    public Challange(){};

    public Challange(String routeUID,String challangerUID, String challangerEmail, String challengeUID, long time, float dist){
        this.routeUID = routeUID;
        this.challengerEmail = challangerEmail;
        this.challengeEmail = challengeUID;
        this.challangerUID = challangerUID;
        this.time = time;
        this.distance = dist;
        this.winner = 0;
    }

    public int getWinner () {return this.winner; }

    public void setWinner (int winner) {this.winner = winner; }

    public String getRouteUID () {return this.routeUID; }

    public void setRouteUID (String routeUID) {this.routeUID = routeUID; }

    public String getChallengerMail () {return this.challengerEmail; }

    public void setChallengerMail (String challengerUID) {this.challengerEmail = challengerUID; }

    public void setChallengerUID (String challengerUID) {this.challangerUID = challengerUID; }

    public String getChallengerUID () {return this.challangerUID; }

    public String getChallengeMail () {return this.challengeEmail; }

    public void setChallengeMail (String challengeUID) {this.challengeEmail = challengeUID; }

    public long getTime () {return this.time; }

    public void setTime (long time) {this.time = time; }

    public float getDistance () {return this.distance; }

    public void setDistance (float distance) {this.distance = distance; }
}
