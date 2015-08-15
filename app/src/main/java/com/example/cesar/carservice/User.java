package com.example.cesar.carservice;

import java.io.Serializable;

/**
 * Created by Cesar on 11/08/15.
 */
public class User implements Serializable{
    public int id, workshop_id, client_id;
    public String first_name, last_name, mother_maiden_name, username;
}
