import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Term {
    String word;
    int wordIndex;
    String docID;
    String tagType;
    boolean isAlreadyParse;
    HashMap<String, ArrayList<Integer>> docsIdIndexesMap; //hash map for doc id and int array for indexes
    String pointerToPosting;
    boolean isEntity;

    /**
     * this is contractor
     * @param word
     * @param wordIndex
     * @param docID
     * @param tagType
     */

    public Term(String word, int wordIndex, String docID, String tagType){
        this.word = word;
        this.wordIndex = wordIndex;
        this.docID = docID;
        this.tagType = tagType;
        this.isAlreadyParse = false;
        docsIdIndexesMap = new HashMap<>();
        pointerToPosting = "";
        isEntity = false;
    }
    public String toString (){ //implement to sting for docs and indexes
        StringBuilder sb = new StringBuilder();
        sb.append(word);
                                                                   //  sb.append(" "+tagType); //string tag type
        sb.append("~");
        Set<String> docsId = docsIdIndexesMap.keySet();
        for (String doc: docsId) {
            sb.append("&"+doc); //string docID
            sb.append(" "+ docsIdIndexesMap.get(doc).size()); //string number of indexes
            for (int i =0; i<docsIdIndexesMap.get(doc).size(); i++){
                sb.append( " " + docsIdIndexesMap.get(doc).get(i)); //string indexes
            }
        }
        return sb.toString();
    }

}
