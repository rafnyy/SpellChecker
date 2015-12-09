/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package energysavvy;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author Rob Fusco
 */
public class SpellcheckerTest {

    public static void main(String[] args) {
        ArrayList<String> words = new ArrayList<String>();
        
        try {
            FileInputStream fstream = new FileInputStream("/usr/share/dict/words");
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            while ((strLine = br.readLine()) != null) {
                words.add(strLine);
            }

            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        String correct = words.get((int)(Math.random() * words.size()));
        String wronge = "";
        for(int i = 0; i < correct.length(); i++)
        {
            char c = correct.charAt(i);
            
            if(Math.random() < .5)
                c = Spellchecker.oppositeCase(c);
            
            if(Spellchecker.isVowel(c))
            {
                c = Spellchecker.vowels[(int)(Math.random() * Spellchecker.vowels.length)];
            }
            
            for(int j = 0; j < (int)(Math.random() * 2) + 1; j++)
            {
                wronge += c;
            }
        }
        
        System.out.println("Original Word:\t" + correct);
        System.out.println("Wronge Word:\t" + wronge);
        System.out.println("Suggestion:\t" + Spellchecker.spellcheck(correct));
    }
}
