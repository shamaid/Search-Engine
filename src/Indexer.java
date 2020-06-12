import javax.print.attribute.standard.QueuedJobCount;
import javax.swing.*;
import java.io.*;
import java.util.*;

import static javafx.scene.input.KeyCode.Q;

public class Indexer {

    boolean useStemmer;
    Stemmer stemmer;
    HashMap<String, String []> mainDictionary; //0 - pointer, 1 - counter for number of docs, 2 - counter for number of times in the corpus
    HashMap<String, Term> dictionary;
    HashMap<String, Document> docsDictionary;
    int postingFileNameCounter;
    String path;
    int numberOfDocs;

    int totalLength ;


    /**
     * this is constructor
     * @throws FileNotFoundException
     */
    public Indexer() throws FileNotFoundException {
        // this.useStemmer = true;///////////////////////////////////////// use stemmer or not ///////////////////////////////////
        stemmer = new Stemmer();
        mainDictionary = new HashMap<>();
        dictionary = new HashMap<>();
        docsDictionary = new HashMap<>();
        numberOfDocs = 0;

        totalLength =0;

    }

    /**
     * this function is sending the term to indexer
     * @param term
     * @throws IOException
     */

    public void termToIndex (Term term) throws IOException {


        if (useStemmer) {
            term.word = stemmer.stem(term.word);
        }

        if (term.isEntity){

            if (docsDictionary.containsKey(term.docID)){ //if the doc is in the docs dictionary
                docsDictionary.get(term.docID).length = docsDictionary.get(term.docID).length+1; //update the length value

                if (docsDictionary.get(term.docID).terms.containsKey(term.word)){//if the term is exist for this doc
                    docsDictionary.get(term.docID).terms.replace(term.word, docsDictionary.get(term.docID).terms.get(term.word)+1); //update counter for this term
                    docsDictionary.get(term.docID).entities.replace(term.word, docsDictionary.get(term.docID).entities.get(term.word)+1); //update counter for this term
                }
                else{ //if the term is not exist for this doc
                    docsDictionary.get(term.docID).terms.put(term.word, 1);
                    docsDictionary.get(term.docID).entities.put(term.word, 1);
                }
            }
            else{ //if the doc is not in the docs dictionary
                Document newDoc = new Document(term.docID);
                docsDictionary.put(term.docID, newDoc);
                docsDictionary.get(term.docID).terms.put(term.word, 1);
                docsDictionary.get(term.docID).entities.put(term.word, 1);
            }
        }
        else {

            lowerUpperCase(term);
            if (docsDictionary.containsKey(term.docID)) { //if the doc is in the docs dictionary
                docsDictionary.get(term.docID).length = docsDictionary.get(term.docID).length + 1; //update the length value

                if (docsDictionary.get(term.docID).terms.containsKey(term.word.toUpperCase())) {//if the term is exist for this doc with capital letters
                    docsDictionary.get(term.docID).terms.replace(term.word.toUpperCase(), docsDictionary.get(term.docID).terms.get(term.word.toUpperCase()) + 1); //update counter for this term
                } else if (docsDictionary.get(term.docID).terms.containsKey(term.word.toLowerCase())) { //if the term is exist for this doc with small letters
                    docsDictionary.get(term.docID).terms.replace(term.word.toLowerCase(), docsDictionary.get(term.docID).terms.get(term.word.toLowerCase()) + 1); //update counter for this term
                } else { //if the term is not exist for this doc
                    docsDictionary.get(term.docID).terms.put(term.word, 1);
                }
            } else { //if the doc is not in the docs dictionary
                Document newDoc = new Document(term.docID);
                docsDictionary.put(term.docID, newDoc);
                docsDictionary.get(term.docID).terms.put(term.word, 1);
            }
        }


        if (mainDictionary.containsKey(term.word)){ //if the word is in the main dictionary only update the counter
            mainDictionary.get(term.word)[2] = Integer.parseInt(mainDictionary.get(term.word)[2])+1 + ""; //update the counter of times in corpus
        }

        if (dictionary.containsKey(term.word)){ //if the word is in the temp dictionary
            if (dictionary.get(term.word).docsIdIndexesMap.containsKey(term.docID)){ //check if doc is exist in docs list
                dictionary.get(term.word).docsIdIndexesMap.get(term.docID).add(term.wordIndex); //add this index to array list
            }
            else{ //doc is not exist in docs list
                ArrayList<Integer> indexes = new ArrayList<>();
                indexes.add(term.wordIndex);
                dictionary.get(term.word).docsIdIndexesMap.put(term.docID, indexes);//put into docs list in term object

                mainDictionary.get(term.word)[1] = Integer.parseInt(mainDictionary.get(term.word)[1])+1 + ""; //update the counter of docs for this word

            }
        }
        else{ //the word is not in the temp dictionary
            term.pointerToPosting = "resource//" + term.word.charAt(0); //pointer to final posing file path
            ArrayList<Integer> indexes = new ArrayList<>();
            indexes.add(term.wordIndex);
            term.docsIdIndexesMap.put(term.docID, indexes); //put into docs list in term object
            dictionary.put(term.word,term); //put the term in dictionary

            if (mainDictionary.containsKey(term.word)){ //if word is in the main dictionary
                mainDictionary.get(term.word)[1] = Integer.parseInt(mainDictionary.get(term.word)[1])+1 + ""; //update the counter of docs for this word
            }
            else { //word is not in the main dictionary
                String[] termPointerAndCounter = new String[3];
                termPointerAndCounter[0] = "resource//" + term.word.charAt(0); //pointer
                termPointerAndCounter[1] = "1"; //counter for number of docs
                termPointerAndCounter[2] = "1"; //counter for number of times in the corpus
                mainDictionary.put(term.word, termPointerAndCounter); //insert the term into the main dictionary
            }
        }
    }

    /**
     * this function is sending everything to posting files
     * @throws IOException
     */

    public void sendToPostingFiles() throws IOException {

        LinkedHashMap<String, Term > sortedTermPerNFiles = new LinkedHashMap<>();

        //sorting dictionary
        dictionary.entrySet()
                .stream()
                .sorted(new Comparator<Map.Entry<String, Term>>() {
                    @Override
                    public int compare(Map.Entry<String, Term> o1, Map.Entry<String, Term> o2) {
                        return o1.getKey().compareToIgnoreCase(o2.getKey());
                    }
                })
                .forEachOrdered(x -> sortedTermPerNFiles.put(x.getKey(), x.getValue()));


        String postingFilePath= "";
        if (useStemmer){
            new File(path + "\\tempPostingsStemmer").mkdir();
            postingFilePath = path + "\\tempPostingsStemmer\\" +"Stemmer_"+ postingFileNameCounter + ".txt";
        }
        else {
            new File(path + "\\tempPostings").mkdir();
            postingFilePath = path + "\\tempPostings\\" + postingFileNameCounter + ".txt";
        }
        postingFileNameCounter++;
        FileWriter posting = new FileWriter(postingFilePath);
        BufferedWriter writer = new BufferedWriter(posting);

        for (HashMap.Entry<String, Term> word: sortedTermPerNFiles.entrySet()) {
            Term term = word.getValue();
            writer.write(term.toString());
            writer.newLine();
        }
        writer.flush();
        writer.close();
        dictionary.clear();
    }

    /**
     * this function is writing the main dictionary in the disk
     * @throws IOException
     */
    public void sendMainDictionaryToPostingFiles() throws IOException {

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

        String postingFilePath = "";
        if (useStemmer){
            postingFilePath = path+"\\Stemmer_dictionary.txt";
        }
        else {
            postingFilePath = path+"\\dictionary.txt";
        }
        FileWriter posting = new FileWriter(postingFilePath);
        BufferedWriter writer = new BufferedWriter(posting);

        for (HashMap.Entry<String, String []> word: sortedTermPerNFiles.entrySet()) {
            String []  term = word.getValue();
            String string =word.getKey() + " "+ term[1] + " " + term[2];
            writer.write(string);
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }

    /**
     * this function is writing the posting files in the disk
     * @throws IOException
     */
    public void sendDocsDictionaryToPostingFiles() throws IOException {

        String postingFilePath = "";
        if (useStemmer){
            postingFilePath = path+"\\Stemmer_DocsDictionary.txt";
        }
        else {
            postingFilePath = path+"\\DocsDictionary.txt";
        }
        FileWriter posting = new FileWriter(postingFilePath, true);
        BufferedWriter writer = new BufferedWriter(posting);


        for (String term: docsDictionary.keySet()) {
            Document doc = docsDictionary.get(term);
            totalLength=totalLength+doc.length;///////////////////////////////
            writer.write(doc.toString());
            writer.newLine();
        }
        writer.write("total length: "+totalLength);/////////////////////////////
        writer.newLine();
        numberOfDocs = numberOfDocs + docsDictionary.size();
        writer.write("number of docs: "+numberOfDocs); ////////////////////////////
        writer.newLine();
        writer.flush();
        writer.close();

        docsDictionary.clear();
    }

    /**
     * this function is changing the small letter and capital letter
     * @param term
     */
    public void lowerUpperCase (Term term){
        char firstLetter = term.word.charAt(0);
        if (mainDictionary.containsKey(term.word.toLowerCase())){ //if the term contain in the main dictionary with lower letters
            if (firstLetter >= 'A' && firstLetter <= 'Z') { //if the term start with capital letter
                term.word = term.word.toLowerCase();
            }
        }
        else if(mainDictionary.containsKey(term.word.toUpperCase())){ //if the term contain in the main dictionary with upper letters
            if (firstLetter >= 'A' && firstLetter <= 'Z') { //if the term start with capital letter
                term.word = term.word.toUpperCase();
            }
            else if (firstLetter >= 'a' && firstLetter <= 'z'){ //if the term start with small letter
                String[] valueTerm =  mainDictionary.get(term.word.toUpperCase());
                mainDictionary.remove(term.word.toUpperCase()); //remove the past key
                mainDictionary.put(term.word,valueTerm); //put same value with new key

                if (dictionary.containsKey(term.word.toUpperCase())){//if the term contain in the temp dictionary with upper letters
                    //change the key in the dictionary
                    dictionary.get(term.word.toUpperCase()).word = dictionary.get(term.word.toUpperCase()).word.toLowerCase(); //change the word into small letters
                    Term newTerm = dictionary.get(term.word.toUpperCase()); //save Term form dictionary
                    dictionary.remove(term.word.toUpperCase()); //remove the key from the dictionary
                    dictionary.put(term.word, newTerm); //put same term with change only the name to small letters
                }
            }
        }
        else if (firstLetter >= 'A' && firstLetter <= 'Z'){ //if the term not contain in the main dictionary and start with capital letter - change all letter to capital letters
            term.word = term.word.toUpperCase();
        }


    }

    /**
     * this function is merging all posting files
     * @throws IOException
     */
    public void margePostingFiles () throws IOException {
        String tempPostingPath=path+"\\tempPostings";
        if (useStemmer == true){
            tempPostingPath=path+"\\tempPostingsStemmer";
        }

        File folder = new File(tempPostingPath);
        File[] files = folder.listFiles();
        String[] names = new String[files.length];

        for (int i =0 ; i<names.length ; i++)
        {
            names[i] = files[i].getName();
        }

        LinkedList<String> postingsPathQueue = new LinkedList<>();
        for (int i =0 ; i< names.length ; i++) { //add full path
                String postingPath = tempPostingPath + "\\" + names[i];
                postingsPathQueue.addLast(postingPath);
        }
        int nameForMergesPostings = 0;
        while (!(postingsPathQueue.size()==1)){
            FileInputStream inputStream1 = new FileInputStream(postingsPathQueue.remove(0));
            FileInputStream inputStream2 = new FileInputStream(postingsPathQueue.remove(0));
            FileOutputStream outputStream = new FileOutputStream(tempPostingPath + "\\marge" + nameForMergesPostings +".txt");

            BufferedReader br1 = new BufferedReader(new InputStreamReader(inputStream1));
            BufferedReader br2 = new BufferedReader(new InputStreamReader(inputStream2));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));

            String line1 = br1.readLine();
            String line2 = br2.readLine();
            String[] splitLine1={};
            String[] splitLine2={};

            while (line1!=null || line2!=null) {
                if (line1!=null){
                    splitLine1 = line1.split("~");
                }
                if (line2!=null){
                    splitLine2=line2.split("~");
                }
                if(line1!=null && line2==null){
                    while (line1!=null){
                        bw.write(splitLine1[0]+"~"+splitLine1[1]);
                        bw.newLine();
                        line1 = br1.readLine();
                    }
                }
                else if(line1==null && line2!=null){
                    while (line2!=null){
                        bw.write(splitLine2[0]+"~"+splitLine2[1]);
                        bw.newLine();
                        line2 = br2.readLine();
                    }
                }
                else if (splitLine1[0].compareToIgnoreCase(splitLine2[0])==0) { //if there are same term
                    String mergeTerm;
                    if (splitLine1[0].charAt(0) >= 'a' && splitLine1[0].charAt(0) <= 'b') { //if the first term is small letter
                        mergeTerm = splitLine1[0] + "~" + splitLine1[1] + " " + splitLine2[1];
                    }
                    else if (splitLine2[0].charAt(0) >= 'a' && splitLine2[0].charAt(0) <= 'b') { //if the second term is small letter
                        mergeTerm = splitLine2[0] + "~" + splitLine1[1] + " " + splitLine2[1];
                    }else {
                        mergeTerm = splitLine1[0] + "~" + splitLine1[1] + " " + splitLine2[1];
                    }
                    bw.write(mergeTerm);
                    bw.newLine();
                    line1 = br1.readLine();
                    line2 = br2.readLine();
                }
                else if(splitLine1[0].compareToIgnoreCase(splitLine2[0]) < 0){ // 1<2. 1 should be before 2
                    bw.write(splitLine1[0]+"~"+splitLine1[1]);
                    bw.newLine();
                    line1 = br1.readLine();
                }
                else{
                    bw.write(splitLine2[0]+"~"+splitLine2[1]);
                    bw.newLine();
                    line2 = br2.readLine();
                }
            }

            inputStream1.close();
            inputStream2.close();
            br1.close();
            br2.close();
            bw.flush();
            bw.close();
            outputStream.close();

            postingsPathQueue.addLast(tempPostingPath + "\\marge" + nameForMergesPostings +".txt");
            nameForMergesPostings++;
        }

        String stemmer = "";
        if (useStemmer==true){
            stemmer = "Stemmer";
        }

        FileInputStream inputStream = new FileInputStream(postingsPathQueue.remove(0));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = bufferedReader.readLine();
        String[] splitLine1={};
        //splitLine1 = line.split("~");
        while (line!=null){
            splitLine1 = line.split("~");
            char firstLetter = splitLine1[0].charAt(0);
            if (!(firstLetter >= 'A' && firstLetter <= 'Z') && !(firstLetter >= 'a' && firstLetter <= 'z')) {
                FileOutputStream outputStream = new FileOutputStream(path + "\\" +stemmer+"numbers.txt", true);
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
                while (line!=null && ((firstLetter >= 'A' && firstLetter <= 'Z') || !(firstLetter >= 'a' && firstLetter <= 'z'))){
                    firstLetter = splitLine1[0].toUpperCase().charAt(0);
                    char nextLetter = splitLine1[0].toUpperCase().charAt(0);
                   // FileOutputStream  outputStream = new FileOutputStream(path + "\\"+stemmer + "" +firstLetter+".txt", true);
                   // BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(outputStream));
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(path + "\\"+stemmer + "" +firstLetter+".txt"))));
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


        File tempDirectory = new File(tempPostingPath); //remove all of the temp posting files
        String[] entries = tempDirectory.list();
        for(String s: entries) {
            File currentFile = new File(tempDirectory.getPath(), s);
            currentFile.delete();
        }
        tempDirectory.delete();
    }
}
