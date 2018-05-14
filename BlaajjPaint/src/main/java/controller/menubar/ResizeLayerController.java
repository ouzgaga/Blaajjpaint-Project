package controller.menubar;

import controller.Layer;
import controller.Project;
import controller.history.ICmd;
import controller.history.RecordCmd;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import utils.UndoException;


public class ResizeLayerController {
	
	@FXML
	private TextField textFieldWidth;
	
	@FXML
	private TextField textFieldHeight;
	
	@FXML
	private CheckBox checkBoxRatio;
	
	@FXML
	private CheckBox checkBoxResizeImage;
	
	@FXML
	private Button validateResizeButton;
	
	private double ratioImage;
	
	private class ResizeSave extends ICmd{
		
			private Layer currentLayer;
			private double oldWidth;
			private double oldHeight;
			private Image oldImage;
			private double newWidth;
			private double newHeight;
		private Image newImage;
		
		private ResizeSave() {
				currentLayer = Project.getInstance().getCurrentLayer();
				oldWidth = currentLayer.getWidth();
				oldHeight = currentLayer.getHeight();
				oldImage = currentLayer.createImageFromCanvas(1);
			}
			
			@Override
			public void execute() {
				newWidth = currentLayer.getWidth();
				newHeight = currentLayer.getHeight();
				newImage = currentLayer.createImageFromCanvas(1);
				RecordCmd.getInstance().saveCmd(this);
			}
			
			@Override
			public void undo() throws UndoException {
				currentLayer.getGraphicsContext2D().clearRect(0, 0, newWidth, newHeight);
				currentLayer.setWidth(oldWidth);
				currentLayer.setHeight(oldHeight);
				currentLayer.getGraphicsContext2D().drawImage(oldImage, 0, 0);
			}
			
			@Override
			public void redo() throws UndoException {
				currentLayer.getGraphicsContext2D().clearRect(0, 0, oldWidth, oldHeight);
				currentLayer.setWidth(newWidth);
				currentLayer.setHeight(newHeight);
				currentLayer.getGraphicsContext2D().drawImage(newImage, 0, 0);
			}
			
			public String toString() {
				return "Redimensionnement de " + currentLayer.toString();
			}
		
	}
	
	
	@FXML
	private void initialize() {
		
		int width = (int) Project.getInstance().getCurrentLayer().getWidth();
		int height = (int) Project.getInstance().getCurrentLayer().getHeight();
		
		ratioImage = (double) width / height;
		
		// affiche la taille du calque actuel
		textFieldWidth.setText(Integer.toString(width));
		textFieldHeight.setText(Integer.toString(height));
		
		textFieldHeight.setDisable(true);
		
		checkBoxRatio.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				textFieldHeight.setDisable(checkBoxRatio.isSelected());
				
				if (textFieldHeight.isDisable()) {
					if (!textFieldWidth.getText().isEmpty()) {
						textFieldHeight.setText(Integer.toString((int) (Math.round(Double.valueOf(textFieldWidth.getText()) / ratioImage))));
					}
				}
			}
		});
		
		textFieldWidth.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
			                    String newValue) {
				if (!newValue.matches("\\d*")) {
					textFieldWidth.setText(newValue.replaceAll("[^\\d]", ""));
				} else {
					if (textFieldHeight.isDisable()) {
						if (!textFieldWidth.getText().isEmpty()) {
							textFieldHeight.setText(Integer.toString((int) (Math.round(Double.valueOf(textFieldWidth.getText()) / ratioImage))));
						}
					}
				}
			}
		});
		
		textFieldHeight.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
			                    String newValue) {
				if (!newValue.matches("\\d*")) {
					textFieldHeight.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});
		
		
	}
	
	
	@FXML
	public void validateResize() {
		if (!textFieldWidth.getText().isEmpty() && !textFieldHeight.getText().isEmpty()) {
			
			ResizeSave rs = new ResizeSave();
			Layer currentLayer = Project.getInstance().getCurrentLayer();
			
			if (checkBoxResizeImage.isSelected()) {
				Image image = Project.getInstance().getCurrentLayer().createImageFromCanvas(1);
				ImageView image2 = new ImageView(image);
				
				image2.setFitWidth(Double.valueOf(textFieldWidth.getText()));
				image2.setFitHeight(Double.valueOf(textFieldHeight.getText()));
				
				currentLayer.getGraphicsContext2D().clearRect(0, 0, currentLayer.getWidth(), currentLayer.getHeight());
				currentLayer.setWidth(Double.valueOf(textFieldWidth.getText()));
				currentLayer.setHeight(Double.valueOf(textFieldHeight.getText()));
				
				currentLayer.getGraphicsContext2D().drawImage(image2.getImage(), 0, 0, image2.getFitWidth(), image2.getFitHeight());
				
			} else {
				currentLayer.setWidth(Double.valueOf(textFieldWidth.getText()));
				currentLayer.setHeight(Double.valueOf(textFieldHeight.getText()));
			}
			rs.execute();
		}
		Stage stage = (Stage) validateResizeButton.getScene().getWindow();
		stage.close();
	}
	
	
	
}