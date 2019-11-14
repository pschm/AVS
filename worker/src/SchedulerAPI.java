import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.Gson;
import org.json.JSONObject;
import sun.net.www.http.HttpClient;

public class SchedulerAPI {
    
    private static HttpURLConnection con;
    private static final String urlWorker = "http://139.6.65.27:8080/worker";
    private static final String urlMap = "http://139.6.65.27:8080/map";
    private static final Path UUIDPATH = Paths.get("/Users/wi2885/Desktop/uuid/id.txt");

    private String postWorker(IndividualPath individualPath) throws IOException
    {
        Gson gson = new Gson();
        String individidualPathAsJson = gson.toJson(individualPath); //convert
    
        try
        {

            URL myurl = new URL(urlWorker);
            con = (HttpURLConnection) myurl.openConnection();
    
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/json; utf-8");

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = individidualPathAsJson.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            StringBuilder response = null;

            InputStream inputStream;
            int status = con.getResponseCode();

            if (status != HttpURLConnection.HTTP_OK)  {
                inputStream = con.getErrorStream();
            }
            else  {
                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
            }
    
            JSONObject jsonObject = new JSONObject(response.toString());

            System.out.println(jsonObject.toString());

            return jsonObject.getString("uuid");
    
        } finally {
    
            con.disconnect();
        }
    }

    public void putWorker(String uuidAsString,IndividualPath individualPath) throws IOException
    {
        String urlParameters = "?uuid="+uuidAsString;

        Gson gson = new Gson();
        String individidualPathAsJson = gson.toJson(individualPath); //convert
    
        try
        {
            URL myurl = new URL(urlWorker + urlParameters);
            con = (HttpURLConnection) myurl.openConnection();
    
            con.setDoOutput(true);
            con.setRequestMethod("PUT");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/json; utf-8");

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = individidualPathAsJson.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
    
            StringBuilder response;
    
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
    
                String line;
                response = new StringBuilder();
    
                while ((line = br.readLine()) != null) {
                    response.append(line);
                    response.append(System.lineSeparator());
                }
            }

            System.out.println("PUT RESPONSE: "+ response.toString());
    
        } finally {
    
            con.disconnect();
        }
    }

    public void getMap() throws IOException
    {
        try
        {
            URL myurl = new URL(urlMap);
            con = (HttpURLConnection) myurl.openConnection();

            con.setDoOutput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/json; utf-8");

            StringBuilder response = null;

            InputStream inputStream;
            int status = con.getResponseCode();

            if (status != HttpURLConnection.HTTP_OK)  {
                inputStream = con.getErrorStream();
            }
            else  {
                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
            }

            JSONObject jsonObject = new JSONObject(response.toString());

            System.out.println("GET RESPONSE: "+ response.toString());

        } finally {

            con.disconnect();
        }
    }

    public void sendFittestPath(IndividualPath individualPath) throws IOException {

        File f = UUIDPATH.toFile();
        if(f.exists() && f.isFile()) {
            List<String> allLines = Files.readAllLines(UUIDPATH,StandardCharsets.UTF_8);
            this.putWorker(allLines.get(0),individualPath);
        }
        else
        {
            String uuidAsString = postWorker(individualPath);

            PrintWriter writer = new PrintWriter(UUIDPATH.toString(), "UTF-8");
            writer.println(uuidAsString);
            writer.close();
        }
    }
}