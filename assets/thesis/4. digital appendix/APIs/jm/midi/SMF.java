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
package jm.midi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.Vector;

import jm.JMC;
import jm.midi.event.EndTrack;
import jm.midi.event.Event;
import jm.midi.event.VoiceEvt;
import ren.util.PO;

/**
 * @author Andrew Sorensen
 * @version 1.0,Sun Feb 25 18:43
 */
public final class SMF implements JMC{
	//--------------------------------------
	//attributes
	//--------------------------------------
	/** The standard MIDI file type to be read or written */
	private short fileType;
	/** The current number of tracks stored by this Class */
	private short numOfTracks;
	/** The number of bytes being read or written */
	private int numOfBytes;
	/** Pulses per quarter note value */
	private short ppqn;
	/** list of Tracks contained within this SMF */
	private Vector trackList;
	/* Print statements */
        private boolean VERBOSE = false;

	//------------------------------------------
	//SMF Default Constructor
	//------------------------------------------
	/**
	 * Create a new empty SMF with default filetype and ppqn settings
	 * SMF objects are useful for retrieving statistical information
	 * about a MIDI file which has just been read or written.
	 */
	public SMF(){
		this((short)1, (short)480);
	}

	/**
	 * Create a new empty SMF. Allows user to set filetype and ppqn settings
	 * SMF Objects are useful for retrieving statistical information
	 * about a MIDI file which has just been read or written.
	 */
	public SMF(short fileType, short ppqn){
		this.fileType = fileType;
		this.ppqn = ppqn;
		this.numOfBytes = 0;
		this.numOfTracks = 0;
		this.trackList = new Vector();
	}
        
        public void setVerbose(boolean val) {
            this.VERBOSE = val;
        }
        
	public Vector getTrackList(){
		return this.trackList;	
	}

	public short getPPQN(){
		return this.ppqn;
	}


	public void clearTracks(){
		if(!this.trackList.isEmpty()){
			//remove any previous tracks
			this.trackList.removeAllElements(); 
		}	
	}


	//------------------------------------------
	//Read SMF
	//------------------------------------------
	/**
	 * Read from a standard MIDI file
	 * @params InputStream - the datasource to read from
	 * @params Score score - the score to place jMusic data translation into
	 * @exception IOException - any IO problems
	 */
	public void read(InputStream is) 
		throws IOException{
		//Given the small size of MIDI files read all
		//data into a ByteArrayStream for further processing
		byte[] fileData = new byte[is.available()];
		is.read(fileData);
		ByteArrayInputStream bais = 
			new ByteArrayInputStream(fileData);
		DataInputStream dis = new DataInputStream(bais);
		//clear any SMF data			
		if(!this.trackList.isEmpty()){
			this.trackList.removeAllElements(); //remove any previous tracks
		}
		//check header for MIDIfile validity
		if(dis.readInt() != 0x4D546864){//Check for MIDI track validity
			throw new IOException("This is NOT a MIDI file !!!");
		}else{//If MIDI file passes skip length bytes
			dis.readInt(); //skip over Length info
		} 
		//get SMF Class data 
		try{
			fileType = dis.readShort();
			if(VERBOSE) System.out.println("MIDI file type = "+ fileType);
			this.numOfTracks = dis.readShort();
                        if(VERBOSE) System.out.println("Number of tracks = " + numOfTracks);
			this.ppqn = dis.readShort();
			if(VERBOSE) System.out.println("ppqn = " + ppqn);
		}catch(IOException e){
			System.out.println(e);
			e.printStackTrace();
		}				
		
		// skip the tempo track fro type 1 files
		/*
		if(fileType == 1) {
		    skipATrack(dis);
		    numOfTracks--;
		}
		//*/
		//Read all track chunks
		for(int i = 0; i < numOfTracks; i++){
			readTrackChunk( dis);
		}
		is.close();
		dis.close();
	}
	
	
//	public void readWithJava()

	//------------------------------------
	//Write a SMF
	//-------------------------------------
	/**
	 * Write to a standard MIDI file
	 * @param OutputStream the datasource to write to 
	 * @param Score score the Score to get data from
	 * @exception IOException did the write go ok
	 */
	public void write(OutputStream os) 
		throws IOException{
		//IO Stream stuff
		DataOutputStream dos = new DataOutputStream(os);
		//find number of tracks
		this.numOfTracks = (short) trackList.size();
		//write header chunk
		try{
			dos.writeInt(0x4D546864);   	//MThd
			dos.writeInt(6);		//Length
			dos.writeShort(1);   		//Midi File Type
			dos.writeShort(numOfTracks);	//Number of tracks
			dos.writeShort(ppqn);    	//Pulses Per Quarter Note
		}
		catch(Exception e){
			e.printStackTrace();
		}				
		//write all tracks
		Enumeration enumr = trackList.elements();
		while(enumr.hasMoreElements()){
			Track smfTrack = (Track) enumr.nextElement();
			writeTrackChunk(dos, smfTrack);
		}
		os.flush();
		os.close();
		dos.flush();
		dos.close();
	}

	/**
	 * Print all MIDI tracks and MIDI events 
	 */
	public void print(){
		Enumeration enumr = trackList.elements();
		while(enumr.hasMoreElements()){
			Track track = (Track) enumr.nextElement();
			track.print();
		}
	}
    	/**
	 * Reads a MIDI track without doing anything to the data
	 */
	private void skipATrack( DataInputStream dos) throws IOException{
                if(VERBOSE) System.out.println("Skipping the tempo track . . .");
		dos.readInt();
		dos.skipBytes(dos.readInt());
	}
	
	//----------------------------------------
	//SMF Track Reads and Writes 
	//----------------------------------------
	/**
	 * Reads a MIDI track chunk
	 * @param DataInputStream dis - the input stream to read from
	 * @exception IOException
	 */
	private void readTrackChunk(DataInputStream dis) 
            throws IOException{
            //local variables for Track class
            Track track = new Track();
            //Insert new Track into a list of tracks
            this.trackList.addElement(track);  
            int deltaTime = 0;
            if(VERBOSE) System.out.println("Reading Track ..........");
            
            PO.p("available " + dis.available());
            PO.p(dis.toString());
            //Read track header
            int trackHead = 0;
            try {
            	trackHead = dis.readInt();
            }catch(EOFException eof) {
            	PO.p("track head "  + trackHead + " was end of file... returning");
            	return;
            }
            
            PO.p("trackHeader = " + trackHead);
            if(trackHead != 0x4D54726B){//If MTrk read is wrong
                    throw new IOException
                            ("Track started in wrong place!!!!  ABORTING");
            }else{//If MTrk read ok get bytesRemaining
                    PO.p("bytes remaining?? = " + dis.readInt());
            }
            //loop variables
            int status, oldStatus =0, eventNum = 0, eventLength = 0;
            //Start gathering event data
            Event event = null;
            while(true){
                try{
                	
                	
                    //get variable length timestamp
                    deltaTime = MidiUtil.readVarLength(dis); 
                    
                    if(deltaTime == Integer.MIN_VALUE)
                    	break;
                    
                    //mark stream so we can return if we need running status
                    dis.mark(2);
                 //   status = 0;
                  //  try {
                    	status = dis.readUnsignedByte();
                 //   } catch(EOFException eof) {
                    //	System.out.println(eof.toString());
                 //   }
                    //decide on running status
                    if(status < 0x80){ //set running status
                        status = oldStatus;
                        //return stream to before status read
                        dis.reset();
                    }
                    //create default event of correct type
                    if(status >= 0xFF){ //Meta Event
                        int type = dis.readUnsignedByte(); 
                        eventLength = MidiUtil.readVarLength(dis);
                        event = jm.midi.MidiUtil.createMetaEvent(type); 
                    }else if(status >= 0xF0){ //System Exclusive --- NOT SUPPORTED
                        System.out.println("SysEX---");
                        eventLength = MidiUtil.readVarLength( dis);
                    }else if(status >= 0x80){ //MIDI voice event 
                        short selection = (short) (status / 0x10);
                        short midiChannel = (short) (status - (selection * 0x10));
                        VoiceEvt evt = (VoiceEvt)MidiUtil.createVoiceEvent(selection);
                        evt.setMidiChannel(midiChannel);
                        event = evt;
                        if(event == null){
                                        throw new IOException("MIDI file read error: invalid voice event type!");
                        }
                    }
                    oldStatus = status;
                }catch(Exception e){
                    e.printStackTrace();
                    System.exit(1);
                }   
                if(event != null){
                    //read data into the new event and
                    //add the new event to the Track object
                    event.setTime(deltaTime);
                    event.read(dis);
                    //if (VERBOSE) event.print();
                    track.addEvent(event);
                    //event.print();
                    if(event instanceof EndTrack)
                            break;
                }else{
                    //skip the stream ahead to next valid event
                    dis.skipBytes(eventLength);
                }	
            }
	}

	/**
	 * Write the Track Chunk
	 * @param DataOutputStream dos 
	 * @param Track track - track to write
	 * @exception IOException
	 */
	private void writeTrackChunk(DataOutputStream odos, Track track)
		throws IOException{
		if(VERBOSE) System.out.println("Writing MIDI Track" );
		//Write to temporary stream to buffer disk writes and
		//calculate the number of bytes written to the stream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		int header = 0x4D54726B;
		Enumeration enumr = track.getEvtList().elements();
		enumr = track.getEvtList().elements();
		//At this stage Except that all events are NoteOn events
		while(enumr.hasMoreElements()){ 
			Event evt = (Event) enumr.nextElement();
			evt.write(dos);
			if(DEBUG)evt.print();
		}
		//Write to the real stream
		odos.writeInt(header);
		odos.writeInt(baos.size());
		odos.write(baos.toByteArray(),0,baos.size());
	}
}
