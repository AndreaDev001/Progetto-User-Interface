package com.progetto.progetto.client;

import com.progetto.progetto.client.util.QueryResult;
import com.progetto.progetto.client.listeners.ErrorListener;
import com.progetto.progetto.client.listeners.SuccessListener;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    final String url;
    String user;
    String token;
    String refreshToken;

    String email;

    public String getEmail() {
        return email;
    }

    public boolean isLogged()
    {
        return email != null;
    }

    private final Authentication authentication;
    private final DatabaseQuery databaseQuery;
    private final ExecutorService queryExecutorService = Executors.newCachedThreadPool();
    private Client() {
        this.url = "http://localhost:8080";
        authentication = new Authentication(this);
        databaseQuery = new DatabaseQuery(this);
    }

    public static Client instance = null;

    public static Client getInstance() {
        if(instance == null)
            instance = new Client();
        return instance;
    }

    QueryResult parseOutput(HttpURLConnection conn, boolean get) throws IOException {
        Objects.requireNonNull(conn, "Connection cannot be null");
        InputStream is = new BufferedInputStream(conn.getInputStream());
        String res = new String(is.readAllBytes());
        is.close();
        try {
            JSONObject object = new JSONObject(res);
            if(object.has("result")) {
                boolean success = "success".equals(object.getString("result"));
                String result = object.getString("result");
                if(object.length() == 1)
                    return new QueryResult(success, result);
                object.remove("result");
                return new QueryResult(success, result, object);
            }
            if(get)
                return new QueryResult(true, object.toString());
        }
        catch (Exception ignored) {
        }
        return new QueryResult(false, "Invalid output");
    }

    HttpURLConnection connect(String urlWithParameters) throws IOException, ConnectionException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlWithParameters).openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new ConnectionException("Response code is: " + responseCode);
        }
        return conn;
    }

    HttpURLConnection connectPost(String urlWithParameters, JSONObject obj) throws IOException, ConnectionException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlWithParameters).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        OutputStream writer = conn.getOutputStream();
        writer.write(obj.toString().getBytes());
        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new ConnectionException("Response code is: " + responseCode);
        }
        return conn;
    }

    /**
     * Method to retrieve all the content of a collection.
     * If the collection has a public access, then the method will retrieve all objects in the collection.
     * Otherwise, it will retrieve only the objects added by the user.
     *
     * @param table is the name of the collection.
     *
     * @throws IllegalArgumentException in case the table is null.
     * */
    public void get(String table,EventHandler<WorkerStateEvent> success,EventHandler<WorkerStateEvent> error){
        Objects.requireNonNull(table, "Table cannot be null");
        Task<JSONObject> task = new Task<JSONObject>()
        {
            @Override
            protected JSONObject call() throws Exception
            {
                QueryResult result = databaseQuery.get(table);
                if (!result.success())
                    throw new ConnectionException("Cannot get object of " + table + ": " + result.message());
                DatabaseReference databaseReference = new DatabaseReference(new JSONObject(result.message()));
                List<JSONObject> values = new Vector<>();
                return databaseReference.result();
            }
        };
        task.setOnSucceeded(success);
        task.setOnFailed(error);
        queryExecutorService.submit(task);
    }

    /**
     * Method to add a json object in a collection.
     *
     * @param table is the name of the collection.
     * @param jsonObject is the object to add in the collection.
     * @param successListener is an object that will contain the result of the query in case of success.
     * @param errorListener is an object containing an exception if it was raised during the execution.
     *
     * @throws IllegalArgumentException in case the table or the jsonObject are null.
     * */
    public void insert(String table, JSONObject jsonObject, SuccessListener successListener, ErrorListener errorListener) {
        Objects.requireNonNull(table, "Table cannot be null");
        Objects.requireNonNull(jsonObject, "Json object cannot be null");
        queryExecutorService.submit(createDaemonThread(() -> {
            try {
                QueryResult result = databaseQuery.insert(table, jsonObject);
                if(!result.success())
                    throw new ConnectionException("Cannot insert object in table " + table + ": " + result.message());
                if(successListener != null) {
                    successListener.onSuccess(new DatabaseReference(new JSONObject(result.toString())));
                }
            } catch (Exception e) {
                if(errorListener != null)
                    errorListener.onError(e);
            }
        }));
    }

    /**
     * Method to update an element of a collection.
     *
     * @param table is the name of the collection.
     * @param elementId is the id of the element to update.
     * @param jsonObject is the new object to add in the collection.
     * @param successListener is an object that will contain the result of the query in case of success.
     * @param errorListener is an object containing an exception if it was raised during the execution.
     *
     * @throws IllegalArgumentException in case the table, the elementId, or the jsonObject are null.
     * */
    public void update(String table, String elementId, JSONObject jsonObject, SuccessListener successListener, ErrorListener errorListener) {
        Objects.requireNonNull(table, "Table cannot be null");
        Objects.requireNonNull(elementId, "Element id cannot be null");
        Objects.requireNonNull(jsonObject, "Json object cannot be null");
        queryExecutorService.submit(createDaemonThread(() -> {
            try {
                QueryResult result = databaseQuery.update(table, elementId, jsonObject);
                if(!result.success())
                    throw new ConnectionException("Cannot update object of table " + table + ": " + result.message());
                if(successListener != null) {
                    successListener.onSuccess(new DatabaseReference(new JSONObject(result.toString())));
                }
            } catch (Exception e) {
                if(errorListener != null)
                    errorListener.onError(e);
            }
        }));
    }

    /**
     * Method to remove an element from a collection.
     *
     * @param table is the name of the collection.
     * @param elementId is the name of the element to remove.
     *
     * @throws IllegalArgumentException in case the table or the elementId are null.
     * */
    public void remove(String table, String elementId, EventHandler<WorkerStateEvent> success,EventHandler<WorkerStateEvent> error) {
        Objects.requireNonNull(table, "Table cannot be null");
        Objects.requireNonNull(elementId, "Element id cannot be null");
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                QueryResult queryResult = databaseQuery.remove(table,elementId);
                if(!queryResult.success())
                    throw new ConnectionException("Cannot remove from" + " " + table + ":" + " " + queryResult.message());
                return null;
            }
        };
        task.setOnSucceeded(success);
        task.setOnFailed(error);
        queryExecutorService.submit(task);
    }

    /**
     * Method to download a file.
     *
     * @param fileId is the id of the file to download.
     * @param successListener is an object that will contain the result of the query in case of success.
     * @param errorListener is an object containing an exception if it was raised during the execution.
     *
     * @throws IllegalArgumentException in case the fileId is null.
     * */
    public void retrieveFile(String fileId, SuccessListener successListener, ErrorListener errorListener) {
        Objects.requireNonNull(fileId, "File id cannot be null");
        queryExecutorService.submit(createDaemonThread(() -> {
            try {
                DatabaseReference databaseReference = databaseQuery.retrieveFile(fileId);
                if(databaseReference == null)
                    throw new ConnectionException("Cannot retrieve file with id " + fileId);
                if(successListener != null) {
                    successListener.onSuccess(databaseReference);
                }
            } catch (IOException | ConnectionException e) {
                if(errorListener != null)
                    errorListener.onError(e);
            }
        }));
    }

    /**
     * Method to upload a file.
     *
     * @param file is the file to upload.
     * @param fileFormat is the extension of the file.
     * @param successListener is an object that will contain the result of the query in case of success.
     * @param errorListener is an object containing an exception if it was raised during the execution.
     *
     * @throws IllegalArgumentException in case the file or the fileFormat are null.
     * */
    public void uploadFile(File file, String fileFormat, SuccessListener successListener, ErrorListener errorListener) {
        Objects.requireNonNull(file, "File cannot be null");
        Objects.requireNonNull(fileFormat, "File format cannot be null");
        queryExecutorService.execute(createDaemonThread(() -> {
            try {
                DatabaseReference databaseReference = databaseQuery.uploadFile(file, fileFormat);
                if(databaseReference == null)
                    throw  new ConnectionException("Cannot upload file");
                if(successListener != null) {
                    successListener.onSuccess(databaseReference);
                }
            } catch (IOException | ConnectionException e) {
                if(errorListener != null)
                    errorListener.onError(e);
            }
        }));
    }

    /**
     * Method to delete a file.
     *
     * @param fileId is the id of the file to delete.
     * @param successListener is an object that will contain the result of the query in case of success.
     * @param errorListener is an object containing an exception if it was raised during the execution.
     * @throws IllegalArgumentException in case the fileId is null.
     * */
    public void deleteFile(String fileId, SuccessListener successListener, ErrorListener errorListener) {
        Objects.requireNonNull(fileId, "File id cannot be null");
        queryExecutorService.submit(createDaemonThread(() -> {
            try {
                DatabaseReference databaseReference = databaseQuery.deleteFile(fileId);
                if(databaseReference == null)
                    throw new ConnectionException("Cannot delete file with id " + fileId);
                if(successListener != null) {
                    successListener.onSuccess(databaseReference);
                }
            } catch (IOException | ConnectionException e) {
                if(errorListener != null)
                    errorListener.onError(e);
            }
        }));
    }

    /**
     * Logout the current user.
     *
     * @return true if the user has been logged out, false otherwise.
     *
     * @throws IOException in case of wrong requests.
     * @throws ConnectionException in case the server cannot be reached.
     * */
    public boolean logout() throws IOException, ConnectionException {
        return authentication.logout();
    }

    /**
     * Login the user with email and password.
     *
     * @param email the email of the user.
     * @param password the password of the user.
     *
     * @return the identifier of the user if the combination email/password is correct, null otherwise.
     *
     * @throws IOException in case of wrong requests.
     * @throws ConnectionException in case the server cannot be reached.
     * @throws IllegalArgumentException in case the email or the password are null.
     * */
    public String login(String email, String password) throws IOException,Exception,ConnectionException {
        return authentication.login(email, password);
    }

    /**
     * Register the user with email and password.
     *
     * @param email the email of the user.
     * @param password the password of the user.
     *
     * @return the identifier of the user if the email was not associated to other users, null otherwise.
     *
     * @throws IOException in case of wrong requests.
     * @throws ConnectionException in case the server cannot be reached.
     * @throws IllegalArgumentException in case the email or the password are null.
     * */
    public String register(String email, String password) throws IOException,Exception,ConnectionException {
        return authentication.register(email, password);
    }

    /**
     * Change the email of the logged user. The mail has to be validated after the change, see sendEmailVerification().
     *
     * @param email is the new email of the user.
     *
     * @return the identifier of the user if the combination email was successfully changed, null otherwise.
     *
     * @throws IOException in case of wrong requests.
     * @throws ConnectionException in case the server cannot be reached.
     * @throws IllegalArgumentException in case the email is null.
     * */
    public String changeEmail(String email) throws IOException, ConnectionException {
        return authentication.changeEmail(email);
    }

    /**
     * Send a link to the email of the logged user.
     *
     * @return true if the email was sent, false otherwise.
     *
     * @throws IOException in case of wrong requests.
     * @throws ConnectionException in case the server cannot be reached.
     * */
    public boolean sendEmailVerification() throws IOException, ConnectionException {
        return authentication.sendEmailVerification();
    }

    /**
     * Reset the password of the logged user and send a link to the email for specifying a new password.
     *
     * @return true if the email was sent, false otherwise.
     *
     * @throws IOException in case of wrong requests.
     * @throws ConnectionException in case the server cannot be reached.
     * @throws IllegalArgumentException in case the email is null.
     * */
    public boolean resetPassword(String email) throws IOException, ConnectionException {
        return authentication.resetPassword(email);
    }

    /**
     * Checks if the email of the user was verified. This should be called after calling sendEmailVerification().
     *
     * @return true if the email was verified, false otherwise.
     *
     * @throws IOException in case of wrong requests.
     * @throws ConnectionException in case the server cannot be reached.
     * */
    public boolean isEmailVerified() throws IOException, ConnectionException {
        return authentication.isEmailVerified();
    }

    /**
     * Updates the password of the user.
     *
     * @return the identifier of the user if the password was changed.
     *
     * @throws IOException in case of wrong requests.
     * @throws ConnectionException in case the server cannot be reached.
     * */
    public String changePassword(String password) throws IOException, ConnectionException {
        return authentication.changePassword(password);
    }

    /**
     * Disconnect the user and close the client. This should be called only before closing the application.
     *
     * @throws IOException in case of wrong requests.
     * @throws ConnectionException in case the server cannot be reached.
     * */
    public void close() throws IOException, ConnectionException {
        authentication.close();
        queryExecutorService.shutdownNow();
    }

    private Thread createDaemonThread(Runnable runnable) {
        Thread t = new Thread(runnable);
        t.setDaemon(true);
        return t;
    }
    public final ExecutorService getQueryExecutorService() {return queryExecutorService;}
}
