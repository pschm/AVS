public class Worker {

	public static void main(String[] args) {
		// Create and add our products
		
		//TODO Need a List of Products from 'Master (Scheduler)' with x and y coordinates.
		
        Product Product = new Product(60, 200);
        PathManager.addProduct(Product);
        Product Product2 = new Product(180, 200);
        PathManager.addProduct(Product2);
        Product Product3 = new Product(80, 180);
        PathManager.addProduct(Product3);
        Product Product4 = new Product(140, 180);
        PathManager.addProduct(Product4);
        Product Product5 = new Product(20, 160);
        PathManager.addProduct(Product5);
        Product Product6 = new Product(100, 160);
        PathManager.addProduct(Product6);
        Product Product7 = new Product(200, 160);
        PathManager.addProduct(Product7);
        Product Product8 = new Product(140, 140);
        PathManager.addProduct(Product8);
        Product Product9 = new Product(40, 120);
        PathManager.addProduct(Product9);
        Product Product10 = new Product(100, 120);
        PathManager.addProduct(Product10);
        Product Product11 = new Product(180, 100);
        PathManager.addProduct(Product11);
        Product Product12 = new Product(60, 80);
        PathManager.addProduct(Product12);
        Product Product13 = new Product(120, 80);
        PathManager.addProduct(Product13);
        Product Product14 = new Product(180, 60);
        PathManager.addProduct(Product14);
        Product Product15 = new Product(20, 40);
        PathManager.addProduct(Product15);
        Product Product16 = new Product(100, 40);
        PathManager.addProduct(Product16);
        Product Product17 = new Product(200, 40);
        PathManager.addProduct(Product17);
        Product Product18 = new Product(20, 20);
        PathManager.addProduct(Product18);
        Product Product19 = new Product(60, 20);
        PathManager.addProduct(Product19);
        Product Product20 = new Product(160, 20);
        PathManager.addProduct(Product20);

        // Initialize population
        Population pop = new Population(50, true);
        System.out.println("Initial distance: " + pop.getFittest().getDistance());

        // Evolve population for 100 generations
        pop = GA.evolvePopulation(pop);
        for (int i = 0; i < 100; i++) {
            pop = GA.evolvePopulation(pop);
        }

        // Print final results
        System.out.println("Finished");
        System.out.println("Final distance: " + pop.getFittest().getDistance());
        System.out.println("Solution:");
        System.out.println(pop.getFittest());
	}

}
