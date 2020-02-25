import app.Graph;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Population {
	// Holds population of paths
    @SerializedName("paths")
    @Expose
    IndividualPath[] paths;

    /**
     * Construct a population
     * @param populationSize
     * @param initialize
     */
    public Population(int populationSize, boolean initialize) {
        paths = new IndividualPath[populationSize];
        // If we need to initialize a population of paths do so
        if (initialize) {
            // Loop and create individuals
            for (int i = 0; i < populationSize(); i++) {
            	IndividualPath newPath = new IndividualPath();
                newPath.generateIndividual();
                savePath(i, newPath);
            }
        }
    }
    
    /**
     * Saves a path
     * @param index
     * @param path
     */
    public void savePath(int index, IndividualPath path) {
        paths[index] = path;
    }
    
    /**
     * Gets a path from population
     * @param index
     * @return path
     */
    public IndividualPath getPath(int index) {
        return paths[index];
    }

    /**
     * Gets the best path in the population
     * @return fittest Path
     */
    public IndividualPath getFittest(Graph graph) {
        return getBestIndividual(graph);

//    	IndividualPath fittest = paths[0];
//        // Loop through individuals to find fittest
//        for (int i = 1; i < populationSize(); i++) {
//            if (fittest.getFitness(graph) <= getPath(i).getFitness(graph)) {
//                fittest = getPath(i);
//            }
//        }
//        return fittest;
    }

    public double getBestDistance(Graph graph)
    {
        double bestDistance = paths[0].getDistance(graph);

        for (int i = 1; i < populationSize(); i++)
        {
            if(bestDistance >= paths[i].getDistance(graph))
            {
                bestDistance = paths[i].getDistance(graph);
            }
        }
        return bestDistance;
    }

    public IndividualPath getBestIndividual(Graph graph)
    {
        IndividualPath bestPath = paths[0];

        for (int i = 1; i < populationSize(); i++)
        {
            if(bestPath.getDistance(graph) >= paths[i].getDistance(graph))
            {
                bestPath = paths[i];
            }
        }
        return bestPath;
    }

    /**
     * Gets population size
     * @return size of population
     */
    public int populationSize() {
        return paths.length;
    }
}
