# Jeff Ens (2019)
# Simon Fraser University
# Code for parsing midi
# the output of parse_midi can be used with functions in compare corpora
# as numpy arrays have a tostring() method that converts them into bytestrings

from mido import MidiFile
import numpy as np
import os
twelvePitchNames = ["C","C#","D","D#","E","F","F#","G", "G#", "A", "A#", "B"]

def getNoteNameOctave(pitch):
    octave = (pitch / 12) - 1
    pitchName = twelvePitchNames[pitch % 12]
    return pitchName+str(octave)

def parse_midi(filepath, res=4):
    # res controls the resolution per beat (4 subdivisions per beat is default)
    m = MidiFile(filepath)
    noteFileLoc = filepath.split(".")[0] + ".txt"
    if not os.path.exists(noteFileLoc):
        ticks = m.ticks_per_beat
        x = []

        for track_no, track in enumerate(m.tracks):
            ticks_at_onset = np.zeros((128,), dtype=np.int32) - 1
            velocity_at_onset = np.zeros((128,), dtype=np.int32)
            tick_position = 0
            for msg in track:
                tick_position += msg.time
                if not msg.is_meta:
                    ONSET = msg.type == "note_on" and msg.velocity > 0
                    OFFSET = msg.type == "note_off" or (msg.type == "note_on" and msg.velocity == 0)

                    if ONSET and ticks_at_onset[msg.note] == -1:
                        ticks_at_onset[msg.note] = tick_position
                        velocity_at_onset[msg.note] = msg.velocity
                    elif OFFSET and ticks_at_onset[msg.note] >= 0:
                        pitch = int(msg.note)
                        start = int(ticks_at_onset[msg.note])
                        end = int(tick_position)
                        x.append((start, pitch)) # onsets on range [0,127]
                        x.append((end, pitch + 128)) # offsets on range [128,255]

                        ticks_at_onset[msg.note] = -1

        # x is a ndim vector with timestamps in first column and code in second col
        x = np.array(x)
        # What's the definition of onset and offset
        # quantize timestamps
        x[:,0] = np.round(x[:,0].astype(np.float32)/(ticks/res)).astype(np.int32)

        # order each event by timestamps then pitch
        x = x[np.lexsort([x[:,1], x[:,0]])]

        noteFile = open(noteFileLoc, 'w')
        for (curr_time, curr_pitch) in x[0:]:
            noteFile.write(getNoteNameOctave(curr_pitch))

    return noteFileLoc



