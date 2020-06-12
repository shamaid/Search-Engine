import java.io.*;
import java.util.*;

public class ReadFile {

    String path;
    int fileIndex;
    ArrayList<String> documents;
    Parse parser;

    /**
     * This is a constructor
     * @param pathOfCorpus
     * @param pathOfPosting
     * @param useStemmer
     * @throws FileNotFoundException
     */
    public ReadFile(String pathOfCorpus , String pathOfPosting , boolean useStemmer) throws FileNotFoundException {
        this.path = pathOfCorpus +"//corpus";
        this.fileIndex = 0;
        this.documents = new ArrayList<>();
        this.parser = new Parse();
        parser.makeStopWordsHashSet(pathOfCorpus);
        parser.indexer.path=pathOfPosting;
        parser.indexer.useStemmer=useStemmer;


    }

    /**
     * This function is splitting the folders names
     * @return list of names
     * @throws FileNotFoundException
     */
    public String[] SplitFoldersNames() throws FileNotFoundException {
        File folder = new File(path);
        File[] files = folder.listFiles();
        String[] names = new String[files.length];

        for (int i =0 ; i<names.length ; i++)
        {
            names[i] = files[i].getName();
        }
        return names;
    }


    /**
     * This function is reading and splitting the files
     * @throws IOException
     */
    public void ReadAndSplitFile() throws IOException {

        String[] folderNames=SplitFoldersNames();
        //BufferedReader reader = null;
        Scanner s =null;
        String filePath =null;
        int docIndex;
        String docString;
        //loop on each folder and sent it to parse
        for (int i =0 ; i< folderNames.length ; i++) {
            documents.clear();
            // System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            //System.out.println(folderNames[i].toString());

            filePath = path + "\\" + folderNames[i] + "\\" + folderNames[i];


            // reader = new BufferedReader(new FileReader(new File(filePath)));
            s = new Scanner(new File(filePath));




            docIndex = 0;
//            String doc = s.next();
            StringBuilder docStringBulider =  new StringBuilder();
            if (s.hasNext()) {
                docStringBulider.append(s.next());
            }

            while (s.hasNext()) {

                String nextWord = s.next();
                docStringBulider.append(" " + nextWord);

                //splitting docs by <DOC> tab
                while (s.hasNext() && !(nextWord.equals("<DOC>"))) {

                    nextWord = s.next();
                    docStringBulider.append(" " + nextWord);

                }



                docString = docStringBulider.toString(); //change from string builder to String
                //remove from doc - ; : "  ) ( [ ] ?

                docString = docString.replaceAll(":","");
                docString = docString.replaceAll(";","");

                docString = docString.replaceAll("[\\[\\](){}]","");
                docString = docString.replaceAll("\\?","");
                docString = docString.replaceAll("!","");
                docString = docString.replaceAll("\\*","");
                // docString = docString.replaceAll("\\..","");
                docString = docString.replaceAll("\\|","");
                docString = docString.replaceAll("\"", "");
                docString = docString.replaceAll("#","");
                docString = docString.replaceAll("&","");
                docString = docString.replaceAll("'","");
                docString = docString.replaceAll("\\+","");
                docString = docString.replaceAll("-/","");
                docString = docString.replaceAll("`","");
                docString = docString.replaceAll("$/","");
                docString = docString.replaceAll("\\\\","");

                //
                /*
                docString.replaceAll("(","");
                docString.replaceAll(")","");
                docString.replaceAll("]","");
                */


                documents.add(docIndex, docString);



                docIndex++;
                docStringBulider =  new StringBuilder();
                docStringBulider.append(nextWord);


            }

            sendDocsToParser();


            if ((i+1) % 20 == 0 || (i+1) == folderNames.length){ //every 20 files create posting file

                parser.indexer.sendToPostingFiles();

                parser.indexer.sendDocsDictionaryToPostingFiles();
            }

        }
        s.close();

        parser.indexer.sendMainDictionaryToPostingFiles();
        parser.indexer.margePostingFiles();

    }

    /**
     * this function is sent the documents to parser
     * @throws IOException
     */

    public void sendDocsToParser() throws IOException {



        for(int i = 0; i< documents.size() ; i++){
            parser.sendDocToParse(documents.get(i));
        }


    }




}
