package de.fhb.fbi.acs.maas.todoapp.model;

import java.io.Serializable;

/**
 * The Credential class represents the login data used by server for user validation
 * @author Esien Novruzov
 */
public class Credentials implements Serializable {

    private String email;
    private String password;

    public Credentials(){

    }

    public Credentials(String username, String password) {
        this.email = username;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}