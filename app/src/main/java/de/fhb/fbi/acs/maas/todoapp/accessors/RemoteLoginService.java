package de.fhb.fbi.acs.maas.todoapp.accessors;


/**
 * Created by deves on 30/01/17.
 */
public class RemoteLoginService implements LoginService {

    @Override
    public boolean processLogin(String email, String password) {
        //TODO validate login via remote web app
        return false;
    }
}
