package client.util;

import org.json.JSONObject;

public record QueryResult (Boolean success, String message, JSONObject obj){

    public QueryResult(Boolean success, String message) {
        this(success, message, null);
    }

    @Override
    public String toString() {
        String additional = "";
        if(obj != null)
            additional= """
, "response": %s
""".formatted(obj.toString(2));

        return """
{
    "success": "%s",
    "message": "%s"
    %s
}
        """.formatted(success, message, additional);
    }
}
