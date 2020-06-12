import java.util.HashMap;

public class Dictionary {

    HashMap<String, String[] > mainDictionary; //0 - counter for number of docs, 1 - counter for number of times in the corpus
    HashMap<String, String[] > docsDictionary; //0 - MaxTF, 1 - number of unique words, 2 - length, 3 - 5 Entities

    int totalLength;
    int numberOfDocs;
    boolean useStemmer;


    public Dictionary(HashMap<String, String[]> mainDictionary , HashMap<String, String[]> docsDictionary) { //load Dictionary from indexer or load from memory
        this.mainDictionary = mainDictionary;
        this.docsDictionary = docsDictionary;
        totalLength=0;
        numberOfDocs=0;
        useStemmer=false;
    }


    public HashMap<String , Double> get5entitiesForDoc(String docId){
        HashMap<String , Double> best5entities = new HashMap<>();
        String[] entitiesAndScore = docsDictionary.get(docId);
        String entities = entitiesAndScore[3];
        String entitie="";
        String score="";
        String[] temp;
        entitiesAndScore=entities.split(","); //split when ,
        for(int i =0 ; i<entitiesAndScore.length ; i++){
            temp = entitiesAndScore[i].split("\\s+");//split score and entitie
            score =temp[0];
            for(int j=1 ; j<temp.length ; j++) {
                entitie = entitie + temp[j]+" ";
            }

            Double scoreOfEntitie = Double.parseDouble(score);
            best5entities.put(entitie.substring(0,entitie.length()-1),scoreOfEntitie);
            entitie="";
        }
        return best5entities;
    }

}
