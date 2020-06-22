package application.model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;

public class ThreadTimerControl implements Runnable {

	private TabPane tabPaneExo;
	private Text tempsTextSection;
	private List<Section> sections;

	public ThreadTimerControl(TabPane tabPaneExo, Text tempsTextSection, List<Section> sections) {
		this.tabPaneExo = tabPaneExo;
		this.tempsTextSection = tempsTextSection;
		this.sections = sections;
	}

	@Override
	public void run() {
		while (tabPaneExo.getScene().getWindow().isShowing()) {
			int tabid = tabPaneExo.getSelectionModel().getSelectedIndex();
			if (!sections.get(tabid).getTimeLimiteCode().equals("00:00:00")) {
				
				if (sections.get(tabid).getTimeLeft() <= 0) {
					tempsTextSection.setText(Lang.TEMPS_ECOULE);
					tabPaneExo.getTabs().get(tabid).setDisable(true);
					if (!getNotDisabledTabsId(tabPaneExo).isEmpty()) {
						tabPaneExo.getSelectionModel().select(getNotDisabledTabsId(tabPaneExo).get(0));
					}
				} else {
					tempsTextSection.setText(format(sections.get(tabid).getTimeLeft()));
				}
			}else {
				tempsTextSection.setText(Lang.NON_CHRONOMETRER);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			
		}
	}

	private List<Integer> getNotDisabledTabsId(TabPane tab) {
		List<Integer> ids = new ArrayList<>();
		int i = 0;
		for (Tab t : tab.getTabs()) {
			if (!t.isDisabled())
				ids.add(i);
			i++;
		}
		return ids;
	}

	private String format(long timeLeft) {
		timeLeft = timeLeft / 1000;
		long h = (timeLeft - (timeLeft % 3600)) / 3600;
		timeLeft -= h * 3600;
		long m = (timeLeft - (timeLeft % 60)) / 60;
		timeLeft -= m * 60;
		long s = timeLeft;
		return h + ":" + m + ":" + s;
	}

}
