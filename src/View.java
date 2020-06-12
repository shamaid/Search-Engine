import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.util.Callback;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;


public class View {

    String corpusPath;
    String postingPath;
    ReadFile reader;
    HashMap<String ,String[]> mainDictionary;
    HashMap<String ,String[]> docsDictionary;
    Dictionary dictionary;
    Searcher searcher;
    String pathQuery;
    HashMap<String,ArrayList<String>> allrankDocs;
    ArrayList<String> rankDocs;
    boolean useStemmer;
    boolean useSemantic;
    boolean runQuery;

    public javafx.scene.control.TextField txtField_corpusAndStopWords;
    public javafx.scene.control.TextField txtField_postingFilesAndDictionary;
    public javafx.scene.control.Button button_Reset;
    public javafx.scene.control.Button button_Start;
    public javafx.scene.control.Button button_BrowsePostingFilesAndDictionary;
    public javafx.scene.control.Button button_BrowseCorpusAndStopWords;
    public javafx.scene.control.Button button_LoadDictionary;
    public javafx.scene.control.CheckBox checkBox_stemming;
    public javafx.scene.control.CheckBox checkBox_semantic;
    public javafx.scene.control.Button button_Run;
    public javafx.scene.control.Button button_queries;
    public javafx.scene.control.Button button_browseSave;
    public javafx.scene.control.Button button_Go;
    public javafx.scene.control.TextField txtField_searchQuery;
    public javafx.scene.control.TextField txtField_chooseQueries;
    public javafx.scene.control.TextField txtField_saveQuery;
    public javafx.scene.control.ListView listView_queryOutput;


    /**
     * this function set Resize Event
     * @param scene
     */
    public void setResizeEvent(Scene scene) {
        long width = 460;
        long height = 700;
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                System.out.println("Width: " + newSceneWidth);
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                System.out.println("Height: " + newSceneHeight);
            }
        });
    }



    /*  public View() throws FileNotFoundException {
          corpusPath = txtField_corpusAndStopWords.getText();
          postingPath = txtField_postingFilesAndDictionary.getText();
      }
  */

    /**
     * this function is choose Directory For Corpus And Stop Words
     * @param actionEvent
     */
    public void chooseDirectoryForCorpusAndStopWords(ActionEvent actionEvent) {

        chooseDirectory(txtField_corpusAndStopWords);
        if(txtField_corpusAndStopWords.getText().equals("")){

            showAlert("The path to the Corpus and Stop Words is missing");

        }

    }

    /**
     * this function choose Directory For Posting Files And Dictionary
     * @param actionEvent
     */
    public void chooseDirectoryForPostingFilesAndDictionary(ActionEvent actionEvent) {

        chooseDirectory(txtField_postingFilesAndDictionary);
        if(txtField_postingFilesAndDictionary.getText().equals("")){

            showAlert("The path to the posting Files and Dictionary is incorrect");

        }


    }

    /**
     * this function is choose Directory with folderDialog
     *
     * */
    private void chooseDirectory(TextField textField){
        final Label labelSelectedDirectory = new Label();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(new Stage());

        try{
            if(selectedDirectory == null){
                //No Directory selected
            }else{
                labelSelectedDirectory.setText(selectedDirectory.getAbsolutePath());
                selectedDirectory.getAbsolutePath();
            }
            textField.textProperty().set(selectedDirectory.getAbsolutePath());

        }catch (NullPointerException n){
            //System.out.println("NullPointerException");
        }

    }

    /**
     * this function start The Program
     * @param actionEvent
     * @throws IOException
     */

    public void startTheProgram(ActionEvent actionEvent) throws IOException {
        if(button_BrowsePostingFilesAndDictionary.isDisable() && button_BrowseCorpusAndStopWords.isDisable()){
            button_Start.setDisable(false);
            button_BrowsePostingFilesAndDictionary.setDisable(false);
            button_BrowseCorpusAndStopWords.setDisable(false);
        }

        corpusPath = txtField_corpusAndStopWords.getText();
        postingPath = txtField_postingFilesAndDictionary.getText();

        if(corpusPath.equals("")){

            showAlert("The path to the Corpus and Stop Words is missing");

        }
        if(postingPath.equals("")){

            showAlert("The path to the posting Files and Dictionary is missing");

        }
        if(!corpusPath.equals("") && !postingPath.equals("")) {
            boolean useStemmer = checkBox_stemming.isSelected();

            long startTime 	= System.nanoTime();




            //System.out.println(startTime);

            reader = new ReadFile(corpusPath, postingPath, useStemmer);
            reader.ReadAndSplitFile();

            long finishTime = System.nanoTime();
            long totalTime = 0;
            totalTime += (finishTime - startTime)/1000000.0;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Information:");
            alert.setContentText("Number of indexed documents:   " + reader.parser.indexer.numberOfDocs +
                    "\nNumber of terms:  " +reader.parser.indexer.mainDictionary.size() +
                    "\nTotal time:  " + (totalTime/60000.0)*60 + " sec");


            alert.showAndWait();



            loadDictionary(actionEvent);
            //System.out.println("Time:  " + (finishTime - startTime)/1000000.0 + " ms");
            //System.out.println("Total time:  " + totalTime/60000.0 + " min");


        }

    }

    /**
     * this function reset Posting Files And Dictionary
     * @param actionEvent
     */
    public void resetPostingFilesAndDictionary(ActionEvent actionEvent) {

        postingPath = txtField_postingFilesAndDictionary.getText();

        /*
        File dir = new File(postingPath);
        for(File file: dir.listFiles())
            if (!file.isDirectory())
                file.delete();
        */


        File postingFiles = new File(postingPath);
        File[] files = postingFiles.listFiles();
        if(files == null || files.length==0){
            //alert.showAndWait();
            showAlert("There is nothing to delete");
        }else {
            //Arrays.stream(new File(postingPath).listFiles()).forEach(File::delete);
            Arrays.stream(files).forEach(File::delete);
        }

        if(txtField_corpusAndStopWords != null) {
            txtField_corpusAndStopWords.clear();
        }
        if(txtField_postingFilesAndDictionary != null) {
            txtField_postingFilesAndDictionary.clear();
        }

        if(reader != null) {
            //clear all database
            reader.documents.clear();
            reader.parser.stopWords.clear();
            reader.parser.namesAndEntities.clear();
            reader.parser.indexer.dictionary.clear();
            reader.parser.indexer.mainDictionary.clear();
            //  reader.parser.indexer.sortedTerms.clear();
            reader.parser.indexer = null;
            reader.parser = null;
            reader = null;
        }



    }

    private String readFile(File file) throws IOException {
        StringBuilder stringBuffer = new StringBuilder();
        BufferedReader bufferedReader = null;

        bufferedReader = new BufferedReader(new FileReader(file));

        String text;

        //stringBuffer.append("term  number of docs\n");
        while ((text = bufferedReader.readLine()) != null) {

            stringBuffer.append(text+"\n");
        }

        return stringBuffer.toString();
    }

    /**
     * this function show dictionary
     * @param actionEvent
     */
    public void showDictionary(ActionEvent actionEvent){
        postingPath = txtField_postingFilesAndDictionary.getText();

        if(postingPath.equals("")){

            showAlert("The path to the posting Files and Dictionary is missing");
        }

        if(!postingPath.equals("")) {
            String name = "";
            if (checkBox_stemming.isSelected())
                name = "\\Stemmer_dictionary.txt";

            else
                name = "\\dictionary.txt";

            try {
                File file = new File(postingPath + name);
                Desktop.getDesktop().edit(file);
            } catch (IOException e) {

                showAlert("File not found!");
            }
        }
    }

    public void showDic(ActionEvent actionEvent) throws IOException {



        /////////////////////////////////////

        Stage stage = new Stage();
        stage.setTitle("show Dictionary");

        String text="";
        String stringNumbers="";
        String stringWords="";
        TextArea textArea = new TextArea();
        HashMap<String , String[]> mainDictionary= reader.parser.indexer.mainDictionary;
        LinkedHashMap<String, String[] > sortedTermPerNFiles = new LinkedHashMap<>();

        mainDictionary.entrySet()
                .stream()
                .sorted(new Comparator<Map.Entry<String, String[]>>() {
                    @Override
                    public int compare(Map.Entry<String, String[]> o1, Map.Entry<String, String[]> o2) {
                        return o1.getKey().compareToIgnoreCase(o2.getKey());
                    }
                })
                .forEachOrdered(x -> sortedTermPerNFiles.put(x.getKey(), x.getValue()));

        for (HashMap.Entry<String, String []> word: sortedTermPerNFiles.entrySet()) {
            String []  term = word.getValue();
            // String string =word.getKey() + " "+ term[2];
            stringWords =stringWords + "\n" + word.getKey();
            stringNumbers = stringNumbers + "\n"+ term[2];
            //text= text +"\n"+string;
            // writer.newLine();
        }

        TableView table = new TableView();


        TableColumn firstNameCol = new TableColumn("Term");
        TableColumn lastNameCol = new TableColumn("Number");
        firstNameCol.setText("Term"+stringWords);
        firstNameCol.setPrefWidth(200);
        lastNameCol.setText("Number"+stringNumbers);
        lastNameCol.setPrefWidth(100);


        table.getColumns().addAll(firstNameCol, lastNameCol);

        ScrollPane sp = new ScrollPane(table);

        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(sp);
        Scene scene = new Scene(stackPane , 300,500);

        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();



    }

    /**
     * this function load dictionary
     * @param actionEvent
     * @throws IOException
     */
    public void loadDictionary(ActionEvent actionEvent) throws IOException {


        postingPath = txtField_postingFilesAndDictionary.getText();
        if(postingPath.equals("")){

            showAlert("The path to the posting Files and Dictionary is missing");
        }

        if(!postingPath.equals("")) {

            mainDictionary = new HashMap<>();
            docsDictionary = new HashMap<>();
            dictionary = new Dictionary(mainDictionary, docsDictionary);
            File fileDictionary;
            File fileDocsDictionary;
            useStemmer = checkBox_stemming.isSelected();
            useSemantic = checkBox_semantic.isSelected();
            if (useStemmer) {
                fileDictionary = new File(postingPath + "\\Stemmer_dictionary.txt");
                fileDocsDictionary = new File(postingPath + "\\Stemmer_DocsDictionary.txt");

            } else {
                fileDictionary = new File(postingPath + "\\dictionary.txt");
                fileDocsDictionary = new File(postingPath + "\\DocsDictionary.txt");
            }
            BufferedReader reader;

            String longTerm = "";
            String[] numbers;

///********************START READ MAIN DICTIONARY************************************************///
            try {
                reader = new BufferedReader(new FileReader(fileDictionary));
                String line = reader.readLine();

                while (line != null) {
                    if (!line.equals("")) {
                        String[] splited = line.split("\\s+");
                        numbers = new String[2];
                        if (splited.length == 3) {
                            numbers[0] = splited[1]; //the number of docs this term is exist
                            numbers[1] = splited[2]; //the number of time this term in the corpus
                            String[] temp = new String[numbers.length];
                            temp[0] = numbers[0];
                            temp[1] = numbers[1];
                            mainDictionary.put(splited[0], temp);
                        } else if (splited.length > 3) {
                            for (int i = 0; i < splited.length - 1; i++) {
                                longTerm = longTerm + " " + splited[i];
                            }
                            numbers[0] = splited[splited.length - 2]; //number of docs that has this term
                            numbers[1] = splited[splited.length - 1]; //the number of time this term in the corpus

                            mainDictionary.put(longTerm, numbers);
                            longTerm = "";
                        }
                        line = reader.readLine();
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

///********************END MAIN DICTIONARY************************************************///

///********************START READ DOCS DICTIONARY************************************************///

            String docID = "";

            try {
                reader = new BufferedReader(new FileReader(fileDocsDictionary));
                String line = reader.readLine();

                while (line != null) {
                    if (!line.equals("")) {
                        if (line.startsWith("total")) {
                            String[] total = line.split(":");
                            String totalLength = total[1];
                            line = reader.readLine();
                            if (line.startsWith("number")) {
                                String[] numOfDocs = line.split(":");
                                String numberOfDocs = numOfDocs[1];
                                line = reader.readLine();
                                //System.out.println(line);
                                if (line == null) {
                                    dictionary.totalLength = Integer.parseInt(totalLength.replaceAll(" ", ""));
                                    dictionary.numberOfDocs = Integer.parseInt(numberOfDocs.replaceAll(" ", ""));
                                    break;
                                }
                            }

                        }
                        String[] splited = line.split(",");
                        //String[] docIDAndNumbers = new String[4];
                        String[] docIDAndNumbers = splited[0].split("\\s+"); //split numbers
                        docID = docIDAndNumbers[0];
                        String[] docsNumbers = new String[4];
                        docsNumbers[0] = docIDAndNumbers[1];
                        docsNumbers[1] = docIDAndNumbers[2];
                        docsNumbers[2] = docIDAndNumbers[3];
                        docsNumbers[3] = "";
                        for (int i = 1; i < splited.length; i++) {
                            docsNumbers[3] = docsNumbers[3] + splited[i] + ",";
                        }

                        //System.out.println(docID);
                        docsDictionary.put(docID, docsNumbers); //set hashmap to docID
                        docID = "";


                        line = reader.readLine();
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


///********************END READ DOCS DICTIONARY************************************************///


            searcher = new Searcher(dictionary, txtField_postingFilesAndDictionary.getText());
            searcher.useStemmer=useStemmer;
            searcher.useSemantic=useSemantic;
           // button_Go.setDisable(false);
            button_browseSave.setDisable(false);
            button_Run.setDisable(false);
            button_queries.setDisable(false);

            System.out.println("Load is done");
        }

    }

    public void runQuery(){
        rankDocs = new ArrayList<>();
        runQuery=true;
        String query = txtField_searchQuery.getText();

        if(query.equals("")){
            showAlert("There isn't a query in the search location ");
            button_Go.setDisable(true);
        }
       // button_Go.setDisable(false);

        if(!query.equals("")) {
            if (query != null) {
                try {
                    rankDocs= searcher.stringQuery(query);
                    identifyEntitiesRun();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void filePathQuery(){
        //String pathQuery = txtField_chooseQueries.getText();
        pathQuery = fileChooser();
        if(pathQuery.equals("nullnull")){
            showAlert("The path to the query file is missing");
        }else {
            button_Go.setDisable(false);
        }

       /* if(searcher != null) {
            if (postingPath != null) {
                if (pathQuery != null) {
                    try {
                        searcher.pathQuery(pathQuery);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }*/
    }

    public void searchAndSaveQuery(){
       allrankDocs = new HashMap<String,ArrayList<String>>();
       if(pathQuery.equals("nullnull")){
           showAlert("The path to the query file is missing");
       }
        if(searcher != null) {
            if (postingPath != null) {
                if (pathQuery != null && !pathQuery.equals("nullnull")) {
                    try {
                        allrankDocs = searcher.pathQuery(pathQuery);
                        identifyEntities();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public String fileChooser(){
        FileDialog fd = new FileDialog(new Frame(), "Choose a file", FileDialog.LOAD);
        fd.setDirectory("C:\\");
        fd.setFile("*.txt");
        fd.setVisible(true);
        String filename = fd.getFile();
        String fileDirectory = fd.getDirectory();
        String filePath = fileDirectory+filename;
        if (filename == null)
            System.out.println("You cancelled the choice");
        else
            System.out.println("You chose " + filename);


        return filePath;


    }

    public void saveQuery(){

        useStemmer=checkBox_stemming.isSelected();
        chooseDirectory(txtField_saveQuery);
        String saveQueryPath = txtField_saveQuery.getText();
        if(saveQueryPath.equals("")){

            showAlert("The path of the save the results is missing");
        }
        if(searcher != null) {
            if (!saveQueryPath.equals("")) {
                if(useStemmer){
                    saveQueryPath = txtField_saveQuery.getText() + "\\Stemmer_results.txt";
                    searcher.outputPath = saveQueryPath;
                }else {
                    saveQueryPath = txtField_saveQuery.getText() + "\\results.txt";
                    searcher.outputPath = saveQueryPath;
                }
            }
        }
    }

    public void identifyEntities(){


        Stage stage = new Stage();
        stage.setTitle("Show ranked docs");

        final Button btn = new Button("Identify Entities");

        TableColumn firstNameCol = new TableColumn("Doc ID");
        TableColumn lastNameCol = new TableColumn("Identify Entities");


        ArrayList<String> arrayOfDocs = new ArrayList<>();
        ArrayList<String> queryNumbers = new ArrayList<>();

        queryNumbers.addAll(allrankDocs.keySet());

        String numberQuery="";
        String docs ="";

            for(int i=0 ; i<queryNumbers.size() ; i++){
                numberQuery=queryNumbers.get(i);
                ArrayList<String> rank = allrankDocs.get(numberQuery);
                for(int j=0 ; j<rank.size() ; j++){
                    arrayOfDocs.add(rank.get(j));
                }
            }

        firstNameCol.setText("Doc ID\n" +docs);
        firstNameCol.setPrefWidth(200);
        lastNameCol.setText("Identify Entities\n" );
        lastNameCol.setPrefWidth(100);



        ///********COMBOBOX*********/////
        ChoiceBox docChoice =  new ChoiceBox();
        ComboBox<String> Queries = new ComboBox<>();
       // ListView<String> listView = new ListView<>();
        ListView<String> listDocs = new ListView<>(Queries.getItems());

        //ArrayList<String>
        String string1 ="";

            for(int i=0 ; i<queryNumbers.size() ; i++){
                numberQuery=queryNumbers.get(i);
                Queries.getItems().add(numberQuery);
               // listView.getItems().add(numberQuery);
                ArrayList<String> rank = allrankDocs.get(numberQuery);
                for(int j=0 ; j<rank.size() ; j++){
                    arrayOfDocs.add(rank.get(j));
                    //list.getItems().add(rank.get(i));
                   // Queries.getValue()

                    //Docs.getItems().add(rank.get(j));


                }
               // listDocs.getItems().addAll(arrayOfDocs);
            }


////////////////////////////*****************************************/////////////////////////////////////


       ChoiceBox<String> queries = new ChoiceBox<>();
        queries.getItems().addAll(queryNumbers);

        queries.getSelectionModel().selectFirst();

        queries.getSelectionModel().selectedItemProperty().addListener(this::itemChange);
       // ChoiceBox c =new ChoiceBox();
        Label l = new Label();
        l.textProperty().bind(queries.valueProperty());
       // c.getItems().addAll(allrankDocs.get(l.getText()));
        //for(int i=0 ; i<queryNumbers.size() ; i++){
        //    c.getItems().add(allrankDocs.get(queryNumbers.get(i)));
       // }




////////////////////////////*****************************************/////////////////////////////////////









        TableView table = new TableView();


        table.getColumns().addAll(firstNameCol, lastNameCol);

        ScrollPane sp = new ScrollPane(table);

        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(sp);
        Scene scene = new Scene(stackPane , 300,500);

        //////////////////////////////

        VBox vBox = new VBox();
        Label label1 = new Label("Rank Docs");
        Label label2 = new Label();
        label2.setText("amount of rank docs: " );

        //Label label2;
        //ChoiceBox docChoice =  new ChoiceBox();



        //***************************************************************************************************//
        Button buttonQ = new Button("choose query");
        buttonQ.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                ArrayList<String> rank = new ArrayList<>();

                rank = allrankDocs.get(Queries.getValue());
                docChoice.getItems().addAll(rank);
                label2.setText("amount of rank docs: " + rank.size());


            }
        });


        //***************************************************************************************************//

        Button button = new Button("Identify Entities");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                HashMap<String , Double> best5entities = new HashMap<>();
                ArrayList<String> rank = new ArrayList<>();
                if(docChoice.getSelectionModel().getSelectedItem() != null) {
                    best5entities = dictionary.get5entitiesForDoc(docChoice.getSelectionModel().getSelectedItem().toString());

                    String temp="";
                    ArrayList set = new ArrayList();
                    set.addAll(best5entities.keySet());
                    for(int i=0 ; i<set.size() ; i++){
                        temp = temp + set.get(i) +" " +best5entities.get(set.get(i))+"\n";

                    }


                    Stage stage = new Stage();
                    stage.setTitle("Show Entities");
                    TextArea textArea = new TextArea(temp);
                    Scene scene1 = new Scene(textArea , 300,300);
                    stage.setScene(scene1);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    //SetStageCloseEvent(stage);
                    stage.show();
                    docChoice.getItems().clear();


                }else {
                    showAlert("Didn't choose query");
                }

                /*String temp="";
                ArrayList set = new ArrayList();
                set.addAll(best5entities.keySet());
                for(int i=0 ; i<set.size() ; i++){
                    temp = temp + set.get(i) +" " +best5entities.get(set.get(i))+"\n";

                }


                Stage stage = new Stage();
                stage.setTitle("Show Entities");
                TextArea textArea = new TextArea(temp);
                Scene scene1 = new Scene(textArea , 300,300);
                stage.setScene(scene1);
                stage.initModality(Modality.APPLICATION_MODAL);
                //SetStageCloseEvent(stage);
                stage.show();
                docChoice.getItems().clear();
*/


            }
        });

        //vBox.getChildren().addAll(label1,label2 ,Docs , button);
        vBox.getChildren().addAll(label1 ,label2,Queries ,docChoice ,buttonQ , button);
        Scene scene1 = new Scene(vBox , 300,300);
        /////////////////////////////
        stage.setScene(scene1);
        stage.initModality(Modality.APPLICATION_MODAL);
        SetStageCloseEvent(stage);
        stage.show();



    }

    private void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(message);

        alert.showAndWait();
    }


    public void SetStageCloseEvent(Stage primaryStage) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to exit?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    // ... user chose OK
                    // Close program
                } else {
                    // ... user chose CANCEL or closed the dialog
                    windowEvent.consume();
                }
            }
        });
    }
    public void itemChange(ObservableValue<? extends String> observableValue ,
                           String oldValue , String newValue){
        ChoiceBox c = new ChoiceBox();
        c.getItems().addAll(allrankDocs.get(newValue));
    }



    public void identifyEntitiesRun(){
        Stage stage = new Stage();
        stage.setTitle("Show ranked docs");

        final Button btn = new Button("Identify Entities");

        TableColumn firstNameCol = new TableColumn("Doc ID");
        TableColumn lastNameCol = new TableColumn("Identify Entities");


        String docs ="";
        for(int i=0 ; i<rankDocs.size() ; i++){
            docs = docs+rankDocs.get(i)+"\n";
        }


        firstNameCol.setText("Doc ID\n" +docs);
        firstNameCol.setPrefWidth(200);
        lastNameCol.setText("Identify Entities\n" );
        lastNameCol.setPrefWidth(100);



        ///********COMBOBOX*********/////
        ComboBox<String> Docs = new ComboBox<>();
        String string1 ="";
        for(int i=0 ; i<rankDocs.size() ; i++){
            string1 =rankDocs.get(i);
            Docs.getItems().add(string1);
        }



        TableView table = new TableView();


        table.getColumns().addAll(firstNameCol, lastNameCol);

        ScrollPane sp = new ScrollPane(table);

        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(sp);
        Scene scene = new Scene(stackPane , 300,500);

        //////////////////////////////
        VBox vBox = new VBox();
        Label label1 = new Label("Rank Docs");
        Label label2 = new Label("amount of rank docs: " + rankDocs.size());
        Button button = new Button("Identify Entities");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                HashMap<String , Double> best5entities = new HashMap<>();
                if(Docs.getValue() != null) {
                    best5entities = dictionary.get5entitiesForDoc(Docs.getValue());
                    String temp = "";
                    ArrayList set = new ArrayList();
                    set.addAll(best5entities.keySet());
                    for (int i = 0; i < set.size(); i++) {
                        temp = temp + set.get(i) + " " + best5entities.get(set.get(i)) + "\n";
                    }

                    Stage stage = new Stage();
                    stage.setTitle("Show Entities");
                    TextArea textArea = new TextArea(temp);
                    Scene scene1 = new Scene(textArea, 300, 300);
                    stage.setScene(scene1);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.show();
                }else{
                    showAlert("Didn't choose docId");
                }


            }
        });

        vBox.getChildren().addAll(label1,label2 ,Docs , button);
        Scene scene1 = new Scene(vBox , 300,300);
        /////////////////////////////
        stage.setScene(scene1);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();



    }

}
