import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Optional;

import static javafx.application.Application.launch;

public class main extends Application {

    public void start(Stage stage) throws IOException {
        stage.setTitle("My Google");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("GUI.fxml").openStream());
        Scene scene = new Scene(root , 800 , 500);
        stage.setScene(scene);

        View view = fxmlLoader.getController();
        view.setResizeEvent(scene);
        //view.setViewModel(viewModel);
        // view.addObserver(view);
        //--------------
        SetStageCloseEvent(stage);
        stage.show();

    }

    public void SetStageCloseEvent(Stage primaryStage) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to exit?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    System.exit(0);
                    // ... user chose OK
                    // Close program
                } else {
                    // ... user chose CANCEL or closed the dialog
                    windowEvent.consume();
                }
            }
        });
    }


    public static void main(String args[]) throws IOException {



        //  long 					startTime 	= System.nanoTime();
      //  System.out.println(startTime);



        launch(args);

/*
            String  stemmer = "Stemmer";


        FileInputStream inputStream = new FileInputStream("d:\\documents\\users\\talmormi\\Downloads\\finalStemmer\\marge89.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = bufferedReader.readLine();
        String[] splitLine1={};
        //splitLine1 = line.split("~");
        while (line!=null){
            splitLine1 = line.split("~");
            char firstLetter = splitLine1[0].charAt(0);
            if (!(firstLetter >= 'A' && firstLetter <= 'Z') && !(firstLetter >= 'a' && firstLetter <= 'z')) {
                FileOutputStream outputStream = new FileOutputStream("d:\\documents\\users\\talmormi\\Downloads\\finalStemmer\\postings" + "\\" +stemmer+"numbers.txt", true);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
                while (!(firstLetter >= 'A' && firstLetter <= 'Z') && !(firstLetter >= 'a' && firstLetter <= 'z') && line!=null) {
                    bw.write(line);
                    bw.newLine();
                    line = bufferedReader.readLine();
                    if (line!=null) {
                        splitLine1 = line.split("~");
                        firstLetter = splitLine1[0].charAt(0);
                    }
                }
                bw.flush();
                bw.close();
                outputStream.close();
            }
            else {
                //FileOutputStream outputStream = new FileOutputStream(path + "\\" +stemmer + "" +firstLetter + ".txt", true);
                //BufferedWriter bw1= new BufferedWriter(new OutputStreamWriter(outputStream));
                while (line!=null && ((firstLetter >= 'A' && firstLetter <= 'Z') || (firstLetter >= 'a' && firstLetter <= 'z'))){
                    firstLetter = splitLine1[0].toUpperCase().charAt(0);
                    char nextLetter = splitLine1[0].toUpperCase().charAt(0);
                    // FileOutputStream  outputStream = new FileOutputStream(path + "\\"+stemmer + "" +firstLetter+".txt", true);
                    // BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(outputStream));
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File("d:\\documents\\users\\talmormi\\Downloads\\finalStemmer\\postings" + "\\"+stemmer + "" +firstLetter+".txt"))));
                    while (line!=null && firstLetter==nextLetter) {
                        out.println(line);
                        line = bufferedReader.readLine();
                        if (line!=null){
                            splitLine1 = line.split("~");
                            nextLetter = splitLine1[0].toUpperCase().charAt(0);
                        }
                    }
                    out.close();
                }

            }
        }
        bufferedReader.close();
        inputStream.close();
        */

        //Searcher searcher = new Searcher();
       //searcher.pathQuery("D:\\לימודים\\שנה ג\\סמסטר א\\אחזור\\מנוע חיפוש\\queries.txt");

       /* String s = "0512";
        String firstWord=s.substring(0,2);
        String secondWord=":"+ s.substring(2);

        String time = firstWord +secondWord;
        double firstnumber = Double.parseDouble(firstWord);
        DecimalFormat df = new DecimalFormat("#.###");
        firstWord =df.format(firstnumber);
*/


      /*  Parse p = new Parse();
        String[] words = {"1" , "Mar" , "1994",};
        p.parser(words,"s","s");
*/
    /*    String s= "4";
        String g =s.substring(s.length()-1);
        //if((s.substring(s.length()-1))== null) {
            g = s.substring(0, s.length() - 1);
            System.out.println(g);
        //}
*/
        // ReadFile r = new ReadFile("C:\\Users\\USER\\Desktop\\לימודים\\'שנה ג\\אחזור מידע\\corpus");
        // ReadFile r = new ReadFile("d:\\documents\\users\\shamaid\\Downloads\\corpus\\corpus");
        // r.ReadAndSplitFile();


   /*     long 					finishTime 	= System.nanoTime();
        System.out.println("Time:  " + (finishTime - startTime)/1000000.0 + " ms");
        long			totalTime = 0;
        totalTime += (finishTime - startTime)/1000000.0;
        System.out.println("Total time:  " + totalTime/60000.0 + " min");
*/
/*
        Parse parser = new Parse();

        String[] test = {"Michal", "Talmor", "Af", "bdfa", "cadf", "Michal", "Talmor", "ijre", "Talmor", "Af"};


            //parser.parseRange(test,0,"text","doc1");
            parser.parser(test,"text", "doc1");
*/

    }


}
