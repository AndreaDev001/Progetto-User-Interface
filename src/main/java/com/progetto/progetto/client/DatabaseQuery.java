package com.progetto.progetto.client;

import com.progetto.progetto.client.util.QueryResult;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

record DatabaseQuery(Client client) {

    private static final String ADD = "add";
    private static final String GET = "get";
    private static final String UPDATE = "update";
    private static final String REMOVE = "remove";
    private static final String UPLOAD_FILE = "upload_file";
    private static final String RETRIEVE_FILE = "retrieve_file";
    private static final String DELETE_FILE = "delete_file";

    private String createURL(String table, String mode) {
        String res = client.url + "/" + (table == null ? "" : table + "/") + mode;
        if(GET.equals(mode) && (client.user == null || client.token == null))
            return res;
        return res + "?id=" + client.user + "&token=" + client.token;
    }

    private String createURL(String table, String element_id, String mode) {
        return client.url + "/" + (table == null ? "" : table + "/") + mode + "?id=" + client.user + "&token=" + client.token + "&element_id=" + element_id;
    }

    public QueryResult get(String table) throws IOException, ConnectionException {
        Objects.requireNonNull(table, "Table cannot be null");
        return client.parseOutput(client.connect(createURL(table, GET)), true);
    }

    public QueryResult insert(String table, JSONObject o) throws Exception {
        Objects.requireNonNull(table, "Table cannot be null");
        Objects.requireNonNull(o, "JSONObject to insert cannot be null");
        return client.parseOutput(client.connectPost(createURL(table, ADD), o), false);
    }

    public QueryResult update(String table, String element_id, JSONObject o) throws Exception {
        Objects.requireNonNull(table, "Table cannot be null");
        Objects.requireNonNull(o, "JSONObject to insert cannot be null");
        Objects.requireNonNull(element_id, "Element id cannot be null");
        return client.parseOutput(client.connectPost(createURL(table, element_id, UPDATE), o), false);
    }

    public QueryResult remove(String table, String element_id) throws IOException, ConnectionException {
        Objects.requireNonNull(table, "Table cannot be null");
        Objects.requireNonNull(element_id, "Element id cannot be null");
        return client.parseOutput(client.connect(createURL(table, element_id, REMOVE)), false);
    }

    DatabaseReference retrieveFile(String fileId) throws IOException, ConnectionException {
        HttpURLConnection conn = (HttpURLConnection) new URL( createURL(null, RETRIEVE_FILE) + "&fileId=" + fileId).openConnection();
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new ConnectionException("Response code is: " + responseCode);
        }
        QueryResult output = client.parseOutput(conn, true);
        if (output.success()) {
            JSONObject res = new JSONObject(output.message());
            if (res.has("fileContent") && res.has("fileFormat")) {
                JSONArray arr = res.getJSONArray("fileContent");
                byte[] ret = new byte[arr.length()];
                for (int i = 0; i < arr.length(); i++)
                    ret[i] = (byte) arr.getInt(i);
                return new DatabaseReference(ret, res.getString("fileFormat"));
            }
        }
        return null;
    }

    DatabaseReference deleteFile(String fileId) throws IOException, ConnectionException {
        HttpURLConnection conn = (HttpURLConnection) new URL( createURL(null, DELETE_FILE) + "&fileId=" + fileId).openConnection();
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new ConnectionException("Response code is: " + responseCode);
        }
        QueryResult output = client.parseOutput(conn, false);
        if (output.success()) {
            JSONObject res = new JSONObject(output.toString());
            return new DatabaseReference(res);
        }
        return null;
    }

    DatabaseReference uploadFile(File file, String fileFormat) throws IOException, ConnectionException {
        Objects.requireNonNull(file, "File cannot be null");
        Objects.requireNonNull(fileFormat, "File format cannot be null");
        byte[] fileContent = Files.readAllBytes(file.toPath());
        HttpURLConnection conn = (HttpURLConnection) new URL(createURL(null, UPLOAD_FILE) + "&fileFormat=" + fileFormat).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/octet-stream");
        conn.setDoOutput(true);
        OutputStream writer = conn.getOutputStream();
        writer.write(fileContent);
        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new ConnectionException("Response code is: " + responseCode);
        }
        QueryResult res = client.parseOutput(conn, true);
        if (res.success()) {
            JSONObject obj = new JSONObject(res.message());
            if (obj.has("fileId"))
                return new DatabaseReference(obj.getString("fileId"));
        }
        return null;
    }
}
