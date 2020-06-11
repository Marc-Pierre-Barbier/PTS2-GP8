package application.model;

import java.time.LocalTime;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class timeCodeTab extends Tab {
	private Slider timeFin;

	public timeCodeTab(Section s, int dureVideo) {
		super("Section " + s.getidTab());
		Slider timeDeb = new Slider();
		Text sliderDebStatus = new Text();
		Text errorText = new Text();
		errorText.setVisible(false);
		errorText.setFill(Color.RED);
		
		timeDeb.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (oldValue != newValue) {
					if (timeFin.getValue() < timeDeb.getValue()) {
						timeFin.setValue(timeDeb.getValue());
					}

					LocalTime timecode = LocalTime.ofSecondOfDay((long) (timeDeb.getValue() * dureVideo / 100));
					sliderDebStatus.setText(timecode.toString());
					s.setTimeStart(timecode.toString());
				}
			}
		});
		timeFin = new Slider();
		Text sliderFinStatus = new Text();
		timeFin.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (oldValue != newValue) {
					if (timeDeb.getValue() < timeFin.getValue()) {
						LocalTime timecode = LocalTime.ofSecondOfDay((long) (timeFin.getValue() * dureVideo / 100));
						sliderFinStatus.setText(timecode.toString());
						s.setTimeStop(timecode.toString());
						errorText.setVisible(false);
					}else {
						LocalTime timecode = getLocalTimeOf(s.getTimeStop());
						timeFin.setValue(timecode.toSecondOfDay() * 100 / dureVideo);
						errorText.setVisible(true);
						errorText.setText(Lang.ERROR_TIME_SLIDER_GREATER);
					}
				}

			}
		});
		Text TtimedebutTxt = new Text(Lang.NOM_DEBUT_TIMECODE);
		Text TtimefinTxt = new Text(Lang.NOM_FIN_TIMECODE);
		VBox deb = new VBox();
		VBox fin = new VBox();
		deb.getChildren().addAll(TtimedebutTxt, timeDeb, sliderDebStatus);
		fin.getChildren().addAll(TtimefinTxt, timeFin, sliderFinStatus);

		LocalTime timecode = getLocalTimeOf(s.getTimeStart());
		timeDeb.setValue(timecode.toSecondOfDay() * 100 / dureVideo);
		sliderDebStatus.setText(timecode.toString());

		
		timecode = getLocalTimeOf(s.getTimeStop());
		timeFin.setValue(timecode.toSecondOfDay() * 100 / dureVideo);
		sliderFinStatus.setText(timecode.toString());

		
		VBox root = new VBox();
		HBox core = new HBox();
		core.getChildren().addAll(deb, fin);
		root.getChildren().addAll(core,errorText);
		super.setContent(root);
	}
	
	/**
	 * @param str time code en hh:mm:ss
	 * @return LocalTime
	 */
	private LocalTime getLocalTimeOf(String str) {
		int hour = Integer.parseInt(str.charAt(0) + "" + str.charAt(1));
		int minute = Integer.parseInt(str.charAt(3) + "" + str.charAt(4));
		int second = Integer.parseInt(str.charAt(6) + "" + str.charAt(7));
		return LocalTime.of(hour, minute, second);
	}
}