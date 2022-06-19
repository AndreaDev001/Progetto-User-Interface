package com.progetto.progetto.client;

import com.progetto.progetto.client.util.QueryResult;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class Authentication {

    private final ScheduledExecutorService executor =  Executors.newSingleThreadScheduledExecutor();
    private final Client client;

    Authentication(Client client) {
        this.client = client;
    }

    private void resetTokens() {
        client.email = null;
        client.user = null;
        client.token = null;
        client.refreshToken = null;
    }

    boolean logout() throws IOException, ConnectionException {
        if(client.email == null || client.user == null || client.token == null || client.refreshToken == null) {
            resetTokens();
            return true;
        }
        QueryResult result = client.parseOutput(client.connect(client.url + "/logout?id=" + client.user + "&token=" + client.token), false);
        if(result.success()) {
            resetTokens();
            return true;
        }
        return false;
    }

    String login(String username, String password) throws IOException, ConnectionException {
        return loginRegister(username, password, true);
    }

    String register(String username, String password) throws IOException, ConnectionException {
        return loginRegister(username, password, false);
    }

    private String loginRegister(String username, String password, boolean login) throws IOException, ConnectionException {
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(password, "Password cannot be null");
        String action = login ? "login" : "register";
        QueryResult result = client.parseOutput(client.connect(client.url + "/" + action + "?username=" + username + "&password=" + password), true);
        if(result.success()) {
            JSONObject output = new JSONObject(result.message());
            if (output.has("localId")) {
                client.email = username;
                client.user = output.getString("localId");
                client.token = output.getString("idToken");
                client.refreshToken = output.getString("refreshToken");
                scheduleRefresh(Integer.parseInt(output.getString("expiresIn")));
                return client.user;
            }
        }
        return null;
    }

    String changeEmail(String email) throws IOException, ConnectionException {
        Objects.requireNonNull(email, "Email cannot be null");
        QueryResult result = client.parseOutput(client.connect(client.url + "/change_email?email=" + email + "&token=" + client.token), true);
        if(result.success()) {
            JSONObject output = new JSONObject(result.message());
            if (output.has("localId")) {
                client.email = email;
                client.user = output.getString("localId");
                client.token = output.getString("idToken");
                client.refreshToken = output.getString("refreshToken");
                scheduleRefresh(Integer.parseInt(output.getString("expiresIn")));
                return client.user;
            }
        }
        return null;
    }

    boolean sendEmailVerification() throws IOException, ConnectionException {
        QueryResult result = client.parseOutput(client.connect(client.url + "/send_email_verification?token=" + client.token), true);
        if(result.success()) {
            JSONObject output = new JSONObject(result.message());
            return (output.has("email"));
        }
        return false;
    }

    boolean resetPassword(String email) throws IOException, ConnectionException {
        Objects.requireNonNull(email, "Email cannot be null");
        QueryResult result = client.parseOutput(client.connect(client.url + "/reset_password?email=" + email), true);
        if(result.success()) {
            JSONObject output = new JSONObject(result.message());
            return (output.has("email"));
        }
        return false;
    }

    boolean isEmailVerified() throws IOException, ConnectionException {
        QueryResult result = client.parseOutput(client.connect(client.url + "/get_user_data?token=" + client.token), true);
        if(result.success()) {
            JSONObject output = new JSONObject(result.message());
            if (output.has("emailVerified")) {
                return output.getBoolean("emailVerified");
            }
        }
        return false;
    }

    String changePassword(String password) throws IOException, ConnectionException {
        Objects.requireNonNull(password, "Password cannot be null");
        QueryResult result = client.parseOutput(client.connect(client.url + "/change_password?password=" + password + "&token=" + client.token), true);
        if(result.success()) {
            JSONObject output = new JSONObject(result.message());
            if (output.has("localId")) {
                client.user = output.getString("localId");
                client.token = output.getString("idToken");
                client.refreshToken = output.getString("refreshToken");
                scheduleRefresh(Integer.parseInt(output.getString("expiresIn")));
                return client.user;
            }
        }
        return null;
    }

    private void refreshToken() {
        try {
            QueryResult result = client.parseOutput(client.connect(client.url + "/refresh?token=" + client.refreshToken), true);
            if (result.success()) {
                JSONObject output = new JSONObject(result.message());
                if (output.has("id_token") && output.has("refresh_token") && output.has("expires_in")) {
                    client.token = output.getString("id_token");
                    client.refreshToken = output.getString("refresh_token");
                    executor.schedule(this::refreshToken, Integer.parseInt(output.getString("expires_in"))+1, TimeUnit.SECONDS);
                }
            }
        } catch (Exception e) {
            resetTokens();
        }
    }

    private void scheduleRefresh(Integer expiresIn) {
        executor.schedule(this::refreshToken, expiresIn+1, TimeUnit.SECONDS);
    }

    void close() throws IOException, ConnectionException {
        logout();
        executor.shutdownNow();
    }
}
