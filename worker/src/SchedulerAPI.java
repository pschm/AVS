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
    //private static final String urlWorker = "http://192.168.0.136:8080/worker";
    //private static final String urlMap = "http://192.168.0.136:8080/map";

    public String getWorker() throws IOException, InterruptedException {
        try
        {
            URL myurl = new URL(urlWorker);
            con = (HttpURLConnection) myurl.openConnection();
    
            con.setDoOutput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/json; utf-8");

            StringBuilder response = null;

            InputStream inputStream;
            int status = con.getResponseCode();

            if (status == HttpURLConnection.HTTP_CREATED){
                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println("GET RESPONSE:" + response.toString());
                }
            }
            else if(status == HttpURLConnection.HTTP_NO_CONTENT)
            {
                con.disconnect();
                Thread.sleep(5000);
                return getWorker();
            }
            else if(status == HttpURLConnection.HTTP_UNAVAILABLE)
            {
                con.disconnect();
                Thread.sleep(30000);
                return getWorker();
            }
    
            //JSONObject jsonObject = new JSONObject(response.toString());

            return response.toString();
    
        } finally {
    
            con.disconnect();
        }
    }

    public JSONObject putWorker(String uuidAsString,Population population) throws IOException, InterruptedException {
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

            if(status == HttpURLConnection.HTTP_OK) {
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
            else if(status == HttpURLConnection.HTTP_NO_CONTENT)
            {
                con.disconnect();
                Thread.sleep(5000);
                return putWorker(uuidAsString, population);
            }
            else if(status == HttpURLConnection.HTTP_BAD_REQUEST)
            {
                JSONObject bad = new JSONObject();
                bad.put("status", "bad");

                return bad;
            }

            JSONObject jsonObject = new JSONObject(response.toString());

            return jsonObject;

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
}