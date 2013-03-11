package hw7;

public class ColorBeanImmutable {
	private String bgColor = "green";
	private String fgColor = "yellow";

	public ColorBeanImmutable(String bgColor, String fgColor) {
		if ((bgColor != null) && !(bgColor.trim().equals(""))) {
			this.bgColor = bgColor;
		}
		if ((fgColor != null) && !(fgColor.trim().equals(""))) {
			this.fgColor = fgColor;
		}
	}

	public String getBgColor() {
		return bgColor;
	}

	public String getFgColor() {
		return fgColor;
	}

}
