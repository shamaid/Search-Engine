import java.util.*;
import java.util.stream.Stream;

public class Document {

    String docID;
    HashMap <String, Integer> terms; //key - term, value - counter
    HashMap <String, Integer> entities; //key - term, value - counter
    int length;

    public Document(String docID) {
        terms = new HashMap<>();
        entities = new HashMap<>();
        this.docID = docID;
        length = 1;
    }
    public int getMaxTF (){
        int maxTF = 0;
        for (String term: terms.keySet()) {
            int termTF = terms.get(term);
            if (termTF > maxTF){
                maxTF = termTF;
            }
        }
        return maxTF;
    }

    public String get5Entities (){

        LinkedHashMap<String, Integer > sortedEntitiesByCount = new LinkedHashMap<>();

        entities.entrySet()
                .stream()
                .sorted(new Comparator<Map.Entry<String, Integer>>() {
                    @Override
                    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                })
                .forEachOrdered(x -> sortedEntitiesByCount.put(x.getKey(), x.getValue()));

        String entitiesString = "";
        double maxTF = 0;
        int flag = 0;

           for (HashMap.Entry<String, Integer> ent: sortedEntitiesByCount.entrySet()) {
               if (flag ==5){
                   break;
               }
               if (flag ==0){
                   maxTF = ent.getValue();
               }
               double rate = ent.getValue()/maxTF;
               entitiesString = entitiesString + ","+rate+" " +ent.getKey();
               flag++;
           }


        return entitiesString;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(docID); //doc ID
        sb.append(" "+getMaxTF()); //MaxTF
        sb.append(" "+terms.size()); //number of unique words
        sb.append(" "+length); //length
        sb.append(" "+get5Entities()); //5 Entities

        return sb.toString();
    }
}
