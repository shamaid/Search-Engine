import java.io.*;
import java.util.*;

public class Ranker {

    Dictionary Dictionaries;
    HashMap <String,String[]> mainDictionary;
    HashMap <String,String[]> docsDictionary;
    String postingPath;
    boolean useStemmer;
    double N = 472525; //number of documents
    double avgdl = 137.768; //average length document - need to calculate it
    double k1 = 1; //value between 1.2-2.0
    double b = 0.2; //value from wikipedia


    public Ranker(Dictionary Dictionaries, String postingPath) {
        this.Dictionaries = Dictionaries;
        this.mainDictionary = Dictionaries.mainDictionary;
        this.docsDictionary = Dictionaries.docsDictionary;
        this.postingPath = postingPath;
        N = Dictionaries.numberOfDocs;
        avgdl = Dictionaries.totalLength/N;
        useStemmer = false;

    }

    public ArrayList<String> rankDocsByQuery (String [] query) throws IOException {

        HashMap<String,Double> scoreDocument = new HashMap<>(); //hashMap for all document are match for all queries. sum the rank here

        for (int i=0; i<query.length ; i++) {
            //reading the posting file of this query
            String firstLetter = ""+query[i].charAt(0);
            ///////////////////////////////////////////////////////////////change for posting path !!!!!!!!!!!!!!!!!!!!!!!!!
            FileInputStream inputStream;
            if (!useStemmer) {
                 inputStream = new FileInputStream(postingPath + "\\" + firstLetter.toUpperCase() + ".txt");
            }
            else {
                 inputStream = new FileInputStream(postingPath+"\\Stemmer"+firstLetter.toUpperCase()+".txt");
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine();
            String [] splitLine = {};
            String [] splitLineToDocs={};
            while (line!=null){
                splitLine = line.split("~"); //split to term name
                if (splitLine[0].equals(query[i]) || splitLine[0].equals(query[i].toUpperCase()) || splitLine[0].equals(query[i].toLowerCase())){ //check if the term is same like query
                    splitLineToDocs = splitLine[1].split("&"); //split to documents

                    int numberOfDocs = splitLineToDocs.length-1;
                    //loop on documents and put into hash map and calculate rankBM25

                    for (int j=1 ; j<splitLineToDocs.length ; j++){
                        String [] documentDetails = splitLineToDocs[j].split(" "); //split to indexes
                        String docID = documentDetails[0];

                        //put into hashMap the rankBM25
                        if (scoreDocument.containsKey(docID)){ //if it contain - add this value to the old one
                            scoreDocument.replace(docID, scoreDocument.get(docID)+rankBM25(documentDetails, numberOfDocs));
                        }
                        else { // if it not contain put it and calculate the value
                            scoreDocument.put(docID, rankBM25(documentDetails, numberOfDocs));
                        }
                    }

                }
                line = bufferedReader.readLine();
            }
            inputStream.close();
            bufferedReader.close();
        }

        //return the most 50 popular documents
        LinkedHashMap<String, Double > sortedDocumentByRank = new LinkedHashMap<>();

        scoreDocument.entrySet()
                .stream()
                .sorted(new Comparator<Map.Entry<String, Double>>() {
                    @Override
                    public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                })
                .forEachOrdered(x -> sortedDocumentByRank.put(x.getKey(), x.getValue()));

        ArrayList <String> rankDocuments = new ArrayList<>();
        int flag = 0;

        for (HashMap.Entry<String, Double> doc: sortedDocumentByRank.entrySet()) {
            if (flag ==50){
                break;
            }
            rankDocuments.add(doc.getKey());
            flag++;
        }



        return rankDocuments;
    }
    public double rankBM25 (String [] documentDetails, int numberOfDocs){


        if (useStemmer){
            k1=1.5;
            b = 0.2;
        }

        String docID = documentDetails[0];
        double termTimesInThisDoc = Double.parseDouble(documentDetails[1]);
        double lengthOfTheDoc = Double.parseDouble(docsDictionary.get(docID)[2]);
        double maxTF = Double.parseDouble(docsDictionary.get(docID)[0]);

        double F = termTimesInThisDoc/maxTF;
        //double F = termTimesInThisDoc;
        double D = lengthOfTheDoc;
        double n = numberOfDocs;

        double IDF = Math.log((N-n+0.5)/n+0.5);
        double bm25 = (IDF*(k1+1))/(F+k1*(1-b+b*(D/avgdl)));


        return bm25;
    }
}
