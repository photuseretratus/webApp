package pt.photuseretratus.webApp.dtos;

import lombok.Data;

import java.util.Map;

@Data
public class RequestToken {

    private String user;
    private String pass;
    private boolean admin;
    private String path;
    private String name;
    private String token;
    private Map<String, String> transformation;

}
