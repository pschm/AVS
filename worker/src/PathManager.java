import java.util.ArrayList;

public class PathManager {
	 // Holds our products
    private static ArrayList<Product> destinationProducts = new ArrayList<Product>();

    /**
     * Adds a destination product
     * @param product
     */
    public static void addProduct(Product product) {
    	destinationProducts.add(product);
    }
    
    /**
     * Get a product
     * @param index
     * @return Product
     */
    public static Product getProduct(int index){
        return (Product)destinationProducts.get(index);
    }

    public static ArrayList<Product> getPath() { return destinationProducts; }
    
    /**
     * Get the number of destination products
     * @return Number of Products
     */
    public static int numberOfProducts(){
        return destinationProducts.size();
    }
}
