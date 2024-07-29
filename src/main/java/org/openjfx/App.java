package org.openjfx;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;

public class App extends Application {
	
	private	Text speedText = new Text();
    private Text weightText = new Text();
    private Text batteryText = new Text();
    private Text temperatureText = new Text();
    private Text voltageText = new Text();
    private Text currentText = new Text();
    private Text qrText = new Text();
    private Text vehicleText = new Text();
    private Text timeText = new Text();
    private Text directionText = new Text();
    private Text countOfQRText = new Text();
    private Text percentOfDoneText = new Text();
    private Text scenarioText = new Text();
    
    private String scenarioName = "";
	
	private int seconds = 0;

	@Override
	public void start(Stage stage) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
        	@Override
            public void run() {
                fetchDataAndUpdateUI(speedText, weightText, batteryText, temperatureText, voltageText, currentText, qrText, vehicleText, timeText, directionText, countOfQRText, percentOfDoneText, scenarioText);
            }
        }, 0, 100);

        try {
			situationPage(stage, speedText, weightText, batteryText, temperatureText, voltageText, currentText, qrText, vehicleText, timeText, directionText, countOfQRText, percentOfDoneText, scenarioName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private String formatTime(int seconds, DateTimeFormatter formatter) {
		LocalTime time = LocalTime.ofSecondOfDay(seconds);
		return time.format(formatter);
	}
		
		private void fetchDataAndUpdateUI(Text speedText, Text weightText, Text batteryText, Text temperatureText, Text voltageText, Text currentText, Text qrText, Text vehicleText, Text timeText, Text directionText, Text countOfQRText, Text percentOfDoneText, Text scenarioText) {
	        new Thread(() -> {
	            try {
	                URI uri = new URI("http://127.0.0.1:5000/data");
	                URL url = uri.toURL();
	                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	                connection.setRequestMethod("GET");
	                connection.setRequestProperty("Accept", "application/json");

	                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	                StringBuilder response = new StringBuilder();
	                String responseLine;

	                while ((responseLine = br.readLine()) != null) {
	                    response.append(responseLine.trim());
	                }

	                // Parsing JSON data
	                String jsonResponse = response.toString();
	                Map<String, String> jsonMap = parseJson(jsonResponse);

	                // JavaFX UI update process
	                Platform.runLater(() -> {
	                    speedText.setText(jsonMap.get("speed") + " m/s");
	                    weightText.setText(jsonMap.get("weight") + " kilos");
	                    batteryText.setText("%" + jsonMap.get("battery"));
	                    temperatureText.setText(jsonMap.get("temperature") + " C");
	                    voltageText.setText(jsonMap.get("voltage") + " V");
	                    currentText.setText(jsonMap.get("current") + " A");
	                    qrText.setText(jsonMap.get("qr"));
	                    vehicleText.setText(jsonMap.get("situation"));
	                    directionText.setText(jsonMap.get("direction"));
	                    countOfQRText.setText(jsonMap.get("count of qr"));
	                    scenarioText.setText(jsonMap.get("scenario"));
	                });
	                connection.disconnect();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }).start();
		}
	
	private static Map<String, String> parseJson(String json) {
		Map<String, String> map = new HashMap<>();
		json = json.trim().substring(1, json.length() - 1);
		String[] keyValuePairs = json.split(",");
		
		for (String pairString : keyValuePairs) {
			String[] keyValue = pairString.split(":");
			String key = keyValue[0].trim().replace("\"", "");
			String value = keyValue[1].trim().replace("\"", "");
			map.put(key, value);
		}
		
		return map;
	}
	
	/*
	private Map<String, String> parseJson1(String jsonString) throws JsonMappingException, JsonProcessingException  {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonString, new TypeReference<Map<String, String>>() {});
	}
	*/
	
	  private void sendDataToPythonServer(Map<String, String> data) { 
		  new Thread(() -> { 
			  try { 
				  URI uri = new URI("http://127.0.0.1:5000/data"); 
				  URL url = uri.toURL(); 
				  HttpURLConnection connection = (HttpURLConnection) url.openConnection(); 
				  connection.setRequestMethod("POST");
				  connection.setRequestProperty("Content-Type", "application/json");
				  connection.setDoOutput(true);
	  
				  ObjectMapper objectMapper = new ObjectMapper(); 
				  String jsonString = objectMapper.writeValueAsString(data);
	  
				  try (OutputStream os = connection.getOutputStream()) { 
					  byte[] input = jsonString.getBytes(StandardCharsets.UTF_8); 
					  os.write(input, 0, input.length);
					  }
	  
				  int responseCode = connection.getResponseCode();
				  System.out.println("POST Response Code :: " + responseCode);
	
			  } catch (Exception e) {
				  e.printStackTrace();
			  } 
	       }); 
	   } 
	
	public void emergencyPage(Stage stage) {
		Text text = new Text("Emergency");
		text.setFont(Font.font("Arial", FontWeight.BOLD, 88));
		text.setFill(Color.WHITE);
		
		VBox comp = new VBox(text);
		comp.setAlignment(javafx.geometry.Pos.CENTER);
		comp.setPrefHeight(1010);
		comp.setPrefWidth(1920);
		comp.setStyle("-fx-background-color: #F00");
		
		PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
		delay.setOnFinished(event -> {
			Platform.exit();
		});
		delay.play();
		
		GridPane gpEmergency = new GridPane();
		gpEmergency.add(comp, 0, 0);

		Scene scene = new Scene(gpEmergency, 1920, 1010);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setTitle("Emergency Page");
	    stage.setScene(scene);
	    stage.show();
	}
	
	public void situationPageAfterAfter(Stage stage, ImageView view, Text speed, Text weight, Text battery, Text temperature, Text voltage, Text current, Text qr, Text vehicle, Text time, Text direction, Text countqr, Text percentdone, String scenarioName) {
		Functions fc = new Functions();
		//new Thread(() -> processData(progressBar)).start();
		
		// Header
		Button btn3 = fc.createHeaderButton("Mapping", 20, 100);
		
		btn3.setOnAction(event -> {
			try {
				mappingPage(stage, view, speed, weight, battery, temperature, voltage, current, qr, vehicle, time, direction, countqr, percentdone,  scenarioName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
		
		HBox header = new HBox();
		header.getChildren().addAll(btn3);
		header.setStyle("-fx-background-color: #2B3D53;");
		header.setPrefWidth(1920);
		header.setPrefHeight(80);
		header.setAlignment(javafx.geometry.Pos.CENTER);
		HBox.setMargin(btn3, new Insets(0, 0, 0, 20));
		
		// Top
		VBox vboxTop1 = fc.vboxText(fc.textBold("Speed"), fc.textNormal(speed), 100, 200);
		VBox vboxTop2 = fc.vboxText(fc.textBold("Weight"), fc.textNormal(weight), 100, 200);
		VBox vboxTop3 = fc.vboxText(fc.textBold("Battery"), fc.textNormal(battery), 100, 200);
		VBox vboxTop4 = fc.vboxText(fc.textBold("Temperature"), fc.textNormal(temperature), 100, 200);
		VBox vboxTop5 = fc.vboxText(fc.textBold("Voltage"), fc.textNormal(voltage), 100, 200);
		VBox vboxTop6 = fc.vboxText(fc.textBold("Current"), fc.textNormal(current), 100, 200);
		
		// Mid
		VBox vboxMid1 = fc.vboxText(fc.textBold("Last QR Code"), fc.textNormal(qr), 150, 480);
		
		Button stop = new Button("Stop");
		stop.setStyle("-fx-background-color: #F00; -fx-background-radius: 15px; -fx-text-fill: #FFF; -fx-font-weight: bold; -fx-font-size: 24;");
		stop.setPrefHeight(150);
		stop.setPrefWidth(150); 
		
		stop.setOnAction(event -> {
			emergencyPage(stage);
		});
		
		VBox vboxMid2 = fc.vboxText(fc.textBold("Vehicle Situation"), fc.textNormal(vehicle), 150, 480);
		
		// Bottom
		VBox vboxBot1 = fc.vboxText(fc.textBold("Time"), fc.textNormal(time), 150, 250);
		VBox vboxBot2 = fc.vboxText(fc.textBold("Direction"), fc.textNormal(direction), 150, 250);
		
		// Map
		view.setFitWidth(450);
		view.setFitHeight(350);
			
		HBox top = new HBox();
		top.getChildren().addAll(vboxTop1, vboxTop2, vboxTop3, vboxTop4, vboxTop5, vboxTop6);
		top.setAlignment(javafx.geometry.Pos.CENTER);
		HBox.setMargin(vboxTop1, new Insets(20, 0, 0, 0));
		HBox.setMargin(vboxTop2, new Insets(20, 0, 0, 50));
		HBox.setMargin(vboxTop3, new Insets(20, 0, 0, 50));
		HBox.setMargin(vboxTop4, new Insets(20, 0, 0, 50));
		HBox.setMargin(vboxTop5, new Insets(20, 0, 0, 50));
		HBox.setMargin(vboxTop6, new Insets(20, 0, 0, 50));
		
		HBox mid = new HBox();
		mid.getChildren().addAll(vboxMid1, stop, vboxMid2);
		mid.setAlignment(javafx.geometry.Pos.CENTER);
		HBox.setMargin(vboxMid1, new Insets(20, 0, 0, 0));
		HBox.setMargin(stop, new Insets(20, 0, 0, 60));
		HBox.setMargin(vboxMid2, new Insets(20, 0, 0, 60));
	
		HBox bot = new HBox();
		bot.getChildren().addAll(vboxBot1, vboxBot2);
		bot.setAlignment(javafx.geometry.Pos.CENTER);
		HBox.setMargin(vboxBot1, new Insets(20, 0, 0, 0));
		HBox.setMargin(vboxBot2, new Insets(20, 0, 0, 60));
		
		VBox body = new VBox(top, mid, view, bot);
		body.setAlignment(javafx.geometry.Pos.CENTER);
		VBox.setMargin(view, new Insets(20, 0, 0, 0));
		
		VBox comp = new VBox(header, body);
		
		GridPane gpSituation = new GridPane();
		gpSituation.add(comp, 0, 0);

		Scene scene = new Scene(gpSituation, 1920, 1010);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setTitle("Situation Page");
	    stage.setScene(scene);
	    stage.show();
	}
	
	public void situationPageAfter(Stage stage, ImageView view, Text speed, Text weight, Text battery, Text temperature, Text voltage, Text current, Text qr, Text vehicle, Text time, Text direction, Text countqr, Text percentdone, String scenarioNameeee, Text scenarioText) {
		Functions fc = new Functions();
		//new Thread(() -> processData(progressBar)).start();
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		timeText.setText(formatTime(seconds, formatter));
		
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
			seconds++;
			timeText.setText(formatTime(seconds, formatter));
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
		
		Map<String, String> data = new HashMap<>();
        data.put("scenario", scenarioNameeee);
        
        sendDataToPythonServer(data);
        
        System.out.println(data);
		
		// Header
		Button btn3 = fc.createHeaderButton("Mapping", 20, 100);
		
		btn3.setOnAction(event -> {
			try {
				mappingPage(stage, view, speed, weight, battery, temperature, voltage, current, qr, vehicle, time, direction, countqr, percentdone, scenarioName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
		
		HBox header = new HBox();
		header.getChildren().addAll(btn3);
		header.setStyle("-fx-background-color: #2B3D53;");
		header.setPrefWidth(1920);
		header.setPrefHeight(80);
		header.setAlignment(javafx.geometry.Pos.CENTER);
		HBox.setMargin(btn3, new Insets(0, 0, 0, 20));
		
		// Top
		VBox vboxTop1 = fc.vboxText(fc.textBold("Speed"), fc.textNormal(speed), 100, 200);
		VBox vboxTop2 = fc.vboxText(fc.textBold("Weight"), fc.textNormal(weight), 100, 200);
		VBox vboxTop3 = fc.vboxText(fc.textBold("Battery"), fc.textNormal(battery), 100, 200);
		VBox vboxTop4 = fc.vboxText(fc.textBold("Temperature"), fc.textNormal(temperature), 100, 200);
		VBox vboxTop5 = fc.vboxText(fc.textBold("Voltage"), fc.textNormal(voltage), 100, 200);
		VBox vboxTop6 = fc.vboxText(fc.textBold("Current"), fc.textNormal(current), 100, 200);
		
		// Mid
		VBox vboxMid1 = fc.vboxText(fc.textBold("Last QR Code"), fc.textNormal(qr), 150, 480);
		
		Button stop = new Button("Stop");
		stop.setStyle("-fx-background-color: #F00; -fx-background-radius: 15px; -fx-text-fill: #FFF; -fx-font-weight: bold; -fx-font-size: 24;");
		stop.setPrefHeight(150);
		stop.setPrefWidth(150); 
		
		stop.setOnAction(event -> {
			emergencyPage(stage);
		});
		
		VBox vboxMid2 = fc.vboxText(fc.textBold("Vehicle Situation"), fc.textNormal(vehicle), 150, 480);
		
		// Bottom
		VBox vboxBot1 = fc.vboxText(fc.textBold("Time"), fc.textNormal(time), 150, 250);
		VBox vboxBot2 = fc.vboxText(fc.textBold("Direction"), fc.textNormal(direction), 150, 250);
		
		// Map
		view.setFitWidth(450);
		view.setFitHeight(350);
			
		HBox top = new HBox();
		top.getChildren().addAll(vboxTop1, vboxTop2, vboxTop3, vboxTop4, vboxTop5, vboxTop6);
		top.setAlignment(javafx.geometry.Pos.CENTER);
		HBox.setMargin(vboxTop1, new Insets(20, 0, 0, 0));
		HBox.setMargin(vboxTop2, new Insets(20, 0, 0, 50));
		HBox.setMargin(vboxTop3, new Insets(20, 0, 0, 50));
		HBox.setMargin(vboxTop4, new Insets(20, 0, 0, 50));
		HBox.setMargin(vboxTop5, new Insets(20, 0, 0, 50));
		HBox.setMargin(vboxTop6, new Insets(20, 0, 0, 50));
		
		HBox mid = new HBox();
		mid.getChildren().addAll(vboxMid1, stop, vboxMid2);
		mid.setAlignment(javafx.geometry.Pos.CENTER);
		HBox.setMargin(vboxMid1, new Insets(20, 0, 0, 0));
		HBox.setMargin(stop, new Insets(20, 0, 0, 60));
		HBox.setMargin(vboxMid2, new Insets(20, 0, 0, 60));
	
		HBox bot = new HBox();
		bot.getChildren().addAll(vboxBot1, vboxBot2);
		bot.setAlignment(javafx.geometry.Pos.CENTER);
		HBox.setMargin(vboxBot1, new Insets(20, 0, 0, 0));
		HBox.setMargin(vboxBot2, new Insets(20, 0, 0, 60));
		
		VBox body = new VBox(top, mid, view, bot);
		body.setAlignment(javafx.geometry.Pos.CENTER);
		VBox.setMargin(view, new Insets(20, 0, 0, 0));
		
		VBox comp = new VBox(header, body);
		
		GridPane gpSituation = new GridPane();
		gpSituation.add(comp, 0, 0);

		Scene scene = new Scene(gpSituation, 1920, 1010);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setTitle("Situation Page");
	    stage.setScene(scene);
	    stage.show();
	}
	
	public void situationPage(Stage stage, Text speed, Text weight, Text battery, Text temperature, Text voltage, Text current, Text qr, Text vehicle, Text time, Text direction, Text countqr, Text percentdone, String scenarioName) throws FileNotFoundException {
		Functions fc = new Functions();
		
		// Header
		Button btn_situation1 = fc.createHeaderButton("Situation", 20, 100);
		Button btn_situation2 = fc.createHeaderButton("Scenario", 20, 100);

		btn_situation1.setOnAction(event -> {
			try {
				situationPage(stage, speed, weight, battery, temperature, voltage, current, qr, vehicle, time, direction, countqr, percentdone, scenarioName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
		
		btn_situation2.setOnAction(event -> {
			try {
				Image img1 = new Image(new String("D:/SeriousProjects/Java - Eclipse/CoskareGUI/Images/Senaryo_Taslak.png"));
				ImageView view1 = new ImageView(img1);
				scenarioPage(stage, view1, speed, weight, battery, temperature, voltage, current, qr, vehicle, time, direction, countqr, percentdone, scenarioName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
		
		HBox header = new HBox();
		header.getChildren().addAll(btn_situation1, btn_situation2);
		header.setStyle("-fx-background-color: #2B3D53;");
		header.setPrefWidth(1920);
		header.setPrefHeight(80);
		header.setAlignment(javafx.geometry.Pos.CENTER);
		HBox.setMargin(btn_situation1, new Insets(0, 0, 0, 0));
		HBox.setMargin(btn_situation2, new Insets(0, 0, 0, 20));
		
		// Top
		VBox vboxTop1 = fc.vboxText(fc.textBold("Speed"), fc.textNormal(speed), 100, 200);
		VBox vboxTop2 = fc.vboxText(fc.textBold("Weight"), fc.textNormal(weight), 100, 200);
		VBox vboxTop3 = fc.vboxText(fc.textBold("Battery"), fc.textNormal(battery), 100, 200);
		VBox vboxTop4 = fc.vboxText(fc.textBold("Temperature"), fc.textNormal(temperature), 100, 200);
		VBox vboxTop5 = fc.vboxText(fc.textBold("Voltage"), fc.textNormal(voltage), 100, 200);
		VBox vboxTop6 = fc.vboxText(fc.textBold("Current"), fc.textNormal(current), 100, 200);
		
		// Mid
		VBox vboxMid1 = fc.vboxText(fc.textBold("Last QR Code"), fc.textNormal(qr), 150, 480);
		
		Button stop = new Button("Stop");
		stop.setStyle("-fx-background-color: #F00; -fx-background-radius: 15px; -fx-text-fill: #FFF; -fx-font-weight: bold; -fx-font-size: 24;");
		stop.setPrefHeight(150);
		stop.setPrefWidth(150);
		
		stop.setOnAction(event -> {
			emergencyPage(stage);
		});
		
		VBox vboxMid2 = fc.vboxText(fc.textBold("Vehicle Situation"), fc.textNormal(vehicle), 150, 480);
		
		time.setText("00:00:00");
		
		// Bottom
		VBox vboxBot1 = fc.vboxText(fc.textBold("Time"), fc.textNormal(time), 150, 250);
		VBox vboxBot2 = fc.vboxText(fc.textBold("Direction"), fc.textNormal(direction), 150, 250);
		
		// Map
		Image img = new Image(new String("D:/SeriousProjects/Java - Eclipse/CoskareGUI/Images/Senaryo_Taslak.png"));
		ImageView view = new ImageView(img);
		view.setFitWidth(450);
		view.setFitHeight(350);
			
		HBox top = new HBox();
		top.getChildren().addAll(vboxTop1, vboxTop2, vboxTop3, vboxTop4, vboxTop5, vboxTop6);
		top.setAlignment(javafx.geometry.Pos.CENTER);
		HBox.setMargin(vboxTop1, new Insets(20, 0, 0, 0));
		HBox.setMargin(vboxTop2, new Insets(20, 0, 0, 50));
		HBox.setMargin(vboxTop3, new Insets(20, 0, 0, 50));
		HBox.setMargin(vboxTop4, new Insets(20, 0, 0, 50));
		HBox.setMargin(vboxTop5, new Insets(20, 0, 0, 50));
		HBox.setMargin(vboxTop6, new Insets(20, 0, 0, 50));
		
		HBox mid = new HBox();
		mid.getChildren().addAll(vboxMid1, stop, vboxMid2);
		mid.setAlignment(javafx.geometry.Pos.CENTER);
		HBox.setMargin(vboxMid1, new Insets(20, 0, 0, 0));
		HBox.setMargin(stop, new Insets(20, 0, 0, 60));
		HBox.setMargin(vboxMid2, new Insets(20, 0, 0, 60));
	
		HBox bot = new HBox();
		bot.getChildren().addAll(vboxBot1, vboxBot2);
		bot.setAlignment(javafx.geometry.Pos.CENTER);
		HBox.setMargin(vboxBot1, new Insets(20, 0, 0, 0));
		HBox.setMargin(vboxBot2, new Insets(20, 0, 0, 60));
		
		VBox body = new VBox(top, mid, view, bot);
		body.setAlignment(javafx.geometry.Pos.CENTER);
		VBox.setMargin(view, new Insets(20, 0, 0, 0));
		
		VBox comp = new VBox(header, body);
		
		GridPane gpSituation = new GridPane();
		gpSituation.add(comp, 0, 0);

		Scene scene = new Scene(gpSituation, 1920, 1010);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setTitle("Situation Page");
	    stage.setScene(scene);
	    stage.show();
	}
	
	public void scenarioPage(Stage stage, ImageView view, Text speed,Text weight, Text battery, Text temperature, Text voltage, Text current, Text qr, Text vehicle, Text time, Text direction, Text countqr, Text percentdone, String scenarioName) throws FileNotFoundException {
		Functions fc = new Functions();
		
		// Header 
		Button btn1_header = fc.createHeaderButton("Situation", 20, 100);
			
		btn1_header.setOnAction(event -> {
			try {
				
				situationPage(stage, speed, weight, battery, temperature, voltage, current, qr, vehicle, time, direction, countqr, percentdone,  scenarioName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});
			
		HBox header = new HBox();
		header.getChildren().addAll(btn1_header);
		header.setStyle("-fx-background-color: #2B3D53;");
		header.setPrefWidth(1920);
		header.setPrefHeight(80);
		header.setAlignment(javafx.geometry.Pos.CENTER);
		HBox.setMargin(btn1_header, new Insets(0, 0, 0, 0));
	
		Button btn1 = fc.createButton("B1", 50, 100);
		Button btn2 = fc.createButton("B2", 50, 100);
		Button btn3 = fc.createButton("B3", 50, 100);
		Button btn4 = fc.createButton("B4", 50, 100);
		Button btn5 = fc.createButton("Y1", 50, 100);
		Button btn6 = fc.createButton("Y2", 50, 100);
		Button btn7 = fc.createButton("Y3", 50, 100);
		Button btn8 = fc.createButton("Y4", 50, 100);
		
		Button selectBtn = fc.createButton("Select", 50, 100);
		
		selectBtn.setOnAction(event -> {
			try {		
				situationPageAfter(stage, view, speed, weight, battery, temperature, voltage, current, qr, vehicle, time, direction, countqr, percentdone, scenarioName, scenarioText);
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
		
		// Hover
		setMouseEnteredEvent(btn1, stage, "D:/SeriousProjects/Java - Eclipse/CoskareGUI/Images/Bos_Senaryo_1.png", speed, weight, battery, temperature, voltage, current, qr, vehicle, time, direction, countqr, percentdone, "B1");
        setMouseEnteredEvent(btn2, stage, "D:/SeriousProjects/Java - Eclipse/CoskareGUI/Images/Bos_Senaryo_2.png", speed, weight, battery, temperature, voltage, current, qr, vehicle, time, direction, countqr, percentdone, "B2");
        setMouseEnteredEvent(btn3, stage, "D:/SeriousProjects/Java - Eclipse/CoskareGUI/Images/Bos_Senaryo_3.png", speed, weight, battery, temperature, voltage, current, qr, vehicle, time, direction, countqr, percentdone, "B3");
        setMouseEnteredEvent(btn4, stage, "D:/SeriousProjects/Java - Eclipse/CoskareGUI/Images/Bos_Senaryo_4.png", speed, weight, battery, temperature, voltage, current, qr, vehicle, time, direction, countqr, percentdone, "B4");
        setMouseEnteredEvent(btn5, stage, "D:/SeriousProjects/Java - Eclipse/CoskareGUI/Images/Yuk_Senaryo_1.png", speed, weight, battery, temperature, voltage, current, qr, vehicle, time, direction, countqr, percentdone, "Y1");
        setMouseEnteredEvent(btn6, stage, "D:/SeriousProjects/Java - Eclipse/CoskareGUI/Images/Yuk_Senaryo_2.png", speed, weight, battery, temperature, voltage, current, qr, vehicle, time, direction, countqr, percentdone, "Y2");
        setMouseEnteredEvent(btn7, stage, "D:/SeriousProjects/Java - Eclipse/CoskareGUI/Images/Yuk_Senaryo_3.png", speed, weight, battery, temperature, voltage, current, qr, vehicle, time, direction, countqr, percentdone, "Y3");
        setMouseEnteredEvent(btn8, stage, "D:/SeriousProjects/Java - Eclipse/CoskareGUI/Images/Yuk_Senaryo_4.png", speed, weight, battery, temperature, voltage, current, qr, vehicle, time, direction, countqr, percentdone, "Y4");
        
		VBox buttons = new VBox();
		buttons.getChildren().addAll(btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8);
		VBox.setMargin(btn1, new Insets(10, 0, 0, 0));
		VBox.setMargin(btn2, new Insets(10, 0, 0, 0));
		VBox.setMargin(btn3, new Insets(10, 0, 0, 0));
		VBox.setMargin(btn4, new Insets(10, 0, 0, 0));
		VBox.setMargin(btn5, new Insets(10, 0, 0, 0));
		VBox.setMargin(btn6, new Insets(10, 0, 0, 0));
		VBox.setMargin(btn7, new Insets(10, 0, 0, 0));
		VBox.setMargin(btn8, new Insets(10, 0, 0, 0));
		
		HBox mid = new HBox(buttons, view, selectBtn);
		mid.setAlignment(javafx.geometry.Pos.CENTER);
		HBox.setMargin(buttons, new Insets(150, 0, 0, 50));
		HBox.setMargin(view, new Insets(150, 0, 0, 50));
		HBox.setMargin(selectBtn, new Insets(150, 0, 0, 50));
	
		VBox comp = new VBox(header, mid);
	
		GridPane gpScenario = new GridPane();
		gpScenario.add(comp, 0, 0);
	
		Scene scene = new Scene(gpScenario, 1920, 1010);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setTitle("Scenario Page");
		stage.setScene(scene);
		stage.show();
	}
	
	private void setMouseEnteredEvent(Button button, Stage stage, String imagePath, Text speed, Text weight, Text battery, Text temperature, Text voltage,Text current, Text qr, Text vehicle, Text time, Text direction, Text countqr, Text percentdone, String scenarioNamee) {
        button.setOnMouseEntered(event -> {
            try {
            	button.setStyle("-fx-background-color: grey; -fx-text-fill: #000; -fx-font-weight: bold;");
            	scenarioName = scenarioNamee;
            	Image img = new Image(new String(imagePath));
            	ImageView view = new ImageView(img);
            	scenarioPage(stage, view, speed, weight, battery, temperature, voltage, current, qr, vehicle, time, direction, countqr, percentdone, scenarioNamee);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
	
	public void mappingPage(Stage stage, ImageView view, Text speed, Text weight, Text battery, Text temperature, Text voltage, Text current, Text qr, Text vehicle, Text time, Text direction, Text countqr, Text percentdone, String scenarioName) throws FileNotFoundException {
		Functions fc = new Functions();
		
		// Header 
		Button btn1_header = fc.createHeaderButton("Situation", 20, 100);
			
		btn1_header.setOnAction(event -> {
			situationPageAfterAfter(stage, view, speed, weight, battery, temperature, voltage, current, qr, vehicle, time, direction, countqr, percentdone, scenarioName);
		});
		
		HBox header = new HBox();
		header.getChildren().addAll(btn1_header);
		header.setStyle("-fx-background-color: #2B3D53;");
		header.setPrefWidth(1920);
		header.setPrefHeight(80);
		header.setAlignment(javafx.geometry.Pos.CENTER);
		HBox.setMargin(btn1_header, new Insets(0, 0, 0, 0));
		
		VBox comp = new VBox(header);
	
		GridPane gpMapping = new GridPane();
		gpMapping.add(comp, 0, 0);

		Scene scene = new Scene(gpMapping, 1920, 1010);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setTitle("Mapping Page");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

}