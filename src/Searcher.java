import com.medallia.word2vec.Word2VecModel;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Searcher {

    boolean useStemmer;
    boolean useSemantic;
    Ranker ranker;
    String outputPath;
    static String queryNumber;
    int queryRndNum;
    public Searcher(Dictionary Dictionaries, String postingPath) {
        ranker = new Ranker(Dictionaries,postingPath);
        outputPath="";
        //queryRndNum = 0;
        useStemmer = false;
        useSemantic = true;
    }

    /**
     * this function get query - words to search on posting filesE
     * @param query - String of words
     */
    public ArrayList<String> stringQuery(String query) throws IOException {
        ranker.useStemmer = useStemmer;

        String [] splitTermsQuery = query.split(" ");

        if (useSemantic){
            splitTermsQuery = semanticValues(splitTermsQuery);
        }

        //in this step we need parse the words in query and send them after parse to Ranker
        splitTermsQuery = parseQuery(splitTermsQuery);
        Random random = new Random();
        ArrayList<String> rankDocs = ranker.rankDocsByQuery(splitTermsQuery); //send query to ranker

        File tempFile = new File(outputPath);
        if (tempFile.exists()) {

            FileInputStream inputStream = new FileInputStream(outputPath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            String lastLine = "";
            line = bufferedReader.readLine();
            while (line != null) {
                if (line.equals("")){
                    String queryNum = lastLine.split(" ")[0];
                    //queryRndNum = Integer.parseInt(queryNum) + 1;
                    //queryRndNum++;
                    break;
                }
                else {
                    lastLine = bufferedReader.readLine();
                    //queryRndNum++;
                    if (lastLine == null || lastLine.equals("")) {
                        String queryNum = line.split(" ")[0];
                        //queryRndNum = Integer.parseInt(queryNum) + 1;
                        //queryRndNum++;
                        break;
                    }
                    line = bufferedReader.readLine();
                }
            }
        }
        else{
            queryRndNum = 100;
        }

        queryRndNum = random.nextInt(900)+100;
        writeResults(rankDocs,""+queryRndNum); //send specific words
        return rankDocs;
    }

    public ArrayList<String> stringQueryFromPath(String query) throws IOException {

        ranker.useStemmer = useStemmer;

        String [] splitTermsQuery = query.split(" ");

        if (useSemantic){
            splitTermsQuery = semanticValues(splitTermsQuery);
        }
        //in this step we need parse the words in query and send them after parse to Ranker
        splitTermsQuery = parseQuery(splitTermsQuery);

        ArrayList<String> rankDocs = ranker.rankDocsByQuery(splitTermsQuery); //send query to ranker


        return rankDocs;
    }

    /**
     * this function get path for queries file
     * @param queryPath - string of pathS
     */
    public HashMap<String,ArrayList<String>> pathQuery(String queryPath) throws IOException {
        FileInputStream inputStream = new FileInputStream(queryPath);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        ArrayList<String> rankDocs = new ArrayList<>();
        HashMap<String,ArrayList<String>> allRankDocs = new HashMap<>();

        String line = bufferedReader.readLine();
        String queryWords = "";
        String [] splitLine = {};
        String queryNumber = "";
        while (line!=null){
            if (line.contains("<num>")){
                splitLine = line.split(" ");
                queryNumber = splitLine[2];
            }
            if (line.contains("<title>")){
                splitLine = line.split("<title> ");
                queryWords = splitLine[1];

                rankDocs = stringQueryFromPath(queryWords.toString());
                allRankDocs.put(queryNumber,rankDocs);
                writeResults(rankDocs,queryNumber); //send specific words
            }
            line = bufferedReader.readLine();
        }
        inputStream.close();
        bufferedReader.close();
        return allRankDocs;
    }

    /**
     * this function parse the terms in the query and return an array of parse words
     * @param query - array of terms
     * @return array of terms after parse
     */
    /////////////////////////////////////////////////////////////need to implement !!!!!!!!!!!!!!!!!!!
    public String [] parseQuery (String [] query){
        //String [] queryAfterParse = new String[query.length];
        ArrayList<String> entities = new ArrayList<>();
        int counter = 0;
        Parse parser = null;
        Stemmer stemmer = new Stemmer();
        String entitie="";

        try {
            parser = new Parse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        //*********************************************//
        for (int i=0 ; i<query.length ;i++) {
            if (!query[i].equals("") && !query[i].equals("-")) {
                if(useStemmer){
                    query[i] = stemmer.stem(query[i]);
                }
                if (parser.isNumber(query[i])) {
                    parser.removePunctuation(query, i);

                    if (parser.isExpression(query, i)) {
                        query[i] = parser.typeOfExpression(query, i);

                    } else if (parser.isPrice(query, i)) {
                        query[i] = parser.typeOfPrice(query, i);

                    } else if (parser.isPercent(query, i)) {
                        query[i] = parser.typeOfPercent(query, i);

                    } else if (parser.isKgsOrKm(query, i)) {
                        query[i] = parser.typeOfKgs(query, i);

                    } else if (parser.isGMT(query, i)) {
                        query[i] = parser.typeOfGMT(query, i);

                    } else if (parser.isDate(query, i)) {
                        query[i] = parser.typeOfDate(query, i);

                    } else if (parser.isRegularNumber(query, i)) {
                        query[i] = parser.typeOfRegularNumber(query, i);
                    }
                }
            }
        }
        return query;
    }

    public void writeResults (ArrayList<String> rankDocs, String queryNum) throws IOException {
        if (!outputPath.equals("")) {
            FileWriter posting = new FileWriter(outputPath, true);
            BufferedWriter writer = new BufferedWriter(posting);
            for (String s : rankDocs) {
                writer.write(queryNum + " 0 " + s + " 1" + " 42.38" + " mt");
                writer.newLine();
            }
            writer.close();
        }

    }
    public String [] semanticValues (String [] query)  {
        String [] queryWithSemanicWords = query;
        ArrayList<String> matchesWords = new ArrayList<>();

        try {
            Word2VecModel model = Word2VecModel.fromTextFile(new File("src\\word2vec.c.output.model.txt"));
            com.medallia.word2vec.Searcher semanticSearcher = model.forSearch();
            int numOfResultInList = 50;

            for (int i=0 ; i<query.length ; i++) {
                List<com.medallia.word2vec.Searcher.Match> matches = semanticSearcher.getMatches(query[i], numOfResultInList);
                //matchesWords.add(matches.get(1).match());
                matchesWords.add(matches.get(2).match());
            }
            queryWithSemanicWords = new String[matchesWords.size()];
            for (int i =0 ; i<matchesWords.size(); i++) {
                queryWithSemanicWords[i] = matchesWords.get(i);
            }


        }
        catch (IOException e) {
           // e.printStackTrace();
            return queryWithSemanicWords;
        } catch (com.medallia.word2vec.Searcher.UnknownWordException e) {
           // e.printStackTrace();
            return queryWithSemanicWords;
        }
        return queryWithSemanicWords;
    }

}
