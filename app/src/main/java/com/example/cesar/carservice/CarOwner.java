package com.example.cesar.carservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Cesar on 10/08/15.
 */
public class CarOwner implements Serializable {
    @JsonIgnoreProperties(ignoreUnknown = true)
    private int id, client_id;
    private String type, business_name, first_name, last_name, mother_maiden_name, username, password,
            street, neighborhood, state, town, postal_code, rfc, email, phone_number, mobile_phone_number;

    public  CarOwner(){

    }

    public CarOwner(int id, String type, String business_name, String first_name, String last_name, String mother_maiden_name, String username, String password, int client_id,
                    String street, String neighborhood, String state, String town, String postal_code, String rfc, String email, String phone_number, String mobile_phone_number) {
        this.id = id;
        this.type = type;
        this.business_name = business_name;
        this.first_name = first_name;
        this.last_name = last_name;
        this.mother_maiden_name = mother_maiden_name;
        this.street = street;
        this.neighborhood = neighborhood;
        this.state = state;
        this.town = town;
        this.postal_code = postal_code;
        this.rfc = rfc;
        this.email = email;
        this.phone_number = phone_number;
        this.mobile_phone_number = mobile_phone_number;
        this.username = username;
        this.password = password;
        this.client_id = client_id;

    }

    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public  String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public  String getMother_maiden_name() {
        return mother_maiden_name;
    }

    public void setMother_maiden_name(String mother_maiden_name) {
        this.mother_maiden_name = mother_maiden_name;
    }

    public  String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public  String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public  String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public  String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public  String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public  String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public  String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public  String getMobile_phone_number() {
        return mobile_phone_number;
    }

    public void setMobile_phone_number(String mobile_phone_number) {
        this.mobile_phone_number = mobile_phone_number;
    }

    public  String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
