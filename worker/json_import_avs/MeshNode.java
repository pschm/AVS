package json_import_avs;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MeshNode {

    @SerializedName("id")
    private final int id;
    @SerializedName("position")
    private final UnityPos pos;
    @SerializedName("nextNodes")
    private final List<Integer> nextNodes;

    public MeshNode(int id, json_import_avs.UnityPos pos, List<Integer> nextNodes) {
        this.id = id;
        this.pos = pos;
        this.nextNodes = nextNodes;
    }

    public int getId() {
        return id;
    }

    public json_import_avs.UnityPos getPos() {
        return pos;
    }

    public List<Integer> getNextNodes() {
        return nextNodes;
    }

    @Override
    public String toString() {
        return id + " " + pos;
    }
}
