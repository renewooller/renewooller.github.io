/*

<This Java Class is part of the jMusic API version 1.5, March 2004.>

Copyright (C) 2000 Andrew Sorensen & Andrew Brown

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or any
later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

*/


//
//  PlayTask.java
//  
//
//  Created by Rene Wooller on Tue Dec 17 2002.
//  Copyright (c) 2002 __MyCompanyName__. All rights reserved.
//
package jmms;

import grame.midishare.*;
import jm.music.data.*;

/**
 *this is an extension of the MidiTask and is used to play the events
 *from a Sequencer
 */
public class PlayTask extends MidiTask {
        
        int fCurEv;
        Sequence seq;
        static int advance = 100;
        
        /**
         *  @param int ev the event that if to be played
         */
        public PlayTask(int ev) {fCurEv = ev;}
        
        public void setSequence(Sequence s) {
            seq = s;
        }
        
        public static void setAdvance(int latency) {
            advance = latency;
        }
        
        /**
         *  call this to execute the current Midishare event
         *  @param MidiAppl appl the MidiAppl that is to be used to send the event
         *  @param int date the actual date that it should be executed
         */
    //   int res24 = (double)(lib.LemuVars.RES/24)*this.;
        public void Execute(MidiAppl appl, int date) {
                p("Excecute");
            if(fCurEv != 0) {
                int curDate = Midi.GetDate(fCurEv);
                // Play all events in the sequence at the same date
                while ((fCurEv != 0) && (Midi.GetDate(fCurEv) == curDate)){
                    	if(Midi.GetType(fCurEv) == Midi.typeCtrlChange) {
                    		//PO.p("control type " + Midi.get)
                    	}
                	
                        SendEvent(appl.refnum, fCurEv, date + advance);
                        if(seq!=null) // if it doesn't use the sequence
                            fCurEv = seq.nextEvent();
                        else  {
                           // this.Forget();
                            Midi.FreeEv(fCurEv);
                            fCurEv = 0;
                        }
                }
                // Schedule task for the next "slice"
                if (fCurEv != 0) {
                   // System.out.println("date of subsequent notes = " + Midi.GetDate(fCurEv));
                    int nd = Midi.GetDate(fCurEv) + date;
                    
                    appl.ScheduleTask(this, nd);// - curDate);
                    
                    //System.out.println("");
                } else { // if it is the end of the subsequence
                    this.Forget();
                    
                }
            }
        }

        private void SendEvent (int refnum, int ev, int date) {
            
            
            //   if (playTable[Midi.GetChan(ev)]  //for muting purposes
                Midi.SendAt(refnum, Midi.CopyEv(ev), date);
        }
     
        private void p(String toPrint) {

           //     System.out.println(toPrint);
        }
}
