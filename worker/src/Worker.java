import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Worker {
    public static void main(String[] args) throws IOException {

        SchedulerAPI schedulerAPI = new SchedulerAPI();

        JSONObject mapAsJSON = new JSONObject(schedulerAPI.getMap());


        JSONArray jsonArray = mapAsJSON.getJSONArray("Items");

        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject indexObject = new JSONObject(jsonArray.get(i));
            JSONObject position = new JSONObject(indexObject.get("position"));

            PathManager.addProduct(new Product(position.getInt("x"),position.getInt("y"),indexObject.getString("name")));
        }

        //----------------------------------------------------------------------------------------------------------------------------------------------------------------------


        // Initialize population
        Population pop = new Population(10, true);
        System.out.println("Initial distance: " + pop.getFittest().getDistance());

        // Evolve population for 100 generations
        pop = GA.evolvePopulation(pop);
        for (int i = 0; i < 100000; i++) {
            pop = GA.evolvePopulation(pop);
        }

        // Print final results
        System.out.println("Finished");
        System.out.println("Final distance: " + pop.getFittest().getDistance());
        System.out.println("Solution:");
        System.out.println(pop);

        schedulerAPI.sendFittestPath(pop);
	}
}
