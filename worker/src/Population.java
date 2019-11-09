public class Population {
	// Holds population of paths
    Path[] paths;

    /**
     * Construct a population
     * @param populationSize
     * @param initialize
     */
    public Population(int populationSize, boolean initialize) {
        paths = new Path[populationSize];
        // If we need to initialize a population of paths do so
        if (initialize) {
            // Loop and create individuals
            for (int i = 0; i < populationSize(); i++) {
            	Path newPath = new Path();
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
    public void savePath(int index, Path path) {
        paths[index] = path;
    }
    
    /**
     * Gets a path from population
     * @param index
     * @return path
     */
    public Path getPath(int index) {
        return paths[index];
    }

    /**
     * Gets the best path in the population
     * @return fittest Path
     */
    public Path getFittest() {
    	Path fittest = paths[0];
        // Loop through individuals to find fittest
        for (int i = 1; i < populationSize(); i++) {
            if (fittest.getFitness() <= getPath(i).getFitness()) {
                fittest = getPath(i);
            }
        }
        return fittest;
    }

    /**
     * Gets population size
     * @return size of population
     */
    public int populationSize() {
        return paths.length;
    }
}
