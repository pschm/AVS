public class Product {
	int x;
    int y;
    String productName;
    
    /**
     * Constructs a randomly placed product
     * @param productName
     */
    public Product(String productName){
        this.x = (int)(Math.random()*200);
        this.y = (int)(Math.random()*200);
        this.productName = productName;
    }
    
    /**
     * Constructs a product at chosen x, y location
     * @param x
     * @param y
     * @param productName
     */
    public Product(int x, int y, String productName){
        this.x = x;
        this.y = y;
        this.productName = productName;
    }
    
    /**
     * Gets product's x coordinate
     * @return X (INT)
     */
    public int getX(){
        return this.x;
    }
    
    /**
     * Gets product's y coordinate
     * @return Y (INT)
     */
    public int getY(){
        return this.y;
    }

    public String getProductName(){
        return this.productName;
    }
    
    /**
     * Gets the distance to given product
     * @param product
     * @return Distance
     */
    public double distanceTo(Product product){
        int xDistance = Math.abs(getX() - product.getX());
        int yDistance = Math.abs(getY() - product.getY());
        double distance = Math.sqrt( (xDistance*xDistance) + (yDistance*yDistance) );
        
        return distance;
    }
    
    @Override
    public String toString(){
        return getX()+","+getY()+","+getProductName();
    }
}
