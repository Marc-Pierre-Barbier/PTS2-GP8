package application.model;

public class timeCode {
	private int hours;
	private int minutes;
	
	public timeCode() {
		setHours("0");
		setMinutes("0");
	}
	
	public void setHours(String hours) {
		try {
			this.hours = Integer.parseInt(hours);
		}catch (Exception e) {
			this.hours = 00;
		}
	}
	
	public void setMinutes(String minutes) {
		try {
			this.minutes = Integer.parseInt(minutes);
		}catch (Exception e) {
			this.minutes = 00;
		}
	}
	
	
	@Override
	public String toString() {
		String out = "00:";
		if(hours >= 10)out += hours + ":";
		if(hours < 10)out += "0"+hours + ":";
		if(minutes >= 10)out += minutes;
		if(minutes < 10)out += "0"+minutes;
		return out;
	}
}
