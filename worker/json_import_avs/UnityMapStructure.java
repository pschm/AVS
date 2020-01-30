package json_import_avs;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UnityMapStructure {

    @SerializedName("Items")
    private List<Product> products;

    @SerializedName("navMesh")
    private List<MeshNode> navMesh;

    public UnityMapStructure(List<Product> products, List<MeshNode> navMesh) {
        this.products = products;
        this.navMesh = navMesh;
    }

    public UnityMapStructure(List<MeshNode> navMesh) {
        this.navMesh = navMesh;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<MeshNode> getNavMesh() {
        return navMesh;
    }

    public void setNavMesh(List<MeshNode> navMesh) {
        this.navMesh = navMesh;
    }
}
