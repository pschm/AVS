public class Product {
	int x;
    int y;
    
    // Constructs a randomly placed product
    public Product(){
        this.x = (int)(Math.random()*200);
        this.y = (int)(Math.random()*200);
    }
    
    // Constructs a product at chosen x, y location
    public Product(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    // Gets product's x coordinate
    public int getX(){
        return this.x;
    }
    
    // Gets product's y coordinate
    public int getY(){
        return this.y;
    }
    
    // Gets the distance to given product
    public double distanceTo(Product product){
        int xDistance = Math.abs(getX() - product.getX());
        int yDistance = Math.abs(getY() - product.getY());
        double distance = Math.sqrt( (xDistance*xDistance) + (yDistance*yDistance) );
        
        return distance;
    }
    
    @Override
    public String toString(){
        return getX()+", "+getY();
    }
}
