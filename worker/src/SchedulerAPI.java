import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONObject;

public class SchedulerAPI {
    
    private static HttpURLConnection con;
    private static final String url = "http://localhost/worker";
    private static final Path UUIDPATH = Paths.get("C:/Users/volka/Desktop/uuid.txt");

    private String postWorker() throws IOException
    {
        String urlParameters = "";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
    
        try
        {
    
            URL myurl = new URL(url);
            con = (HttpURLConnection) myurl.openConnection();
    
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/json");
    
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
    
                wr.write(postData);
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
    
            JSONObject jsonObject = new JSONObject(response.toString());

            return jsonObject.getString("uuid");
    
        } finally {
    
            con.disconnect();
        }
    }

    private void putWorker(String uuidAsString) throws IOException
    {
        String urlParameters = "uuid="+uuidAsString;
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
    
        try
        {
            URL myurl = new URL(url);
            con = (HttpURLConnection) myurl.openConnection();
    
            con.setDoOutput(true);
            con.setRequestMethod("PUT");
            con.setRequestProperty("User-Agent", "Java client");
            con.setRequestProperty("Content-Type", "application/json");
    
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
    
                wr.write(postData);
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

    public void setWorker() throws IOException {

        File f = UUIDPATH.toFile();
        if(f.exists() && f.isFile()) {
            String uuidAsString = Files.readString(UUIDPATH, StandardCharsets.UTF_8);
            this.putWorker(uuidAsString);
        }
        else
        {
            String uuidAsString = postWorker();

            PrintWriter writer = new PrintWriter("C:/Users/volka/Desktop/uuid.txt", "UTF-8");
            writer.println(uuidAsString);
            writer.close();
        }
    }
}