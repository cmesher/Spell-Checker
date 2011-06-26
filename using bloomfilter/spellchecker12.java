import java.io.*;
import java.util.*;

public class spellchecker12 {
    
    bloomfilter12 <String> dictionary;   // To store all the words of the dictionary
    boolean suggestWord ;           // To indicate whether the word is spelled correctly or not.
    
    public static void main(String [] args) 
    {
        spellchecker12 checker = new spellchecker12();
    }
    
    public spellchecker12() 
    {
        System.out.println("******Welcome to the spell checker using Bloom Filter(false positive = 0.24 )");
        System.out.println("The spell checker would check every line from the input file and then give suggestions if needed after each line. \n\n");

        dictionary = new bloomfilter12<String>(0.24,99000);      // Assigning a false positive probability of 0.24
        
        try 
        {
            
            //Read and store the words of the dictionary 
            BufferedReader dictReader = new BufferedReader(new FileReader("dictionary.txt"));
            
            while (dictReader.ready()) 
            {
                String dictInput = dictReader.readLine() ;
                String [] dict = dictInput.split("\\s");
                
                for(int i = 0; i < dict.length;i++) 
                {
                    // key and value are identical
                    dictionary.add(dict[i]);
                }
            }
            dictReader.close();
            
           String file = "inputtext.txt";
           // Read and check the input from the text file 
            BufferedReader inputFile = new BufferedReader(new FileReader(file));
            System.out.println("Reading from "+file);
            
            // Initialising a spelling suggest object
            spellingsuggest suggest = new spellingsuggest("wordprobabilityDatabase.txt");
         
            // Reads input lines one by one
            while ( inputFile.ready() ) 
            {
                String s = inputFile.readLine() ;
                System.out.println (s);
                String[] result = s.split("\\s");
                
                 for (int x=0; x<result.length; x++)
                {
                    suggestWord = true;
                    String outputWord = checkWord(result[x]);
                    
                    if(suggestWord)
                    {
                        System.out.println("Suggestions for "+outputWord+" are:  "+suggest.correct(outputWord)+"\n");
                    }
                }
                
            }
            
            inputFile.close();
        }
        catch (IOException e) 
        {
            System.out.println("IOException Occured! ");
            e.printStackTrace();
      //      System.exit(-1);
        }
    }
    
    public String checkWord(String wordToCheck) 
    {
        String unpunctWord;
        String word = wordToCheck.toLowerCase();
        
        // if word is found in dictionary then it is spelt correctly, so return as it is.
        //note: inflections like "es","ing" provided in the dictionary itself.
        if ( dictionary.contains(word) )
        {
            suggestWord = false;            // no need to ask for suggestion for a correct word.
            return wordToCheck;
        }
        
        // Removing punctuations at end of word and giving it a shot ("." or "." or "?!")
        int length = word.length();
        
 
         //Checking for the beginning of quotes(example: "she )
        if (length > 1 && word.substring(0,1).equals("\"")) 
        {
            unpunctWord = word.substring(1, length);
            
            if ( dictionary.contains(unpunctWord) )
            {
                suggestWord = false;            // no need to ask for suggestion for a correct word.
                return wordToCheck ;
            }
            else // not found
                return unpunctWord;                  // removing the punctuations and returning
        }
 
        // Checking if "." or "," at the end is the problem(example: book. when book is present in the dictionary).
        if( word.substring(length - 1).equals(".")  || word.substring(length - 1).equals(",") ||  word.substring(length - 1).equals("!")
        ||  word.substring(length - 1).equals(";") || word.substring(length - 1).equals(":"))
        {
            unpunctWord = word.substring(0, length-1);
            
            if ( dictionary.contains(unpunctWord) )
            {
                suggestWord = false;            // no need to ask for suggestion for a correct word.
                return wordToCheck ;
            }
            else
            {
                return unpunctWord;                  // removing the punctuations and returning
            }
        }

        // Checking if an extra 'es' or "ed" or " ,"" is the problem (example: watches when watch is present in the dictionary)
        if (length > 2 && word.substring(length-2).equals(",\"")  || word.substring(length-2).equals(".\"") 
            || word.substring(length-2).equals("?\"") || word.substring(length-2).equals("!\"") )
        {
            unpunctWord = word.substring(0, length-2);
            
            if ( dictionary.contains(unpunctWord) )
            {
                suggestWord = false;            // no need to ask for suggestion for a correct word.
                return wordToCheck ;
            }
            else // not found
                return unpunctWord;                  // removing the inflections and returning
        }
        
        
        
        // After all these checks too, word could not be corrected, hence it must be misspelt word.
        return word;
    }
    
}

