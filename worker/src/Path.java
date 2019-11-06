import java.util.ArrayList;
import java.util.Collections;

public class Path {
	 // Holds our path of products
    private ArrayList<Product> path = new ArrayList<Product>();
    // Cache
    private double fitness = 0;
    private int distance = 0;
    
    // Constructs a blank path
    public Path(){
        for (int i = 0; i < PathManager.numberOfProducts(); i++) {
            path.add(null);
        }
    }
    
    public Path(ArrayList<Product> path){
        this.path = path;
    }

    // Creates a random individual
    public void generateIndividual() {
        // Loop through all our destination products and add them to our path
        for (int productIndex = 0; productIndex < PathManager.numberOfProducts(); productIndex++) {
          setProduct(productIndex, PathManager.getProduct(productIndex));
        }
        // Randomly reorder the path
        Collections.shuffle(path);
    }

    // Gets a product from the path
    public Product getProduct(int tourPosition) {
        return (Product)path.get(tourPosition);
    }

    // Sets a product in a certain position within a path
    public void setProduct(int pathPosition, Product product) {
        path.set(pathPosition, product);
        // If the paths been altered we need to reset the fitness and distance
        fitness = 0;
        distance = 0;
    }
    
    // Gets the paths fitness
    public double getFitness() {
        if (fitness == 0) {
            fitness = 1/(double)getDistance();
        }
        return fitness;
    }
    
    // Gets the total distance of the path
    public int getDistance(){
        if (distance == 0) {
            int pathDistance = 0;
            // Loop through our path's products
            for (int productIndex=0; productIndex < pathSize(); productIndex++) {
                // Get product we're travelling from
            	Product fromProduct = getProduct(productIndex);
                // Product we're travelling to
            	Product destinationProduct;
                // Check we're not on our path's last product, if we are set our
                // path's final destination product to our starting product
                if(productIndex+1 < pathSize()){
                    destinationProduct = getProduct(productIndex+1);
                }
                else{
                    destinationProduct = getProduct(0);
                }
                // Get the distance between the two products
                pathDistance += fromProduct.distanceTo(destinationProduct);
            }
            distance = pathDistance;
        }
        return distance;
    }

    // Get number of products on our path
    public int pathSize() {
        return path.size();
    }
    
    // Check if the path contains a product
    public boolean containsProduct(Product product){
        return path.contains(product);
    }
    
    @Override
    public String toString() {
        String geneString = "|";
        for (int i = 0; i < pathSize(); i++) {
            geneString += getProduct(i)+"|";
        }
        return geneString;
    }
}
