package controller.rightMenu;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class HistoryController {
	
	@FXML
	private HBox historyElem;
	
	@FXML
	private Label label;
	
	public HBox getHistoryElem() {
		return historyElem;
	}
	
	@FXML
	private void initialize() {
	
	}
	
	@FXML
	void handleMouseClicked(MouseEvent event) {
	
	}
	
	public void setLabel(String label) {
		this.label.setText(label);
	}
}