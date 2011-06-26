import java.io.*;
import java.util.*;
import java.util.regex.*;


class spellingsuggest {

	private final HashMap<String, Integer> DBWords = new HashMap<String, Integer>();

	public spellingsuggest(String file) throws IOException 
	{
	    try
	    {
    		BufferedReader in = new BufferedReader(new FileReader(file));
	    	Pattern p = Pattern.compile("\\w+");
	    	for(String temp = ""; temp != null; temp = in.readLine() )      // Reading the dictionary and updating the probabalistic values accordingly
	    	{                                                               // of the words according.
	    		Matcher m = p.matcher(temp.toLowerCase());
	    		while(m.find())
	    		{
	    		    DBWords.put( (temp = m.group()), DBWords.containsKey(temp) ? DBWords.get(temp) + 1 : 1 ); // This will serve as an indicator to
	    	    }                                                                                             // probability of a word      
	    	}
	    	in.close();
	    }
	    catch(IOException e)
	    {
	        System.out.println("Uh-Oh Exception occured!");
	        e.printStackTrace();
	    }
	}

    // Return an array containing all possible corrections to the word passed.
	private final ArrayList<String> edits(String word) 
	{
		ArrayList<String> result = new ArrayList<String>();
		
		for(int i=0; i < word.length(); ++i) 
		{
		    result.add(word.substring(0, i) + word.substring(i+1)); 
		}
		for(int i=0; i < word.length()-1; ++i) 
		{
		    result.add(word.substring(0, i) + word.substring(i+1, i+2) + word.substring(i, i+1) + word.substring(i+2)); 
		}
		for(int i=0; i < word.length(); ++i) 
		{    
		    for(char c='a'; c <= 'z'; ++c) 
		    {
		        result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i+1));
		    }
		}
		for(int i=0; i <= word.length(); ++i) 
		{
		    for(char c='a'; c <= 'z'; ++c) 
		    {
		        result.add(word.substring(0, i) + String.valueOf(c) + word.substring(i));
		    }
		}
		return result;
	}

	public final String correct(String word) 
	{
		if(DBWords.containsKey(word)) 
		{
		    return word;    // this is a perfectly safe word.
		}
		ArrayList<String> list_edits = edits(word);
		HashMap<Integer, String> candidates = new HashMap<Integer, String>();
		
		for(String s : list_edits) // Iterating through the list of all possible corrections to the word.
		{
		    if(DBWords.containsKey(s)) 
		    {
		        candidates.put(DBWords.get(s),s);
		    }
		} 
		// In the first stage of error correction, any of the possible corrections from the list_edits are found in our word database DBWords 
		// then we return the one verified correction with maximum probability.
		if(candidates.size() > 0) 
		{
		     return candidates.get(Collections.max(candidates.keySet()));
		}
		// In the second stage we apply the first stage method on the possible collections of the list_edits.By the second stage statistics 
		// suggest we obtain an accuracy of about 98% !! 
		for(String s : list_edits) 
		{    
		     for(String w : edits(s))
		     { 
		            if(DBWords.containsKey(w))
		            { 
		                candidates.put(DBWords.get(w),w);
		            }
		     }
		}
		   
		    return candidates.size() > 0 ? candidates.get(Collections.max(candidates.keySet())) : "Sorry but no possible corrections found!";
	}

	public static void main(String [] args) throws IOException 
	{
		if(args.length > 0) 
		{
		    System.out.println((new spellingsuggest("wordprobabilityDatabase.txt")).correct(args[0]));
	    }
	}
}
