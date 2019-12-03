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

public class SchedulerAPI {
    
    private static HttpURLConnection con;
    private static final String urlWorker = "http://139.6.65.27:8080/worker";
    private static final String urlMap = "http://139.6.65.27:8080/map";
    private static final Path UUIDPATH = Paths.get("/Users/wi2885/Desktop/uuid/uuid.txt");

    private String postWorker(Population population) throws IOException
    {
        Gson gson = new Gson();
        String populationAsJSON = gson.toJson(population); //convert
    
        try
        {

            URL myurl = new URL(urlWorker);
            con = (HttpURLConnection) myurl.openConnection();
    
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/json; utf-8");

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = populationAsJSON.getBytes("utf-8");
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
                    System.out.println("POST RESPONSE:" + response.toString());
                }
            }
    
            JSONObject jsonObject = new JSONObject(response.toString());

            return jsonObject.getString("uuid");
    
        } finally {
    
            con.disconnect();
        }
    }

    private void putWorker(String uuidAsString,Population population) throws IOException
    {
        String urlParameters = "?uuid="+uuidAsString;

        Gson gson = new Gson();
        String populationAsJson = gson.toJson(population); //convert
    
        try
        {
            URL myurl = new URL(urlWorker + urlParameters);
            con = (HttpURLConnection) myurl.openConnection();
    
            con.setDoOutput(true);
            con.setRequestMethod("PUT");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/json; utf-8");

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = populationAsJson.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            StringBuilder response = null;

            InputStream inputStream;
            int status = con.getResponseCode();

            if (status != HttpURLConnection.HTTP_OK)  {
                inputStream = con.getErrorStream();
            }
            else {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("RESPONSE:" + response.toString());
                }
            }
        } finally {
    
            con.disconnect();
        }
    }

    public String getMap() throws IOException
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
                        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                    response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
            }

            return response.toString();

        } finally {

            con.disconnect();
        }
    }

    void sendFittestPath(Population population) throws IOException {

        File f = UUIDPATH.toFile();
        if(f.exists() && f.isFile()) {
            List<String> allLines = Files.readAllLines(UUIDPATH,StandardCharsets.UTF_8);
            this.putWorker(allLines.get(0),population);
        }
        else
        {
            String uuidAsString = postWorker(population);

            PrintWriter writer = new PrintWriter(UUIDPATH.toString());
            writer.println(uuidAsString);
            writer.close();
        }
    }
}