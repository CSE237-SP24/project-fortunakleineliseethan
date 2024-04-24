package bankapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.lang.StringBuilder;

public class LLMInterface {
    private static final String ENDPOINT_URL = "https://v2q8ub7m7ue6l97w.us-east-1.aws.endpoints.huggingface.cloud";
    private static final String REQUEST_METHOD = "POST";
    private static final String ACCEPT_HEADER = "application/json";
    private static final String CONTENT_TYPE_HEADER = "application/json";
    private static final String ENCODING = StandardCharsets.UTF_8.name();

    public static String sendQueryToLLM(String userInput, String context) throws IOException {
        // Create URL object with the endpoint URL
        URL url = new URL(ENDPOINT_URL);


        // Open a connection to the endpoint URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the request method to POST
        connection.setRequestMethod(REQUEST_METHOD);

        // Sets the request headers
        connection.setRequestProperty("Accept", ACCEPT_HEADER);
        connection.setRequestProperty("Content-Type", CONTENT_TYPE_HEADER);

        // Allows output from URL connection
        connection.setDoOutput(true);

        // Sets the parameters for the request
        
        double topK = 0.9;
        double topP = 0.1;
        double temperature = 0.9;
        int maxNewTokens = 1024;
        String prefix = "";
        

        // Keep Default Values by passing nothing for these parameters
        
/*      boolean doSample = false;
        boolean returnText = true;
        boolean returnFullText = true;
        boolean returnTensors = false;
        boolean cleanUpTokenizationSpaces = false;
        String handleLongGeneration = "hole"; */


        // Create a StringBuilder to build the JSON input string
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");
        jsonBuilder.append("\"inputs\":\"");
        jsonBuilder.append(userInput);
        jsonBuilder.append("\",");
        jsonBuilder.append("\"parameters\":{");
        jsonBuilder.append("\"top_k\":");
        jsonBuilder.append(topK);
        jsonBuilder.append(",");
        jsonBuilder.append("\"top_p\":");
        jsonBuilder.append(topP);
        jsonBuilder.append(",");
        jsonBuilder.append("\"temperature\":");
        jsonBuilder.append(temperature);
        jsonBuilder.append(",");
        jsonBuilder.append("\"max_new_tokens\":");
        jsonBuilder.append(maxNewTokens);
        jsonBuilder.append(",");
        jsonBuilder.append("\"do_sample\":");
  //    jsonBuilder.append(doSample);
        jsonBuilder.append(",");
        jsonBuilder.append("\"return_text\":");
    // jsonBuilder.append(returnText);
        jsonBuilder.append(",");
        jsonBuilder.append("\"return_full_text\":");
     // jsonBuilder.append(returnFullText);
        jsonBuilder.append(",");
        jsonBuilder.append("\"return_tensors\":");
    //  jsonBuilder.append(returnTensors);
        jsonBuilder.append(",");
        jsonBuilder.append("\"clean_up_tokenization_spaces\":");
    //  jsonBuilder.append(cleanUpTokenizationSpaces);
        jsonBuilder.append(",");        
        jsonBuilder.append("\"prefix\":\"");
        jsonBuilder.append(prefix);
        jsonBuilder.append("\",");        
        jsonBuilder.append("\"handle_long_generation\":\"");
    //  jsonBuilder.append(handleLongGeneration);
        jsonBuilder.append("\"");        
        jsonBuilder.append("}");
        jsonBuilder.append("}");

        // JSON input string derived from the StringBuilder and parameter values
        String jsonInputString = jsonBuilder.toString();

        // Convert the JSON input string to bytes
        byte[] input = jsonInputString.getBytes(ENCODING);

        // Output stream of the connection
        OutputStream outputStream = connection.getOutputStream();

        // Write the input bytes to the output stream
        outputStream.write(input, 0, input.length);
        outputStream.flush();
        outputStream.close();

        // Get the input stream of the connection
        InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream(), ENCODING);

        // Create a buffered reader to read the response
        BufferedReader reader = new BufferedReader(inputStreamReader);

        // Read the response line by line and append it to a StringBuilder
        StringBuilder response = new StringBuilder();
        String responseLine;
        
        while ((responseLine = reader.readLine()) != null) {
            
            response.append(responseLine.trim());
        }

        // Close the reader
        reader.close();

        // Return the response as a string
        return response.toString();

    }

}