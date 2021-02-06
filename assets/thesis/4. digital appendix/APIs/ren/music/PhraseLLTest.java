package ren.music;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import jm.music.data.Note;
import jm.music.data.Phrase;
import music.LPart;
import music.LScore;
import ren.util.PO;

public class PhraseLLTest {// extends MidiAppl {
	public static void main(String[] args) {
      
        PO.p("testing");
        
       // PhraseQ pq = new PhraseQ(10);
        PhraseLL pq = new PhraseLL();
        
        Phrase torem = null;
        
        double st = 0;
        for(int i=0; i<4; i ++) {
            Phrase phr = new Phrase();
            phr.addNote(new Note(i, 1.0));
            
            phr.getNote(0).setRhythmValue(4.0 - i);
            
            if(i == 3)
                torem = phr;
            
            
                st = i*1.0;
            
            phr.setStartTime(st);
            //PO.p("putting phrase" + phr.toString());
            pq.add(phr);
            PO.p(pq.en().toString());
        }
        
        
        PO.p("sucessful? = " + pq.remove(torem));
        
        PO.p("going through and poppin them all");
        Phrase phr = null;
        
        while(phr != pq.en()) {   
            phr = pq.iter();
            PO.p(phr.toString()); 
        }
        
    }
}