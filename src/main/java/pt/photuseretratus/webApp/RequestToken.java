package pt.photuseretratus.webApp;

import java.util.Map;

public class RequestToken {

    private String user;
    private String pass;
    private boolean admin;
    private String path;
    private String name;
    private String token;
    private Map<String, String> transformation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getTransformation() {
        return transformation;
    }

    public void setTransformation(Map<String, String> transformation) {
        this.transformation = transformation;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
