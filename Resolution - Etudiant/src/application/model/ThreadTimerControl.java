package application.model;

import java.util.List;

import javafx.scene.control.TabPane;
import javafx.scene.text.Text;

public class ThreadTimerControl implements Runnable{

	private TabPane tabPaneExo;
	private Text tempsTextSection;
	private List<Section> sections;
	
	public ThreadTimerControl(TabPane tabPaneExo, Text tempsTextSection,List<Section> sections) {
		this.tabPaneExo = tabPaneExo;
		this.tempsTextSection = tempsTextSection;
		this.sections=sections;
	}

	@Override
	public void run() {
		while (true) {
			tempsTextSection.setText(format(sections.get(tabPaneExo.getSelectionModel().getSelectedIndex()).getTimeLeft()));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
	}

	private String format(long timeLeft) {
		timeLeft = timeLeft/1000;
		long h =(timeLeft-(timeLeft%3600))/3600;
		timeLeft -= h*3600;
		long m =(timeLeft-(timeLeft%60))/60;
		timeLeft -= m*60;
		long s =timeLeft;
		return h+":"+m+":"+s;
	}
	
}
