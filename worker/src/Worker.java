import java.io.IOException;

public class Worker {
    public static void main(String[] args) throws IOException {

        Product Product = new Product(60, 200,"Product");
        PathManager.addProduct(Product);
        Product Product2 = new Product(180, 200,"Product2");
        PathManager.addProduct(Product2);
        Product Product3 = new Product(80, 180,"Product3");
        PathManager.addProduct(Product3);
        Product Product4 = new Product(140, 180,"Product4");
        PathManager.addProduct(Product4);
        Product Product5 = new Product(20, 160,"Product5");
        PathManager.addProduct(Product5);
        Product Product6 = new Product(100, 160,"Product6");
        PathManager.addProduct(Product6);
        Product Product7 = new Product(200, 160,"Product7");
        PathManager.addProduct(Product7);
        Product Product8 = new Product(140, 140,"Product8");
        PathManager.addProduct(Product8);
        Product Product9 = new Product(40, 120,"Product9");
        PathManager.addProduct(Product9);
        Product Product10 = new Product(100, 120,"Product10");
        PathManager.addProduct(Product10);
        Product Product11 = new Product(180, 100,"Product11");
        PathManager.addProduct(Product11);
        Product Product12 = new Product(60, 80,"Product12");
        PathManager.addProduct(Product12);
        Product Product13 = new Product(120, 80,"Product13");
        PathManager.addProduct(Product13);
        Product Product14 = new Product(180, 60,"Product14");
        PathManager.addProduct(Product14);
        Product Product15 = new Product(20, 40,"Product15");
        PathManager.addProduct(Product15);
        Product Product16 = new Product(100, 40,"Product16");
        PathManager.addProduct(Product16);
        Product Product17 = new Product(200, 40,"Product17");
        PathManager.addProduct(Product17);
        Product Product18 = new Product(20, 20,"Product18");
        PathManager.addProduct(Product18);
        Product Product19 = new Product(60, 20,"Product19");
        PathManager.addProduct(Product19);
        Product Product20 = new Product(160, 20,"Product20");
        PathManager.addProduct(Product20);

        SchedulerAPI api = new SchedulerAPI();

        api.putWorker("23423-23423-sada-123",new IndividualPath(PathManager.getPath()));

        //api.getMap();

        //----------------------------------------------------------------------------------------------------------------------------------------------------------------------


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
