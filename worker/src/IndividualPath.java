import java.util.ArrayList;
import java.util.Collections;

public class IndividualPath {
	 // Holds our IndividualPath of products
    private ArrayList<Product> IndividualPath = new ArrayList<Product>();
    // Cache
    private double fitness = 0;
    private int distance = 0;
    
    /**
     * Constructs a blank IndividualPath
     */
    public IndividualPath(){
        for (int i = 0; i < PathManager.numberOfProducts(); i++) {
            IndividualPath.add(null);
        }
    }
    
    /**
     * Constructs a IndividualPath
     * @param IndividualPath
     */
    public IndividualPath(ArrayList<Product> IndividualPath){
        this.IndividualPath = IndividualPath;
    }

    /**
     * Creates a random individual
     */
    public void generateIndividual() {
        // Loop through all our destination products and add them to our IndividualPath
        for (int productIndex = 0; productIndex < PathManager.numberOfProducts(); productIndex++) {
          setProduct(productIndex, PathManager.getProduct(productIndex));
        }
        // Randomly reorder the IndividualPath
        Collections.shuffle(IndividualPath);
    }

    /**
     * Gets a product from the IndividualPath
     * @param tourPosition
     * @return Product
     */
    public Product getProduct(int tourPosition) {
        return (Product)IndividualPath.get(tourPosition);
    }

    /**
     * Sets a product in a certain position within a IndividualPath
     * @param IndividualPathPosition
     * @param product
     */
    public void setProduct(int IndividualPathPosition, Product product) {
        IndividualPath.set(IndividualPathPosition, product);
        // If the IndividualPaths been altered we need to reset the fitness and distance
        fitness = 0;
        distance = 0;
    }
    
    /**
     * Gets the IndividualPaths fitness
     * @return Fitness (Double)
     */
    public double getFitness() {
        if (fitness == 0) {
            fitness = 1/(double)getDistance();
        }
        return fitness;
    }
    
    /**
     * Gets the total distance of the IndividualPath
     * @return Distance (INT)
     */
    public int getDistance(){
        if (distance == 0) {
            int IndividualPathDistance = 0;
            // Loop through our IndividualPath's products
            for (int productIndex=0; productIndex < pathSize(); productIndex++) {
                // Get product we're travelling from
            	Product fromProduct = getProduct(productIndex);
                // Product we're travelling to
            	Product destinationProduct;
                // Check we're not on our IndividualPath's last product, if we are set our
                // IndividualPath's final destination product to our starting product
                if(productIndex+1 < pathSize()){
                    destinationProduct = getProduct(productIndex+1);
                }
                else{
                    destinationProduct = getProduct(0);
                }
                // Get the distance between the two products
                IndividualPathDistance += fromProduct.distanceTo(destinationProduct);
            }
            distance = IndividualPathDistance;
        }
        return distance;
    }

    /**
     * Get number of products on our IndividualPath
     * @return
     */
    public int pathSize() {
        return IndividualPath.size();
    }
    
    /**
     * Check if the IndividualPath contains a product
     * @param product
     * @return Boolean
     */
    public boolean containsProduct(Product product){
        return IndividualPath.contains(product);
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
