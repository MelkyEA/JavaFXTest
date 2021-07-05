package com.binfit.test;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;

import java.util.*;
import java.io.*;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        final FileChooser fileChooser = new FileChooser();
        //Создание текстового поля
        TextArea textArea = new TextArea();
        Label label = new Label("Содержимое текстового файла:");
        //Создание таблицы
        TableView<Map.Entry<String, Long>> table = new TableView<>();
        //Первая колонка
        TableColumn<Map.Entry <String, Long>, String> column1 = new TableColumn("Слово");
        column1.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getKey()));
        column1.setMinWidth(100);
        //Вторая колонка
        TableColumn<Map.Entry <String, Long>, String> column2 = new TableColumn("Количество повторений");
        column2.setCellValueFactory(p -> new SimpleStringProperty(String.valueOf(p.getValue().getValue())));
        column2.setMinWidth(350);


        table.getColumns().setAll(column1, column2);
        //Создание кнопки
        Button button1 = new Button("Открыть файл");
        final String[] text = {""};

        //Считывание текста из файла
        button1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                textArea.clear();
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    text[0] = textFromFile(file);
                    textArea.appendText(text[0]);
                    ObservableList<Map.Entry<String, Long>> counts = FXCollections.observableArrayList(splitWord(text[0]).entrySet());
                    table.setItems(counts);
                }

            }
        });
        //Выделение слова в таблице при выделении текста
        textArea.selectedTextProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                Iterator<Map.Entry<String, Long>> it = table.getItems().iterator();
                while(it.hasNext()){
                    Map.Entry<String, Long> entry = it.next();
                    if(t1.trim().equalsIgnoreCase(entry.getKey())){
                        table.getSelectionModel().select(entry);
                    }
                }
            }
        });

                //Создание вертикальной группировки
        VBox vert = new VBox();
        vert.setPadding(new Insets(10));
        vert.setSpacing(5);
        //Изменение размеров поля и кнопки
        textArea.setMinSize(500,400);
        button1.setMinSize(500, 5);

        vert.getChildren().addAll(label, textArea, button1);
        //Изменение размеров таблицы
        table.setMinWidth(450);
        table.setMaxHeight(462);

        HBox gor = new HBox();
        gor.setPadding(new Insets(10));
        gor.setSpacing(5);
        gor.getChildren().addAll(vert, table);



        Scene scene = new Scene(gor, 1000, 500);

        primaryStage.setTitle("Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    //Считывание текста из выбранного файла
     public static String textFromFile(File file){
        String text = "";
         try(BufferedReader reader = new BufferedReader(new FileReader(file))){
             String line;
             while ((line = reader.readLine()) != null){
                 text += line + "\n";
             }
         } catch (FileNotFoundException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }
         return text;
     }

    //Поиск повторений каждого слова
    public static Map<String, Long> splitWord(String text){
        Map<String, Long> result = new HashMap<>();
        String[] words = text.split("\\p{P}?[\\s\\t\\n\\r]+");
        for(int i = 0; i < words.length; i++){
            String el = words[i];
            long count = Arrays.stream(words).filter(word -> word.equalsIgnoreCase(el)).count();
            if(!result.containsKey(el.toLowerCase()) && !el.equals("")){
                result.put(el.toLowerCase(), count);
            }
        }
        return result;
    }
}
