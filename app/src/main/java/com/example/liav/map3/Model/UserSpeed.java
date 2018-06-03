package com.example.liav.map3.Model;

/**
 * Created by Liav on 5/6/2018.
 */

public class UserSpeed {
    private float speed;
    private String email;

    public UserSpeed (){}
    public UserSpeed(float speed, String email){
        this.speed = speed;
        this.email = email;
    }

    public float getSpeed () {
        return this.speed;
    }

    public void setSpeed (float speed){ this.speed = speed;}

    public String getEmail () {
        return this.email;
    }

    public void setEmail (String email){ this.email = email;}

}
