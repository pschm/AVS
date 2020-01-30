package json_import;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

	@SerializedName("vertices")
	@Expose
	private List<VertexImport> vertices = null;
	@SerializedName("edges")
	@Expose
	private List<EdgeImport> edges = null;

	public List<VertexImport> getVertices() {
		return vertices;
	}

	public void setVertices(List<VertexImport> vertices) {
		this.vertices = vertices;
	}

	public List<EdgeImport> getEdges() {
		return edges;
	}

	public void setEdges(List<EdgeImport> edges) {
		this.edges = edges;
	}

}