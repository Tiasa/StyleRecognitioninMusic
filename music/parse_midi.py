from mido import MidiFile
import numpy as np


def parse_midi(filepath, res=4):
    # res controls the resolution per beat (4 subdivisions per beat is default)

    m = MidiFile(filepath)
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
                    x.append((end, pitch)) # offsets on range [128,255]

                    ticks_at_onset[msg.note] = -1

    # x is a ndim vector with timestamps in first column and code in second col
    x = np.array(x)

    # quantize timestamps
    x[:,0] = np.round(x[:,0].astype(np.float32)/(ticks/res)).astype(np.int32)

    # order each event by timestamps then pitch
    x = x[np.lexsort([x[:,1], x[:,0]])]

    # add time deltas on range [256,...]
    sequence = []
    for (curr_time, curr_pitch) in x[0:]:
         #if last_time < curr_time:
         #   sequence.append(curr_time - last_time + 256)
        sequence.append(curr_pitch)

    return sequence

