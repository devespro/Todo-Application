package de.fhb.fbi.acs.maas.todoapp.accessors;

/**
 * @author novruzov
 */
public interface LoginService {
    boolean processLogin(String email, String password);
}
