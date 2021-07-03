package com.arraybase.flare;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

public final class TMID {
    static Random randomGenerator = new Random();
    
    static String[] random_words = generateRandomWords(100);
    
	
	public final static String create() {
		String t = "";
		for ( int i = 0; i < 10; i++){
	       int randomInt = randomGenerator.nextInt(10000);
	       int wordran = randomGenerator.nextInt(100);
			t+= ""+random_words[wordran] + randomInt;
		}
		return t;
	}
	public static String[] generateRandomWords(int numberOfWords)
	{
	    String[] randomStrings = new String[numberOfWords];
	    Random random = new Random();
	    for(int i = 0; i < numberOfWords; i++)
	    {
	        char[] word = new char[random.nextInt(8)+3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
	        for(int j = 0; j < word.length; j++)
	        {
	            word[j] = (char)('a' + random.nextInt(26));
	        }
	        randomStrings[i] = new String(word);
	    }
	    return randomStrings;
	}
}
