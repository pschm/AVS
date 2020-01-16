import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Worker {
    public static void main(String[] args) throws IOException, InterruptedException {

        SchedulerAPI schedulerAPI = new SchedulerAPI();

        Population actualPopulation = null;
        String uuid = "";

        while(true)
        {
            Thread.sleep(1000);

            if (!uuid.isEmpty())
            {
                if (actualPopulation != null)
                {
                    System.out.println("Initial distance: " + actualPopulation.getFittest().getDistance());

                    actualPopulation = GA.evolvePopulation(actualPopulation);
                    for (int i = 0; i < 100; i++)
                    {
                        actualPopulation = GA.evolvePopulation(actualPopulation);
                    }

                    // Print final results
                    System.out.println("Finished");
                    System.out.println("Final distance: " + actualPopulation.getFittest().getDistance());
                    System.out.println("Solution:");
                    System.out.println(actualPopulation.getFittest());

                    JSONObject obj = schedulerAPI.putWorker(uuid, actualPopulation);

                    if (obj != null) {
                        if(obj.has("status"))
                        {
                            if(obj.getString("status").equals("bad"))
                            {
                                String popAndUUID = schedulerAPI.getWorker();

                                if (popAndUUID != null) {

                                    JSONObject popUUIDObj = new JSONObject(popAndUUID);

                                    JSONObject popObj = popUUIDObj.getJSONObject("population");

                                    String s = popObj.toString();

                                    Gson gson = new Gson();
                                    actualPopulation = gson.fromJson(popObj.toString(), Population.class);

                                    uuid = popUUIDObj.getString("uuid");
                                }
                            }
                        }
                        else {
                            Gson gson = new Gson();
                            actualPopulation = gson.fromJson(obj.getJSONObject("population").toString(), Population.class);
                        }
                    } else {
                        System.out.println("PUT Worker: Response NULL");
                    }
                } else {
                    //...
                }
            } else {
                String popAndUUID = schedulerAPI.getWorker();

                if (popAndUUID != null) {

                    JSONObject popUUIDObj = new JSONObject(popAndUUID);

                    JSONObject popObj = popUUIDObj.getJSONObject("population");

                    String s = popObj.toString();

                    Gson gson = new Gson();
                    actualPopulation = gson.fromJson(popObj.toString(), Population.class);

                    uuid = popUUIDObj.getString("uuid");
                }
            }
        }
	}
}