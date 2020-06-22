package application.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.TimerTask;

public class SectionTimer extends TimerTask {
	
	private LocalTime time;
	private boolean running;
	private Section section;
	
	public SectionTimer(LocalTime time, Section section) {
		this.time = time;
		this.section = section;
	}
	
	public void start(boolean start) {
		this.running = start;
	}
	
	public LocalTime getTimeObject() {
		return time;
	}
	
	public String getTimeText() {
		return time.format(DateTimeFormatter.ISO_TIME);
	}
	
	@Override
	public void run() {
		if(time.getHour() == 0 && time.getMinute() == 0 && time.getSecond() == 0 && time.getNano() == 0) {
			//ApplicationController.lockAll();
			section.lock();
			cancel();
		}else {
			time = time.minus(1, ChronoUnit.SECONDS);
		}
	}

}
