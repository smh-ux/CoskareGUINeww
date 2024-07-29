package org.openjfx;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Functions {
	public DropShadow shadow() {
		DropShadow shadow = new DropShadow();
		shadow.setRadius(10.0);
	    shadow.setOffsetX(5.0);
	    shadow.setOffsetY(5.0);
	    shadow.setColor(Color.color(0.4, 0.4, 0.4));
		
		return shadow;
	}
	
	public DropShadow shadowHeader() {
		DropShadow shadow = new DropShadow();
		shadow.setRadius(10.0);
	    shadow.setOffsetX(0);
	    shadow.setOffsetY(0);
	    shadow.setColor(Color.color(0, 0, 0.4));
		
		return shadow;
	}
	
	public Text textNormal(Text texttt) {
		texttt.setFont(Font.font("Arial", 22));
		texttt.setFill(Color.BLACK);
		
		return texttt;
	}
	
	public Text textBold(String texttt) {
		Text text = new Text();
		text.setText(texttt);
		text.setFont(Font.font("Arial", FontWeight.BOLD, 22));
		text.setFill(Color.BLACK);
		
		return text;
	}
	
	public VBox vboxText(Text add, Text add2, int height, int width) {
		VBox vbox = new VBox();
		vbox.getChildren().addAll(add, add2);
		vbox.setPrefHeight(height);
		vbox.setPrefWidth(width);
		vbox.setEffect(shadow());
		vbox.setStyle("-fx-background-color: #FFF;");
		vbox.setAlignment(javafx.geometry.Pos.CENTER);
		
		return vbox;
	}
	
	public Button createButton(String name, int height, int width) {
		Button btn = new Button(name);
		btn.setPrefHeight(height);
		btn.setPrefWidth(width);
		btn.setEffect(shadow());
		btn.setStyle("-fx-background-color: #000; -fx-text-fill: #FFF; -fx-font-weight: bold;");
		btn.setOnMouseExited(event -> {btn.setStyle("-fx-background-color: #000; -fx-text-fill: #FFF; -fx-font-weight: bold;");});
		
		return btn;
	}
	
	public Button createHeaderButton(String name, int height, int width) {
		Button btn = new Button(name);
		btn.setPrefHeight(height);
		btn.setPrefWidth(width);
		btn.setEffect(shadowHeader());
		btn.setStyle("-fx-background-color: #2B3D53; -fx-text-fill: #FFF; -fx-font-weight: bold;");
		btn.setOnMouseEntered(event -> {btn.setStyle("-fx-background-color: #FFF; -fx-text-fill: #2B3D53; -fx-font-weight: bold;");});
		btn.setOnMouseExited(event -> {btn.setStyle("-fx-background-color: #2B3D53; -fx-text-fill: #FFF; -fx-font-weight: bold;");});
		
		return btn;
	}
	
	public HBox header(Stage stage) {
		Button btn1 = createButton("Situation", 20, 100);
		Button btn2 = createButton("Senario", 20, 100);
		Button btn3 = createButton("Mapping", 20, 100);
		Button btn4 = createButton("Settings", 20, 100);
		
		HBox header = new HBox();
		header.getChildren().addAll(btn1, btn2, btn3, btn4);
		header.setStyle("-fx-background-color: #2B3D53;");
		header.setPrefWidth(1920);
		header.setPrefHeight(80);
		header.setAlignment(javafx.geometry.Pos.CENTER);
		HBox.setMargin(btn1, new Insets(0, 0, 0, 0));
		HBox.setMargin(btn2, new Insets(0, 0, 0, 20));
		HBox.setMargin(btn3, new Insets(0, 0, 0, 20));
		HBox.setMargin(btn4, new Insets(0, 0, 0, 20));
		
		return header;
	}
	
	
}
