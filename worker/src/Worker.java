import com.google.gson.Gson;
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

        while(true)
        {
            Thread.sleep(5000);

            //Path UUIDPATH = Paths.get("C:\\Users\\volka\\Desktop\\uuid.txt");
            Path UUIDPATH = Paths.get("/Users/wi2885/Desktop/uuid/uuid.txt");
            File f = UUIDPATH.toFile();
            if (f.exists() && f.isFile()) {
                if (actualPopulation != null) {
                    List<String> allLines = Files.readAllLines(UUIDPATH, StandardCharsets.UTF_8);

                    System.out.println("Initial distance: " + actualPopulation.getFittest().getDistance());

                    actualPopulation = GA.evolvePopulation(actualPopulation);

                    // Print final results
                    System.out.println("Finished");
                    System.out.println("Final distance: " + actualPopulation.getFittest().getDistance());
                    System.out.println("Solution:");
                    System.out.println(actualPopulation.getFittest());

                    JSONObject pop = schedulerAPI.putWorker(allLines.get(0), actualPopulation);

                    if (pop != null) {
                        Gson gson = new Gson();
                        actualPopulation = gson.fromJson(pop.toString(), Population.class);
                    } else {
                        System.out.println("PUT Worker: Response NULL");
                    }
                } else {
                    //...
                }
            } else {
                JSONObject popAndUUID = schedulerAPI.postWorker();

                if (popAndUUID != null) {
                    Gson gson = new Gson();
                    actualPopulation = gson.fromJson(popAndUUID.get("population").toString(), Population.class);

                    PrintWriter writer = new PrintWriter(UUIDPATH.toString());
                    writer.println(popAndUUID.get("uuid").toString());
                    writer.close();
                }
            }
        }
	}
}
