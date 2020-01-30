import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("position")
    @Expose
    Position position;
    @SerializedName("name")
    @Expose
    String name;
    
    /**
     * Constructs a randomly placed product
     * @param productName
     */
    public Product(String productName){
        this.position = new Position((double) (Math.random()*200), (double) (Math.random()*200));
        this.name = productName;
    }
    
    /**
     * Constructs a product at chosen x, y location
     * @param x
     * @param y
     * @param productName
     */
    public Product(double x, double y, String productName){
        this.position = new Position(x, y);
        this.name = productName;
    }
    
    /**
     * Gets product's x coordinate
     * @return X (INT)
     */
    public double getX(){
        return this.position.getX();
    }
    
    /**
     * Gets product's y coordinate
     * @return Y (INT)
     */
    public double getY(){
        return this.position.getY();
    }

    public String getProductName(){
        return this.name;
    }
    
    /**
     * Gets the distance to given product
     * @param product
     * @return Distance
     */
    public double distanceTo(Product product){
        double xDistance = Math.abs(getX() - product.getX());
        double yDistance = Math.abs(getY() - product.getY());
        double distance = Math.sqrt( (xDistance*xDistance) + (yDistance*yDistance) );
        
        return distance;
    }
    
    @Override
    public String toString(){
        return getX()+","+getY()+","+getProductName();
    }
}
