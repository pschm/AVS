public class GA {
	 /* GA parameters */
    private static final double mutationRate = 0.015;
    private static final int tournamentSize = 5;
    private static final boolean elitism = true;

    /**
     * Evolves a population over one generation
     * @param pop
     * @return new Population
     */
    public static Population evolvePopulation(Population pop) {
        Population newPopulation = new Population(pop.populationSize(), false);

        // Keep our best individual if elitism is enabled
        int elitismOffset = 0;
        if (elitism) {
            newPopulation.savePath(0, pop.getFittest());
            elitismOffset = 1;
        }

        // Crossover population
        // Loop over the new population's size and create individuals from
        // Current population
        for (int i = elitismOffset; i < newPopulation.populationSize(); i++) {
            // Select parents
            IndividualPath parent1 = tournamentSelection(pop);
            IndividualPath parent2 = tournamentSelection(pop);

            // Crossover parents
            IndividualPath child = crossover(parent1, parent2);

            // Add child to new population
            newPopulation.savePath(i, child);
        }

        // Mutate the new population a bit to add some new genetic material
        for (int i = elitismOffset; i < newPopulation.populationSize(); i++) {
            mutate(newPopulation.getPath(i));
        }

        return newPopulation;
    }

    /**
     * Applies crossover to a set of parents and creates offspring
     * @param parent1
     * @param parent2
     * @return new Path
     */
    public static IndividualPath crossover(IndividualPath parent1, IndividualPath parent2) {
        // Create new child tour
    	IndividualPath child = new IndividualPath(parent1.pathSize());

        // Get start and end sub tour positions for parent1's tour
        int startPos = 0;
        int endPos = parent1.pathSize();

        // Loop and add the sub path from parent1 to our child
        for (int i = 0; i < child.pathSize(); i++) {
            // If our start position is less than the end position
            if (startPos < endPos && i > startPos && i < endPos) {
                child.setProduct(i, parent1.getProduct(i));
            } // If our start position is larger
            else if (startPos > endPos) {
                if (!(i < startPos && i > endPos)) {
                    child.setProduct(i, parent1.getProduct(i));
                }
            }
        }

        // Loop through parent2's product path
        for (int i = 0; i < parent2.pathSize(); i++) {
            // If child doesn't have the product add it
            if (!child.containsProduct(parent2.getProduct(i))) {
                // Loop to find a spare position in the child's path
                for (int ii = 0; ii < child.pathSize(); ii++) {
                    // Spare position found, add product
                    if (child.getProduct(ii) == null) {
                        child.setProduct(ii, parent2.getProduct(i));
                        break;
                    }
                }
            }
        }
        return child;
    }

    /**
     * Mutate a path using swap mutation
     * @param path
     */
    private static void mutate(IndividualPath path) {
        // Loop through path products
        for(int tourPos1=1; tourPos1 < path.pathSize()-1; tourPos1++){
            // Apply mutation rate
            if(Math.random() < mutationRate){
                // Get a second random position in the path

                int tourPos2 = (int) (path.pathSize() * Math.random());

                if(tourPos2!=0 || tourPos2!=path.pathSize()-1)
                {
                    // Get the products at target position in path
                    Product city1 = path.getProduct(tourPos1);
                    Product city2 = path.getProduct(tourPos2);

                    // Swap them around
                    path.setProduct(tourPos2, city1);
                    path.setProduct(tourPos1, city2);
                }
            }
        }
    }

    /**
     * Selects candidate path for crossover
     * @param pop
     * @return fittest path
     */
    private static IndividualPath tournamentSelection(Population pop) {
        // Create a tournament population
        Population tournament = new Population(tournamentSize, false);
        // For each place in the tournament get a random candidate path and
        // add it
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.populationSize());
            tournament.savePath(i, pop.getPath(randomId));
        }
        // Get the fittest path
        IndividualPath fittest = tournament.getFittest();
        return fittest;
    }
}
