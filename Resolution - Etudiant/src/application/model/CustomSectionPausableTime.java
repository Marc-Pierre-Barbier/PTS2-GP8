package application.model;
import java.util.ArrayList;

public class CustomSectionPausableTime {
	
	private ArrayList<Long> start;
	private ArrayList<Long> stop;
	
	public CustomSectionPausableTime() {
		start = new ArrayList<>();
		stop = new ArrayList<>();
	}
	
	public void start() throws AlredyRunningExeption {
		if(start.size() <= stop.size())start.add(System.currentTimeMillis());
		else throw new AlredyRunningExeption();
	}
	
	public void pause() throws NotRunningExeption {
		if(start.size() > stop.size())stop.add(System.currentTimeMillis());
		else throw new NotRunningExeption();
	}
	
	
	/**
	 * retourne le temps mesuré avec le chrono
	 * @return en millis
	 */
	public long getTimeElipsed()  {
		long time=0;
		
		for(int i=0 ; i< start.size() ;i++) {
			try {
				time += stop.get(i) - start.get(i);
			}catch (IndexOutOfBoundsException e) {
				time += System.currentTimeMillis() - start.get(i);
			}
		}
		return time;
	}

}
