package model;

public class CourierLoginRequest {

    private String login;
    private String password;

    public CourierLoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public CourierLoginRequest() {
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
