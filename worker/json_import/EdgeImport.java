package json_import;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EdgeImport {

	@SerializedName("id")
	@Expose
	private Integer id;
	@SerializedName("start")
	@Expose
	private Integer start;
	@SerializedName("end")
	@Expose
	private Integer end;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getEnd() {
		return end;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}

}