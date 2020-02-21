package json_import_avs;

import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("position")
    private final UnityPos position;
    @SerializedName("name")
    private final String name;

    public Product(UnityPos position, String name) {
        this.position = position;
        this.name = name;
    }

    // TODO ggf. weitere funktionen übernehmen...
}
