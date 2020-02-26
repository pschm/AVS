import com.google.gson.Gson;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class SchedulerAPI {
    
    private static HttpURLConnection con;
    private static String urlWorker = "";

    public SchedulerAPI(String ipAndPort)
    {
        urlWorker = ipAndPort + "/worker";
    }

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
                    //System.out.println("GET RESPONSE:" + response.toString());
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
    
        } catch (Exception e)
        {
            System.out.println("Versuche getWorker nochmal: " + e.toString());
            Thread.sleep(5000);
            return getWorker();
        } finally
        {
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
                    //System.out.println("RESPONSE:" + response.toString());
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
            else if(status == HttpURLConnection.HTTP_FORBIDDEN || status == HttpURLConnection.HTTP_NOT_FOUND)
            {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        try {
                            Worker.start(urlWorker);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                Thread.currentThread().stop();
            }

            return new JSONObject(response.toString());

        } catch (Exception e)
        {
            System.out.println("Versuche getWorker nochmal: " + e.toString());
            Thread.sleep(5000);
            return putWorker(uuidAsString, population);
        } finally
        {
            con.disconnect();
        }
    }
}