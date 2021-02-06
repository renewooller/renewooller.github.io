
/*
 * Mod.java 18th February 2003
 *
 * Copyright (C) 2000-2003 Andrew Sorensen, Andrew Brown, Adam Kirby
 *                             
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 *
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package jm.music.tools;

import java.util.Enumeration;
import java.util.Vector;

import jm.JMC;
import jm.music.data.CPhrase;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import music.LPart;
import music.LScore;
import ren.tonal.Scales;
import ren.util.AD;
import ren.util.RMath;
import ai.An;

/**
 * A utility class that handles the modification of the
 * basic jMusic types.
 * 
 * @author Andrew Sorensen
 * @author Andrew Brown
 * @author Adam Kirby
 * @version $Revision: 1.1 $, $Date: 2006/03/21 06:14:31 $
 */
public class Mod implements JMC {

    /**
     * The Mod class provides static methods and is not
     * meant to be instansiated.
     */
    private Mod() {
    }

    // ----------------------- NOTE MODIFICATIONS
    // -----------------------------//

    /**
     * Appends the duration and rhythm value of the
     * second note onto the first.
     * 
     * <P>
     * The second note remains unchanged. If either
     * <CODE>note1</CODE> or <CODE>note2</CODE> is
     * null then this method does nothing.
     * 
     * @param note1
     *            Note that absorbs the rhythm features
     * @param note2
     *            Note that passes on its rhythm
     *            features
     */
    public static void append(Note note1, final Note note2) {
        try {
            if (note1 == null || note2 == null)
                new NullPointerException();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        note1.setRhythmValue(note1.getRhythmValue() + note2.getRhythmValue());
        note1.setDuration(note1.getDuration() + note2.getDuration());
    }

    /**
     * Transpose the note up or down in semitones.
     * 
     * <P>
     * If <CODE>note</CODE> is null then this method
     * does nothing. If the transposition shifts a pitch
     * above {@link Note#MAX_PITCH MAX_PITCH}or below
     * {@link Note#MIN_PITCH MIN_PITCH}, the pitch will
     * probably cap at those values. See the description
     * of {@link Note#setPitch}method for exact details
     * of what occurs when trying to set the pitch
     * beyond the allowed range.
     * 
     * @param note
     *            The Note to be transposed
     * @param transposition
     *            the amount to transpose in semitones
     */
    public static void transpose(Note note, final int transposition) {
        try {
            if (note == null)
                new NullPointerException();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (note.getPitchType() == Note.MIDI_PITCH && note.getPitch() != REST)
            note.setPitch(note.getPitch() + transposition);
        if (note.getPitchType() == Note.FREQUENCY)
            System.err.println("jMusic Mod transpose: No action taken - notes with frequency values cannot yet be transposed.");
    }

    /**
     * Transpose the phrase up or down in scale degrees.
     * 
     * <P>
     * If <CODE>note</CODE> is null then this method
     * does nothing. If the transposition shifts a pitch
     * above {@link Note#MAX_PITCH MAX_PITCH}or below
     * {@link Note#MIN_PITCH MIN_PITCH}, the pitch will
     * probably cap at those values. See the description
     * of {@link Note#setPitch}method for exact details
     * of what occurs when trying to set the pitch
     * beyond the allowed range.
     * 
     * Transposition is in diatonic steps. For example
     * in C major the note C transposed 1 will become D,
     * transposed 4 will become G, and transposed by 7
     * will beome C an octave above. This can be
     * somewhat unintuitive so be careful.
     * 
     * @param note
     *            Note to be transposed
     * @param transposition
     *            the amount to transpose in semitones
     * @param mode
     *            the scale to use for the transposition
     *            (the JMC has some scale constants)
     * @param key
     *            the chromatic note to be used as the
     *            root of the mode. i.e., 0 = C, 1 = C#
     *            etc.
     */
    public static void transpose(Note note, final int transposition,
            final int[] mode, int key) {
        try {
            if (note == null)
                new NullPointerException();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        int pitch = note.getPitch();
        if (pitch != Note.REST) {
            // work out the original scale octave,
            // degree and accidental
            int octave = pitch / 12;
            int accidental = 0;
            Note n = note.copy();
            while (!n.isScale(mode)) {
                n.setPitch(n.getPitch() - 1);
                accidental++;
            }
            int degree = 0;
            for (int i = 0; i < mode.length; i++) {
                if (pitch % 12 - accidental == mode[i]) {
                    degree = i;
                    i = mode.length; // break out of
                    // loop next time
                }
            }
            // calculate the new pitch
            int newDegree = degree + transposition;
            while (newDegree >= mode.length) {
                octave++;
                newDegree -= mode.length;
            }
            while (newDegree < 0) {
                octave--;
                newDegree += mode.length;
            }
            note.setPitch(mode[newDegree] + octave * 12 + accidental);
        }
    }

    // ---------------------- PHRASE MODIFICATIONS
    // ----------------------------//

    public static final void crescendo(final Phrase phrase,
            final double startTime, final double endTime,
            final int startDynamic, final int endDynamic) {
        double dynDiff = endDynamic - startDynamic;
        double timeDiff = endTime - startTime;
        if (timeDiff == 0.0) {
            // This prevents a divide-by-zero error.
            // Since the change requested applies to no
            // region at all in
            // the phrase, it is ideal to do nothing and
            // return.
            return;
        }
        double time = 0.0;
        Vector v = phrase.getNoteList();
        for (int i = 0; i < v.size(); i++) {
            Note n = (Note) v.elementAt(i);
            if (time >= startTime) {
                n.setDynamic((int) ((time - startTime) / timeDiff * dynDiff + startDynamic));
            }
            time += n.getRhythmValue();
            if (time > endTime) {
                break;
            }
        }
    }

    public static final void diminuendo(final Phrase phrase,
            final double startTime, final double endTime,
            final int startDynamic, final int endDynamic) {
        crescendo(phrase, startTime, endTime, startDynamic, endDynamic);
    }

    public static final void decrescendo(final Phrase phrase,
            final double startTime, final double endTime,
            final int startDynamic, final int endDynamic) {
        crescendo(phrase, startTime, endTime, startDynamic, endDynamic);
    }

    /**
     * Transpose the phrase up or down in semitones.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null then this method
     * does nothing. If the transposition shifts a pitch
     * above {@link Note#MAX_PITCH MAX_PITCH}or below
     * {@link Note#MIN_PITCH MIN_PITCH}, the pitch will
     * probably cap at those values. See the description
     * of {@link Note#setPitch}method for exact details
     * of what occurs when trying to set the pitch
     * beyond the allowed range.
     * 
     * @param phrase
     *            Phrase to be transposed
     * @param transposition
     *            the amount to transpose in semitones
     */
    public static void transpose(Phrase phrase, final int transposition) {
        try {
            if (phrase == null)
                new NullPointerException();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        Vector noteList = phrase.getNoteList();
        Enumeration enumr = noteList.elements();
        while (enumr.hasMoreElements()) {
            Note note = (Note) enumr.nextElement();
            if (note.getPitch() != Note.REST) {
                note.setPitch(note.getPitch() + transposition);
            }
        }
        phrase.setNoteList(noteList);
    }

    /**
     * Transpose the phrase up or down in scale degrees.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null then this method
     * does nothing. If the transposition shifts a pitch
     * above {@link Note#MAX_PITCH MAX_PITCH}or below
     * {@link Note#MIN_PITCH MIN_PITCH}, the pitch will
     * probably cap at those values. See the description
     * of {@link Note#setPitch}method for exact details
     * of what occurs when trying to set the pitch
     * beyond the allowed range.
     * 
     * Transposition is in diatonic steps. For example
     * in C major the note C transposed 1 will become D,
     * transposed 4 will become G, and transposed by 7
     * will beome C an octave above. This can be
     * somewhat unintuitive so be careful.
     * 
     * @param phrase
     *            Phrase to be transposed
     * @param transposition
     *            the amount to transpose in semitones
     * @param mode
     *            the scale to use for the transposition
     * @param key
     *            the chromatic note to be used as the
     *            rooth of the mode. i.e., 0 = C, 1 = C#
     *            etc.
     */
    public static void transpose(Phrase phrase, final int transposition,
            final int[] mode, int key) {
        try {
            if (phrase == null)
                new NullPointerException();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        // make sure the root is in the first octave
        int rootNote = key % 12;
        Vector noteList = phrase.getNoteList();
        Enumeration enumr = noteList.elements();
        while (enumr.hasMoreElements()) {
            Note note = (Note) enumr.nextElement();
            Mod.transpose(note, transposition, mode, key);
        }
        phrase.setNoteList(noteList);
    }

    /**
     * Appends a copy of the <CODE>phrase</CODE> to
     * the end of itself.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null then this method
     * does nothing.
     * 
     * @param phrase
     *            Phrase to be repeated
     */
    public static void repeat(Phrase phrase) {
        repeat(phrase, 2);
    }

    /**
     * Makes the <CODE>phrase</CODE> <CODE>n</CODE>
     * times as long by repeating.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null or <CODE>n
     * </CODE> is less than on then this method does
     * nothing.
     * 
     * @param phrase
     *            Phrase to be repeated
     * @param n
     *            integer representing the number of
     *            repeats, 1 will leave the phrase
     *            unchanged, 2 a single added
     *            repetitions, and so forth
     */
    public static void repeat(Phrase phrase, final int n) {
        try {
            if (phrase == null)
                new NullPointerException();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        int initialLength = phrase.size();
        for (int t = 0; t < (n - 1); t++) {
            for (int i = 0; i < initialLength; i++) {
                phrase.addNote(phrase.getNote(i)
                    .copy());
            }
        }
    }

    /**
     * Places a duplicate of a section of a phrase
     * immediately after the end of the section.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null; or <CODE>
     * startLoc</CODE> is greater than or equal to
     * <CODE>endLoc</CODE> then this method does
     * nothing.
     * 
     * @param phrase
     *            Phrase with section to be repeated
     * @param startLoc
     *            double describing the time of the
     *            beginning of the repeated section, in
     *            crotchets.
     * @param endLoc
     *            double describing the time of the end
     *            of the repeated section, in crotchets.
     */
    public static void repeat(Phrase phrase, final double startLoc,
            final double endLoc) {
        repeat(phrase, 2, startLoc, endLoc);
    }

    /**
     * Loops a section of <CODE>phrase</CODE> <CODE>n
     * </CODE> times.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null; <CODE>startLoc
     * </CODE> is greater than or equal to <CODE>endLoc
     * </CODE>; or <CODE>n</CODE> is less than one
     * then this method does nothing.
     * 
     * @param phrase
     *            Phrase with section to be repeated
     * @param times
     *            integer representing the number of
     *            repeats
     * @param startLoc
     *            double describing the time of the
     *            beginning of the repeated section, in
     *            crotchets.
     * @param endLoc
     *            double describing the time of the end
     *            of the repeated section, in crotchets.
     */
    public static void repeat(Phrase phrase, int times, double startLoc,
            double endLoc) {
        // are the arguments valid?
        if (phrase == null) {
            System.err.println("phrase is null");
            return;
        } else if (startLoc >= endLoc) {
            System.err.println("startlocation is bigger or equal to end "
                    + "location");
            return;
        } else if (times < 2) {
            System.err.println("times is smaller than 2");
            return;
        } else if (startLoc < 0) {
            System.err.println("startLoc is smaller than 0");
            return;
        }

        // copy the section to repeat
        Phrase repeatedBit = phrase.copy(startLoc, endLoc);
        // setup
        Phrase tempPhr = new Phrase();
        boolean overlappingFirst = false;
        int overlappingNoteAt = 0;
        boolean overlappingLast = false;
        double beatCounter = (phrase.getStartTime() < 0.0) ? 0.0
                : phrase.getStartTime();
        // Add notes for the first time through
        int beforeCount;
        for (beforeCount = 0; beforeCount < phrase.size()
                && beatCounter + phrase.getNote(beforeCount)
                    .getRhythmValue() <= endLoc; beforeCount++) {
            // yes add the whole note
            tempPhr.addNote(phrase.getNote(beforeCount));
            // overlapping first note?
            if (beatCounter < startLoc
                    && beatCounter + phrase.getNote(beforeCount)
                        .getRhythmValue() > startLoc) {
                overlappingFirst = true;
                overlappingNoteAt = beforeCount;
            }
            beatCounter += phrase.getNote(beforeCount)
                .getRhythmValue();

        }
        // is the next note overlapping the end?
        if (beforeCount + 1 < phrase.size()) { // make
            // sure
            // we
            // haven't
            // gone
            // through all notes
            if (beatCounter < endLoc
                    && beatCounter + phrase.getNote(beforeCount + 1)
                        .getRhythmValue() > endLoc) {
                overlappingLast = true;
                // add partial note
                Note partialNote = phrase.getNote(beforeCount)
                    .copy();
                partialNote.setDuration(partialNote.getDuration() * endLoc
                        - beatCounter / partialNote.getRhythmValue());
                partialNote.setRhythmValue(endLoc - beatCounter);
                tempPhr.addNote(partialNote);
            }
        }

        // do the repeats
        boolean endLoopFlag = false;
        for (int r = 0; r < (times - 1); r++) {
            for (int j = 0; j < repeatedBit.size(); j++) {
                if (!endLoopFlag)
                    tempPhr.addNote(repeatedBit.getNote(j));
            }
        }

        // add the remaining notes
        if (overlappingLast) {
            // delete lst partial note
            tempPhr.removeLastNote();
            // beforeCount--;
        }

        for (int i = beforeCount; i < phrase.size(); i++) {
            tempPhr.addNote(phrase.getNote(i));
        }

        // update this phrase
        phrase.setNoteList(tempPhr.getNoteList());
        // if(startLoc >= 0.0 && phrase.getStartTime() >
        // startLoc)
        // phrase.setStartTime(startLoc);
    }

    /**
     * increases the dynamic by a certain amount -<br>
     * obviously a negative number will decrease it
     * 
     * @param Phrase
     *            the phase that is to be affected
     * @param int
     *            the amount that it is to be affected
     *            by
     */
    public static void increaseDynamic(Phrase phr, int amount) {
        try {
            if (phr == null)
                new NullPointerException();
        } catch (NullPointerException e) {
            e.toString();
            return;
        }

        Enumeration enumr = phr.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            Note n = (Note) enumr.nextElement();
            n.setDynamic(n.getDynamic() + amount);
        }
    }

    /**
     * Linearly fades in the <CODE>phrase</CODE>.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null; or if <CODE>
     * fadeLength</CODE> is less than or equal to zero
     * then this method does nothing.
     * 
     * @param phrase
     *            Phrase to be faded
     * @param fadelength
     *            double describing the number of beats
     *            (crotchets) to fade over
     */
    public static void fadeIn(Phrase phrase, final double fadeLength) {
        if (phrase == null || fadeLength <= 0.0) {
            return;
        }
        double rhythmValueCounter = 0.0;
        Enumeration enumr = phrase.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            if (rhythmValueCounter > fadeLength) {
                break;
            }
            Note nextNote = (Note) enumr.nextElement();
            double fadeFactor = rhythmValueCounter / fadeLength;
            int dynamic = (int) ((double) nextNote.getDynamic() * fadeFactor);
            if (dynamic == 0) {
                // start fade in at dynamic of 1 as 0 is
                // MIDI note off
                dynamic = 1;
            }
            nextNote.setDynamic(dynamic);
            rhythmValueCounter += nextNote.getRhythmValue();
        }
    }

    /**
     * Linearly fades in the <CODE>phrase</CODE>,
     * with the fade beginning before the <CODE>phrase
     * </CODE>.
     * 
     * <P>
     * This is mainly used by when fading multiple
     * Phrases of a Part.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null; <CODE>
     * fadeLength</CODE> is less than or equal to zero;
     * <CODE>phraseStart</CODE> is less than zero; or
     * <CODE>fadeLength</CODE> is less than or equal
     * to <CODE>phraseStart</CODE> then this method
     * does nothing.
     * 
     * @param phrase
     *            Phrase to be faded
     * @param fadeLength
     *            double describing the duration of the
     *            fade, in crotchets
     * @param phraseStartTime
     *            double describing how far into the
     *            fade the phrase starts
     */
    public static void fadeIn(Phrase phrase, final double fadeLength,
            final double phraseStartTime) {
        if (phrase == null || fadeLength <= 0.0 || phraseStartTime < 0.0) {
            return;
        }
        double rhythmValueCounter = phraseStartTime;
        Enumeration enumr = phrase.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            if (rhythmValueCounter >= fadeLength) {
                break;
            }
            Note nextNote = (Note) enumr.nextElement();
            double fadeFactor = rhythmValueCounter / fadeLength;
            int dynamic = (int) ((double) nextNote.getDynamic() * fadeFactor);
            if (dynamic == 0) {
                // start fade in at dynamic of 1 as 0 is
                // MIDI note off
                dynamic = 1;
            }
            nextNote.setDynamic(dynamic);
            rhythmValueCounter += nextNote.getRhythmValue();
        }
    }

    /**
     * Linearly fades out the <CODE>phrase</CODE>.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null; or if <CODE>
     * fadeLength</CODE> is less than or equal to zero
     * then this method does nothing.
     * 
     * @param phrase
     *            Phrase to be faded
     * @param fadeLength
     *            double describing the duration of the
     *            fade out in crotchets
     */
    public static void fadeOut(Phrase phrase, final double fadeLength) {
        if (phrase == null || fadeLength <= 0.0) {
            return;
        }
        double rhythmValueCounter = 0.0;
        int phraseLength = phrase.size() - 1;// -1
        // due
        // to
        // Vector
        // elements
        // starting at 0
        for (int i = 0; i <= phraseLength; i++) {
            Note nextNote = (Note) phrase.getNoteList()
                .elementAt(phraseLength - i);
            if (rhythmValueCounter > fadeLength) {
                break;
            }

            double fadeFactor = rhythmValueCounter / fadeLength;
            int dynamic = (int) ((double) nextNote.getDynamic() * fadeFactor);
            if (dynamic == 0) { // only fade out to
                // dynamic of 1 as 0 is
                // note
                // off
                dynamic = 1;
            }
            nextNote.setDynamic(dynamic);
            rhythmValueCounter += nextNote.getRhythmValue();
        }
    }

    /**
     * Linearly fades out the <CODE>phrase</CODE>,
     * with the fade ending after the phrase ends.
     * 
     * <P>
     * This method is mainly used by a Part when fading
     * multiple Phrases.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null; <CODE>
     * fadeLength</CODE> is less than or equal to zero;
     * <CODE>phraseEndTime</CODE> is less than zero;
     * or <CODE>fadeLength</CODE> is less than <CODE>
     * phraseEndTime</CODE> then this method does
     * nothing.
     * 
     * @param phrase
     *            Phrase to be faded
     * @param fadeLength
     *            double describing the duration of the
     *            fade out in crotchets
     * @param phraseEndTime
     *            double describing the length of time,
     *            in crotchets, between the end of the
     *            phrase and the end of the fade.
     */
    public static void fadeOut(Phrase phrase, final double fadeLength,
            final double phraseEndTime) {
        if (phrase == null || fadeLength <= 0.0 || phraseEndTime < 0.0) {
            return;
        }
        double rhythmValueCounter = phraseEndTime;
        int phraseLength = phrase.size() - 1; // -1
        // due
        // to
        // Vector
        // elements
        // starting at 0
        for (int i = 0; i <= phraseLength; i++) {
            Note nextNote = (Note) phrase.getNoteList()
                .elementAt(phraseLength - i);
            if (rhythmValueCounter >= fadeLength) {
                break;
            }
            double fadeFactor = rhythmValueCounter / fadeLength;
            int dynamic = (int) ((double) nextNote.getDynamic() * fadeFactor);
            if (dynamic == 0) {

                // only fade out to dynamic of 1 as 0 is
                // note off
                dynamic = 1;
            }
            nextNote.setDynamic(dynamic);
            rhythmValueCounter += nextNote.getRhythmValue();
        }
    }

    /**
     * A compressor/expander routine. Compression ratio
     * numbers between 0 and 1 compress, values larger
     * than 1 expand. Negative values invert the dynamic
     * about the mean.
     * 
     * <P>
     * This compression applies only to the volume,
     * technically the dynamic, of the notes. It will
     * multiply the difference between each of the
     * notes' dynmaics and the average dynamic, by the
     * compression factor. Thus, a <CODE>ratio</CODE>
     * of zero will change every note's volume to the
     * average volume; one will leave every note
     * unchanged; and two will make every note's volume
     * twice as far from the mean;
     * 
     * <P>
     * Negative values will have a similar affect but
     * leave the dynamic of each note on the other side
     * of the mean.
     * 
     * <P>
     * Note also that if the dynamic is expanded to a
     * value greater than
     * {@link Note#MAX_DYNAMIC MAX_DYNAMIC}or less than
     * {@link Note#MIN_DYNAMICMIN_DYNAMIC} then the
     * dynamic will be set to a value as described in
     * the {@link Note#setDynamic}method.
     * 
     * <P>
     * Finally, if <CODE>phrase</CODE> is null then
     * this method will do nothing.
     * 
     * @param phrase
     *            Phrase to be expanded/compressed
     * @param ratio
     *            double describing the compression
     *            factor.
     */
    public static void compress(Phrase phrase, final double ratio) {
        if (phrase == null) {
            return;
        }

        // compress the velocities
        Enumeration enumr = phrase.getNoteList()
            .elements();

        // find the max, min, and mean velocities
        int max = Note.MIN_DYNAMIC;
        int min = Note.MAX_DYNAMIC;
        int curr;
        int mean;

        while (enumr.hasMoreElements()) {
            Note note = (Note) enumr.nextElement();
            if (note.getPitch() != REST) { // reject
                // rests
                curr = note.getDynamic();
                if (curr > max) {
                    max = curr;
                }
                if (curr < min) {
                    min = curr;
                }
            }
        }
        mean = (min + max) / 2;

        // compress the sucker
        enumr = phrase.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            Note note = (Note) enumr.nextElement();
            curr = (int) (mean + ((note.getDynamic() - mean) * ratio));
            note.setDynamic(curr);
        }
    }

    /**
     * Adds phrase2 to the end of phrase1.
     * 
     * <P>
     * If either <CODE>phrase1</CODE> or <CODE>
     * phrase2</CODE> is null then this method does
     * nothing.
     * 
     * @param phrase1
     *            the base Phrase
     * @param phrase2
     *            the Phrase to be appended
     */
    public static void append(Phrase phrase1, Phrase phrase2) {
        if (phrase1 == null || phrase2 == null) {
            return;
        }
        Enumeration enumr = phrase2.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            phrase1.addNote(((Note) enumr.nextElement()).copy());
        }
    }

    /**
     * Quantizes the <CODE>phrase</CODE>. See
     * {@link #quantise(Phrase, double)}for more
     * details.
     * 
     * @param phrase
     *            Phrase to be quantized
     * @param qValue
     *            the rhythm value to quantize to, in
     *            crotchets
     */
    public static void quantize(Phrase phrase, double qValue) {
        quantise(phrase, qValue);
    }

    /**
     * Aligns all notes in a phrase to the closest beat
     * subdivision.
     * 
     * <P>
     * This is a basic quantisation that doesn't take
     * into account a note's offset, duration or
     * position within a phrase. Each note is quantised
     * by changing it's rhythm value to a integer
     * multiple of <CODE>qValue</CODE>.
     * 
     * <P>
     * As an example of how this might cause unwanted
     * side-effects consider quantising the three notes
     * in a quaver triple, to the <CODE>qValue</CODE>
     * of 0.25. Each note's rhythm value is slightly
     * less than a quaver, so is quantised to exactly a
     * quaver. Together the three notes now extend over
     * a dotted crotchet, whereas previously they
     * occupied only a crotchet. If this triple was in a
     * larger phrase all notes beyond the triplet would
     * begin a quaver later. In common time this would
     * have the effect of syncopating the rhythm.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null or <CODE>qValue
     * </CODE> is less than or equal to zero then this
     * method does nothing.
     * 
     * @param phrase
     *            Phrase to be quantised.
     * @param qValue
     *            the beat subdivision value to quantise
     *            to, in crotchets
     */
    public static void quantise(Phrase phrase, final double qValue) {
        if (phrase == null || qValue <= 0.0) {
            return;
        }
        Enumeration enumr = phrase.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            Note note = (Note) enumr.nextElement();
            double rv = note.getRhythmValue();

            // do quantise
            if (rv % qValue < qValue / 2) {
                note.setRhythmValue((int) (rv / qValue) * qValue);
            } else {
                note.setRhythmValue((int) (rv / qValue + 1) * qValue);
            }
        }
    }

    /**
     * Quantise all the phrases in this part.
     * 
     * <P>
     * See {@link #quantise(Phrase, double)} for
     * details.
     * 
     * <P>
     * If <CODE>part</CODE> is null or <CODE>qValue</CODE>
     * is less than or equal to zero then this method
     * does nothing.
     * 
     * @param part
     *            Part to be quantised
     * @param double -
     *            the amount to quantise too
     * @param boolean -
     *            wether or not to shift the start time
     *            of the phrases as well
     */
    public static void quantise(Part part, double qValue,
            boolean startTimeShifts, boolean roundDown, boolean notes) {
        if (part == null || qValue <= 0.0) {
            return;
        }

        for(int i=0; i< part.size(); i++) {
            double startTime = part.getPhrase(i).getStartTime();
            if (startTimeShifts) {
                if (!roundDown) {
                    if (startTime % qValue < qValue / 2) {
                        part.getPhrase(i).setStartTime((int) (startTime / qValue) * qValue);
                    } else {
                        part.getPhrase(i).setStartTime((int) (startTime / qValue + 1)
                                * qValue);
                    }
                } else {
                    part.getPhrase(i).setStartTime((int) (startTime / qValue) * qValue);
                }
            }
            if (notes)
                quantise(part.getPhrase(i), qValue);
        }
    }

    /**
     * Extends the <CODE>phrase</CODE> by repeating it
     * until it contains the number of notes as
     * specified by <CODE>numNotes</CODE>.
     * 
     * <P>
     * The repetitions work in the same manner as
     * {@link #repeat}, except that the final
     * repetition will not be a complete copy of the
     * original phrase if the note count is reached
     * before the repetition is completed.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null or if <CODE>
     * numNotes</CODE> is less than or equal to the
     * number of notes in the phrase then this method
     * does nothing.
     * 
     * @param phrase
     *            Phrase to be cycled
     * @param numbNotes
     *            the number of notes in the final
     *            phrase
     */
    public static void cycle(Phrase phrase, final int numNotes) {
        if (phrase == null) {
            return;
        }

        // check to see if the argument is legal
        int size = phrase.size();
        if (numNotes <= size) {
            return;
        }

        Phrase newPhr = new Phrase();

        // add extra cycled notes
        for (int i = 0; i < numNotes; i++) {
            newPhr.addNote(phrase.getNote(i % size)
                .copy());
        }

        // update the phrase
        phrase.getNoteList()
            .removeAllElements();
        Enumeration enumr = newPhr.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            phrase.getNoteList()
                .addElement(enumr.nextElement());
        }
    }

    /**
     * Extends the <CODE>phrase</CODE> by repeating it
     * until it is as long as the specified length.
     * 
     * <P>
     * The repetitions work in the same manner as
     * {@link #repeat}, except that the final
     * repetition will not be a complete copy of the
     * original phrase if the rhythm value count is
     * reached before the repetition is completed.
     * 
     * <P>
     * This method does not truncate the last note to
     * make the final phrase exactly equal to <CODE>
     * numBeats</CODE>. It guarantee that the final
     * phrase is at least <CODE>numBeats</CODE> but
     * may be greater depending on the rhythm value of
     * the last note added.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null or if <CODE>
     * numNotes</CODE> is less than or equal to the
     * number of notes in the phrase then this method
     * does nothing.
     * 
     * @param phrase
     *            Phrase to be cycled
     * @param numBeats
     *            double describing the minimum length
     *            of the final phrase, in crotchets
     */
    public static void cycle(Phrase phrase, final double numBeats) {

        // check to see if the argument is legal
        if (phrase == null || numBeats <= phrase.getEndTime()) {
            return;
        }

        int size = phrase.size();
        Phrase newPhr = new Phrase();

        // add extra cycled notes
        for (int i = 0; newPhr.getEndTime() < numBeats; i++) {
            newPhr.addNote(phrase.getNote(i % size)
                .copy());
        }

        // update the phrase
        phrase.getNoteList()
            .removeAllElements();
        Enumeration enumr = newPhr.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            phrase.getNoteList()
                .addElement(enumr.nextElement());
        }
    }

    /**
     * Randomise the order of notes in the phrase
     * without repeating any note.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null then this method
     * does nothing.
     * 
     * @param phrase
     *            Phrase to be shuffled
     */
    public static void shuffle(Phrase phrase) {
        if (phrase == null) {
            return;
        }
        // set up a new phrase
        Phrase newPhr = new Phrase();
        // put one note in to start off the new phrase
        newPhr.addNote(phrase.getNote((int) (Math.random() * phrase.size())));
        // create the rest of the new note order
        for (int i = 0; i < phrase.size() - 1;) {
            // select a new note from this phrase
            Note n = phrase.getNote((int) (Math.random() * phrase.size()));
            // check if it is a note already used
            boolean notUsed = true;
            for (int j = 0; j < newPhr.size(); j++) {
                if (n == newPhr.getNote(j)) {
                    notUsed = false;
                }
            }
            // if the note is not already present then
            // add it to the phrase
            // and move to the next note in the phrase
            if (notUsed) {
                newPhr.addNote(n);
                i++;
            }
        }

        // update the phrase
        phrase.getNoteList()
            .removeAllElements();
        Enumeration enumr = newPhr.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            phrase.getNoteList()
                .addElement(enumr.nextElement());
        }
    }

    /**
     * Extend the phrase by adding all notes backwards,
     * repeating the last note of the phrase.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null this method does
     * nothing.
     * 
     * @param phrase
     *            Phrase to be extended with its mirror
     */
    public static void palindrome(Phrase phrase) {
        palindrome(phrase, true);
    }

    /**
     * Extend the phrase by adding all notes backwards.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null this method does
     * nothing.
     * 
     * @param phrase
     *            Phras to be extended with its mirror
     * @param repeatLastNote
     *            boolean specifying whether the last
     *            note is to be repeated
     */
    public static void palindrome(Phrase phrase, final boolean repeatLastNote) {
        if (phrase == null) {
            return;
        }

        int numbNotes = (repeatLastNote) ? phrase.size() : phrase.size() - 1;

        // add the existing note backwards
        for (int i = numbNotes - 1; i >= 0; i--) {
            phrase.addNote(phrase.getNote(i));
        }
    }

    /**
     * Move the notes around the phrase, first becoming
     * second, second becoming third, and so forth with
     * last becoming first.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null this method does
     * nothing.
     * 
     * @param phrase
     *            Phrase whose notes are to be rotated
     */
    public static void rotate(Phrase phrase) {
        rotate(phrase, 1);
    }

    /**
     * Move the notes around a number of steps as
     * specified by <CODE>numSteps</CODE> which each
     * step involving the first note becoming the
     * second, second the third, and so forth with the
     * last becoming first.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null this method does
     * nothing.
     * 
     * @param phrase
     *            Phrase whose notes are to be rotated
     * @param int
     *            number of steps
     */
    public static void rotate(Phrase phrase, int numSteps) {
        if (phrase == null) {
            return;
        }
        Vector v = phrase.getNoteList();
        for (int i = 0; i < numSteps; i++) {

            // rotate
            v.insertElementAt(v.lastElement(), 0);
            v.removeElementAt(v.size() - 1);
        }
    }

    /**
     * Reverse the order of notes in the phrase.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null then this method
     * does nothing.
     * 
     * @param phrase
     *            Phrase to be mirrored
     */
    public static void retrograde(Phrase phrase) {
        if (phrase == null) {
            return;
        }
        Phrase backwards = new Phrase();
        for (int i = phrase.size(); i > 0; i--) {
            backwards.addNote(phrase.getNote(i - 1));
        }

        // update the phrase
        phrase.getNoteList()
            .removeAllElements();
        Enumeration enumr = backwards.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            phrase.getNoteList()
                .addElement(enumr.nextElement());
        }
    }

    /**
     * Mirror the pitch of notes in the phrase around
     * the first note's pitch. The order of the notes is
     * not affected it is only the pitches that are
     * mirrored. That is, notes which are n semitones
     * above the first pitch will be changed to be n
     * semitones below.
     * 
     * <P>
     * For exact details of what happens when pitches
     * are shifted below
     * {@link Note#MIN_PITCH MIN_PITCH}or above
     * {@link Note#MAX_PITCHMAX_PITCH} see the
     * description for {@link Note#setPitch}
     * 
     * <P>
     * If <CODE>phrase</CODE> is null then this method
     * does nothing.
     * 
     * @param phrase
     *            Phrase to be inverted
     */
    public static void inversion(Phrase phrase) {
        if (phrase == null) {
            return;
        }

        int i = 0;
        int firstNote = (int) Note.REST;

        // get the first pitch
        while (i < phrase.size() && firstNote == Note.REST) {
            firstNote = phrase.getNote(i++)
                .getPitch();
        }

        for (; i < phrase.size(); i++) {

            // change the pitch to invert each around
            // the first note
            phrase.getNote(i)
                .setPitch(firstNote - (phrase.getNote(i)
                    .getPitch() - firstNote));
        }
    }

    /**
     * Alters the phrase so that it's notes are
     * stretched or compressed until the phrase is the
     * length specified. This method works in the same
     * manner as elongate, except it takes an absolute
     * length parameter not a ratio.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null or <CODE>
     * newLength</CODE> is less than or equal to zero
     * then this method does nothing.
     * 
     * @param phrase
     *            Phrase to be lengthened
     * @param newLength
     *            double describing the number of beats
     *            to change the phrase to.
     */
    public static void changeLength(Phrase phrase, double newLength) {
        if (phrase == null || newLength <= 0.0) {
            return;
        }
        final double oldLength = phrase.getEndTime() - phrase.getStartTime();
        elongate(phrase, newLength / oldLength);
    }

    /**
     * Stretch the time of each note in the phrase by
     * <CODE>scaleFactor</CODE>
     * 
     * <P>
     * If <CODE>phrase</CODE> is null or <CODE>
     * scaleFactor</CODE> is less than or equal to zero
     * then this method does nothing.
     * 
     * @param phrase
     *            Phrase to be lengthened
     * @param scaleFactor
     *            double describing the scale factor
     */
    public static void elongate(Phrase phrase, double scaleFactor) {
        if (phrase == null || scaleFactor <= 0.0) {
            return;
        }

        Enumeration enumr = phrase.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            Note note = (Note) enumr.nextElement();
            note.setRhythmValue(note.getRhythmValue() * scaleFactor);
            note.setDuration(note.getDuration() * scaleFactor);
        }
    }

    /**
     * Increase the dynamic of notes at regularly
     * occuring pulse locations which generates the
     * sound of regular meter by emphasising the first
     * beat of each bar/measure.
     * 
     * <P>
     * The dynamic of each note starting on the beat
     * will be increased by 20. If this raises the
     * dynamic above
     * {@link Note#MAX_DYNAMIC MAX_DYNAMIC}then the
     * dynamic will be set to a value as described in
     * {@linkNote#setDynamic}.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null or <CODE>meter
     * </CODE> is less than or equal to zero then this
     * method does nothing.
     * 
     * @param phrase
     *            Phrase whose beats are to accented
     * @param meter
     *            double describing the number of
     *            croctets per bar/measure
     */
    public static void accents(Phrase phrase, double meter) {
        double[] beats = { 0.0 };
        accents(phrase, meter, beats);
    }

    /**
     * Increase the dynamic of notes at regularly
     * occuring pulse locations which generates the
     * sound of regular meter by accenting specified
     * beats within each bar/measure.
     * 
     * <P>
     * <CODE>accentedBeats</CODE> is an array of
     * values describing where the accents begin
     * relative to the start of each bar, in crochets.
     * 
     * <P>
     * Each accented note's dynamic will be increased by
     * 20. If this raises the dynamic above
     * {@link Note#MAX_DYNAMIC MAX_DYNAMIC}then the
     * dynamic will be set to a value as described in
     * {@link Note#setDynamic}.
     * 
     * <P>
     * Each of the double values in <CODE>accentedBeats
     * </CODE> must be greater than or equal to zero and
     * less than <CODE>meter</CODE> or this method
     * does nothing.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null or <CODE>meter
     * </CODE> is less than or equal to zero then this
     * method does nothing.
     * 
     * @param phrase
     *            Phrase to be accented
     * @param meter
     *            double describing the number of
     *            croctets per bar/measure
     * @param accentedBeats
     *            double array describing the time of
     *            the accents in the bar.
     */
    public static void accents(Phrase phrase, double meter,
            double[] accentedBeats) {
        accents(phrase, meter, accentedBeats, 20);
    }

    /**
     * Increase the dynamic of notes at regularly
     * occuring pulse locations which generates the
     * sound of regular meter. The meter is the total
     * beat length of the bar/measure, while the array
     * of accented beats is the times within the bar at
     * which an accent should be made. For example, 6/8
     * time would be implied by a meter of 3 and a
     * accent array of {0.0, 1.5}.
     * 
     * <P>
     * <CODE>accentedBeats</CODE> is an array of
     * values describing where the accents begin
     * relative to the start of each bar, in crochets.
     * 
     * <P>
     * Accented beats will have their dynamic increased
     * by <CODE>accentAmount</CODE>. If this causes
     * the dynamic to be above {@linkNote#MAX_DYNAMIC
     * MAX_DYNAMIC} or below
     * {@link Note#MIN_DYNAMICMIN_DYNAMIC}(when <CODE>
     * accentAmount</CODE>) then the dynamic will be
     * set to a value as described by
     * {@link Note#setDynamic}.
     * 
     * <P>
     * Each of the double values in <CODE>accentedBeats
     * </CODE> must be greater than or equal to zero and
     * less than <CODE>meter</CODE> or this method
     * does nothing.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null or <CODE>meter
     * </CODE> is less than or equal to zero then this
     * method does nothing.
     * 
     * @param phrase
     *            Phrase to be accented
     * @param meter
     *            double describing the number of
     *            croctets per bar/measure
     * @param accentedBeats
     *            double array describing the time of
     *            the accents in the bar.
     * @param accentAmount
     *            integer describing the value that the
     *            dynamic of accented beats are
     *            increased by.
     */
    public static void accents(Phrase phrase, final double meter,
            final double[] accentedBeats, final int accentAmount) {
        if (phrase == null || meter <= 0.0) {
            return;
        }
        for (int i = 0; i < accentedBeats.length; i++) {
            if (accentedBeats[i] < 0.0 || accentedBeats[i] >= meter) {
                return;
            }
        }

        double beatCounter = (phrase.getStartTime() < 0.0) ? 0.0
                : phrase.getStartTime();
        Vector v = phrase.getNoteList();
        for (int i = 0; i < v.size(); i++) {
            Note n = (Note) v.elementAt(i);

            // check to see if that note occurs on an
            // accented beat
            // and if so increase its dynamic level
            for (int j = 0; j < accentedBeats.length; j++) {
                if (beatCounter % meter == accentedBeats[j]) {
                    int tempDyn = n.getDynamic();
                    tempDyn += accentAmount;
                    n.setDynamic(tempDyn);
                }
            }
            beatCounter += n.getRhythmValue();
        }
    }

    /**
     * Increases dynamic values so that the loudest is
     * at maxiumim level.
     * 
     * This process only effects the dynamic value of
     * the notes in the phrase.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null then this method
     * does nothing.
     * 
     * @param phrase
     *            the Phrase to be effected
     */
    public static void normalise(Phrase phrase) {
        if (phrase == null) {
            return;
        }
        // get the curent max
        int max = 0;
        Enumeration enumr = phrase.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            Note n = (Note) enumr.nextElement();
            if (n.getDynamic() > max)
                max = n.getDynamic();
        }
        // increase the normalisation
        if (max == Note.MAX_DYNAMIC) {
            return;
        }
        int diff = Note.MAX_DYNAMIC - max;
        Enumeration enumr2 = phrase.getNoteList()
            .elements();
        while (enumr2.hasMoreElements()) {
            Note n = (Note) enumr2.nextElement();
            n.setDynamic(n.getDynamic() + diff);
        }
    }

    /**
     * Randomly adjusts Note dynamic values to create
     * uneven loudness. This process only effects the
     * dynamic value of the notes in the phrase.
     * <P>
     * If <CODE>phrase</CODE> is null then this method
     * does nothing.
     * 
     * @param phrase
     *            the Phrase to be effected
     */
    public static void shake(Phrase phrase) {
        Mod.shake(phrase, 20);
    }

    /**
     * Randomly adjusts all Notes' dynamic value to
     * create uneven loudness.
     * 
     * This process only effects the dynamic value of
     * the notes in the phrase.
     * 
     * <P>
     * If <CODE>phrase</CODE> is null then this method
     * does nothing.
     * 
     * @param phrase
     *            The Phrase to be effected
     * @param int
     *            The amount of effect - e.g., 5 will be
     *            +-5 from the current amount
     */
    public static void shake(Phrase phrase, int amount) {
        if (phrase == null) {
            return;
        }
        int currentValue, newValue;
        Enumeration enumr = phrase.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            Note n = (Note) enumr.nextElement();
            currentValue = n.getDynamic();
            // create new dynamic
            do {
                newValue = currentValue
                        + (int) (Math.random() * 2 * amount - amount);
            } while (newValue < 0 || newValue > 127);
            n.setDynamic(newValue);
        }
    }

    /**
     * Mutates the <CODE>phrase</CODE> by changing one
     * pitch and one rhythm value.
     * 
     * @param Phrase
     *            Phrase to be mutated
     */
    public static void mutate(Phrase phrase) {
        mutate(phrase, 1, 1, CHROMATIC_SCALE, phrase.getLowestPitch(),
            phrase.getHighestPitch(), new double[] { 0.25, 0.5, 1.0, 1.5, 2.0 });
    }

    /**
     * Mutates the <CODE>phrase</CODE> by changing
     * pitches and rhythm values. The number of pitches
     * and rhythm values to change can be set, and the
     * pitches and rhythmValues to select from can be
     * specified. The notes within the phrase to change
     * are selected at random. The scale values need to
     * be within 0-11 and will typically use the JMC
     * scale constants.
     * 
     * @param Phrase
     *            Phrase to be mutated
     * @param int
     *            pitchCount The number of notes to have
     *            pitch altered
     * @param double
     *            rhythmCount The number of notes to
     *            have thier rhythm altered
     * @param int[]
     *            pitches The scale values from which to
     *            select for relacements
     * @param int
     *            lowestPitch The smallest value a
     *            relacement pitch can be
     * @param int
     *            highestPitch The largest value a
     *            replacement pitch can be
     * @param double[]
     *            rhythms The rhyythm values from which
     *            to select replacements
     */
    public static void mutate(Phrase phrase, int pitchCount, int[] scale) {
        mutate(phrase, 1, 0, scale, phrase.getLowestPitch(),
            phrase.getHighestPitch(), new double[] {});
    }

    /**
     * Mutates the <CODE>phrase</CODE> by changing
     * pitches and rhythm values. The number of pitches
     * and rhythm values to change can be set, and the
     * pitches and rhythmValues to select from can be
     * specified. The notes within the phrase to change
     * are selected at random. The scale values need to
     * be within 0-11 and will typically use the JMC
     * scale constants.
     * 
     * @param Phrase
     *            Phrase to be mutated
     * @param int
     *            pitchCount The number of notes to have
     *            pitch altered
     * @param double
     *            rhythmCount The number of notes to
     *            have thier rhythm altered
     * @param int[]
     *            pitches The scale values from which to
     *            select for relacements
     * @param int
     *            lowestPitch The smallest value a
     *            relacement pitch can be
     * @param int
     *            highestPitch The largest value a
     *            replacement pitch can be
     * @param double[]
     *            rhythms The rhyythm values from which
     *            to select replacements
     */
    public static void mutate(Phrase phrase, int pitchCount, int rhythmCount,
            int[] pitches, int lowestPitch, int highestPitch, double[] rhythms) {
        // pitch mutation
        for (int i = 0; i < pitchCount; i++) {
            int newPitch = (int) (Math.random() * (highestPitch - lowestPitch) + lowestPitch);
            int pitchToChange = (int) (Math.random() * phrase.size());
            Note noteToChange = phrase.getNote(pitchToChange);
            noteToChange.setPitch(newPitch);
            while (!noteToChange.isScale(pitches)) {
                newPitch = (int) (Math.random() * (highestPitch - lowestPitch) + lowestPitch);
                pitchToChange = (int) (Math.random() * phrase.size());
                noteToChange = phrase.getNote(pitchToChange);
            }
        }
        // rhythm mutatation
        for (int i = 0; i < rhythmCount; i++) {
            double newRV = rhythms[(int) (Math.random() * rhythms.length)];
            Note noteToChange = phrase.getNote((int) (Math.random() * phrase.size()));
            noteToChange.setRhythmValue(newRV);
            noteToChange.setDuration(newRV * 0.9);
        }
    }

    /**
     * Joins consecutive notes in the <CODE>phrase
     * </CODE> that have the same pitch, creating one
     * longer note. This is simmilar to the musical
     * function of a tie. All note in the phrase meeting
     * the conditions are effected. This modification
     * may reduce the overall note count.
     * 
     * @param Phrase
     *            Phrase to be processed
     */
    public static void tiePitches(Phrase phrase) {
        for (int i = 0; i < phrase.size() - 1;) {
            Note currNote = phrase.getNote(i);
            Note nextNote = phrase.getNote(i + 1);
            if (currNote.getPitch() == nextNote.getPitch()) {
                currNote.setRhythmValue(currNote.getRhythmValue()
                        + nextNote.getRhythmValue());
                currNote.setDuration(currNote.getDuration()
                        + nextNote.getDuration());
                phrase.removeNote(i + 1);
            } else
                i++;
        }
    }

    /**
     * Joins consecutive rests in the <CODE>phrase
     * </CODE> creating one longer note. This is
     * simmilar to the musical function of a tie. This
     * modification may reduce the overall rest count.
     * 
     * @param Phrase
     *            Phrase to be processed
     */
    public static void tieRests(Phrase phrase) {
        for (int i = 0; i < phrase.size() - 1;) {
            Note currNote = phrase.getNote(i);
            Note nextNote = phrase.getNote(i + 1);
            if (currNote.getPitch() == REST && nextNote.getPitch() == REST) {
                currNote.setRhythmValue(currNote.getRhythmValue()
                        + nextNote.getRhythmValue());
                currNote.setDuration(currNote.getDuration()
                        + nextNote.getDuration());
                phrase.removeNote(i + 1);
            } else
                i++;
        }
    }

    /**
     * Lengthens notes followed by a rest in the <CODE>
     * phrase</CODE> by creating one longer note and
     * deleting the rest. This modification may reduce
     * the overall note count in the phrase, be careful
     * that use of this method does not to cause array
     * out of bounds exceptions.
     * 
     * @param Phrase
     *            Phrase to be processed
     */
    public static void fillRests(Phrase phrase) {
        for (int i = 0; i < phrase.size() - 1;) {
            Note currNote = phrase.getNote(i);
            Note nextNote = phrase.getNote(i + 1);
            if (currNote.getPitch() != REST && nextNote.getPitch() == REST) {
                currNote.setRhythmValue(currNote.getRhythmValue()
                        + nextNote.getRhythmValue());
                currNote.setDuration(currNote.getDuration()
                        + nextNote.getDuration());
                phrase.removeNote(i + 1);
            } else
                i++;
        }
    }

    /**
     * Randomly adjusts all Notes' pan value to create
     * an even spread acroos the stereo spectrum. This
     * process only effects the pan value of the notes
     * in the phrase.
     * <P>
     * If <CODE>phrase</CODE> is null then this method
     * does nothing.
     * 
     * @param phrase
     *            the Phrase to be effected
     */
    public static void spread(Phrase phrase) {
        if (phrase == null) {
            return;
        }
        int currentValue, newValue;
        Enumeration enumr = phrase.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            Note n = (Note) enumr.nextElement();
            // create new pan value
            n.setPan(Math.random());
        }
    }

    /**
     * Adjusts all Note pan values to alternate between
     * extreme left and right from note to note. This
     * process only effects the pan value of the notes
     * in the phrase.
     * <P>
     * If <CODE>phrase</CODE> is null then this method
     * does nothing.
     * 
     * @param phrase
     *            The Phrase to be effected
     */
    public static void bounce(Phrase phrase) {
        if (phrase == null) {
            return;
        }
        boolean left = true;
        Enumeration enumr = phrase.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            Note n = (Note) enumr.nextElement();
            // create new pan value
            if (left)
                n.setPan(0.0);
            else
                n.setPan(1.0);
            left = !left;
        }
    }

    /**
     * Adjusts all Note duration values to vary randomly
     * between specified values from note to note. This
     * process only effects the duration attribute of
     * the notes in the phrase.
     * <P>
     * If <CODE>phrase</CODE> is null then this method
     * does nothing.
     * 
     * @param phrase
     *            The Phrase to be effected
     * @param minlength
     *            The shortest possible duration
     * @param maxlength
     *            The longest possible duration
     */
    public static void varyLength(Phrase phrase, double minLength,
            double maxLength) {
        if (phrase == null || maxLength < minLength) {
            return;
        }
        Enumeration enumr = phrase.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            Note n = (Note) enumr.nextElement();
            double dur = Math.random() * (maxLength - minLength) + minLength;
            n.setDuration(dur);
        }
    }

    /**
     * Adjusts Note values to any value plus or minus a
     * specified amount. This process can effect pitch,
     * rhythm & duration, dynamic, and pan values of the
     * notes in the phrase.
     * <P>
     * If <CODE>phrase</CODE> is null then this method
     * does nothing.
     * 
     * @param phrase
     *            The Phrase to be effected
     * @param pitchVariation
     *            The degree of pitch change to apply.
     */
    public static void randomize(Phrase phrase, int pitchVariation) {
        randomize(phrase, pitchVariation, 0.0);
    }

    /**
     * Adjusts Note values to any value plus or minus a
     * specified amount. This process can effect pitch,
     * rhythm & duration, dynamic, and pan values of the
     * notes in the phrase.
     * <P>
     * If <CODE>phrase</CODE> is null then this method
     * does nothing.
     * 
     * @param phrase
     *            The Phrase to be effected
     * @param pitchVariation
     *            The degree of pitch change to apply.
     * @param rhythmVariation
     *            The degree of rhythm value change to
     *            apply.
     */
    public static void randomize(Phrase phrase, int pitchVariation,
            double rhythmVariation) {
        randomize(phrase, pitchVariation, rhythmVariation, 0);
    }

    /**
     * Adjusts Note values to any value plus or minus a
     * specified amount. This process can effect pitch,
     * rhythm & duration, dynamic, and pan values of the
     * notes in the phrase.
     * <P>
     * If <CODE>phrase</CODE> is null then this method
     * does nothing.
     * 
     * @param phrase
     *            The Phrase to be effected
     * @param pitchVariation
     *            The degree of pitch change to apply.
     * @param rhythmVariation
     *            The degree of rhythm value change to
     *            apply.
     * @param dynamicVariation
     *            The degree of dynamic change to apply.
     */
    public static void randomize(Phrase phrase, int pitchVariation,
            double rhythmVariation, int dynamicVariation) {
        if (phrase == null) {
            return;
        }
        boolean left = true;
        Enumeration enumr = phrase.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            Note n = (Note) enumr.nextElement();
            // create new pitch value
            if (pitchVariation > 0) {
                n.setPitch(n.getPitch()
                        + (int) (Math.random() * (pitchVariation * 2) - pitchVariation));
            }
            // create new rhythm and duration values
            if (rhythmVariation > 0.0) {
                double var = (Math.random() * (rhythmVariation * 2) - rhythmVariation);
                n.setRhythmValue(n.getRhythmValue() + var);
                n.setDuration(n.getDuration() + var);
            }
            // create new dynamic value
            if (dynamicVariation > 0) {
                n.setDynamic(n.getDynamic()
                        + (int) (Math.random() * (dynamicVariation * 2) - dynamicVariation));
            }
        }
    }

    /**
     * All ascending sequences of notes are slured by
     * having thier duration extended to 100% of the
     * rhythm value. This algorithm assumes notes are
     * arranged in the standard monophonic series used
     * by jMusic. Notes with durations longer than
     * rhythm values will produce unintended results.
     * 
     * @param Phrase
     *            The phrase to be modified.
     */
    public static void slurUp(Phrase phrase) {
        slurUp(phrase, 2);
    }

    /**
     * All descending sequences of notes are slured by
     * having thier duration extended to 100% of the
     * rhythm value. This algorithm assumes notes are
     * arranged in the standard monophonic series used
     * by jMusic. Notes with durations longer than
     * rhythm values will produce unintended results.
     * 
     * @param Phrase
     *            The phrase to be modified.
     */
    public static void slurDown(Phrase phrase) {
        slurDown(phrase, 2);
    }

    /**
     * All ascending sequences of the number of notes or
     * more are slured by having thier duration extended
     * to 100% of the rhythm value. This algorithm
     * assumes notes are arranged in the standard
     * monophonic series used by jMusic. Notes with
     * durations longer than rhythm values will produce
     * unintended results.
     * 
     * @param Phrase
     *            The phrase to be modified.
     * @param int
     *            The number of notes in a row to
     *            consitute a sequence
     */
    public static void slurUp(Phrase phrase, int numberOfNotes) {
        if (phrase == null || phrase.size() < numberOfNotes
                || numberOfNotes < 2) {
            System.err.println("jMusic Mod.slurUp error: Arguments not valid.");
            return;
        }
        boolean change = false;
        int max = phrase.size() - numberOfNotes;
        for (int i = 0; i < max;) {
            for (int j = 0; j < numberOfNotes - 1; j++) {
                if ((phrase.getNote(i + j)
                    .getPitch() >= 0) && (phrase.getNote(i + j)
                    .getPitch() < phrase.getNote(i + j + 1)
                    .getPitch())) {
                    change = true;
                } else {
                    change = false;
                    break;
                }
            }
            if (change) {
                for (int k = 0; k < numberOfNotes - 1; k++) {
                    phrase.getNote(i + k)
                        .setDuration(phrase.getNote(i + k)
                            .getRhythmValue());
                }
                i += numberOfNotes - 1;
            } else
                i++;
            change = false;
        }
    }

    /**
     * All descending sequences of the number of notes
     * or more are slured by having the duration of all
     * but the last note extended to 100% of the rhythm
     * value. This algorithm assumes notes are arranged
     * in the standard monophonic series used by jMusic.
     * Notes with durations longer than rhythm values
     * will produce unintended results.
     * 
     * @param Phrase
     *            The phrase to be modified.
     * @param int
     *            The number of notes in a row to
     *            consitute a sequence
     */
    public static void slurDown(Phrase phrase, int numberOfNotes) {
        if (phrase == null || phrase.size() < numberOfNotes
                || numberOfNotes < 2) {
            System.err.println("jMusic Mod.slurDown error: Arguments not valid.");
            return;
        }
        boolean change = false;
        int max = phrase.size() - numberOfNotes;
        for (int i = 0; i < max;) {
            for (int j = 0; j < numberOfNotes - 1; j++) {
                if ((phrase.getNote(i + j)
                    .getPitch() >= 0) && (phrase.getNote(i + j)
                    .getPitch() > phrase.getNote(i + j + 1)
                    .getPitch())) {
                    change = true;
                } else {
                    change = false;
                    break;
                }
            }
            if (change) {
                for (int k = 0; k < numberOfNotes - 1; k++) {
                    phrase.getNote(i + k)
                        .setDuration(phrase.getNote(i + k)
                            .getRhythmValue());
                }
                i += numberOfNotes - 1;
            } else
                i++;
            change = false;
        }
    }

    /**
     * Vary the duration of each note in the phrase by
     * the multiplyer.
     * 
     * @param Phrase
     *            The phrase to be modified.
     * @param double
     *            The amount to multiply the duration
     *            by.
     */
    public static void increaseDuration(Phrase phrase, double multiplyer) {
        Enumeration enumr = phrase.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            Note n = (Note) enumr.nextElement();
            n.setDuration(n.getDuration() * multiplyer);
        }
    }

    /**
     * Vary the duration of each note in the phrase by
     * the specified amount.
     * 
     * @param Phrase
     *            The phrase to be modified.
     * @param double
     *            The amount to add to the duration.
     */
    public static void addToDuration(Phrase phrase, double amount) {
        Enumeration enumr = phrase.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            Note n = (Note) enumr.nextElement();
            n.setDuration(n.getDuration() + amount);
        }
    }

    /**
     * Vary the rhythm value of each note in the phrase
     * by the specified amount.
     * 
     * @param Phrase
     *            The phrase to be modified.
     * @param double
     *            The amount to add.
     */
    public static void addToRhythmValue(Phrase phrase, double amount) {
        Enumeration enumr = phrase.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            Note n = (Note) enumr.nextElement();
            n.setRhythmValue(n.getRhythmValue() + amount);
        }
    }

    /**
     * Vary both the rhythm value and duration of each
     * note in the phrase by the specified amount.
     * 
     * @param phrase
     *            The phrase to be modified.
     * @param double
     *            The amount to add.
     */
    public static void addToLength(Phrase phrase, double amount) {
        Enumeration enumr = phrase.getNoteList()
            .elements();
        double articulation = 0.0;
        while (enumr.hasMoreElements()) {
            Note n = (Note) enumr.nextElement();
            articulation = n.getRhythmValue() / n.getDuration();
            n.setRhythmValue(n.getRhythmValue() + amount);
            n.setDuration(n.getRhythmValue() * articulation);
        }
    }

    /**
     * Vary the interval between notes scaling by the
     * specified amount to each interval.
     * 
     * @param phrase -
     *            The phrase to be modified.
     * @param amount -
     *            The scaling multiplyer for the
     *            intervals, i.e., 2.0 doubles width.
     */
    public static void expandIntervals(Phrase phrase, double amount) {
        int phraseSize = phrase.size();
        if (phraseSize < 2)
            return;
        Note firstNote = phrase.getNote(0);
        for (int i = 1; i < phraseSize; i++) {
            Note currNote = phrase.getNote(i);
            int newInterval = (int) ((currNote.getPitch() - firstNote.getPitch()) * amount);
            currNote.setPitch(currNote.getPitch() + newInterval);
        }
    }

    // ---------------------- CPHRASE MODIFICATIONS
    // ---------------------------//

    /**
     * Transposes the <CODE>cphrase</CODE>.
     * 
     * <P>
     * If <CODE>cphrase</CODE> is null then this
     * method does nothing. If the pitch is transposed
     * to a value greater than {@link Note#MAX_PITCH
     * MAX_PITCH} or less than
     * {@link Note#MIN_PITCH MIN_DYNAMIC}then the pitch
     * will be set to a value as described in the
     * {@link Note#setPitch}method.
     * 
     * @param cphrase
     *            CPhrase to be transposed
     * @param transpose
     *            integer describing the amount to
     *            transpose in semitones
     */
    public static void transpose(CPhrase cphrase, final int trans) {
        if (cphrase == null) {
            return;
        }

        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            transpose((Phrase) enumr.nextElement(), trans);
        }
    }

    /**
     * Transpose the CPhrase up or down in scale
     * degrees.
     * 
     * <P>
     * If <CODE>cphrase</CODE> is null then this
     * method does nothing. If the transposition shifts
     * a pitch above {@link Note#MAX_PITCH MAX_PITCH}or
     * below {@link Note#MIN_PITCH MIN_PITCH}, the
     * pitch will probably cap at those values. See the
     * description of {@link Note#setPitch}method for
     * exact details of what occurs when trying to set
     * the pitch beyond the allowed range.
     * 
     * Transposition is in diatonic steps. For example
     * in C major the note C transposed 1 will become D,
     * transposed 4 will become G, and transposed by 7
     * will beome C an octave above. This can be
     * somewhat unintuitive so be careful.
     * 
     * @param cphrase
     *            CPhrase to be transposed
     * @param transposition
     *            the amount to transpose in semitones
     * @param mode
     *            the scale to use for the transposition
     * @param key
     *            the chromatic note to be used as the
     *            rooth of the mode. i.e., 0 = C, 1 = C#
     *            etc.
     */
    public static void transpose(CPhrase cphrase, final int transposition,
            final int[] mode, int key) {
        if (cphrase == null) {
            return;
        }

        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            transpose((Phrase) enumr.nextElement(), transposition, mode, key);
        }
    }

    /**
     * Makes the CPhrase twice as long by repeating it
     * once.
     * 
     * <P>
     * If <CODE>cphrase</CODE> is null this method
     * does nothing.
     * 
     * @param cphrase
     *            CPhrase to be repeated
     */
    public static void repeat(CPhrase cphrase) {
        repeat(cphrase, 2);
    }

    /**
     * Makes the CPhrase n times as long by repeating.
     * 
     * <P>
     * If <CODE>cphrase</CODE> is null or <CODE>times
     * </CODE> is less than one this mthod does nothing.
     * 
     * @param cphrase
     *            CPhrase to be repeated
     * @param times
     *            number of repeats (default is 1)
     */
    public static void repeat(CPhrase cphrase, final int times) {
        if (cphrase == null) {
            return;
        }

        int initialLength = cphrase.getPhraseList()
            .size();
        for (int t = 0; t < (times - 1); t++) {
            double initialEndTime = cphrase.getEndTime();
            for (int i = 0; i < initialLength; i++) {
                Phrase phr = (Phrase) cphrase.getPhraseList()
                    .elementAt(i);
                Phrase phrCopy = phr.copy();
                phrCopy.setStartTime(initialEndTime + phr.getStartTime());
                cphrase.addPhrase(phrCopy);
            }
        }
    }

    /**
     * Loops a section of a the CPhrase once
     * 
     * <P>
     * If <CODE>cphrase</CODE> is null; or <CODE>
     * startLoc</CODE> is greater than or equal to
     * <CODE>endLoc</CODE> then this method does
     * nothing.
     * 
     * @param cphrase
     *            CPhrase to be repeated
     * @param startLoc
     *            location of the loop start in beats
     * @param endLoc
     *            location of the loop end in beats
     */
    public static void repeat(CPhrase cphrase, final double startLoc,
            final double endLoc) {
        repeat(cphrase, 2, startLoc, endLoc);
    }

    /**
     * Loops a section of the CPhrase n times.
     * 
     * <P>
     * If <CODE>cphrase</CODE> is null; <CODE>
     * startLoc</CODE> is greater than or equal to
     * <CODE>endLoc</CODE>; or <CODE>n</CODE> is
     * less than one then this method does nothing.
     * 
     * @param cphrase
     *            CPhrase to be repeated
     * @param times
     *            int number of repeats (default is 1)
     * @param double
     *            location of the loop start in beats
     * @param double
     *            location of the loop end in beats
     */
    public static void repeat(CPhrase cphrase, final int times,
            final double startLoc, final double endLoc) {
        if (cphrase == null || startLoc >= endLoc || times < 2) {
            return;
        }
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase nextPhrase = (Phrase) enumr.nextElement();
            repeat(nextPhrase, times, startLoc - cphrase.getStartTime(), endLoc
                    - cphrase.getStartTime());
        }
    }

    /**
     * Linearly fades in the CPhrase
     * 
     * <P>
     * If <CODE>cphrase</CODE> is null; or if <CODE>
     * fadeLength</CODE> is less than or equal to zero
     * then this method does nothing.
     * 
     * @param cphrase
     *            CPhrase to be faded
     * @param fadeLength
     *            double describing the time of the
     *            fade, in crotchets.
     */
    public static void fadeIn(CPhrase cphrase, final double fadeLength) {
        if (cphrase == null || fadeLength <= 0.0) {
            return;
        }
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase nextPhrase = (Phrase) enumr.nextElement();
            // make the correction for phrases that
            // don't start at the same time
            // as the CPhrase does
            fadeIn(nextPhrase, fadeLength, nextPhrase.getStartTime());
        }
    }

    /**
     * Linearly fades in the CPhrase.
     * 
     * <P>
     * If <CODE>cphrase</CODE> is null; <CODE>
     * fadeLength</CODE> is less than or equal to zero;
     * <CODE>cphraseStartTime</CODE> is less than
     * zero; or <CODE>fadeLength</CODE> is less than
     * or equal to <CODE>cphraseStartTime</CODE> then
     * this method does nothing.
     * 
     * @param cphrase
     *            CPhrase to be faded
     * @param fadeLength
     *            double describing the time of the
     *            fade, in crotchets
     * @param cpharseStartTime
     *            double describing how far into the
     *            fade the phrase starts.
     */
    public static void fadeIn(CPhrase cphrase, final double fadeLength,
            final double cphraseStartTime) {
        if (cphrase == null || fadeLength < 0.0 || cphraseStartTime < 0.0
                || fadeLength <= cphraseStartTime) {
            return;
        }

        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase nextPhrase = (Phrase) enumr.nextElement();
            // make the correction for phrases that
            // don't start at the same time
            // as the Part does (0.0)
            fadeIn(nextPhrase, fadeLength,
                (cphraseStartTime + nextPhrase.getStartTime()));
        }
    }

    /**
     * Linearly fades out the CPhrase.
     * 
     * <P>
     * If <CODE>cphrase</CODE> is null; or if <CODE>
     * fadeLength</CODE> is less than or equal to zero
     * then this method does nothing.
     * 
     * @param cphrase
     *            CPhrase to be faded
     * @param fadeLength
     *            double describing the time of the fade
     *            out in crotchets
     */
    public static void fadeOut(CPhrase cphrase, final double fadeLength) {
        if (cphrase == null || fadeLength <= 0) {
            return;
        }

        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase nextPhrase = (Phrase) enumr.nextElement();

            // make the correction for phrases that
            // don't end at the same time
            // as the CPhrase does
            fadeOut(nextPhrase, fadeLength,
                (cphrase.getEndTime() - nextPhrase.getEndTime()));
        }
    }

    /**
     * Linearly fades out the CPhrase.
     * 
     * <P>
     * If <CODE>cphrase</CODE> is null; <CODE>
     * fadeLength</CODE> is less than or equal to zero;
     * <CODE>cphraseEndTime</CODE> is less than zero;
     * or <CODE>fadeLength</CODE> is less than <CODE>
     * cphraseEndTime</CODE> then this method does
     * nothing.
     * 
     * @param cphrase
     *            CPhrase to be faded
     * @param fadeLength
     *            double describing the time of the fade
     *            out in crotchets
     * @param phraseEndTime
     *            double describing the length of time,
     *            in crothcets, between the end of the
     *            phrase and the end of the fade.
     */
    public static void fadeOut(CPhrase cphrase, final double fadeLength,
            final double cphraseEndTime) {

        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase nextPhrase = (Phrase) enumr.nextElement();

            // make the correction for phrases that
            // don't end at the same time
            // as the Part does
            fadeOut(nextPhrase, fadeLength, (cphraseEndTime
                    + cphrase.getEndTime() - nextPhrase.getEndTime()));
        }
    }

    /**
     * A compressor/expander routine. Compression ratio
     * numbers between 0 and 1 compress, values larger
     * than 1 expand.
     * 
     * <P>
     * See {@link #compress(Phrase, double)}for further
     * details.
     * 
     * <P>
     * If <CODE>cphrase</CODE> is null then this
     * method does nothing.
     * 
     * @param cphrase
     *            CPhrase to be compressed
     * @param retio
     *            double describing the compression
     *            factor.
     */
    public static void compress(CPhrase cphrase, final double ratio) {
        if (cphrase == null) {
            return;
        }
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            compress((Phrase) enumr.nextElement(), ratio);
        }
    }

    /**
     * Adds a second CPhrase to the end of the first.
     * 
     * <P>
     * If <CODE>cphrase1</CODE> or <CODE>cphrase2
     * </CODE> is null then this method does nothing.
     * 
     * @param cphrase1
     *            the base CPhrase
     * @param cphrase2
     *            CPhrase to be appended
     */
    public static void append(CPhrase cphrase1, final CPhrase cphrase2) {
        if (cphrase1 == null || cphrase2 == null) {
            return;
        }

        // go through the phrases in the new part
        // and add then one by one
        double et = cphrase1.getEndTime();
        Enumeration enumr = cphrase2.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase tempPhrase = (Phrase) enumr.nextElement();
            tempPhrase.setStartTime(et + tempPhrase.getStartTime());
            cphrase1.addPhrase(tempPhrase);
        }
    }

    /**
     * Combines the phrases from a second CPhrase into
     * the first.
     * 
     * <P>
     * If <CODE>cphrase1</CODE> or <CODE>cphrase2
     * </CODE> is null then this method does nothing.
     * 
     * @param cphrase1
     *            the base CPhrase
     * @param cphrase2
     *            the CPhrase to be merged with the
     *            first
     */
    public static void merge(CPhrase cphrase1, final CPhrase cphrase2) {
        if (cphrase1 == null || cphrase2 == null) {
            return;
        }

        // go through the phrases in the provided
        // CPhrase
        // and add then one by one
        Enumeration enumr = cphrase2.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            cphrase1.addPhrase((Phrase) enumr.nextElement());
        }
    }

    /**
     * Quantize the cphrase.
     * 
     * <P>
     * See {@link #quantise(CPhrase, double)}.
     * 
     * @param cphrase
     *            CPhrase to be quantized
     * @param qValue
     *            the amount to quantize to
     */
    public static void quantize(CPhrase cphrase, final double qValue) {
        quantise(cphrase, qValue);
    }

    /**
     * Quantise all the phrases in this CPhrase.
     * 
     * <P>
     * See {@link #quantise(Phrase, double)}for further
     * details.
     * 
     * <P>
     * If <CODE>cphrase</CODE> is null or <CODE>
     * qValue</CODE> is less than or equal to zero then
     * this method does nothing.
     * 
     * @param cphrase
     *            CPhrase to be quantised
     * @param qValue
     *            the amount to quantise to
     */
    public static void quantise(CPhrase cphrase, final double qValue) {
        if (cphrase == null || qValue <= 0.0) {
            return;
        }

        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            quantise((Phrase) enumr.nextElement(), qValue);
        }
    }

    /**
     * Randomly order notes within each phrase.
     * 
     * @param cphrase
     *            -The CPhrase that contains phrases to
     *            be shuffled
     */
    public static void shuffle(CPhrase cphrase) {
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            shuffle((Phrase) enumr.nextElement());
        }
    }

    /**
     * A version of Cycle that works for parts
     * 
     * @param Part
     *            the part to cycle
     * @param double
     *            position of where it should cycle to
     */
    public static void cycle(Part part, double to) {
        // make sure parameters are valid
        if (part == null || to <= 0.0 || to == part.getEndTime()) {
            return;
        }
        double endTime = part.getEndTime();

        // if to is shorter then the actual part, make a
        // truncated copy
        if (to < endTime) {
            Part copy = part.copy(0.0, to);
            part.empty();
            part.addPhraseList(copy.getPhraseArray());
            return;
        } // other wise add cycles of itself to itself

        // go through each whole cycle that needs to be
        // gone through.
        int startPoint = 1;
        double cycleAt = to;
        for (cycleAt = to; (int) (cycleAt / endTime) > 1; cycleAt -= endTime) {
            Phrase[] phrases = part.getPhraseArray(); // go
            // through
            // phrases
            for (int i = 0; i < phrases.length; i++) {
                // setStartTime to make it in phase with
                // that cycle
                phrases[i].setStartTime(phrases[i].getStartTime() + startPoint
                        * endTime);
                part.addPhrase(phrases[i]);
            }
            startPoint++;
        }

        // copy in the remainding stuff in
        double remainder = (to - (startPoint * endTime));

        if (remainder > 0.0) {
            Part copy = part.copy(0.0, remainder, true, true, false);
            Phrase[] phrases = copy.getPhraseArray(); // go
            // through
            // phrases
            for (int i = 0; i < phrases.length; i++) {
                // setStartTime to make it in phase with
                // that cycle
                phrases[i].setStartTime(phrases[i].getStartTime() + startPoint
                        * endTime);
                part.addPhrase(phrases[i]);
            }
        }
    }

    /**
     * Lengthen each note in the CPhrase.
     * 
     * <P>
     * See {@link #elongate(Phrase, double)}for further
     * details.
     * 
     * <P>
     * If <CODE>cphrase</CODE> is null or <CODE>
     * scaleFactor</CODE> is less than or equal to zero
     * then this method does nothing.
     * 
     * @param cphrase
     *            CPhrase to be lengthened
     * @param scaleFactor
     *            double describing the scale factor
     */
    public static void elongate(CPhrase cphrase, final double scaleFactor) {
        if (cphrase == null || scaleFactor <= 0.0) {
            return;
        }
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = ((Phrase) enumr.nextElement());
            elongate(phr, scaleFactor);
            phr.setStartTime(phr.getStartTime() * scaleFactor);
        }
    }

    /**
     * Increase the dynamic of notes at regularly
     * occuring pulse locations which generates the
     * sound of regular meter by emphasising the first
     * beat of each bar/measure.
     * 
     * <P>
     * The dynamic of each note starting on the beat
     * will be increased by 20. If this raises the
     * dynamic above
     * {@link Note#MAX_DYNAMIC MAX_DYNAMIC}then the
     * dynamic will be set to a value as described in
     * {@linkNote#setDynamic}.
     * 
     * <P>
     * If <CODE>cphrase</CODE> is null or <CODE>meter
     * </CODE> is less than or equal to zero then this
     * method does nothing.
     * 
     * @param cphrase
     *            CPhrase to be accented
     * @param meter
     *            double describing the number of
     *            croctets per bar/measure
     */
    public static void accents(CPhrase cphrase, final double meter) {
        double[] beats = { 0.0 };
        accents(cphrase, meter, beats);
    }

    /**
     * Increase the dynamic of notes at regularly
     * occuring pulse locations which generates the
     * sound of regular meter by accenting specified
     * beats within each bar/measure.
     * 
     * <P>
     * <CODE>accentedBeats</CODE> is an array of
     * values describing where the accents begin
     * relative to the start of each bar, in crochets.
     * 
     * <P>
     * Each accented note's dynamic will be increased by
     * 20. If this raises the dynamic above
     * {@link Note#MAX_DYNAMIC MAX_DYNAMIC}then the
     * dynamic will be set to a value as described in
     * {@link Note#setDynamic}.
     * 
     * <P>
     * Each of the double values in <CODE>accentedBeats
     * </CODE> must be greater than or equal to zero and
     * less than <CODE>meter</CODE> or this method
     * does nothing.
     * 
     * <P>
     * If <CODE>cphrase</CODE> is null or <CODE>meter
     * </CODE> is less than or equal to zero then this
     * method does nothing.
     * 
     * @param cphrase
     *            CPhrase to be accented
     * @param meter
     *            double describing the number of
     *            croctets per bar/measure
     * @param accentedBeats
     *            double array describing the time of
     *            the accents in the bar.
     */
    public static void accents(CPhrase cphrase, final double meter,
            final double[] accentedBeats) {
        accents(cphrase, meter, accentedBeats, 20);
    }

    /**
     * Increase the dynamic of notes at regularly
     * occuring pulse locations which generates the
     * sound of regular meter. The meter is the total
     * beat length of the bar/measure, while the array
     * of accented beats is the times within the bar at
     * which an accent should be made. For example, 6/8
     * time would be implied by a meter of 3 and a
     * accent array of {0.0, 1.5}.
     * 
     * <P>
     * <CODE>accentedBeats</CODE> is an array of
     * values describing where the accents begin
     * relative to the start of each bar, in crochets.
     * 
     * <P>
     * Accented beats will have their dynamic increased
     * by <CODE>accentAmount</CODE>. If this causes
     * the dynamic to be above {@linkNote#MAX_DYNAMIC
     * MAX_DYNAMIC} or below
     * {@link Note#MIN_DYNAMICMIN_DYNAMIC}(when <CODE>
     * accentAmount</CODE>) then the dynamic will be
     * set to a value as described by
     * {@link Note#setDynamic}.
     * 
     * <P>
     * Each of the double values in <CODE>accentedBeats
     * </CODE> must be greater than or equal to zero and
     * less than <CODE>meter</CODE> or this method
     * does nothing.
     * 
     * <P>
     * If <CODE>cphrase</CODE> is null or <CODE>meter
     * </CODE> is less than or equal to zero then this
     * method does nothing.
     * 
     * @param cphrase
     *            CPhrase to be accented
     * @param meter
     *            double describing the number of
     *            croctets per bar/measure
     * @param accentedBeats
     *            double array describing the time of
     *            the accents in the bar.
     * @param accentAmount
     *            integer describing the value that the
     *            dynamic of accented beats are
     *            increased by.
     */
    public static void accents(CPhrase cphrase, final double meter,
            final double[] accentedBeats, final int accentAmount) {
        if (cphrase == null || meter <= 0.0) {
            return;
        }
        for (int i = 0; i < accentedBeats.length; i++) {
            if (accentedBeats[i] < 0.0 || accentedBeats[i] >= meter) {
                return;
            }
        }

        // go through the phrases one by one and accent
        // them
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phrase = (Phrase) enumr.nextElement();
            accents(phrase, meter, accentedBeats, accentAmount);
        }
    }

    /**
     * Increases dynamic values so that the loudest is
     * at maxiumim level.
     * 
     * <P>
     * If <CODE>cphrase</CODE> is null then this
     * method does nothing.
     * 
     * @param cphrase
     *            the CPhrase to be effected
     */
    public static void normalise(CPhrase cphrase) {
        if (cphrase == null) {
            return;
        }
        // get the curent max
        int max = 0;
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phrase = (Phrase) enumr.nextElement();
            Enumeration enumr1 = phrase.getNoteList()
                .elements();
            while (enumr1.hasMoreElements()) {
                Note n = (Note) enumr1.nextElement();
                if (n.getDynamic() > max)
                    max = n.getDynamic();
            }
        }
        // increase the normalisation
        if (max == Note.MAX_DYNAMIC) {
            return;
        }
        int diff = Note.MAX_DYNAMIC - max;
        Enumeration enumr3 = cphrase.getPhraseList()
            .elements();
        while (enumr3.hasMoreElements()) {
            Phrase phrase = (Phrase) enumr3.nextElement();
            Enumeration enumr2 = phrase.getNoteList()
                .elements();
            while (enumr2.hasMoreElements()) {
                Note n = (Note) enumr2.nextElement();
                n.setDynamic(n.getDynamic() + diff);
            }
        }
    }

    /**
     * Randomly adjusts all Notes' pan value to create
     * an even spread acroos the stereo spectrum. This
     * process only effects the pan value of the notes
     * in the cphrase.
     * <P>
     * If <CODE>cphrase</CODE> is null then this
     * method does nothing.
     * 
     * @param cphrase
     *            the CPhrase to be effected
     */
    public static void spread(CPhrase cphrase) {
        if (cphrase == null) {
            return;
        }
        int currentValue, newValue;
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            // create new pan value
            Mod.spread(phr);
        }
    }

    /**
     * Adjusts all Notes' pan value to alternate between
     * extreme left and right from note to note. This
     * process only effects the pan value of the notes
     * in the cphrase.
     * <P>
     * If <CODE>cphrase</CODE> is null then this
     * method does nothing.
     * 
     * @param cphrase
     *            The CPhrase to be effected
     */
    public static void bounce(CPhrase cphrase) {
        if (cphrase == null) {
            return;
        }
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            Mod.bounce(phr);
        }
    }

    /**
     * Joins consecutive notes in each <CODE>CPhrase
     * </CODE> that have the same pitch, creating one
     * longer note. This is simmilar to the musical
     * function of a tie. All note in the phrase meeting
     * the conditions are effected. This modification
     * may reduce the overall note count.
     * 
     * @param CPhrase
     *            CPhrase to be processed
     */
    public static void tiePitches(CPhrase cphrase) {
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phrase = (Phrase) enumr.nextElement();
            tiePitches(phrase);
        }
    }

    /**
     * Joins consecutive rests in each <CODE>CPhrase
     * </CODE> creating one longer note. This is
     * simmilar to the musical function of a tie. This
     * modification may reduce the overall rest count.
     * 
     * @param CPhrase
     *            CPhrase to be processed
     */
    public static void tieRests(CPhrase cphrase) {
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phrase = (Phrase) enumr.nextElement();
            tieRests(phrase);
        }
    }

    /**
     * Lengthens notes followed by a rest in the <CODE>
     * CPhrase</CODE> by creating one longer note and
     * deleting the rest. This modification may reduce
     * the overall note count in the phrase, be careful
     * that use of this method does not to cause array
     * out of bounds exceptions.
     * 
     * @param CPhrase
     *            CPhrase to be processed
     */
    public static void fillRests(CPhrase cphrase) {
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phrase = (Phrase) enumr.nextElement();
            fillRests(phrase);
        }
    }

    /**
     * Adjusts all Notes' duration values to vary
     * randomly between specified values from note to
     * note. This process only effects the duration
     * attribute of the notes in the phrase.
     * <P>
     * If <CODE>cphrase</CODE> is null then this
     * method does nothing.
     * 
     * @param cphrase
     *            The CPhrase to be effected
     * @param minlength
     *            The shortest possible duration
     * @param maxlength
     *            The longest possible duration
     */
    public static void varyLength(CPhrase cphrase, double minLength,
            double maxLength) {
        if (cphrase == null || maxLength < minLength) {
            return;
        }
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phrase = (Phrase) enumr.nextElement();
            varyLength(phrase, minLength, maxLength);
        }
    }

    /**
     * Adjusts Note values to any value plus or minus a
     * specified amount. This process can effect pitch,
     * rhythm & duration, dynamic, and pan values of the
     * notes in the cphrase.
     * <P>
     * If <CODE>cphrase</CODE> is null then this
     * method does nothing.
     * 
     * @param cphrase
     *            The CPhrase to be effected
     * @param pitchVariation
     *            The degree of pitch change to apply.
     */
    public static void randomize(CPhrase cphrase, int pitchVariation) {
        randomize(cphrase, pitchVariation, 0.0);
    }

    /**
     * Adjusts Note values to any value plus or minus a
     * specified amount. This process can effect pitch,
     * rhythm & duration, dynamic, and pan values of the
     * notes in the cphrase.
     * <P>
     * If <CODE>cphrase</CODE> is null then this
     * method does nothing.
     * 
     * @param cphrase
     *            The CPhrase to be effected
     * @param pitchVariation
     *            The degree of pitch change to apply.
     * @param rhythmVariation
     *            The degree of rhythm value change to
     *            apply.
     */
    public static void randomize(CPhrase cphrase, int pitchVariation,
            double rhythmVariation) {
        randomize(cphrase, pitchVariation, rhythmVariation, 0);
    }

    /**
     * Adjusts Note values to any value plus or minus a
     * specified amount. This process can effect pitch,
     * rhythm & duration, dynamic, and pan values of the
     * notes in the cphrase.
     * <P>
     * If <CODE>cphrase</CODE> is null then this
     * method does nothing.
     * 
     * @param cphrase
     *            The CPhrase to be effected
     * @param pitchVariation
     *            The degree of pitch change to apply.
     * @param rhythmVariation
     *            The degree of rhythm value change to
     *            apply.
     * @param dynamicVariation
     *            The degree of dynamic change to apply.
     */
    public static void randomize(CPhrase cphrase, int pitchVariation,
            double rhythmVariation, int dynamicVariation) {
        if (cphrase == null) {
            return;
        }
        boolean left = true;
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            randomize(phr, pitchVariation, rhythmVariation, dynamicVariation);
        }
    }

    /**
     * All ascending sequences of the number of notes or
     * more are slured by having thier duration extended
     * to 100% of the rhythm value. This algorithm
     * assumes notes are arranged in the standard
     * monophonic series used by jMusic. Notes with
     * durations longer than rhythm values will produce
     * unintended results.
     * 
     * @param CPhrase
     *            The cphrase to be modified.
     * @param int
     *            The number of notes in a row to
     *            consitute a sequence
     */
    public static void slurUp(CPhrase cphrase, int numberOfNotes) {
        if (cphrase == null) {
            return;
        }
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            slurUp(phr, numberOfNotes);
        }
    }

    /**
     * All descending sequences of the number of notes
     * or more are slured by having thier duration
     * extended to 100% of the rhythm value. This
     * algorithm assumes notes are arranged in the
     * standard monophonic series used by jMusic. Notes
     * with durations longer than rhythm values will
     * produce unintended results.
     * 
     * @param CPhrase
     *            The cphrase to be modified.
     * @param int
     *            The number of notes in a row to
     *            consitute a sequence
     */
    public static void slurDown(CPhrase cphrase, int numberOfNotes) {
        if (cphrase == null) {
            return;
        }
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            slurDown(phr, numberOfNotes);
        }
    }

    /**
     * Vary the duration of each note in the phrase by
     * the multiplyer.
     * 
     * @param CPhrase
     *            The cphrase to be modified.
     * @param double
     *            The amount to multiply the duration
     *            by.
     */
    public static void increaseDuration(CPhrase cphrase, double multiplyer) {
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            increaseDuration(phr, multiplyer);
        }
    }

    /**
     * Vary the duration of each note in the cphrase by
     * the specified amount.
     * 
     * @param cphrase -
     *            The cphrase to be modified.
     * @param amount -
     *            The number of beats to add to the
     *            duration.
     */
    public static void addToDuration(CPhrase cphrase, double amount) {
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            addToDuration(phr, amount);
        }
    }

    /**
     * Vary the rhythm value of each note in the cphrase
     * by the specified amount.
     * 
     * @param cphrase -
     *            The phrase to be modified.
     * @param amount -
     *            The number of beats to add.
     */
    public static void addToRhythmValue(CPhrase cphrase, double amount) {
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            addToRhythmValue(phr, amount);

        }
    }

    /**
     * Vary both the rhythm value and duration of each
     * note in the phrase by the specified amount.
     * 
     * @param cphrase -
     *            The cphrase to be modified.
     * @param amount -
     *            The number of beats to add.
     */
    public static void addToLength(CPhrase cphrase, double amount) {
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            addToLength(phr, amount);
        }
    }

    /**
     * Vary the interval between notes scaling by the
     * specified amount to each interval.
     * 
     * @param cphrase -
     *            The CPhrase to be modified.
     * @param amount -
     *            The scaling multiplyer for the
     *            intervals, i.e., 2.0 doubles width.
     */
    public static void expandIntervals(CPhrase cphrase, double amount) {
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            expandIntervals(phr, amount);
        }
    }

    /**
     * Randomise the dynamic values of notes up to a
     * specified amount either side of the current
     * value.
     * 
     * @param cphrase -
     *            The CPhrase to be modified.
     * @param amount -
     *            The dynamic change possible either
     *            side of the curent dynamic.
     */
    public static void shake(CPhrase cphrase, int amount) {
        Enumeration enumr = cphrase.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            shake(phr, amount);
        }
    }

    // ------------------------ PART MODIFICATIONS
    // ----------------------------//
    /**
     * Makes the Part twice as long by repeating it
     * once.
     * <P>
     * If <CODE>part</CODE> is null this method does
     * nothing.
     * 
     * @param part
     *            Part to be repeated
     */
    public static void repeat(Part part) {
        repeat(part, 2);
    }

    /**
     * Makes the Part n times as long by repeating.
     * 
     * <P>
     * If <CODE>part</CODE> is null or <CODE>times
     * </CODE> is less than one this method does
     * nothing.
     * 
     * @param cphrase
     *            Part to be repeated
     * @param times
     *            number of repeats (default is 1)
     */
    public static void repeat(Part part, final int times) {
        if (part == null) {
            return;
        }

        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase nextPhrase = (Phrase) enumr.nextElement();
            repeat(nextPhrase, times);
        }

        /*
         * // Alternate logic: Repeats the phase rather
         * than lengthens the phrase int initialLength =
         * part.getPhraseList().size(); for (int t = 0;
         * t < (times - 1); t++){ double initialEndTime =
         * part.getEndTime(); for(int i = 0; i <
         * initialLength; i++) { Phrase phr =
         * (Phrase)part.getPhraseList().elementAt(i);
         * Phrase phrCopy = phr.copy();
         * phrCopy.setStartTime(initialEndTime +
         * phr.getStartTime()); part.addPhrase(phrCopy); } }
         */
    }

    /**
     * Loops a section of a the Part once
     * 
     * <P>
     * If <CODE>part</CODE> is null; or <CODE>
     * startLoc</CODE> is greater than or equal to
     * <CODE>endLoc</CODE> then this method does
     * nothing.
     * 
     * @param part
     *            Part to be repeated
     * @param startLoc
     *            location of the loop start in beats
     * @param endLoc
     *            location of the loop end in beats
     */
    public static void repeat(Part part, final double startLoc,
            final double endLoc) {
        repeat(part, 2, startLoc, endLoc);
    }

    /**
     * Loops a section of the Part n times.
     * 
     * <P>
     * If <CODE>part</CODE> is null; <CODE>startLoc
     * </CODE> is greater than or equal to <CODE>endLoc
     * </CODE>; or <CODE>n</CODE> is less than one
     * then this method does nothing.
     * 
     * @param part
     *            Part to be repeated
     * @param times
     *            int number of repeats (default is 1)
     * @param double
     *            location of the loop start in beats
     * @param double
     *            location of the loop end in beats
     */
    public static void repeat(Part part, final int times,
            final double startLoc, final double endLoc) {
        if (part == null || startLoc >= endLoc || times < 2) {
            return;
        }
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase nextPhrase = (Phrase) enumr.nextElement();
            repeat(nextPhrase, times, startLoc, endLoc);
        }
    }

    /**
     * Transpose the part up or down in semitone steps.
     * This transposition is chromatic, not diatonic.
     * 
     * @param part
     *            the Part to be transposed
     * @param transposition
     *            the number of semitone steps to shift
     *            the pitch
     */
    public static void transpose(Part part, final int transposition) {
        if (part == null || transposition == 0)
            return;
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            transpose(phr, transposition);
        }
    }

    /**
     * Transpose the Part up or down in scale degrees.
     * 
     * <P>
     * If <CODE>part</CODE> is null then this method
     * does nothing. If the transposition shifts a pitch
     * above {@link Note#MAX_PITCH MAX_PITCH}or below
     * {@link Note#MIN_PITCH MIN_PITCH}, the pitch will
     * probably cap at those values. See the description
     * of {@link Note#setPitch}method for exact details
     * of what occurs when trying to set the pitch
     * beyond the allowed range.
     * 
     * Transposition is in diatonic steps. For example
     * in C major the note C transposed 1 will become D,
     * transposed 4 will become G, and transposed by 7
     * will beome C an octave above. This can be
     * somewhat unintuitive so be careful.
     * 
     * @param part
     *            Part to be transposed
     * @param degrees
     *            the number of scale degrees to
     *            transpose
     * @param mode
     *            the scale to use for the transposition
     * @param key
     *            the chromatic note to be used as the
     *            rooth of the mode. i.e., 0 = C, 1 = C#
     *            etc.
     */
    public static void transpose(Part part, final int degrees,
            final int[] mode, int key) {
        if (part == null) {
            return;
        }

        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            transpose((Phrase) enumr.nextElement(), degrees, mode, key);
        }
    }

    /**
     * A compressor/expander routine. Compression ratio
     * numbers between 0 and 1 compress, values larger
     * than 1 expand.
     * 
     * <P>
     * This is a basic compression routine and is not
     * perfect. Specifically, each phrase compresses
     * relative to itself, not to the overall part as
     * might be expected.
     * 
     * <P>
     * If <CODE>part</CODE> is null then this method
     * does nothing.
     * 
     * @param part
     *            Part to be compressed
     * @param ratio
     *            double describing the compression
     *            factor
     */
    public static void compress(Part part, final double ratio) {
        if (part == null) {
            return;
        }

        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phrase = (Phrase) enumr.nextElement();
            compress(phrase, ratio);
        }
    }

    /**
     * Adds a second part to the end of this one.
     * 
     * <P>
     * If <CODE>part1</CODE> or <CODE>part2</CODE>
     * is null then this method does nothing.
     * 
     * @param part1
     *            the base Part
     * @param part2
     *            the Part to be appended
     */
    public static void append(Part part1, final Part part2) {
        append(part1, part2, part1.getEndTime());
    }

    /**
     * Adds a second part to this one starting from a
     * specified location
     * 
     * <P>
     * If <CODE>part1</CODE> or <CODE>part2</CODE>
     * is null
     * 
     * @param part1
     *            the base Part
     * @param part2
     *            the Part to be appended
     * @param fromLoc
     *            double describing the start time for
     *            the second part to be appended
     */
    public static void append(Part part1, final Part part2, final double fromLoc) {
        if (part1 == null || part2 == null) {
            return;
        }

        // go through the phrases in the new part
        // and add then one by one
        Enumeration enumr = part2.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase tempPhrase = ((Phrase) enumr.nextElement()).copy();
            tempPhrase.setStartTime(fromLoc + tempPhrase.getStartTime());
            if (tempPhrase.getInstrument() == part1.getInstrument())
                tempPhrase.setInstrument(Phrase.DEFAULT_INSTRUMENT);
            part1.addPhrase(tempPhrase);
        }
    }

    /**
     * Increases the dynamic by a certain amount -<br>
     * obviously a negative number will decrease it.
     * 
     * @param Part
     *            the part that is to be affected
     * @param int
     *            the amount that it is to be affected
     *            by
     */
    public static void increaseDynamic(Part p, int amount) {
        try {
            if (p == null)
                new NullPointerException();
        } catch (NullPointerException e) {
            e.toString();
            return;
        }

        Enumeration enumr = p.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            increaseDynamic(phr, amount);
        }
    }

    /**
     * Linearly fades in the Part.
     * 
     * <P>
     * If <CODE>part</CODE> is null; or if <CODE>
     * fadeLength</CODE> is less than or equal to zero
     * then this method does nothing.
     * 
     * @param part
     *            Part to be faded
     * @param fadelength
     *            double describing the number of
     *            crotchets to fade over
     */
    public static void fadeIn(Part part, final double fadeLength) {
        if (part == null || fadeLength <= 0.0) {
            return;
        }

        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase nextPhrase = (Phrase) enumr.nextElement();

            // make the correction for phrases that
            // don't start at the same time
            // as the Part does (0.0)
            fadeIn(nextPhrase, fadeLength, nextPhrase.getStartTime());
        }
    }

    /**
     * Linearly fades in the Part.
     * 
     * <P>
     * If <CODE>part</CODE> is null; <CODE>fadeLength
     * </CODE> is less than or equal to zero; <CODE>
     * phraseStart</CODE> is less than zero; or <CODE>
     * fadeLength</CODE> is less than or equal to
     * <CODE>phraseStart</CODE> then this method does
     * nothing.
     * 
     * @param part
     *            Part to be faded
     * @param fadeLength
     *            double describing the duration of the
     *            fade, in crotchets
     * @param partStartTime
     *            double describing how far into the
     *            fade the part starts
     */
    public static void fadeIn(Part part, final double fadeLength,
            final double partStartTime) {
        if (part == null || fadeLength <= 0.0 || partStartTime < 0.0) {
            return;
        }

        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase nextPhrase = (Phrase) enumr.nextElement();
            // make the correction for phrases that
            // don't start at the same time
            // as the Part does (0.0)
            fadeIn(nextPhrase, fadeLength,
                (partStartTime + nextPhrase.getStartTime()));
        }
    }

    /**
     * Linearly fades out the Part.
     * 
     * <P>
     * If <CODE>part</CODE> is null; or if <CODE>
     * fadeLength</CODE> is less than or equal to zero
     * then this method does nothing.
     * 
     * @param part
     *            Part to be faded
     * @param fadeLength
     *            double describing the duration of the
     *            fade out in crotchets
     */
    public static void fadeOut(Part part, final double fadeLength) {
        if (part == null || fadeLength <= 0.0) {
            return;
        }

        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase nextPhrase = (Phrase) enumr.nextElement();
            // make the correction for phrases that
            // don't end at the same time
            // as the Part does
            fadeOut(nextPhrase, fadeLength,
                (part.getEndTime() - nextPhrase.getEndTime()));
        }
    }

    /**
     * Linearly fades out the Part.
     * 
     * <P>
     * If <CODE>part</CODE> is null; <CODE>fadeLength
     * </CODE> is less than or equal to zero; <CODE>
     * phraseEndTime</CODE> is less than zero; or
     * <CODE>fadeLength</CODE> is less than <CODE>
     * phraseEndTime</CODE> then this method does
     * nothing.
     * 
     * @param part
     *            Part to be faded
     * @param fadeLength
     *            double describing the duration of the
     *            fade out in crotchets
     * @param partEndTime
     *            double describing the length of time,
     *            in crotchets, between the end of the
     *            part and the end of the fade.
     */
    public static void fadeOut(Part part, final double fadeLength,
            final double partEndTime) {
        if (part == null || fadeLength <= 0.0 || partEndTime < 0.0) {
            return;
        }

        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase nextPhrase = (Phrase) enumr.nextElement();
            // make the correction for phrases that
            // don't end at the same time
            // as the Part does
            fadeOut(nextPhrase, fadeLength,
                (partEndTime + part.getEndTime() - nextPhrase.getEndTime()));
        }
    }

    /**
     * Combines the phrases from a second part into this
     * one.
     * 
     * <P>
     * If <CODE>part1</CODE> or <CODE>part2</CODE>
     * is null this method does nothing.
     * 
     * @param part1
     *            the base Part
     * @param part2
     *            the Part to be merged to the first
     */
    public static void merge(Part part1, final Part part2) {
        if (part1 == null || part2 == null) {
            return;
        }

        // go through the phrases in the new part
        // and add then one by one
        Enumeration enumr = part2.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            part1.addPhrase((Phrase) enumr.nextElement());
        }
    }

    /**
     * Quantize the <CODE>part</CODE>.
     * 
     * <P>
     * See {@link #quantise(Part, double)}.
     * 
     * @param part
     *            Part to be quantized
     * @param qValue
     *            the amount to quantize to
     */
    public static void quantize(Part part, final double qValue) {
        quantise(part, qValue);
    }

    /**
     * Quantise all the phrases in this part.
     * 
     * <P>
     * See {@link #quantise(Phrase, double)}for
     * details.
     * 
     * <P>
     * If <CODE>part</CODE> is null or <CODE>qValue
     * </CODE> is less than or equal to zero then this
     * method does nothing.
     * 
     * @param part
     *            Part to be quantised
     * @param double -
     *            the amount to quantise too
     */
    public static void quantise(Part part, final double qValue) {
        if (part == null || qValue <= 0.0) {
            return;
        }

        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phrase = (Phrase) enumr.nextElement();
            quantise(phrase, qValue);
        }
    }

    public static void quantiseRT(Part pa, final double qv) {
        for (int i = 0; i < pa.size(); i++) {
            pa.getPhrase(i)
                .getStartTime();
        }
    }

    /**
     * Randomly order notes within each phrase.
     * 
     * @param part
     *            -The Part that contains phrases to be
     *            shuffled
     */
    public static void shuffle(Part part) {
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            shuffle((Phrase) enumr.nextElement());
        }
    }

    /**
     * Increase the dynamic of notes at regularly
     * occuring pulse locations which generates the
     * sound of regular meter by emphasising the first
     * beat of each bar/measure.
     * 
     * <P>
     * The dynamic of each note starting on the beat
     * will be increased by 20. If this raises the
     * dynamic above
     * {@link Note#MAX_DYNAMIC MAX_DYNAMIC}then the
     * dynamic will be set to a value as described in
     * {@linkNote#setDynamic}.
     * 
     * <P>
     * If <CODE>part</CODE> is null or <CODE>meter
     * </CODE> is less than or equal to zero then this
     * method does nothing.
     * 
     * @param part
     *            Part to be accented
     * @param meter
     *            double describing the number of
     *            croctets per bar/measure
     */
    public static void accents(Part part, final double meter) {
        double[] beats = { 0.0 };
        accents(part, meter, beats);
    }

    /**
     * Increase the dynamic of notes at regularly
     * occuring pulse locations which generates the
     * sound of regular meter by accenting specified
     * beats within each bar/measure.
     * 
     * <P>
     * <CODE>accentedBeats</CODE> is an array of
     * values describing where the accents begin
     * relative to the start of each bar, in crochets.
     * 
     * <P>
     * Each accented note's dynamic will be increased by
     * 20. If this raises the dynamic above
     * {@link Note#MAX_DYNAMIC MAX_DYNAMIC}then the
     * dynamic will be set to a value as described in
     * {@link Note#setDynamic}.
     * 
     * <P>
     * Each of the double values in <CODE>accentedBeats
     * </CODE> must be greater than or equal to zero and
     * less than <CODE>meter</CODE> or this method
     * does nothing.
     * 
     * <P>
     * If <CODE>part</CODE> is null or <CODE>meter
     * </CODE> is less than or equal to zero then this
     * method does nothing.
     * 
     * @param part
     *            Part to be accented
     * @param meter
     *            double describing the number of
     *            croctets per bar/measure
     * @param accentedBeats
     *            double array describing the time of
     *            the accents in the bar.
     */
    public static void accents(Part part, double meter, double[] accentedBeats) {
        accents(part, meter, accentedBeats, 20);
    }

    /**
     * Increase the dynamic of notes at regularly
     * occuring pulse locations which generates the
     * sound of regular meter. The meter is the total
     * beat length of the bar/measure, while the array
     * of accented beats is the times within the bar at
     * which an accent should be made. For example, 6/8
     * time would be implied by a meter of 3 and a
     * accent array of {0.0, 1.5}.
     * 
     * <P>
     * <CODE>accentedBeats</CODE> is an array of
     * values describing where the accents begin
     * relative to the start of each bar, in crochets.
     * 
     * <P>
     * Accented beats will have their dynamic increased
     * by <CODE>accentAmount</CODE>. If this causes
     * the dynamic to be above {@linkNote#MAX_DYNAMIC
     * MAX_DYNAMIC} or below
     * {@link Note#MIN_DYNAMICMIN_DYNAMIC}(when <CODE>
     * accentAmount</CODE>) then the dynamic will be
     * set to a value as described by
     * {@link Note#setDynamic}.
     * 
     * <P>
     * Each of the double values in <CODE>accentedBeats
     * </CODE> must be greater than or equal to zero and
     * less than <CODE>meter</CODE> or this method
     * does nothing.
     * 
     * <P>
     * If <CODE>part</CODE> is null or <CODE>meter
     * </CODE> is less than or equal to zero then this
     * method does nothing.
     * 
     * @param part
     *            Part to be accented
     * @param meter
     *            double describing the number of
     *            croctets per bar/measure
     * @param accentedBeats
     *            double array describing the time of
     *            the accents in the bar.
     * @param accentAmount
     *            integer describing the value that the
     *            dynamic of accented beats are
     *            increased by.
     */
    public static void accents(Part part, final double meter,
            final double[] accentedBeats, final int accentAmount) {
        if (part == null || meter <= 0.0) {
            return;
        }
        for (int i = 0; i < accentedBeats.length; i++) {
            if (accentedBeats[i] < 0.0 || accentedBeats[i] >= meter) {
                return;
            }
        }

        // go through the phrases one by one and accent
        // them
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phrase = (Phrase) enumr.nextElement();
            accents(phrase, meter, accentedBeats, accentAmount);
        }
    }

    /**
     * Increases dynamic values so that the loudest is
     * at maxiumim level.
     * 
     * <P>
     * If <CODE>part</CODE> is null then this method
     * does nothing.
     * 
     * @param part
     *            the Part to be effected
     */
    public static void normalise(Part part) {
        if (part == null) {
            return;
        }
        // get the curent max
        int max = 0;
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phrase = (Phrase) enumr.nextElement();
            Enumeration enumr1 = phrase.getNoteList()
                .elements();
            while (enumr1.hasMoreElements()) {
                Note n = (Note) enumr1.nextElement();
                if (n.getDynamic() > max)
                    max = n.getDynamic();
            }
        }
        // increase the normalisation
        if (max == Note.MAX_DYNAMIC) {
            return;
        }
        int diff = Note.MAX_DYNAMIC - max;
        Enumeration enumr3 = part.getPhraseList()
            .elements();
        while (enumr3.hasMoreElements()) {
            Phrase phrase = (Phrase) enumr3.nextElement();
            Enumeration enumr2 = phrase.getNoteList()
                .elements();
            while (enumr2.hasMoreElements()) {
                Note n = (Note) enumr2.nextElement();
                n.setDynamic(n.getDynamic() + diff);
            }
        }
    }

    /**
     * Stretch the time of each note in each phrase by
     * the <CODE>scaleFactor</CODE>
     * 
     * <P>
     * If <CODE>phrase</CODE> is null or <CODE>
     * scaleFactor</CODE> is less than or equal to zero
     * then this method does nothing.
     * 
     * @param part
     *            Part to be lengthened
     * @param scaleFactor
     *            double describing the scale factor
     */
    public static void elongate(Part part, double scaleFactor) {
        if (part == null || scaleFactor <= 0.0) {
            return;
        }
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phrase = (Phrase) enumr.nextElement();
            elongate(phrase, scaleFactor);
        }
    }

    /**
     * Joins consecutive notes in each <CODE>phrase
     * </CODE> that have the same pitch, creating one
     * longer note. This is simmilar to the musical
     * function of a tie. All note in the phrase meeting
     * the conditions are effected. This modification
     * may reduce the overall note count.
     * 
     * @param Part
     *            Part to be processed
     */
    public static void tiePitches(Part part) {
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phrase = (Phrase) enumr.nextElement();
            tiePitches(phrase);
        }
    }

    /**
     * Joins consecutive rests in each <CODE>part
     * </CODE> creating one longer note. This is
     * simmilar to the musical function of a tie. This
     * modification may reduce the overall rest count.
     * 
     * @param part
     *            Part to be processed
     */
    public static void tieRests(Part part) {
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phrase = (Phrase) enumr.nextElement();
            tieRests(phrase);
        }
    }

    /**
     * Lengthens notes followed by a rest in the <CODE>
     * part</CODE> by creating one longer note and
     * deleting the rest. This modification may reduce
     * the overall note count in the phrase, be careful
     * that use of this method does not to cause array
     * out of bounds exceptions.
     * 
     * @param Part
     *            Part to be processed
     */
    public static void fillRests(Part part) {
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phrase = (Phrase) enumr.nextElement();
            fillRests(phrase);
        }
    }

    /**
     * Randomly adjusts all Notes' pan value to create
     * an even spread acroos the stereo spectrum. This
     * process only effects the pan value of the notes
     * in the part.
     * <P>
     * If <CODE>part</CODE> is null then this method
     * does nothing.
     * 
     * @param part
     *            the Part to be effected
     */
    public static void spread(Part part) {
        if (part == null) {
            return;
        }
        int currentValue, newValue;
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            // create new pan value
            Mod.spread(phr);
        }
    }

    /**
     * Adjusts all Notes' pan value to alternate between
     * extreme left and right from note to note. This
     * process only effects the pan value of the notes
     * in the part.
     * <P>
     * If <CODE>part</CODE> is null then this method
     * does nothing.
     * 
     * @param part
     *            The Part to be effected
     */
    public static void bounce(Part part) {
        if (part == null) {
            return;
        }
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            Mod.bounce(phr);
        }
    }

    /**
     * Adjusts all Notes' duration values to vary
     * randomly between specified values from note to
     * note. This process only effects the duration
     * attribute of the notes in the phrase.
     * <P>
     * If <CODE>Part</CODE> is null then this method
     * does nothing.
     * 
     * @param part
     *            The Part to be effected
     * @param minlength
     *            The shortest possible duration
     * @param maxlength
     *            The longest possible duration
     */
    public static void varyLength(Part part, double minLength, double maxLength) {
        if (part == null || maxLength < minLength) {
            return;
        }
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phrase = (Phrase) enumr.nextElement();
            varyLength(phrase, minLength, maxLength);
        }
    }

    /**
     * Adjusts Note values to any value plus or minus a
     * specified amount. This process can effect pitch,
     * rhythm & duration, dynamic, and pan values of the
     * notes in the part.
     * <P>
     * If <CODE>Part</CODE> is null then this method
     * does nothing.
     * 
     * @param part
     *            The Part to be effected
     * @param pitchVariation
     *            The degree of pitch change to apply.
     */
    public static void randomize(Part part, int pitchVariation) {
        randomize(part, pitchVariation, 0.0);
    }

    /**
     * Adjusts Note values to any value plus or minus a
     * specified amount. This process can effect pitch,
     * rhythm & duration, dynamic, and pan values of the
     * notes in the part.
     * <P>
     * If <CODE>Part</CODE> is null then this method
     * does nothing.
     * 
     * @param part
     *            The Part to be effected
     * @param pitchVariation
     *            The degree of pitch change to apply.
     * @param rhythmVariation
     *            The degree of rhythm value change to
     *            apply.
     */
    public static void randomize(Part part, int pitchVariation,
            double rhythmVariation) {
        randomize(part, pitchVariation, rhythmVariation, 0);
    }

    /**
     * Adjusts Note values to any value plus or minus a
     * specified amount. This process can effect pitch,
     * rhythm & duration, dynamic, and pan values of the
     * notes in the part.
     * <P>
     * If <CODE>Part</CODE> is null then this method
     * does nothing.
     * 
     * @param part
     *            The Part to be effected
     * @param pitchVariation
     *            The degree of pitch change to apply.
     * @param rhythmVariation
     *            The degree of rhythm value change to
     *            apply.
     * @param dynamicVariation
     *            The degree of dynamic change to apply.
     */
    public static void randomize(Part part, int pitchVariation,
            double rhythmVariation, int dynamicVariation) {
        if (part == null) {
            return;
        }
        boolean left = true;
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            randomize(phr, pitchVariation, rhythmVariation, dynamicVariation);
        }
    }

    /**
     * All ascending sequences of the number of notes or
     * more are slured by having thier duration extended
     * to 100% of the rhythm value. This algorithm
     * assumes notes are arranged in the standard
     * monophonic series used by jMusic. Notes with
     * durations longer than rhythm values will produce
     * unintended results.
     * 
     * @param Part
     *            The part to be modified.
     * @param int
     *            The number of notes in a row to
     *            consitute a sequence
     */
    public static void slurUp(Part part, int numberOfNotes) {
        if (part == null) {
            return;
        }
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            slurUp(phr, numberOfNotes);
        }
    }

    /**
     * All descending sequences of the number of notes
     * or more are slured by having thier duration
     * extended to 100% of the rhythm value. This
     * algorithm assumes notes are arranged in the
     * standard monophonic series used by jMusic. Notes
     * with durations longer than rhythm values will
     * produce unintended results.
     * 
     * @param Part
     *            The part to be modified.
     * @param int
     *            The number of notes in a row to
     *            consitute a sequence
     */
    public static void slurDown(Part part, int numberOfNotes) {
        if (part == null) {
            return;
        }
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            slurDown(phr, numberOfNotes);
        }
    }

    /**
     * Vary the duration of each note in the part by the
     * multiplyer.
     * 
     * @param Part
     *            The part to be modified.
     * @param double
     *            The amount to multiply the duration
     *            by.
     */
    public static void increaseDuration(Part part, double multiplyer) {
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            increaseDuration(phr, multiplyer);
        }
    }

    /**
     * Vary the duration of each note in the part by the
     * specified amount.
     * 
     * @param part -
     *            The Part to be modified.
     * @param amount -
     *            The number of beats to add to the
     *            duration.
     */
    public static void addToDuration(Part part, double amount) {
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            addToDuration(phr, amount);
        }
    }

    /**
     * Vary the rhythm value of each note in the part by
     * the specified amount.
     * 
     * @param part -
     *            The Part to be modified.
     * @param amount -
     *            The number of beats to add.
     */
    public static void addToRhythmValue(Part part, double amount) {
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            addToRhythmValue(phr, amount);

        }
    }

    /**
     * Vary both the rhythm value and duration of each
     * note in the part by the specified amount.
     * 
     * @param part -
     *            The Part to be modified.
     * @param amount -
     *            The number of beats to add.
     */
    public static void addToLength(Part part, double amount) {
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            addToLength(phr, amount);
        }
    }

    /**
     * Vary the interval between notes scaling by the
     * specified amount to each interval.
     * 
     * @param part -
     *            The Part to be modified.
     * @param amount -
     *            The scaling multiplyer for the
     *            intervals, i.e., 2.0 doubles width.
     */
    public static void expandIntervals(Part part, double amount) {
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            expandIntervals(phr, amount);
        }
    }

    /**
     * Randomise the dynamic values of notes up to a
     * specified amount either side of the current
     * value.
     * 
     * @param part -
     *            The Part to be modified.
     * @param amount -
     *            The dynamic change possible either
     *            side of the curent dynamic.
     */
    public static void shake(Part part, int amount) {
        Enumeration enumr = part.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            Phrase phr = (Phrase) enumr.nextElement();
            shake(phr, amount);
        }
    }

    // ------------------------ SCORE MODIFICATIONS
    // ---------------------------//

    /**
     * Transpose a Score
     * 
     * @param score
     *            Score to be faded
     * @param transposition
     *            The number of semitones to transpose
     *            by
     * 
     */
    public static void transpose(Score scr, final int transposition) {
        if (scr == null || transposition == 0)
            return;
        Enumeration enumr = scr.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part part = (Part) enumr.nextElement();
            transpose(part, transposition);
        }
    }

    /**
     * Transpose the score up or down in scale degrees.
     * 
     * <P>
     * If <CODE>score</CODE> is null then this method
     * does nothing. If the transposition shifts a pitch
     * above {@link Note#MAX_PITCH MAX_PITCH}or below
     * {@link Note#MIN_PITCH MIN_PITCH}, the pitch will
     * probably cap at those values. See the description
     * of {@link Note#setPitch}method for exact details
     * of what occurs when trying to set the pitch
     * beyond the allowed range.
     * 
     * Transposition is in diatonic steps. For example
     * in C major the note C transposed 1 will become D,
     * transposed 4 will become G, and transposed by 7
     * will beome C an octave above. This can be
     * somewhat unintuitive so be careful.
     * 
     * @param score
     *            Score to be transposed
     * @param degrees
     *            the amount to transpose in scale steps
     * @param mode
     *            the scale to use for the transposition
     * @param key
     *            the chromatic note to be used as the
     *            rooth of the mode. i.e., 0 = C, 1 = C#
     *            etc.
     */
    public static void transpose(Score score, final int degrees,
            final int[] mode, int key) {
        if (score == null) {
            return;
        }

        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            transpose((Part) enumr.nextElement(), degrees, mode, key);
        }
    }

    /**
     * Linearly fades in the Score.
     * 
     * <P>
     * See {@link #fadeIn(Phrase, double)}.
     * 
     * <P>
     * If <CODE>score</CODE> is null or <CODE>
     * fadeLength</CODE> is less than or equal to zero
     * then this method does nothing.
     * 
     * @param score
     *            Score to be faded
     * @param fadelength
     *            double describing the number of
     *            crotchets to fade over
     */
    public static void fadeIn(Score score, final double fadeLength) {
        if (score == null || fadeLength <= 0.0) {
            return;
        }

        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part nextPart = (Part) enumr.nextElement();
            fadeIn(nextPart, fadeLength);
        }
    }

    /**
     * increases the dynamic by a certain amount -<br>
     * obviously a negative number will decrease it
     * 
     * @param Score
     *            the score that is to be affected
     * @param int
     *            the amount
     * 
     */
    public static void increaseDynamic(Score s, int amount) {
        try {
            if (s == null)
                new NullPointerException();
        } catch (NullPointerException e) {
            e.toString();
            return;
        }

        Enumeration enumr = s.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part p = (Part) enumr.nextElement();
            increaseDynamic(p, amount);
        }
    }

    /**
     * Linearly fades out the Score.
     * 
     * <P>
     * See {@link #fadeOut(Phrase, double)}.
     * 
     * <P>
     * If <CODE>score</CODE> is null or <CODE>
     * fadeLength</CODE> is less than or equal to zero
     * then this method does nothing.
     * 
     * @param score
     *            Score to be faded
     * @param fadelength
     *            double describing the number of
     *            crotchets to fade over
     */
    public static void fadeOut(Score score, final double fadeLength) {
        if (score == null || fadeLength <= 0.0) {
            return;
        }

        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part nextPart = (Part) enumr.nextElement();
            // make the correction for Parts that don't
            // end at the same time as
            // the Score does
            fadeOut(nextPart, fadeLength,
                (score.getEndTime() - nextPart.getEndTime()));
        }
    }

    /**
     * A compressor/expander routine. Compression ratio
     * numbers between 0 and 1 compress, values larger
     * than 1 expand.
     * 
     * <P>
     * See {@link #compress(Part, double)}.
     * 
     * <P>
     * If <CODE>score</CODE> is null then this method
     * does nothing.
     * 
     * @param score
     *            Score to be expanded/compressed
     * @param ratio
     *            double describing the compression
     *            factor
     */
    public static void compress(Score score, final double ratio) {
        if (score == null) {
            return;
        }

        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part part = (Part) enumr.nextElement();
            compress(part, ratio);
        }
    }

    /**
     * Makes a the <CODE>score </SCORE> twice times as
     * long by repeating
     * 
     * <P>
     * If <CODE>score</CODE> is null then this method
     * does nothing.
     * 
     * @param score
     *            Score to be repeated
     */
    public static void repeat(Score score) {
        repeat(score, 2);
    }

    /**
     * Loops the <CODE>score</CODE> <CODE>n</CODE>
     * times.
     * 
     * <P>
     * If <CODE>score</CODE> is null or <CODE>times
     * </CODE> is less than one then this method does
     * nothing.
     * 
     * @param score
     *            Score to be repeated
     * @param times
     *            integer representing the number of
     *            repeats
     */
    public static void repeat(Score score, final int times) {
        if (score == null || times < 2) {
            return;
        }

        // get the longest end time
        double maxEndTime = 0.0;
        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part part = (Part) enumr.nextElement();
            if (maxEndTime < part.getEndTime())
                maxEndTime = part.getEndTime();
        }

        for (int i = 0; i < score.getPartList()
            .size(); i++) {
            Part p = (Part) score.getPartList()
                .elementAt(i);
            int numbOfPhrases = p.getPhraseList()
                .size();
            for (int t = 0; t < (times - 1); t++) {
                double initialEndTime = maxEndTime * (t + 1);
                for (int j = 0; j < numbOfPhrases; j++) {
                    Phrase phr = (Phrase) p.getPhraseList()
                        .elementAt(j);
                    Phrase phrCopy = phr.copy();
                    phrCopy.setStartTime(initialEndTime + phr.getStartTime());
                    p.addPhrase(phrCopy);
                }
            }
        }
    }

    /**
     * Adds a second score to the end of this one.
     * 
     * <P>
     * If <CODE>score1</CODE> or <CODE>score2</CODE>
     * is null then this method does nothing.
     * 
     * @param score1
     *            the base Score
     * @param score2
     *            the Score to be appended
     */
    public static void append(Score score1, final Score score2) {
        if (score1 == null || score2 == null) {
            return;
        }

        score2.clean();
        if (score2.size() == 0)
            return;

        double endTime = score1.getEndTime();

        Enumeration enumr = score2.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part currPart = (Part) enumr.nextElement();
            // update start times and program changes
            Enumeration enumr2 = currPart.getPhraseList()
                .elements();
            while (enumr2.hasMoreElements()) {
                Phrase currPhrase = (Phrase) enumr2.nextElement();
                currPhrase.setStartTime(currPhrase.getStartTime() + endTime);
                if (currPhrase.getInstrument() != 250
                        && currPhrase.getInstrument() != currPart.getInstrument())
                    currPhrase.setInstrument(currPart.getInstrument());
                if (currPhrase.getInstrument() == currPart.getInstrument())
                    currPhrase.setInstrument(Phrase.DEFAULT_INSTRUMENT);
            }
        }

        // add to score1
        Mod.merge(score1, score2);
    }

    /**
     * Combines the parts from a second score into the
     * first one. Parts on an existing channel will have
     * phases extracted and added to that part,
     * otherwise a part with a unique channel number
     * will be added.
     * <P>
     * If <CODE>score1</CODE> or <CODE>score2</CODE>
     * is null then this method does nothing.
     * 
     * @param score1
     *            the base score
     * @param score2
     *            the Score to be merged to the base
     *            score
     */
    public static void merge(Score score1, final Score score2) {
        if (score1 == null || score2 == null) {
            return;
        }
        // Go through the parts in the new score
        // and merge them one by one
        boolean channelExists = false;
        Part existingPart;
        Part partToMerge;

        int s1Size = score1.size();
        int s2Size = score2.size();
        for (int i = 0; i < s2Size; i++) {
            partToMerge = score2.getPart(i);
            // check if its channel exists
            int chan = partToMerge.getChannel();

            for (int j = 0; j < s1Size; j++) {
                existingPart = score1.getPart(j);
                if (chan == existingPart.getChannel()) {
                    // transfer phrases
                    int phraseNumb = partToMerge.size();
                    for (int k = 0; k < phraseNumb; k++) {
                        existingPart.addPhrase(partToMerge.getPhrase(k));
                    }
                    channelExists = true;
                    j = s1Size; // get out of loop
                }
            }
            // create a new part with a unique channel,
            // if required
            if (!channelExists) {
                score1.addPart(partToMerge);
                channelExists = false;
            }
        }
    }

    static Phrase tphrm;

    public static void merge(Score score1, final Score score2, double insertAt,
            boolean instFromExisting) {
        if (score1 == null || score2 == null) {
            return;
        }

        // Go through the parts in the new score
        // and merge them one by one
        boolean channelExists = false;
        Part existingPart = null;
        Part partToMerge;

        int s1Size = score1.size();
        int s2Size = score2.size();
        for (int i = 0; i < s2Size; i++) {
            partToMerge = score2.getPart(i);
            // check if its channel exists
            int chan = partToMerge.getChannel();

            for (int j = 0; j < s1Size; j++) {
                existingPart = score1.getPart(j);
                if (chan == existingPart.getChannel()) {
                    if (!instFromExisting) {
                        // make it the instrument from
                        // the new score
                        if (existingPart != null) {
                            existingPart.setInstrument(partToMerge.getInstrument());
                        }
                    }
                    // transfer phrases
                    int phraseNumb = partToMerge.size();
                    for (int k = 0; k < phraseNumb; k++) {
                        tphrm = partToMerge.getPhrase(k);
                        tphrm.setStartTime(tphrm.getStartTime() + insertAt);
                        if (instFromExisting) {
                            tphrm.setInstrument(existingPart.getInstrument());
                        }
                        existingPart.addPhrase(tphrm);

                    }
                    channelExists = true;
                    j = s1Size; // get out of loop
                }
            }

            // System.out.println("merge chan " +
            // existingPart.getChannel()+
            // " inst " + existingPart.getInstrument());

            // create a new part with a unique channel,
            // if required
            if (!channelExists) {
                score1.addPart(partToMerge.copy());
                channelExists = false;
            }
        }
    }

    /**
     * Quantize all the parts in this score. American
     * spelling to save frustration!
     * 
     * <P>
     * See {@link #quantise(Score, double)}.
     * 
     * @param score
     *            Score to quantize
     * @param qValue
     *            double describing the amount to
     *            quantize to
     */
    public static void quantize(Score score, final double qValue) {
        quantise(score, qValue);
    }

    /**
     * Quantise all the parts in this score
     * 
     * See {@link #quantise(Phrase, double)}.
     * 
     * <P>
     * If <CODE>score</CODE> is null or <CODE>qValue
     * </CODE> is less than or equal to zero then this
     * method does nothing.
     * 
     * @param score
     *            Score to quantise
     * @param qValue
     *            double describing the amount to
     *            quantise to
     */
    public static void quantise(Score score, final double qValue) {
        if (score == null || qValue <= 0.0) {
            return;
        }

        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part part = (Part) enumr.nextElement();
            quantise(part, qValue);
        }
    }

    /**
     * Randomly order notes within each phrase.
     * 
     * @param score
     *            -The Score that contains phrases to be
     *            shuffled
     */
    public static void shuffle(Score score) {
        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            shuffle((Part) enumr.nextElement());
        }
    }

    /**
     * Increase the dynamic of notes at regularly
     * occuring pulse locations which generates the
     * sound of regular meter by emphasising the first
     * beat of each bar/measure.
     * 
     * <P>
     * The dynamic of each note starting on the beat
     * will be increased by 20. If this raises the
     * dynamic above
     * {@link Note#MAX_DYNAMIC MAX_DYNAMIC}then the
     * dynamic will be set to a value as described in
     * {@linkNote#setDynamic}.
     * 
     * <P>
     * If <CODE>score</CODE> is null or <CODE>meter
     * </CODE> is less than or equal to zero then this
     * method does nothing.
     * 
     * @param score
     *            Score whose beats are to be accented
     * @param meter
     *            double describing the number of
     *            crochets per bar/measure
     */
    public static void accents(Score score, final double meter) {
        double[] beats = { 0.0 };
        accents(score, meter, beats);
    }

    /**
     * Increase the dynamic of notes at regularly
     * occuring pulse locations which generates the
     * sound of regular meter by accenting specified
     * beats within each bar/measure.
     * 
     * <P>
     * <CODE>accentedBeats</CODE> is an array of
     * values describing where the accents begin
     * relative to the start of each bar, in crochets.
     * 
     * <P>
     * Each accented note's dynamic will be increased by
     * 20. If this raises the dynamic above
     * {@link Note#MAX_DYNAMIC MAX_DYNAMIC}then the
     * dynamic will be set to a value as described in
     * {@link Note#setDynamic}.
     * 
     * <P>
     * Each of the double values in <CODE>accentedBeats
     * </CODE> must be greater than or equal to zero and
     * less than <CODE>meter</CODE> or this method
     * does nothing.
     * 
     * <P>
     * If <CODE>score</CODE> is null or <CODE>meter
     * </CODE> is less than or equal to zero then this
     * method does nothing.
     * 
     * @param score
     *            Score to be accented
     * @param meter
     *            double describing the number of
     *            croctets per bar/measure
     * @param accentedBeats
     *            double array describing the time of
     *            the accents in the bar.
     */
    public static void accents(Score score, final double meter,
            final double[] accentedBeats) {
        accents(score, meter, accentedBeats, 20);
    }

    /**
     * Increase the dynamic of notes at regularly
     * occuring pulse locations which generates the
     * sound of regular meter. The meter is the total
     * beat length of the bar/measure, while the array
     * of accented beats is the times within the bar at
     * which an accent should be made. For example, 6/8
     * time would be implied by a meter of 3 and a
     * accent array of {0.0, 1.5}.
     * 
     * <P>
     * <CODE>accentedBeats</CODE> is an array of
     * values describing where the accents begin
     * relative to the start of each bar, in crochets.
     * 
     * <P>
     * Accented beats will have their dynamic increased
     * by <CODE>accentAmount</CODE>. If this causes
     * the dynamic to be above {@linkNote#MAX_DYNAMIC
     * MAX_DYNAMIC} or below
     * {@link Note#MIN_DYNAMICMIN_DYNAMIC}(when <CODE>
     * accentAmount</CODE>) then the dynamic will be
     * set to a value as described by
     * {@link Note#setDynamic}.
     * 
     * <P>
     * Each of the double values in <CODE>accentedBeats
     * </CODE> must be greater than or equal to zero and
     * less than <CODE>meter</CODE> or this method
     * does nothing.
     * 
     * <P>
     * If <CODE>score</CODE> is null or <CODE>meter
     * </CODE> is less than or equal to zero then this
     * method does nothing.
     * 
     * @param score
     *            Score to be accented
     * @param meter
     *            double describing the number of
     *            croctets per bar/measure
     * @param accentedBeats
     *            double array describing the time of
     *            the accents in the bar.
     * @param accentAmount
     *            integer describing the value that the
     *            dynamic of accented beats are
     *            increased by.
     */
    public static void accents(Score score, final double meter,
            final double[] accentedBeats, final int accentAmount) {
        if (score == null || meter <= 0.0) {
            return;
        }
        for (int i = 0; i < accentedBeats.length; i++) {
            if (accentedBeats[i] < 0.0 || accentedBeats[i] >= meter) {
                return;
            }
        }

        // go through the phrases one by one and accent
        // them
        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part part = (Part) enumr.nextElement();
            accents(part, meter, accentedBeats, accentAmount);
        }
    }

    /**
     * Increases dynamic values so that the loudest is
     * at maxiumim level.
     * 
     * <P>
     * If <CODE>score</CODE> is null then this method
     * does nothing.
     * 
     * @param score
     *            the Score to be effected
     */
    public static void normalise(Score score) {
        if (score == null) {
            return;
        }
        // get the curent max
        int max = 0;
        Enumeration enumrS = score.getPartList()
            .elements();
        while (enumrS.hasMoreElements()) {
            Part part = (Part) enumrS.nextElement();
            Enumeration enumr = part.getPhraseList()
                .elements();
            while (enumr.hasMoreElements()) {
                Phrase phrase = (Phrase) enumr.nextElement();
                Enumeration enumr1 = phrase.getNoteList()
                    .elements();
                while (enumr1.hasMoreElements()) {
                    Note n = (Note) enumr1.nextElement();
                    if (n.getDynamic() > max)
                        max = n.getDynamic();
                }
            }
        }
        // increase the normalisation
        if (max == Note.MAX_DYNAMIC) {
            return;
        }
        int diff = Note.MAX_DYNAMIC - max;
        Enumeration enumrS2 = score.getPartList()
            .elements();
        while (enumrS2.hasMoreElements()) {
            Part part = (Part) enumrS2.nextElement();
            Enumeration enumr3 = part.getPhraseList()
                .elements();
            while (enumr3.hasMoreElements()) {
                Phrase phrase = (Phrase) enumr3.nextElement();
                Enumeration enumr2 = phrase.getNoteList()
                    .elements();
                while (enumr2.hasMoreElements()) {
                    Note n = (Note) enumr2.nextElement();
                    n.setDynamic(n.getDynamic() + diff);
                }
            }
        }
    }

    /**
     * Pack all phrases from parts with the same channel
     * into one part. Data from all parts with the same
     * channel is consolidated into one part. This is
     * often useful after appending several scores with
     * simmilar instrumentation.
     * 
     * <P>
     * If <CODE>score</CODE> is null then this method
     * does nothing.
     * 
     * @param score
     *            the Score to be effected
     */

    public static void consolidate(Score score) {
        if (score == null) {
            return;
        }
        int chan, phraseSize, scoreSize;
        for (int c = 0; c < score.size(); c++) {
            Part p = score.getPart(c);
            chan = p.getChannel();
            scoreSize = score.size();
            for (int i = scoreSize - 1; i > c; i--) {
                // System.out.println("i = " + i + "
                // Part = " + c + " channel =
                // " + chan);
                Part p2 = score.getPart(i);
                if (p2.getChannel() == chan) {
                    // move all phrases into the other
                    // part
                    phraseSize = p2.size();
                    for (int j = 0; j < phraseSize; j++) {
                        // System.out.println("Moving a
                        // phrase");
                        Phrase phr = p2.getPhrase(j);
                        phr.setAppend(false);
                        p.addPhrase(phr);
                    }
                    // delete the part
                    score.removePart(i);
                }
            }
        }
    }

    /**
     * Stretch the time of each note in each phrase by
     * the <CODE>scaleFactor</CODE>
     * 
     * <P>
     * If <CODE>phrase</CODE> is null or <CODE>
     * scaleFactor</CODE> is less than or equal to zero
     * then this method does nothing.
     * 
     * @param score
     *            Score to be lengthened
     * @param scaleFactor
     *            double describing the scale factor
     */
    public static void elongate(Score score, double scaleFactor) {
        if (score == null || scaleFactor <= 0.0) {
            return;
        }
        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part part = (Part) enumr.nextElement();
            elongate(part, scaleFactor);
        }
    }

    /**
     * Joins consecutive notes in each <CODE>phrase
     * </CODE> that have the same pitch, creating one
     * longer note. This is simmilar to the musical
     * function of a tie. All note in the phrase meeting
     * the conditions are effected. This modification
     * may reduce the overall note count.
     * 
     * @param score
     *            Score to be processed
     */
    public static void tiePitches(Score score) {
        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part part = (Part) enumr.nextElement();
            tiePitches(part);
        }
    }

    /**
     * Joins consecutive rests in each <CODE>phrase
     * </CODE> creating one longer note. This is
     * simmilar to the musical function of a tie. This
     * modification may reduce the overall rest count.
     * 
     * @param score
     *            Score to be processed
     */
    public static void tieRests(Score score) {
        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part part = (Part) enumr.nextElement();
            tieRests(part);
        }
    }

    /**
     * Lengthens notes followed by a rest in the <CODE>
     * score</CODE> by creating one longer note and
     * deleting the rest. This modification may reduce
     * the overall note count in the phrase, be careful
     * that use of this method does not to cause array
     * out of bounds exceptions.
     * 
     * @param Score
     *            Score to be processed
     */
    public static void fillRests(Score score) {
        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part part = (Part) enumr.nextElement();
            fillRests(part);
        }
    }

    /**
     * Randomly adjusts all Notes' pan value to create
     * an even spread acroos the stereo spectrum. This
     * process only effects the pan value of the notes
     * in the score.
     * <P>
     * If <CODE>score</CODE> is null then this method
     * does nothing.
     * 
     * @param cphrase
     *            the CPhrase to be effected
     */
    public static void spread(Score score) {
        if (score == null) {
            return;
        }
        int currentValue, newValue;
        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part p = (Part) enumr.nextElement();
            // create new pan value
            Mod.spread(p);
        }
    }

    /**
     * Adjusts all Notes' pan value to alternate between
     * extreme left and right from note to note. This
     * process only effects the pan value of the notes
     * in the score.
     * <P>
     * If <CODE>score</CODE> is null then this method
     * does nothing.
     * 
     * @param score
     *            The Score to be effected
     */
    public static void bounce(Score score) {
        if (score == null) {
            return;
        }
        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part p = (Part) enumr.nextElement();
            Mod.bounce(p);
        }
    }

    /**
     * Adjusts all Notes' duration values to vary
     * randomly between specified values from note to
     * note. This process only effects the duration
     * attribute of the notes in the phrase.
     * <P>
     * If the <CODE>Score</CODE> is null then this
     * method does nothing.
     * 
     * @param score
     *            The Score to be effected
     * @param minlength
     *            The shortest possible duration
     * @param maxlength
     *            The longest possible duration
     */
    public static void varyLength(Score score, double minLength,
            double maxLength) {
        if (score == null || maxLength < minLength) {
            return;
        }
        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part part = (Part) enumr.nextElement();
            varyLength(part, minLength, maxLength);
        }
    }

    /**
     * Adjusts Note values to any value plus or minus a
     * specified amount. This process can effect pitch,
     * rhythm & duration, dynamic, and pan values of the
     * notes in the score.
     * <P>
     * If <CODE>Score</CODE> is null then this method
     * does nothing.
     * 
     * @param score
     *            The Score to be effected
     * @param pitchVariation
     *            The degree of pitch change to apply.
     */
    public static void randomize(Score score, int pitchVariation) {
        randomize(score, pitchVariation, 0.0);
    }

    /**
     * Adjusts Note values to any value plus or minus a
     * specified amount. This process can effect pitch,
     * rhythm & duration, dynamic, and pan values of the
     * notes in the score.
     * <P>
     * If <CODE>score</CODE> is null then this method
     * does nothing.
     * 
     * @param score
     *            The Score to be effected
     * @param pitchVariation
     *            The degree of pitch change to apply.
     * @param rhythmVariation
     *            The degree of rhythm value change to
     *            apply.
     */
    public static void randomize(Score score, int pitchVariation,
            double rhythmVariation) {
        randomize(score, pitchVariation, rhythmVariation, 0);
    }

    /**
     * Adjusts Note values to any value plus or minus a
     * specified amount. This process can effect pitch,
     * rhythm & duration, dynamic, and pan values of the
     * notes in the score.
     * <P>
     * If <CODE>Score</CODE> is null then this method
     * does nothing.
     * 
     * @param score
     *            The Score to be effected
     * @param pitchVariation
     *            The degree of pitch change to apply.
     * @param rhythmVariation
     *            The degree of rhythm value change to
     *            apply.
     * @param dynamicVariation
     *            The degree of dynamic change to apply.
     */
    public static void randomize(Score score, int pitchVariation,
            double rhythmVariation, int dynamicVariation) {
        if (score == null) {
            return;
        }
        boolean left = true;
        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part p = (Part) enumr.nextElement();
            randomize(p, pitchVariation, rhythmVariation, dynamicVariation);
        }
    }

    /**
     * All ascending sequences of the number of notes or
     * more are slured by having thier duration extended
     * to 100% of the rhythm value. This algorithm
     * assumes notes are arranged in the standard
     * monophonic series used by jMusic. Notes with
     * durations longer than rhythm values will produce
     * unintended results.
     * 
     * @param Score
     *            The score to be modified.
     * @param int
     *            The number of notes in a row to
     *            consitute a sequence
     */
    public static void slurUp(Score score, int numberOfNotes) {
        if (score == null) {
            return;
        }
        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part p = (Part) enumr.nextElement();
            slurUp(p, numberOfNotes);
        }
    }

    /**
     * All descending sequences of the number of notes
     * or more are slured by having thier duration
     * extended to 100% of the rhythm value. This
     * algorithm assumes notes are arranged in the
     * standard monophonic series used by jMusic. Notes
     * with durations longer than rhythm values will
     * produce unintended results.
     * 
     * @param Score
     *            The score to be modified.
     * @param int
     *            The number of notes in a row to
     *            consitute a sequence
     */
    public static void slurDown(Score score, int numberOfNotes) {
        if (score == null) {
            return;
        }
        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part p = (Part) enumr.nextElement();
            slurDown(p, numberOfNotes);
        }
    }

    /**
     * Vary the duration of each note in the score by
     * the multiplyer.
     * 
     * @param Score
     *            The score to be modified.
     * @param double
     *            The amount to multiply the duration
     *            by.
     */
    public static void increaseDuration(Score score, double multiplyer) {
        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part p = (Part) enumr.nextElement();
            increaseDuration(p, multiplyer);
        }
    }

    /**
     * Vary the duration of each note in the score by
     * the specified amount.
     * 
     * @param score -
     *            The Score to be modified.
     * @param amount -
     *            The number of beats to add to the
     *            duration.
     */
    public static void addToDuration(Score score, double amount) {
        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part p = (Part) enumr.nextElement();
            addToDuration(p, amount);
        }
    }

    /**
     * Vary the rhythm value of each note in the Score
     * by the specified amount.
     * 
     * @param score -
     *            The Score to be modified.
     * @param amount -
     *            The number of beats to add.
     */
    public static void addToRhythmValue(Score score, double amount) {
        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part p = (Part) enumr.nextElement();
            addToRhythmValue(p, amount);
        }
    }

    /**
     * Vary both the rhythm value and duration of each
     * note in the Score by the specified amount.
     * 
     * @param score -
     *            The Score to be modified.
     * @param amount -
     *            The number of beats to add.
     */
    public static void addToLength(Score score, double amount) {
        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part p = (Part) enumr.nextElement();
            addToLength(p, amount);
        }
    }

    /**
     * Vary the interval between notes scaling by the
     * specified amount to each interval.
     * 
     * @param score -
     *            The Score to be modified.
     * @param amount -
     *            The scaling multiplyer for the
     *            intervals, i.e., 2.0 doubles width.
     */
    public static void expandIntervals(Score score, double amount) {
        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part p = (Part) enumr.nextElement();
            expandIntervals(p, amount);
        }
    }

    /**
     * Randomise the dynamic values of notes up to a
     * specified amount either side of the current
     * value.
     * 
     * @param score -
     *            The score to be modified.
     * @param amount -
     *            The dynamic change possible either
     *            side of the curent dynamic.
     */
    public static void shake(Score score, int amount) {
        Enumeration enumr = score.getPartList()
            .elements();
        while (enumr.hasMoreElements()) {
            Part part = (Part) enumr.nextElement();
            shake(part, amount);
        }
    }

    public static void wrapNotesInPhrases(Part p) {
        Phrase[] phr = p.getPhraseArray();
        for (int i = 0; i < phr.length; i++) {
            // check to see if the phrase is already a
            // wrapper
            if (phr[i].getSize() > 1) {

                Note[] note = phr[i].getNoteArray();
                double counter = phr[i].getStartTime();
                // go through each note
                for (int j = 0; j < note.length; j++) {
                    // if it is real note, copy it into
                    // a phrase at the st
                    if (!note[j].isRest()) {
                        Phrase nphr = new Phrase(note[j].copy(), counter);
                        p.addPhrase(nphr);
                    }

                    counter += note[j].getRhythmValue();
                    counter = ((int) (counter * 100000 + 0.5)) / 100000.0; // acuracy
                }
                // now that all the notes in the phrase
                // have been wrapped and
                // added, remove the phrase from the
                // part
                p.removePhrase(phr[i]);
            }
            // else if it has already only got one note,
            // nothing needs to do
        }
    }

    /**
     * packs all of the phrases within this part into a
     * single phrase
     * 
     */
    public static void consolidate(Part p) {

        Phrase[] phr = p.getPhraseArray();
        if (phr.length < 2)
            return;
        // the new phrase has the start time of the
        // earliest one
        Phrase nphr = new Phrase(phr[0].getStartTime());

        Note n;
        boolean finished = false;
        double prevsst = phr[0].getStartTime();
        while (!finished) {
            double sst = Double.POSITIVE_INFINITY; // smallest
            // start
            // time
            // a temporary phrase (pointer to the one in
            // the array
            Phrase tphr = null;

            // find the phrase with the smallest start
            // time
            for (int i = 0; i < phr.length; i++) {

                if (phr[i].getSize() > 0 && phr[i].getStartTime() < sst) {
                    sst = phr[i].getStartTime();
                    tphr = phr[i];
                }
            }
            if (tphr == null) {
                finished = true;
                break;
            }

            // get a note out of that phrase and, if it
            // is not a rest,
            // put it into the new phrase, adjusting the
            // rhythmValue
            // of the previous note accordingly
            n = tphr.getNote(0);

            if (!n.isRest()) {
                if (nphr.getSize() > 0) { // if it is
                    // not the
                    // first note
                    nphr.getNote(nphr.getSize() - 1)
                        .setRhythmValue(
                            ((int) ((sst - prevsst) * 100000 + 0.5)) / 100000.0);
                } else {// if it is the first note to go
                    // in, set the startime
                    nphr.setStartTime(sst);
                }
                nphr.addNote(n);
            }
            // adjust the start time and remove the note
            tphr.setStartTime(((int) ((sst + n.getRhythmValue()) * 100000 + 0.5)) / 100000.0);
            tphr.removeNote(0);

            prevsst = sst;
        }
        p.empty();
        p.addPhrase(nphr);
    }

    public static void scaleDynamic(Part p, double scale) {
        Enumeration enumr = p.getPhraseList()
            .elements();
        while (enumr.hasMoreElements()) {
            scaleDynamic((Phrase) enumr.nextElement(), scale);
        }
    }

    public static void scaleDynamic(Phrase phr, double scale) {
        Enumeration enumr = phr.getNoteList()
            .elements();
        while (enumr.hasMoreElements()) {
            scaleDynamic((Note) enumr.nextElement(), scale);
        }
    }

    public static void scaleDynamic(Note n, double scale) {
        int dyn = (int) (n.getDynamic() * scale);
        if (dyn < 0)
            dyn = 0;
        else if (dyn > 127)
            dyn = 127;

        n.setDynamic(dyn);
    }

    /**
     * makes all the parts in the score go from channels
     * 1 to 16
     * 
     * @param s
     */
    public static void channels1to16(Score s) {
        if (s.getPart(0) != null) {
            Part[] parts = s.getPartArray();
            for (int i = 0; i < parts.length; i++) {
                parts[i].setChannel(parts[i].getChannel() + 1);
            }
        }
    }

    static Enumeration enumr;

    static Phrase phr;

    /**
     * goes through every phrase in the part and shifts
     * it's start time by the ratio specified. Assumes
     * that phrases only have one note in them
     * 
     * 
     * @param p
     * @param ratio
     * @param durs - how much the duration should be affected by the rate
     */
    public static void rate(Part p, double ratio, boolean rests, double durs) {
    	
    	for(int i=0; i<p.size(); i++) {
    	
            if (!p.getPhrase(i).getNote(0)
                .isRest() || rests) {
            	p.getPhrase(i).setStartTime(AD.mult(p.getPhrase(i).getStartTime(), ratio));
            	
            	
            	p.getPhrase(i).getNote(0).setDuration(
            			AD.mult(p.getPhrase(i).getNote(0).getDuration(), ratio)*durs +
            			p.getPhrase(i).getNote(0).getDuration()*(1-durs));
                
            }		
         
        }
    }
    
    public static void rate(Part p, double ratio, boolean rests) {
    	rate(p, ratio, rests, 0.0);
    }
    
    
    public static void rateLoop(Part p, double ratio, double scope) {
    	if(ratio == 1.0)
    		return;
    	
    	rate(p, ratio, false);
    
    	Mod.repeatRT(p, scope*ratio, scope);
    }

    public static void quickSort(Score s) {
        for (int i = 0; i < s.size(); i++) {
            Mod.quickSort(s.getPart(i));
        }
    }

    public static void quickSort(Part p) {
        quickSort(p, 0, p.size() - 1);
    }

    private static void quickSort(Part p, int lowIndex, int highIndex) {
        int lowToHighIndex;
        int highToLowIndex;
        int pivotIndex;
        Phrase pivotValue;
        Phrase lowToHighValue;
        Phrase highToLowValue;
        Phrase parking;
        int newLowIndex;
        int newHighIndex;
        int compareResult;

        lowToHighIndex = lowIndex;
        highToLowIndex = highIndex;

        /**
         * Choose a pivot, remember it's value No
         * special action for the pivot element itself.
         * It will be treated just like any other
         * element.
         */
        pivotIndex = (lowToHighIndex + highToLowIndex) / 2;
        pivotValue = p.getPhrase(pivotIndex);

        /**
         * Split the Vector in two parts.
         * 
         * The lower part will be lowIndex -
         * newHighIndex, containing elements <= pivot
         * Value
         * 
         * The higher part will be newLowIndex -
         * highIndex, containting elements >= pivot
         * Value
         */
        newLowIndex = highIndex + 1;
        newHighIndex = lowIndex - 1;
        // loop until low meets high
        while ((newHighIndex + 1) < newLowIndex) // loop
        // until
        // partition
        // complete
        { // loop from low to high to find a candidate
            // for swapping
            lowToHighValue = p.getPhrase(lowToHighIndex);
            while (lowToHighIndex < newLowIndex
                    && lowToHighValue.getStartTime() < pivotValue.getStartTime()) {
                newHighIndex = lowToHighIndex; // add
                // element
                // to
                // lower
                // part
                lowToHighIndex++;
                lowToHighValue = p.getPhrase(lowToHighIndex);
            }

            // loop from high to low find other
            // candidate for swapping
            highToLowValue = p.getPhrase(highToLowIndex);
            while (newHighIndex <= highToLowIndex
                    && highToLowValue.getStartTime() > pivotValue.getStartTime()) {
                newLowIndex = highToLowIndex; // add
                // element
                // to
                // higher
                // part
                highToLowIndex--;
                highToLowValue = p.getPhrase(highToLowIndex);
            }

            // swap if needed
            if (lowToHighIndex == highToLowIndex) // one
            // last
            // element,
            // may
            // go
            // in
            // either
            // part
            {
                newHighIndex = lowToHighIndex; // move
                // element
                // arbitrary
                // to
                // lower
                // part
            } else if (lowToHighIndex < highToLowIndex) // not
            // last
            // element
            // yet
            {

                if (lowToHighValue.getStartTime() >= highToLowValue.getStartTime()) // low
                // >=
                // high,
                // swap, even if
                // equal
                {
                    parking = lowToHighValue;
                    (p.getPhraseList()).setElementAt(highToLowValue,
                        lowToHighIndex);
                    (p.getPhraseList()).setElementAt(parking, highToLowIndex);

                    newLowIndex = highToLowIndex;
                    newHighIndex = lowToHighIndex;

                    lowToHighIndex++;
                    highToLowIndex--;
                }
            }
        }

        // Continue recursion for parts that have more
        // than one element
        if (lowIndex < newHighIndex) {
            quickSort(p, lowIndex, newHighIndex); // sort
            // lower
            // subpart
        }
        if (newLowIndex < highIndex) {
            quickSort(p, newLowIndex, highIndex); // sort
            // higher
            // subpart
        }

    }

    public static void crop(Score s, double st, double en) {
        for(int i=0; i<s.length(); i++) {
            crop(s.getPart(i), st, en);
        }
    }
    
    
    /**
     * removes all of the phrases outside of a specified
     * region. and shift the start time of the remaining
     * phrases to zero Note!: this algorithm is
     * optimised for real-time operations and assumes
     * that all phrases have only one note.
     * 
     * It doesn't make any copies, so be sure that the
     * part is allowed to be modified
     * 
     * @param p
     *            the part that is going to have parts
     *            removed
     * @param st
     *            the start of the region
     * @param en
     *            the end of the region
     */
    public static void crop(Part p, double st, double en) {
        quickSort(p);
        cropFaster(p, st, en, true);
    }

    public static void cropFaster(Part p, double st, double en, boolean norest) {
      //   PO.p("crop faster");
     //    PO.p("st = " + st);
     //    PO.p("et = " + en);
         
         
        for (int i = 0; i < p.size(); i++) {
            double pst = p.getPhrase(i)
                .getStartTime();
       //     PO.p("pst = " + pst);
            if ((norest && p.getPhrase(i)
                .getNote(0)
                .isRest()) || pst < st || pst >= en) {
                p.removePhrase(i);
                i--;
            } else {
                p.getPhrase(i)
                    .setStartTime(pst - st);

                p.getPhrase(i)
                    .getNote(0)
                    .setRhythmValue((en - st) - p.getPhrase(i)
                        .getStartTime());
            }
        }

    }

    public static void cropFaster(Part p, double st, double en) {
        cropFaster(p, st, en, false);
    }

    /**
     * p must be ordered in time for this method to work
     * properly
     * 
     * @param p
     * @param st
     * @param en
     */
    public static void cropFast(Part p, double st, double en) {
        // PO.p(p.toString());
        double cst = p.getPhrase(0)
            .getStartTime();
        // go through and remove all of the phrases with
        // start times that are before the specified
        // start time
        while (cst < st) {

            p.removePhrase(0);

            if (p.size() > 0) // if there is another
                // phrase
                cst = p.getPhrase(0)
                    .getStartTime();
            else
                // if there are no phrases left, job is
                // done
                return;
        }

        // go through phrases in the required section
        // and make sure that the notes in them are
        // of the appropriate rhythm value
        int i = 0;
        while (cst < en) {

            // set the rhythm value so that it take up
            // the whole of the region specified
            p.getPhrase(i)
                .getNote(0)
                .setRhythmValue((en - cst));

            // set the start time so that it is relative
            // to 0
            p.getPhrase(i)
                .setStartTime((cst - st));

            // if there is another one, move on to the
            // next one
            if (p.size() > i + 1) {
                cst = p.getPhrase(++i)
                    .getStartTime();
            } else { // otherwise, the job is done
                return;
            }
        }

        // now we are in the bit after en - we need to
        // remove
        // the remainder
        final int sz = i;
        while (p.size() > sz) {

            p.removePhrase(p.size() - 1);
        }

    }

    public static void convertToRealTime(Score ts, double cutPoint) {
        for (int si = 0; si < ts.size(); si++) {
            Part p = ts.getPart(si);

            Phrase[] phr = p.getPhraseArray();
            p.removeAllPhrases();

            // go through each phrase in the part
            for (int phri = 0; phri < phr.length; phri++) {
                double counter = phr[phri].getStartTime();

                // go through each not in the phrase
                for (int ni = 0; ni < phr[phri].size(); ni++) {
                    // if it's not a rest, add it
                    if (!phr[phri].getNote(ni)
                        .isRest()) {
                        Phrase nphr = new Phrase(counter);
                        nphr.add(phr[phri].getNote(ni)
                            .copy());
                        p.add(nphr);
                    }
                    counter += phr[phri].getNote(ni)
                        .getRhythmValue();
                    if (counter > cutPoint)
                        break;
                }
            }
        }
    }

    public static void removePriorityBelow(Part p, int level) {
        for (int i = 0; i < p.size(); i++) {
            if (p.getPhrase(i)
                .getPriority() < level) {
                p.removePhrase(i);
                i--;
            }

        }
    }

    public static void swing(Part stream, double amt, double qres) {
        if (qres == 0)
            return;
        if (amt < 0 || amt > 1.0) {
            try {
                Exception ex = new Exception("amount passed to swing"
                        + "is out of range  = " + amt);
                ex.fillInStackTrace();
                throw ex;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        for (int i = 0; i < stream.size(); i++) {
            double st = stream.getPhrase(i)
                .getStartTime();
            if (st % (qres * 2.0) == qres) {
                st = st + qres * amt;
                stream.getPhrase(i)
                    .setStartTime(st);
            } else if (st % (qres * 2.0) != 0) {
                System.out.println("shuffle being used without quantise!");
            }

        }
    }

    /**
     * different to the other versions of repeat,  becuase from dictates the
     * length of the part (eg, the end point of the loop - 
     * st point is assumed to be 0).  To dictates the point at which it stops
     * looping
     * 
     * crops it if the from is bigger than to.
     * 
     * makes no changes if fr = to
     * 
     * @param part
     * @param fr the end point of the loop to be repeated
     * @param to the point at which it stops looping
     */
    public static void repeatRT(Part part, double fr, double to) {
      //  PO.p("fr = " + fr + " to = " + to);
    	
        
        if(fr > to) {
        	Mod.cropFaster(part, 0, to);
        	return;
        } else if(fr == to)
        	return;
        // from here, to > fr
        int times = 0;
        double tdf = to/fr;
        // if it divides perfrectly, we actually want it to be one less 
        // (up to, but not over, "to")
        if(tdf%1.0 == 0)
        	times = (int)(tdf-1);
        else 
        	times = (int)(to/fr);
        
        Part pc = part.copy();
        // repeat it certain number of times
        for(int i=1; i <= times; i++) {
            //on each repeat, go through the phrases
            for(int j = 0; j< pc.size(); j++) {
            	// if it is within the loop point
                if(pc.getPhrase(j).getStartTime() < fr) {
                	double nst = pc.getPhrase(j).getStartTime() + i*1.0*fr;
                	if(nst > to)
                		break;
                		
                    Phrase phr = pc.getPhrase(j).copy();
           //         PO.p("i*fr = " + i*1.0*fr);
                    phr.setStartTime(nst);
                    part.add(phr);
                }
            }
            
        }
    
    }
    
    /**
     * shifts the start time of all the phrases within the part by the amount
     * specified.  If a phrases is shifted < 0 or > scope, it is wrapped around
     * @param p
     * @param amt
     * @param scope
     */
    public static void phaseShiftRT(Part p, double amt, double scope) {
    	
    	for(int i=0; i< p.size(); i++) {
    		//if amt is negative, do %scope)+scope to make it positive,
    		// but it might be positive, so add the final %scope
    		p.getPhrase(i).setStartTime(((p.getPhrase(i).getStartTime() +
    								    amt)%scope + scope)%scope);
    	}
    	    	
    }
    
    /**
     * shifts the start time of all the phrases within the part by the amount
     * specified.  If a phrases is shifted < 0 or > scope, it is wrapped around
     * @param p
     * @param amt
     * @param scope
     */
	public static void phaseShiftRT(LScore score, double amt) {
		for(int i=0; i< score.size(); i++) {
			Mod.phaseShiftRT(score.getLPart(i).getPart(), 
					amt, score.getLPart(i).getScope().getValue());
		}
		
	}

    
    /**
     * scale the pitch interval of each pitch in the part, relative to the
     * fundamental, by the factor d
     * 
     * bounds the result within lower and upper bounds, while preserving pitch 
     * class
     * 
     * @param part
     * @param fund
     * @param d
     * @param lowerBound TODO
     * @param upperBound TODO
     */
	public static void expandFunIntRT(Part part, int fund, double d, int spo, int lowerBound, int upperBound) {
		for(int i=0; i< part.size(); i++) {
			int np = (int)((part.getPhrase(i).getNote(0).getPitch()-fund)*d) 
					+ fund;
			np = Scales.boundPitch(np, spo, lowerBound,upperBound);
			part.getPhrase(i).getNote(0).setPitch(np);
			
		}
	}
	
	public static void expandFunDEPAintRT(LPart part, int fund, double d, 
			int spo, int lowerBound, int upperBound) {
		
		for(int i=0; i< part.size(); i++) {
			// absolute distance
			int abd = (part.getPart().getPhrase(i).getNote(0).getPitch()-fund);
			//// distance in DEPA degree steps
			//int ste = (int)(abd*1.0/part.getTonalManager().getPassingSteps()*1.0);
			// the type of degree (eg passing note or not)
			int paste = part.getTonalManager().getPassingSteps();
			
			int rem = abd%paste;
			
			//apply the stretch
			abd = (int)(abd*d);
			
			int nrem = abd%paste;
			
			// shift to to the nearest point with a similar remainder
			if(Math.abs(rem-nrem) < Math.abs(rem+paste-nrem))
				abd +=rem-nrem;
			else
				abd +=rem+paste-nrem;
		
			abd +=fund;
			
			abd = Scales.boundPitch(abd, spo, lowerBound, upperBound);
			
			part.getPart().getPhrase(i).getNote(0).setPitch(abd);
		}
	}

	/**
	 * shift pitches up or down an octave (like chord inversion).  The number
	 * of pitches is given by the value of v (between 0 anf 1), and the 
	 * direction is given by the sign of i (-, +).  
	 * If it is -, it shifts down, taking pitches from
	 * the top, and visa-versa dito for +.
	 * eg if i == -12 == spo, all pitches in the scale are shifted, down an 
	 * octave.
	 * 
	 * @param p part to be modified
	 * @param v the % of pitches to be inverted, and the direction (-,+)
	 * @param spo steps per octave, ussually 12
	 * @param lowerBounds TODO
	 * @param upperBounds TODO
	 */
	public static void invertRT(Part p, double v, int spo, int lowerBounds, int upperBounds) {
		// if the value is out of range, throw an error
		if(v > 1.0 || v < 0) {
			try {Exception e= new Exception("invert v must be < 1 and >0");
			e.fillInStackTrace();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		// don't bother inverting it if it is 0
		if(p.length() == 0)
			return;
		
		int [] pst = An.pitchRange(p); // range, lowest, highest
		int shift = ((int)(pst[0]*1.0/spo*1.0) + 1)*spo;
		
		//PO.p("shift = " + shift);
		for(int i=0; i< p.size(); i++) {
			int pitch = p.getPhrase(i).getNote(0).getPitch();
		//	PO.p("1 pitch = " + pitch);
			// if shifting down
			if(v < 0) {
				// if the pitch is larger than the cutoff (%range + lowest)
				if(pitch >= (v+1.0)*pst[0]+pst[1]) {
					//shift it down by however many octaves
					pitch = pitch - shift;
				}
			// if shifting up
			} else if(v > 0) {
				// if the pitch is smaller than the cutoff (%range + lowest)
				if(pitch <= v*pst[0]+pst[1]) {
					//shift it down by however many octaves
					pitch = pitch + shift;
				}
			}
			
			// bound the pitches while keeping the pitch class
			pitch = Scales.boundPitch(pitch, spo, lowerBounds, upperBounds);
			
		//	PO.p("final pitch = " + pitch);
			p.getPhrase(i).getNote(0).setPitch(pitch);
		}
	}

	public static void transposeRT(Part p, int v) {
		for(int i=0; i<p.length(); i++) {
			int pitch = p.getPhrase(i).getNote(0).getPitch()+v;
			
			pitch = RMath.boundHard(pitch, 0, 1000);
			
			p.getPhrase(i).getNote(0).setPitch(pitch);
		}
	}

	public static void transposeRT(Phrase phrase, int v) {
		
		phrase.getNote(0).setPitch(phrase.getNote(0).getPitch() + v);
	}
	
	/**
	 * removes all consecutive duplicate note phrases
	 * p must be quicksorted
	 * @param p
	 */
	public static void removeSameRT(Part p) {
		
		for(int i=1; i< p.length(); i++) {
			if(p.getPhrase(i).getStartTime() > p.getPhrase(i-1).getStartTime() - 0.000001 &&
			   p.getPhrase(i).getStartTime() < p.getPhrase(i-1).getStartTime() + 0.000001 &&
			   p.getPhrase(i).getNote(0).getPitch() == 
				   p.getPhrase(i-1).getNote(0).getPitch()) {
					
				p.removePhrase(i);
			}	
		}
	}

	public static void octRT(Part part, int a) {
		for(int i=0; i< part.length(); i++) {
			Mod.transposeRT(part.getPhrase(i), a*12);
		}
	}


}