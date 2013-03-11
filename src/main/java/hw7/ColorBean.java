package hw7;

import java.io.*;

public class ColorBean implements Serializable {
	private static final long serialVersionUID = -2688265122694182708L;
	private String bgColor = "green";
	private String fgColor = "yellow";

	public String getBgColor() {
		return bgColor;
	}

	public void setBgColor(String bgColor) {
		if ((bgColor != null) && !(bgColor.trim().equals(""))) {
			this.bgColor = bgColor;
		}
	}
	
	public String getFgColor() {
		return fgColor;
	}

	public void setFgColor(String fgColor) {
		if ((fgColor != null) && !(fgColor.trim().equals(""))) {
			this.fgColor = fgColor;
		}
	}
}
