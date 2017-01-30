package de.fhb.fbi.acs.maas.todoapp.model;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Esien Novruzov
 */
public interface MediaResourceAccessor {

    InputStream readMediaResource(String url) throws IOException;

    String getBaseUrl();

}
