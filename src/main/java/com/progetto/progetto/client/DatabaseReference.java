package com.progetto.progetto.client;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @param result is a JSONObject representing the result of an operation. It is null in case of operations related to download and upload of files.
 * @param fileContent is a byte array containing the data of a downloaded file. It is null for all other operations.
 * @param fileFormat is a String representing the extension of the downloaded file. It is null for all other operations.
 * @param fileId is the id associated to a file uploaded to the server. It is null for all other operations.
 */
public record DatabaseReference(JSONObject result, byte[] fileContent, String fileFormat, String fileId) {

    /**
     * Method to save a file in a path. This method should be invoked after a download operation.
     *
     * @param path is the path where the file must be saved.
     *
     * @return a File object representing the saved file.
     *
     * @throws IOException if it is not possible to save the file in the desired path.
     * */
    public File saveFile(String path) throws IOException {
        if(fileContent() == null || fileFormat() == null)
            return null;
        File file = new File(path + "." + fileFormat());
        OutputStream os = new FileOutputStream(file);
        os.write(fileContent());
        os.close();
        return file;
    }

    DatabaseReference(JSONObject result) {
        this(result, null, null, null);
    }

    DatabaseReference(byte[] fileContent, String fileFormat) {
        this(null, fileContent, fileFormat, null);
    }

    DatabaseReference(String fileId) {
        this(null, null, null, fileId);
    }

}
