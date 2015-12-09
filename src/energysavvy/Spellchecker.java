/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package energysavvy;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Rob Fusco
 */
public class Spellchecker {

    public static char[] vowels = {'a', 'e', 'i', 'o', 'u', 'y',
            'A', 'E', 'I', 'O', 'U', 'Y'};

    public static void main(String[] args) {

        do {
            System.out.print(">");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            String input = null;
            try {
                input = br.readLine();
            } catch (IOException ioe) {
                System.out.println("IO error trying to read your input");
                System.exit(1);
            }

            System.out.println(spellcheck(input));
        } while (true);
    }

    /**
     * Assumes one word (no spaces) only alphabetic input
     * @param word
     * @return String suggestion of a word that appears in the dictionary
     */
    public static String spellcheck(String word) {
        // leading and trailing white space is required to make sure only full
        // words are returned as hits
        String regex = "\n";
        char lastChar = ' ';
        // String used to select from multiple matches, choose the one with
        // the same vowels in the same order
        String vowelsInWord = "";

        for (int i = 0; i < word.length(); i++) {
            char currChar = word.charAt(i);

            if (lastChar != currChar && lastChar != oppositeCase(currChar)) {
                if (isVowel(currChar)) {
                    vowelsInWord += currChar;
                    regex += "([a]+|[e]+|[i]+|[o]+|[u]+|[y]+|"
                            + "[A]+|[E]+|[I]+|[O]+|[U]+|[Y]+)";
                } else {
                    regex += "[" + currChar + oppositeCase(currChar) + "]+";
                }
            }

            lastChar = currChar;
        }
        regex += "\n";

        ArrayList<String> matches = new ArrayList<String>();

        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(fromFile("/usr/share/dict/words"));

            while (matcher.find()) {
                matches.add(matcher.group());
            }
        } catch (IOException ioe) {
            System.out.println("IO error trying to parse dictionary");
            System.exit(1);
        }
        // pull only unique matches from list
        matches = new ArrayList(new HashSet(matches));

        if(matches.isEmpty())
            return "NO SUGGESTION";
        
        // loop to select the most relevant match if there are multiples
        for(int i = 0; i < matches.size(); i++)
        {
            String match = matches.get(i).trim();
            if(match.equalsIgnoreCase(word))
                return match;
            
            int k = 0;
            for(int j = 0; j < match.length(); j++)
            {
                char c = match.charAt(j);
                if(isVowel(c))
                {
                    if(k < vowelsInWord.length() && c != vowelsInWord.charAt(k))
                        break;
                    
                    k++;
                }
                
                if(j == match.length() - 1)
                    return match;
            }
        }
        
        return matches.get((int)(Math.random() * matches.size())).trim();
    }

    /**
     * 
     * @param c, must be alphabetic character to return valid result
     * @return the same character of the opposite case
     */
    public static char oppositeCase(char c) {
        char oppoCase = ' ';
        if (c < 97) {
            oppoCase = (char) (c + 32);
        } else {
            oppoCase = (char) (c - 32);
        }

        return oppoCase;
    }

    private static CharSequence fromFile(String filename) throws IOException {
        FileInputStream fis = new FileInputStream(filename);
        FileChannel fc = fis.getChannel();

        ByteBuffer bbuf = fc.map(FileChannel.MapMode.READ_ONLY, 0, (int) fc.size());
        CharBuffer cbuf = Charset.forName("8859_1").newDecoder().decode(bbuf);
        return cbuf;
    }

    public static boolean isVowel(char c) {
        for (int i = 0; i < vowels.length; i++) {
            if (vowels[i] == c) {
                return true;
            }
        }

        return false;
    }
}