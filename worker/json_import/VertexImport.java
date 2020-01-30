package json_import;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VertexImport {

	@SerializedName("id")
	@Expose
	private Integer id;
	@SerializedName("position")
	@Expose
	private PositionImport position;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public PositionImport getPosition() {
		return position;
	}

	public void setPosition(PositionImport position) {
		this.position = position;
	}

}