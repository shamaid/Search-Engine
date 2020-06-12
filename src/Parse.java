import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class Parse {


    Indexer indexer;
    HashMap<String, Term> namesAndEntities;
    HashSet<String> stopWords;
    //HashSet<String> allNumbersInCorpus;


    /**
     * this is a constructor
     * @throws FileNotFoundException
     */
    public Parse() throws FileNotFoundException {

        indexer = new Indexer();
        namesAndEntities = new HashMap<>();
        stopWords = new HashSet<>();
        // makeStopWordsHashSet();
    }

    /**
     * this function make a hash map for the stop words from path
     * @param path - list of stop words
     * @throws FileNotFoundException
     */
    public void makeStopWordsHashSet (String path)throws FileNotFoundException {

        //"d:\\documents\\users\\shamaid\\Downloads\\posting with past indexer\\noPosting\\resource\\stop_words.txt"
        Scanner in = new Scanner(new FileReader(path +"\\stop_words.txt"));
        while (in.hasNextLine()){
            stopWords.add(in.nextLine());
        }

    }

    //public boolean isNumber(String word){
    //     return true;
    //}

    /* public String parseANumber (String number){
         //return the number value after parsing without indexing
         String newNumber = removeDotOrCommaFromLastChar(number);
         return newNumber;
     }*/

    /**
     * this function parse numbers
     * @param termObject
     * @param words
     * @param i - index
     * @param docID
     * @param tagType
     * @return
     * @throws IOException
     */
    public String parseANumber (Term termObject, String[] words , int i , String docID , String tagType) throws IOException {
        //return the number value after parsing without indexing


        words[i] =  removeDotOrCommaFromLastChar(words[i]);
        if(!words[i].equals("") && !words[i].equals("-")) {
            if (isNumber(words[i])) {
                removePunctuation(words, i);

                if (isExpression(words, i)) {
                    termObject.word = typeOfExpression(words, i);
                    if (!termObject.isAlreadyParse) {
                        termObject.isAlreadyParse = true;
                        indexer.termToIndex(termObject);
                    }
                    return termObject.word;
                } else if (isPrice(words, i)) {
                    termObject.word = typeOfPrice(words, i);
                    if (!termObject.isAlreadyParse) {
                        termObject.isAlreadyParse = true;
                        indexer.termToIndex(termObject);
                    }
                    return termObject.word;
                } else if (isPercent(words, i)) {
                    termObject.word = typeOfPercent(words, i);
                    if (!termObject.isAlreadyParse) {
                        termObject.isAlreadyParse = true;
                        indexer.termToIndex(termObject);
                    }
                    return termObject.word;

                } else if (isKgsOrKm(words, i)) {
                    termObject.word = typeOfKgs(words, i);
                    if (!termObject.isAlreadyParse) {
                        termObject.isAlreadyParse = true;
                        indexer.termToIndex(termObject);
                    }
                    return termObject.word;
                } /*else if (isKms(words, i)) {
                    termObject.word = typeOfKms(words, i);
                    if (!termObject.isAlreadyParse) {
                        termObject.isAlreadyParse = true;
                        indexer.termToIndex(termObject);
                    }
                    return termObject.word;
                }*/ else if (isGMT(words, i)) {
                    termObject.word = typeOfGMT(words, i);
                    if (!termObject.isAlreadyParse) {
                        termObject.isAlreadyParse = true;
                        indexer.termToIndex(termObject);
                    }
                    return termObject.word;
                } else if (isDate(words, i)) {
                    termObject.word = typeOfDate(words, i);
                    if (!termObject.isAlreadyParse) {
                        termObject.isAlreadyParse = true;
                        indexer.termToIndex(termObject);
                    }
                    return termObject.word;
                } else if (isRegularNumber(words, i)) {
                    termObject.word = typeOfRegularNumber(words, i);
                    if (!termObject.isAlreadyParse) {
                        termObject.isAlreadyParse = true;
                        indexer.termToIndex(termObject);
                    }
                    return termObject.word;
                }


            }//else it is a word
        }
        return "";
    }



    /**
     * Separate text tab from doc file and return it
     * @param doc
     * @return array of separate words
     */
    public String[] extractTextFromDoc(String doc) {


        String[] separateWords = doc.split("\\s+");
        //find the start index of TEXT tab (i) and final index of TEXT tab (j)
        for (int i = 0 ; i<separateWords.length ; i++){
            if (separateWords[i].equals("<TEXT>")){
                for (int j=i+1 ; j<separateWords.length ; j++){
                    if (separateWords[j].equals("</TEXT>")){
                        String[] text = Arrays.copyOfRange(separateWords, i+1, j); //copy to new words array
                        return text;
                    }
                }
            }

        }
        return new String[0];
    }

    /**
     * this function extract the document ID  form document
     * @param doc
     * @return doc ID
     */
    public String extractDocIDFromDoc(String doc){
        String[] separateWords = doc.split("\\s+");
        //find the start index of DOC tab (i) and final index of DOC tab (j)
        for (int i = 0 ; i<separateWords.length ; i++){
            if (separateWords[i].equals("<DOCNO>")){
                String docNo = separateWords[i+1]; //put the next word into wordID
                return docNo;

            }
            if (separateWords[i].contains("<DOCNO>")){
                String docNo = separateWords[i].replaceAll("<DOCNO>" ,"");
                docNo = docNo.replaceAll("</DOCNO>" ,"");
                return docNo;
            }

        }

        return "";
    }



    /**
     *   return each word of string on array
     * @param doc
     * @return each word
     */
    public String [] putEachWordOnArray(String doc){

        String [] separateWords = doc.split("\\s+");

        return separateWords;
    }


    /**
     * this function sent the doc to parse
     * @param doc to be parse
     * @throws IOException
     */
    public void sendDocToParse (String doc) throws IOException {
        //this.doc = doc;

        //all the words between TEXT tab.
        // (maybe in part B we will implement more function like it for more tabs)
        String [] textWords = extractTextFromDoc(doc);
        String docID = extractDocIDFromDoc(doc);
        parser(textWords, docID,"TEXT"); //parse only TEXT tab
    }

    /**
     * this function removes dots and commas from the last char if they exist
     * @param term - to remove form
     * @
     */
    public String removeDotOrCommaFromLastChar (String term){

        while(term.length() >= 2) {
            String startOfTerm = term.substring(0, 2);
            if (startOfTerm.equals("..") || startOfTerm.equals("//") || startOfTerm.equals("--") || startOfTerm.equals(",,") || startOfTerm.equals("$$") || startOfTerm.equals("%%")) {
                term = term.substring(2);
            }else {
                break;
            }
        }

        while(term.length() >= 1){
            String startOfTerm = term.substring(0, 1);
            if((startOfTerm.equals("-") && !isNumber(term)) || (startOfTerm.equals("$") && !isNumber(term)) || (startOfTerm.equals("%") && !isNumber(term))) {
                term = term.substring(1);
            }
            else if (startOfTerm.equals(".") || startOfTerm.equals("/") || startOfTerm.equals(",")) {
                term = term.substring(1);
            }
            else{
                break;
            }
        }


        //end of term
        while(term.length() >=2){
            String startOfTerm = term.substring(term.length()-2);
            if(startOfTerm.equals("..") || startOfTerm.equals("//") || startOfTerm.equals("--") || startOfTerm.equals(",,") || startOfTerm.equals("$$") || startOfTerm.equals("%%")){
                term=term.substring(0,term.length() - 2);
            }
            if(term.length() >=1) {
                startOfTerm = term.substring(term.length() - 1);
                if (startOfTerm.equals(".") || startOfTerm.equals("/") || startOfTerm.equals("-") || startOfTerm.equals(",") || startOfTerm.equals("$") || startOfTerm.equals("%")) {
                    term = term.substring(0, term.length() - 1);
                } else {
                    break;
                }
            }
        }



        if(term.length() >=1) {
            char lastChar = term.charAt(term.length() - 1);
            String lastNot = term.substring(term.length() - 1);

            // if (lastNot.equals("\'")) {
            //    words[i] = words[i].substring(0, words[i].length() - 1);
            //}
            if (lastChar == '.' || lastChar == ',' || lastChar == '"' || lastChar == '*' || lastChar == '\'' || lastChar == '-') {
                term = term.substring(0, term.length() - 1);
                if (term.contains("\"")) {
                    term = term.replaceAll("\"", "");
                }
            }

        }
        //System.out.println(words[i]);

        if(term.equals("-") || term.equals("/") || term.equals("%") || term.equals("$")){
            term="";
            return term;
        }
        return term;
    }


    /**
     * this function is combine all parse function here
     * @param words
     * @param docID
     * @param tagType
     * @throws IOException
     */

    public void parser (String[] words,String docID, String tagType) throws IOException {

        for (int i =0 ; i <words.length ; i ++) {

            if (!words[i].equals("") && !stopWords.contains(words[i]) && !words[i].equals("-") || words[i].equals("between")) {
                words[i] = removeDotOrCommaFromLastChar(words[i]); //remove dot or comma from the last char
                if (!words[i].equals("") && !stopWords.contains(words[i]) && !words[i].equals("-") || words[i].equals("between")) {
                    if (!words[i].equals("-")) {
                        Term term = new Term(words[i], i, docID, tagType);
                        //if the string is a number
                        if (isNumber(words[i])) {
                            if (!words[i].equals("")) {
                                parseANumber(term, words, i, docID, tagType);
                            }
                        }//else it is a word
                        else {

                            //Check if it "-" expression
                            String lowerCaseWord = words[i].toLowerCase();
                            if (!parseRange(term, words, i, docID, tagType) && !stopWords.contains(lowerCaseWord)) {
                                term.isAlreadyParse = true;
                                indexer.termToIndex(term);
                            }

                            //Check if the first letter is capital letter


                            char firstLetter = words[i].charAt(0);

                            if (firstLetter >= 'A' && firstLetter <= 'Z') {


                                ////////////call to parseNamesOrEntities function////////////////////
                                parseNamesOrEntities(term, words, i, docID, tagType);
                                ///////////call to capitalOrSmallLetter function//////////////////////

                            }

                        }
                    }
                    else {
                        System.out.println("-");
                    }
                }
            }

        }
    }

    /**
     * check if it 'Names or entities'
     * @return int - next i index to parse - if we find Entity - dont check the next indexes
     */
    public int parseNamesOrEntities (Term term, String[] words, int wordIndex, String docID, String tagType) throws IOException {

        //finding the next words with capital letter
        int nextWordIndex = wordIndex + 1;
        if (nextWordIndex >= words.length){ //there is no next word
            return wordIndex;
        }
        int firstLetter = words[nextWordIndex].charAt(0);

        //check if the next word start with capital letter
        if (!(firstLetter >= 'A' && firstLetter <= 'Z')) {
            return wordIndex;
        }

        //Make String with all the words in the expression
        words[nextWordIndex] = removeDotOrCommaFromLastChar(words[nextWordIndex]);
        String nameOrEntitie = words[wordIndex] + " " + words[nextWordIndex];

        //putNameOrEntitieIntoHashMapOrIndexer(nameOrEntitie, wordIndex,docID,tagType); //put subString into indexer


        //looking for the wordIndex of the last word with capital letter

        nextWordIndex = nextWordIndex + 1;
        if (nextWordIndex < words.length) { //there are more words in the array
            firstLetter = words[nextWordIndex].charAt(0);
            while (firstLetter >= 'A' && firstLetter <= 'Z') {

                words[nextWordIndex] = removeDotOrCommaFromLastChar(words[nextWordIndex]);
                nameOrEntitie = nameOrEntitie + " " + words[nextWordIndex]; //put on string

                // putNameOrEntitieIntoHashMapOrIndexer(nameOrEntitie, wordIndex, docID, tagType); //put subString into indexer

                nextWordIndex = nextWordIndex + 1;
                if (nextWordIndex < words.length) { //there are more words in the array
                    firstLetter = words[nextWordIndex].charAt(0);
                }
                else{
                    firstLetter = 'a'; //no more words so get out from 'while loop'
                }
            }
        }
        Term entity = new Term(nameOrEntitie,wordIndex,docID,tagType);
        entity.isEntity = true;
        putNameOrEntitieIntoHashMapOrIndexer(entity, nameOrEntitie, wordIndex, docID, tagType);
        return nextWordIndex-1;
    }

    /**
     * this function put names and entities on hash map
     * @param term
     * @param nameOrEntitie
     * @param wordIndex
     * @param docID
     * @param tagType
     * @return
     * @throws IOException
     */
    public boolean putNameOrEntitieIntoHashMapOrIndexer (Term term, String nameOrEntitie, int wordIndex, String docID, String tagType) throws IOException {
        //check if the expression is contains in the hashMap
        //if not - put the expression into the hashMap and put also all details
        //if yes -  check if there are details also.
        //          if yes - send the old expression and the new expression to Indexer
        //                 - remove the old expression details from the hashMap
        //          if not - send the expression to Indexer

        if (!(namesAndEntities.containsKey(nameOrEntitie))){ //if not contain
            namesAndEntities.put(nameOrEntitie, term);
        }
        else{ // if it contain

            if (namesAndEntities.get(nameOrEntitie) == null){ // if no details
                term.isAlreadyParse = true;
                indexer.termToIndex(term);
                return true;
            }
            else{

                int oldWordIndex =  namesAndEntities.get(nameOrEntitie).wordIndex;
                String oldDocID =  namesAndEntities.get(nameOrEntitie).docID;
                String oldTagType =  namesAndEntities.get(nameOrEntitie).tagType;

                namesAndEntities.get(nameOrEntitie).isAlreadyParse = true;

               /////////////////////////////////////// indexer.termToIndex(namesAndEntities.get(nameOrEntitie)); //insert the old term
                term.isAlreadyParse = true;
                indexer.termToIndex(term); //insert the new term
                //remove details from the hashMap
                namesAndEntities.replace(nameOrEntitie, null);
                return true;
            }
        }

        return true;
    }
    //Check if it a-z or A-Z words



    /**
     * this function Check if it "-" expression and parse it
     * @param term
     * @param words
     * @param wordIndex
     * @param docID
     * @param tagType
     * @return true - if it is, false if not
     * @throws IOException
     */
    public boolean parseRange (Term term, String[] words, int wordIndex, String docID, String tagType) throws IOException {

        if (words[wordIndex].contains("-")){
            if (words[wordIndex].charAt(0)!='-' && words[wordIndex].charAt(words[wordIndex].length()-1)!= '-') {
                term.isAlreadyParse = true;
                indexer.termToIndex(term);
                return true;
            }

        }
        else{
            if (words[wordIndex].contains("between") || words[wordIndex].contains("Between")){ //The first word is 'between'
                if (wordIndex+3 < words.length && isNumber(words[wordIndex+1])&&isNumber(words[wordIndex+3])){ //the second and the 4th word is a number
                    if (words [wordIndex+2].contains("and")){ //the third word is "and"
                        // String s = parseANumber(words[wordIndex+1]) + "-" + parseANumber(words[wordIndex+3]); //combine to "-" expression and parse numbers
                        String s = parseANumber(term , words,wordIndex+1 , docID , tagType) + "-" + parseANumber(term , words,wordIndex+3 , docID , tagType); //combine to "-" expression and parse numbers
                        term.isAlreadyParse = true;
                        indexer.termToIndex(term);
                        return true;
                    }

                }
            }
        }
        return false;
    }

    /**
     * this function remove Punctuation
     * @param words
     * @param i - index
     */
    public void removePunctuation(String[] words , int i){
        words[i] = words[i].replaceAll("!","");
        words[i] = words[i].replaceAll("\\?","");
        words[i] = words[i].replaceAll("@","");
        words[i] = words[i].replaceAll("#","");
        words[i] = words[i].replaceAll("^","");
        words[i] = words[i].replaceAll("&","");
        words[i] = words[i].replaceAll("\\+","");
        words[i] = words[i].replaceAll("=","");
        words[i] = words[i].replaceAll("\\*","");
        words[i] = words[i].replaceAll("\\(","");
        words[i] = words[i].replaceAll("\\)","");
        words[i] = words[i].replaceAll("\\|","");
        words[i] = words[i].replaceAll("'","");
        words[i] = words[i].replaceAll("`","");
        if(words[i].contains("\\") && words[i].length()<3){
            words[i] = words[i].replaceAll("\\\\","");

        }

    }

    /**
     * this function is checking if it is a number and parse it
     * @param word
     * @return true - if it is a number, false - if not
     */
    public boolean isNumber(String word){




        // if(word.matches("^((\\$)?[-]?)?+[0-9]+,?+[0-9]*+,?+[0-9]*+,?+[0-9]*+.?+[0-9]*+%?+$")){
       /* if(word.matches("^([$%]?(.)?)[0-9]++$")){
            if(!word.matches(".*[a-zA-Z].*")){
                return true;
            }
        }*/
        //^((\$)?[-]?)?.?+[0-9]*+,?+[0-9]*+,?+[0-9]*+,?+[0-9]*+.?+[0-9]*+%?+$
        //^((\\$)?[-]?)?+[0-9]+,?+[0-9]*+,?+[0-9]*+,?+[0-9]*+.?+[0-9]*+%?+$"
        //^([\\$%]?[-]?)?[0-9]*+,?+[0-9]*+,?+[0-9]*+,?+[0-9]*+.?+[0-9]*+%?+$


        //^([$%]?[-]?)?[0-9]+,?+[0-9]*+,?+[0-9]*+,?+[0-9]*+.?+[0-9]*+%?+$" ////////OK
        ///^[0-9]*[$%]?[-]?[.]?+[0-9]+,?+[0-9]*+,?+[0-9]*+,?+[0-9]*+.?+[0-9]*+%?+$
        //^[0-9]*(\$)?[-]?[0-9]+,?+[0-9]*+,?+[0-9]*+,?+[0-9]*+.?+[0-9]*+%?+$


        //^[0-9]*(\$)?[-]?[0-9]+,?+[0-9]*+,?+[0-9]*+,?+[0-9]*+.?+[0-9]*+%?+$
        //^(\$)?[-]?[0-9]+,?+[0-9]*+,?+[0-9]*+,?+[0-9]*+.?+[0-9]*+%?+$ //try after run
        if(word.matches("^(\\$)?[-]?[0-9]+,?+[0-9]*+,?+[0-9]*+,?+[0-9]*+.?+[0-9]*+%?")){
            if(!word.matches(".*[a-zA-Z].*") || word.contains("bn") || word.contains("m")){
                return true;
            }
        }
        return false ;
    }

    /**
     * this function is checking if it is percent
     * @param words
     * @param i
     * @return true - if it is, false - if not
     */
    public boolean isPercent (String[] words, int i){
        if(i+1 < words.length){
            if(words[i+1].matches("^percent+$") || words[i+1].matches("^percentage+$")){
                return true;
            }
        }
        if(words[i].matches(".*%.*")){
            return true;
        }
        return false;
    }

    /**
     * this function is chrcking if it is a date
     * @param words
     * @param i
     * @return true - if it is, false - if not
     */

    public boolean isDate (String[] words, int i){
        String []monthPart = {"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
        String []monthFull = {"JANUARY","FEBRUARY","MARCH","APRIL","MAY","JUNE","JULY","AUGUST","SEPTEMBER","OCTOBER","NOVEMBER","DECEMBER"};

        if(words[i].contains("-") || words[i].contains("/") || words[i].contains("%") || words[i].contains("$") || words[i].contains(".")  || words[i].contains(",")){
            return false;
        }
        for(int j=0 ; j<monthFull.length ; j++) {
            if(i+1 < words.length) {
                if ( (words[i + 1].toUpperCase().equals(monthFull[j]) && isNumber(words[i])) || (words[i + 1].toUpperCase().startsWith(monthPart[j]) && isNumber(words[i])))
                    return true;
            }
            if(i != 0) {
                if (  (words[i - 1].toUpperCase().equals(monthFull[j]) && isNumber(words[i])) || (words[i-1].toUpperCase().startsWith(monthPart[j]) && isNumber(words[i])))
                    return true;
            }
        }

        return false;
    }

    /**
     * this function is checking if it is a price
     * @param words
     * @param i
     * @return true - if it is, false - if not
     */
    public boolean isPrice(String[] words , int i) {
        if(words[i].contains("%")){
            return false;
        }
        if(i+1 < words.length) {
            if (words[i+1].matches("^Dollars+$")) {
                return true;
            }
        }
        if(i+2 < words.length) {
            if (words[i+1].contains("/") && words[i+2].matches("^Dollars+$")) {
                return true;
            }
        }

        if(i+4 < words.length){
            if(words[i+4].matches("^Dollars+$")) {
                return true;
            }
        }
        if( words[i].matches(".*[$].*"))
        {
            return true;
        }
        if(words[i].contains("m") || words[i].contains("bn")){
            words[i] = words[i].replaceAll("m", "");
            words[i] = words[i].replaceAll("bn", "");
            return false;
        }
        return false;
    }

    /**
     * this function is checking if it is a Kgs or kn
     * @param words
     * @param i
     * @return true - if it is, false - if not
     */
    public boolean isKgsOrKm(String[] words , int i){
        if(i+1 < words.length){
            if(words[i + 1].matches("^kgs+$") || words[i + 1].matches("^ton+$")
                    || words[i + 1].matches("^tons+$") || words[i + 1].matches("^Kilograms+$")
                    || words[i + 1].matches("^grams+$") || words[i + 1].matches("^km+$") || words[i + 1].matches("^meter+$")){
                return true;
            }
        }
        if(i+2 < words.length) {
            if ((words[i + 2].matches("^kgs+$") || words[i + 2].matches("^ton+$")
                    || words[i + 2].matches("^tons+$") || words[i + 2].matches("^Kilograms+$")
                    || words[i + 2].matches("^grams+$") || words[i + 2].matches("^km+$") || words[i + 2].matches("^meter+$")) && words[i+1].contains("/")) {
                return true;
            }
        }
        return false;
    }

    public boolean isKms(String[] words , int i){
        if(i+1 < words.length){
            if(words[i + 1].matches("meter") || words[i + 1].matches("km")
                    || words[i + 1].matches("kilometer") || words[i + 1].matches("centimeter")){
                return true;
            }
        }
        if(i+2 < words.length){
            if((words[i + 2].matches("meter") || words[i + 2].matches("km")
                    || words[i + 2].matches("kilometer") || words[i + 2].matches("centimeter")) && words[1].contains("/")){
                return true;
            }
        }
        return false;
    }

    public boolean isGMT(String[] words , int i){
        if(i+1 < words.length){
            if(words[i+1].equals("GMT"))
                return true;
        }
        return false;
    }

    public boolean isRegularNumber(String[] words , int i){
        if(words[i].contains("\\$") || words[i].contains("%") )
            return false;
        return true;
    }

    public boolean isExpression(String[] words , int i){
        //if( ( words[i].contains("$") || words[i].contains("%") ) && words[i].contains("-"))
        if(words[i].matches("^(-)?[$%]?[0-9]+-(-)?[$%]?[0-9]++$")) {
            return true;
        }
        if(  words[i].contains("-"))
            return true;
        else if( words[i].matches("^-?[0-9]*--?[0-9]*+$"))
            return true;
        return false;
    }




    public String typeOfExpression(String[] words , int i){
        if(words[i].matches("^(-)?[$%]?[0-9]+-(-)?[$%]?[0-9]++$")) {
            String firstWord = "";
            String secondWord = "";

            char[] charsOfExpression;
            charsOfExpression = words[i].toCharArray();

            for (int j = 0; j < charsOfExpression.length; j++) {
                if (charsOfExpression[0] == '-') {
                    continue;
                }
                if (charsOfExpression[j] == '-') {
                    firstWord = words[i].substring(0, j);
                    secondWord = words[i].substring(j + 1);
                    break;
                }
            }


            if (!firstWord.contains("$") && !firstWord.equals("")) {
                if (!firstWord.contains("%")) {
                    firstWord = ExpressionToRegularNumber(firstWord);
                }
            }
            if (!secondWord.contains("$") && !secondWord.equals("")) {
                if (!secondWord.contains("%")) {
                    secondWord = ExpressionToRegularNumber(secondWord);
                }
            }
            if (firstWord.contains("$") && firstWord.length() > 1) {
                firstWord = ExpressionToPrice(firstWord);
            }
            if (secondWord.contains("$") && secondWord.length() > 1) {
                secondWord = ExpressionToPrice(secondWord);
            }
            words[i] = firstWord + "-" + secondWord;

            return words[i];

        }
        return words[i];
    }

    public String ExpressionToRegularNumber(String word){

        if(word.contains("/")){
            return word;
        }

        String toNumber;
        toNumber = word.replaceAll(",", "");
        toNumber = toNumber.replaceAll("'", "");

        double number;
        number = Double.parseDouble(toNumber);

        if(number < 1000 && number >-1000){
            if(word.contains("/")){
                return word;
            }
            return numberType(number);
        }
        else if((number >= 1000 && number <1000000) || (number<=-1000 && number>-1000000)){

            return  numberType(number/1000)+ "K";
        }
        else if((number >= 1000000 && number < 1000000000) || (number<=-1000000 && number>-1000000000)){

            return  numberType(number/1000000)+ "M";
        }
        else if(number >= 1000000000 || number<=-1000000000){
            return  numberType(number/1000000000)+ "B";
        }
        return word;
    }

    public String ExpressionToPrice(String word){

        double number;
        String wordToNumber=word;

        wordToNumber=wordToNumber.replaceAll("\\$","");
        wordToNumber=wordToNumber.replaceAll(",","");

        number = Double.parseDouble(wordToNumber);

        if(number >1000000){
            word = numberType(number/1000000) +"M Dollars";
        }
        else{
            word = word +" Dollars";
        }
        return word;
    }

    public String typeOfRegularNumber(String[] words , int i){

        String toNumber;
        toNumber = words[i].replaceAll(",", "");
        toNumber = toNumber.replaceAll("'", "");

        //toNumber = toNumber.replaceAll(" " , "");

        if(toNumber.contains("/")){
            if(words[i].contains("/")){
                return words[i];
            }
            if(i+1 < words.length) {
                if (words[i + 1].contains("/")) {
                    return words[i] + " " + words[i + 1];
                }
            }
        }


        double number;
        number = Double.parseDouble(toNumber);


        if(i+1 < words.length) {
            if (words[i + 1].matches("^Thousand+$")) {
                return numberType(number) + "K";
            }
            if (words[i + 1].matches("^Million+$")) {
                return numberType(number) + "M";
            }
            if (words[i + 1].matches("^Billion+$")) {
                return numberType(number) + "B";
            }
        }



        if(number < 1000 && number >-1000){
            if(words[i].contains("/")){
                return words[i];
            }
            if(i+1 < words.length) {
                if (words[i + 1].contains("/") && isNumber(words[i+1])) {
                    return words[i] + " " + words[i + 1];
                }
            }
            return numberType(number);

        }
        else if((number >= 1000 && number <1000000) || (number<=-1000 && number>-1000000)){

            return  numberType(number/1000)+ "K";
        }
        else if((number >= 1000000 && number < 1000000000) || (number<=-1000000 && number>-1000000000)){

            return  numberType(number/1000000)+ "M";
        }
        else if(number >= 1000000000 || number<=-1000000000){
            return  numberType(number/1000000000)+ "B";
        }

        return "NOT A REGULAR NUMBER";

    }

    public String numberType (double number){
        DecimalFormat df = new DecimalFormat("#.###");
        return df.format(number);
    }

    public String typeOfPrice(String[] words , int i) {

        String toNumber;
        toNumber = words[i].replaceAll("\\$", "");

        double number;

        if(overMillion(words,i)) {

            toNumber = toNumber.replaceAll(",", "");
            toNumber = toNumber.replaceAll("m", "");
            toNumber = toNumber.replaceAll("bn", "");

            if(words[i].contains("/")){
                return words[i] ;
            }

            number = Double.parseDouble(toNumber);
            if(i+1 < words.length) {

                if (words[i].matches(".*m.*") && words[i + 1].matches("^Dollars+$"))
                {
                    return numberType(number) + " M Dollars";
                }
                else if (words[i].matches(".*bn.*") && words[i + 1].matches("^Dollars+$"))
                {
                    return numberType(number * 1000) + " M Dollars";
                }
                else if (words[i].matches(".*[$].*") && words[i + 1].matches(".*million.*"))
                {
                    return numberType(number) + " M Dollars";
                }
                else if (words[i].matches(".*[$].*") && words[i + 1].matches(".*billion.*"))
                {
                    return numberType(number * 1000) + " M Dollars";
                }
                else if (words[i + 1].matches("^million+$"))
                {
                    return numberType(number) + " M Dollars";
                }
                else if (words[i + 1].matches("^billion+$"))
                {
                    return numberType(number * 1000) + " M Dollars";
                }
                else if (words[i + 1].matches("^trillion+$"))
                {
                    return numberType(number * 1000000) + " M Dollars";
                }
                else if (words[i + 1].matches("^Dollars+$"))
                {
                    return numberType(number / 1000000) + " M Dollars";
                }
            }
            else if(words[i].matches(".*[$].*"))
            {
                return numberType(number/1000000) +" M Dollars";
            }


        }
        //less then million
        else{
            //  number = Double.parseDouble(toNumber);

            if(words[i].matches(".*[$].*"))
            {
                return toNumber + " Dollars";
            }
            //if there is a fraction
            if(i+1 < words.length) {
                if (words[i + 1].contains("/") && words[i + 2].matches("^Dollars+$"))
                {
                    return toNumber + " " + words[i + 1] + " Dollars";
                }
                else if (words[i + 1].matches("^Dollars+$"))
                {
                    return toNumber + " Dollars";
                }
            }

        }//end else

        return words[i];
    }

    public String typeOfPercent(String[] words , int i){

        if(words[i].matches(".*%.*")){
            return words[i];
        }
        if(i+1 < words.length) {
            if (words[i + 1].matches("^percent+$") || words[i + 1].matches("^percentage+$")) {
                return words[i] + "%";
            }
        }

        return "NOT A PERCENT !";
    }

    public String typeOfDate (String[] words , int i){

        String []month = {"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
        int numOfMonth=0;
        int number;

        if(isNumber(words[i])){
            for(int j=0 ; j<month.length ; j++){
                if(i+1 < words.length) {
                    if (words[i + 1].toUpperCase().contains(month[j])) {
                        numOfMonth = j + 1;
                        //words[i + 1] = "";
                        break;
                    }
                }
                if(i != 0){
                    if(words[i-1].toUpperCase().contains(month[j]) ){
                        numOfMonth=j+1;
                        // words[i-1]="";
                        break;
                    }
                }
            }

            number = Integer.parseInt(words[i]);
            if(number>31){//years
                if(numOfMonth <10) {
                    return words[i] + "-0" + numOfMonth;
                }else{
                    return words[i] + "-" + numOfMonth;
                }
            }
            else if(number < 10){
                if(numOfMonth <10) {
                    return "0" + numOfMonth + "-0" + words[i];
                }else{
                    return numOfMonth + "-0" + words[i];
                }
            }else {
                if(numOfMonth <10) {
                    return "0" + numOfMonth + "-" + words[i];
                }else{
                    return  numOfMonth + "-" + words[i];
                }
            }
        }

        return "NOT A DATE !";
    }

    /*public String typeOfKgs(String[] words , int i){
        words[i]=words[i].replaceAll(",","");
        if(i+1 < words.length) {
            if (words[i + 1].matches("^kgs+$") || words[i + 1].matches("^Kilograms+$")) {
                return words[i] + " Kilograms";
            }
            if(words[i + 1].matches("^ton+$") || words[i + 1].matches("^tons+$")){
                if(words[i].contains("/")){
                    String firstWord="";
                    String secondWord="";

                    char[] charsOfExpression;
                    charsOfExpression=words[i].toCharArray();

                    for(int j=0 ; j<charsOfExpression.length ; j++){
                        if(charsOfExpression[j]=='/'){
                            firstWord=words[i].substring(0,j);
                            secondWord=words[i].substring(j+1);
                            break;
                        }
                    }
                    double firstnumber = Double.parseDouble(firstWord);
                    firstnumber=firstnumber*1000;
                    double secondnumber = Double.parseDouble(secondWord);
                    return numberType(firstnumber/secondnumber) + " Kilograms";
                }
                double number = Double.parseDouble(words[i]);
                return numberType(number*1000) +  " Kilograms";

            }
            if(words[i + 1].matches("^grams$")){
                if(words[i].contains("/")){
                    String firstWord="";
                    String secondWord="";

                    char[] charsOfExpression;
                    charsOfExpression=words[i].toCharArray();

                    for(int j=0 ; j<charsOfExpression.length ; j++){
                        if(charsOfExpression[j]=='/'){
                            firstWord=words[i].substring(0,j);
                            secondWord=words[i].substring(j+1);
                            break;
                        }
                    }
                    double firstnumber = Double.parseDouble(firstWord);
                    double secondnumber = Double.parseDouble(secondWord);
                    secondnumber = secondnumber*1000;
                    return numberType(firstnumber/secondnumber) + " Kilograms";
                }
                double number = Double.parseDouble(words[i]);
                return numberType(number/1000) +  " Kilograms";
            }
        }
        if(i+2 < words.length) {
            if ((words[i + 2].matches("^kgs+$") || words[i + 2].matches("^Kilograms+$")) && words[1].contains("/")) {
                return words[i] +""+ words[i+1] + " Kilograms";

            }
            if(words[i + 2].matches("^ton+$") || words[i + 2].matches("^tons+$")){
                if(words[i+1].contains("/")){
                    String firstWord="";
                    String secondWord="";

                    char[] charsOfExpression;
                    charsOfExpression=words[i+1].toCharArray();

                    for(int j=0 ; j<charsOfExpression.length ; j++){
                        if(charsOfExpression[j]=='/'){
                            firstWord=words[i+1].substring(0,j);
                            secondWord=words[i+1].substring(j+1);
                            break;
                        }
                    }
                    double number = Double.parseDouble(words[i]);
                    number = number*1000;
                    double firstnumber = Double.parseDouble(firstWord);
                    firstnumber=firstnumber*1000;
                    double secondnumber = Double.parseDouble(secondWord);
                    return numberType(number + firstnumber/secondnumber) + " Kilograms";
                }
                double number = Double.parseDouble(words[i+1]);
                return numberType(number*1000) +  " Kilograms";

            }
            if(words[i + 2].matches("^grams$")){
                if(words[i+1].contains("/")){
                    String firstWord="";
                    String secondWord="";

                    char[] charsOfExpression;
                    charsOfExpression=words[i+1].toCharArray();

                    for(int j=0 ; j<charsOfExpression.length ; j++){
                        if(charsOfExpression[j]=='/'){
                            firstWord=words[i+1].substring(0,j);
                            secondWord=words[i+1].substring(j+1);
                            break;
                        }
                    }
                    double number = Double.parseDouble(words[i]);
                    number = number/1000;
                    double firstnumber = Double.parseDouble(firstWord);
                    double secondnumber = Double.parseDouble(secondWord);
                    secondnumber = secondnumber*1000;
                    return numberType(number + firstnumber/secondnumber) + " Kilograms";
                }
                double number = Double.parseDouble(words[i+1]);
                return numberType(number/1000) +  " Kilograms";

            }
        }
        return words[i];
    }

    */

    public String typeOfKgs(String[] words , int i){
        if(i+1 < words.length) {
            if (words[i + 1].matches("^kgs+$") || words[i + 1].matches("^Kilograms+$")) {
                return words[i] + " Kilograms";
            }
            if (words[i + 1].matches("^ton+$") || words[i + 1].matches("^tons+$")) {
                return words[i] + " Tons";
            }
            if (words[i + 1].matches("^grams+$") ) {
                return words[i] + " Grams";
            }
            if (words[i + 1].matches("^km+$")) {
                return words[i] + " Kilometer";
            }
            if (words[i + 1].matches("^meter+$") ) {
                return words[i] + " Meter";
            }
        }
        if(i+2 < words.length) {
            if ((words[i + 2].matches("^kgs+$") || words[i + 2].matches("^Kilograms+$") ) && words[i+1].contains("/")) {
                return  words[i] +""+words[i+1]+" Kilograms";
            }
            if ((words[i + 2].matches("^ton+$") || words[i + 2].matches("^tons+$")) && words[i+1].contains("/")) {
                return  words[i] +""+words[i+1]+" Tons";
            }
            if ((words[i + 2].matches("^grams+$") )&& words[i+1].contains("/")) {
                return words[i] +""+words[i+1]+ " Grams";
            }
            if ((words[i + 2].matches("^km+$") )&& words[i+1].contains("/")) {
                return words[i] +""+words[i+1]+ " Kilometer";
            }
            if ((words[i + 2].matches("^meter+$") )&& words[i+1].contains("/")) {
                return words[i] +""+words[i+1]+ " Meter";
            }
        }
        return words[i];
    }

    public String typeOfKms(String[] words , int i){
        words[i]=words[i].replaceAll(",","");
        if(i+1 < words.length) {
            if (words[i + 1].matches("^meter+$")) {
                return words[i] + " Meters ";
            }
            if (words[i + 1].matches("^km+$") || words[i + 1].matches("^kilometer+$")) {
                if(words[i].contains("/")){
                    String firstWord="";
                    String secondWord="";

                    char[] charsOfExpression;
                    charsOfExpression=words[i].toCharArray();

                    for(int j=0 ; j<charsOfExpression.length ; j++){
                        if(charsOfExpression[j]=='/'){
                            firstWord=words[i].substring(0,j);
                            secondWord=words[i].substring(j+1);
                            break;
                        }
                    }
                    double firstnumber = Double.parseDouble(firstWord);
                    firstnumber=firstnumber*1000;
                    double secondnumber = Double.parseDouble(secondWord);
                    return numberType(firstnumber/secondnumber) + " Meters";
                }
                double number = Double.parseDouble(words[i]);
                return numberType(number*1000) +  " Meters";

            }
            if(words[i + 1].matches("^centimeter+$")){
                if(words[i].contains("/")){
                    String firstWord="";
                    String secondWord="";

                    char[] charsOfExpression;
                    charsOfExpression=words[i].toCharArray();

                    for(int j=0 ; j<charsOfExpression.length ; j++){
                        if(charsOfExpression[j]=='/'){
                            firstWord=words[i].substring(0,j);
                            secondWord=words[i].substring(j+1);
                            break;
                        }
                    }
                    double firstnumber = Double.parseDouble(firstWord);
                    double secondnumber = Double.parseDouble(secondWord);
                    secondnumber = secondnumber*100;
                    return numberType(firstnumber/secondnumber) + " Meters";
                }
                double number = Double.parseDouble(words[i]);
                return numberType(number/100) +  " Meters";
            }
        }
        if(i+2 < words.length) {
            if (words[i + 2].matches("^meter+$")) {
                return words[i] +""+words[i+1]+ " Meters ";
            }
            if (words[i + 2].matches("^km+$") || words[i + 2].matches("^kilometer+$")) {
                if(words[i+1].contains("/")){
                    String firstWord="";
                    String secondWord="";

                    char[] charsOfExpression;
                    charsOfExpression=words[i+1].toCharArray();

                    for(int j=0 ; j<charsOfExpression.length ; j++){
                        if(charsOfExpression[j]=='/'){
                            firstWord=words[i+1].substring(0,j);
                            secondWord=words[i+1].substring(j+1);
                            break;
                        }
                    }
                    double number = Double.parseDouble(words[i]);
                    number = number*1000;
                    double firstnumber = Double.parseDouble(firstWord);
                    firstnumber=firstnumber*1000;
                    double secondnumber = Double.parseDouble(secondWord);
                    return numberType(number + firstnumber/secondnumber) + " Meters";
                }
                double number = Double.parseDouble(words[i+1]);
                return numberType(number*1000) +  " Meters";

            }
            if(words[i + 2].matches("^centimeter+$")){
                if(words[i+1].contains("/")){
                    String firstWord="";
                    String secondWord="";

                    char[] charsOfExpression;
                    charsOfExpression=words[i+1].toCharArray();

                    for(int j=0 ; j<charsOfExpression.length ; j++){
                        if(charsOfExpression[j]=='/'){
                            firstWord=words[i+1].substring(0,j);
                            secondWord=words[i+1].substring(j+1);
                            break;
                        }
                    }
                    double number = Double.parseDouble(words[i]);
                    number = number/100;
                    double firstnumber = Double.parseDouble(firstWord);
                    double secondnumber = Double.parseDouble(secondWord);
                    secondnumber = secondnumber*100;
                    return numberType(number+firstnumber/secondnumber) + " Meters";
                }
                double number = Double.parseDouble(words[i+1]);
                return numberType(number/100) +  " Meters";
            }
        }
        return words[i];
    }

    public String typeOfGMT(String[] words , int i){
        if(i+1 < words.length) {
            if (words[i + 1].matches("^GMT+$")) {
                if(words[i].length() ==4){
                    String firstWord=words[i].substring(0,2);
                    String secondWord=":"+ words[i].substring(2);

                    String time = firstWord +secondWord;

                    double firstnumber = Double.parseDouble(firstWord);
                    firstWord = numberType(firstnumber);

                    if(firstnumber <12){
                        return time + " AM";
                    }else if(firstnumber >= 12){
                        return time +" PM";
                    }
                }
            }
        }
        return words[i];
    }

    public boolean overMillion(String[] words , int i) {
        double number;
        String toNumber = words[i].replaceAll(",", "");
        toNumber = toNumber.replaceAll("\\$", "");

        if(toNumber.contains("m") || toNumber.contains("bn")){
            return true;
        }
        if(i+1 < words.length) {
            if (words[i + 1].equals("million") || words[i + 1].equals("billion"))
            {
                return true;
            }
        }
        if(toNumber.contains("/")){
            return false;
        }
        number = Double.parseDouble(toNumber);
        if(number >= 1000000 || number<-1000000)
            return true;
        return false;
    }



}
