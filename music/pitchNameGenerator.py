import subprocess
import sys
import os
import shutil
import multiprocessing as mp
from parse_midi import parse_midi

# def cosiatec(trackLoc):
#     subprocess.call(['java', '-Xmx32G',
#                      '-jar', jarFileLoc,
#                      '-i', trackLoc,
#                      '-nodate', '-d'])

def jazzConverter(trackLoc):
    subprocess.call([melConvLoc,'-i',
                     trackLoc,'-f','notes',
                     '-o',pitchFolderLoc])

def generatePitchName1():
    objectFiles = []
    numObjects = 0
    for r, d, f in os.walk(melodyFolderLoc):
        for objectFile in f:
            # # .grammar is the extension we are using for outputting grammar in a file
            # if ".txt" in objectFile:
            #     numObjects += 1
            #     objectFiles.append(os. path.join(r, objectFile))
            # else:
            if ".mel" in objectFile:
                numObjects += 1
                outputFileLoc = outputFolderLoc + "/" + objectFile.split(".")[0] + ".pitch"
                pitchNames = subprocess.check_output([pitchNameGeneratorFileLoc, "1", "1", os.path.join(r, objectFile)])

                if "Error" not in pitchNames:
                    if not os.path.exists(outputFileLoc):
                        outputFile = open(outputFileLoc, 'w')
                        outputFile.write(pitchNames)

def generatePitchName2():
    # midiFilesList = []
    #midiNameList = []
    for objectFile in os.listdir(midiFolderLoc):
        if ".midi" in objectFile:
            #midiNameList.append(objectFile.split(".")[0])
            # midiFilesList.append()
            # jazzConverter(os.path.join(midiFolderLoc, objectFile))
            midifile = os.path.join(midiFolderLoc, objectFile)
            pitchList = parse_midi(midifile)
            outputFileLoc = pitchFolderLoc + "/" + objectFile.split(".")[0] + "_tm.pitch"
            if os.path.exists(outputFileLoc):
                os.remove(outputFileLoc)
            outputFile = open(outputFileLoc, 'w')
            for pitch in pitchList:
                outputFile.write(str(pitch)+" ")
            outputFile.close()

    # pool = mp.Pool(processes=1)
    # pool.map(jazzConverter, midiFilesList)
    # pool.close()
    # pool.join()
    # for midi in midiNameList:
    #     notesFileLoc = pitchFolderLoc+"/"+midi+".txt"
    #
    #     if (os.path.exists(notesFileLoc)):
    #         pitchFileLoc = pitchFolderLoc+"/"+midi+".pitch"
    #         pitchFile = open(pitchFolderLoc, 'w')
    #         with open(notesFileLoc) as notesFile:
    #             for line in notesFile:
    #                 if len(line)>0:
    #                     pitch = line.split(",")[1]
    #                     pitchFile.write(str(pitch)+"\n")
    #         pitchFile.close()
    #     os.remove(notesFileLoc)

    # for midifile in midiFilesList:
    #
    #     shutil.rmtree(midifile.split('.')[0] + '-midi')

    # infoFileLoc = pitchFolderLoc + "/" + pitchFolderLoc.split("/")[-1].split("_")[0] + ".txt"
    # if os.path.exists(infoFileLoc):
    #     os.remove(infoFileLoc)
    # infoFile = open(infoFileLoc,'w')
    # for objectFile in os.listdir(pitchFolderLoc):
    #     if ".pitch" in objectFile:
    #         infoFile.write(objectFile.split("_")[0])
    #         infoFile.write("\t\t"+str(1))
    #         infoFile.write("\t\t"+objectFile.split("_")[0])
    #         artist = ""
    #         if "Chop" in objectFile:
    #             artist = "Chopin"
    #         elif "Moz" in objectFile:
    #             artist = "Mozart"
    #         elif "Haydn" in objectFile:
    #             artist = "Haydn"
    #         elif "Bach" in objectFile:
    #             artist = "Bach"
    #         elif "Beet" in objectFile:
    #             artist = "Beethoven"
    #         elif "Schumann" in objectFile:
    #             artist = "Schumann"
    #         elif "Moz" in objectFile:
    #             artist = "Mozart"
    #         else:
    #             artist = "Tchaikovsky"
    #         infoFile.write("\t\t" + artist)
    #         infoFile.write("\t\t"+"1800\n")
    # infoFile.close()


def generatePitchName3():
    # infoFile = open(infoFileLoc, 'w')
    for objectFile in os.listdir(midiFolderLoc):
        if ".mid" in objectFile or ".midi" in objectFile:
            # print objectFile
            pitchNames = subprocess.check_output([midiConverterLoc, os.path.join(midiFolderLoc, objectFile)])
            outputFileLoc = pitchFolderLoc + "/" + objectFile.split(".")[0] + "_dt.pitch"
            if "Error" not in pitchNames:
                if os.path.exists(outputFileLoc):
                    os.remove(outputFileLoc)
                outputFile = open(outputFileLoc, 'w')
                outputFile.write(pitchNames)
                outputFile.close()

                # infoFile.write(objectFile.split(".")[0])
                # infoFile.write("\t\t"+str(1))
                # infoFile.write("\t\t"+objectFile.split(".")[0])
                # artist = ""
                # if "Chop" in objectFile:
                #     artist = "Chopin"
                # elif "Moz" in objectFile:
                #     artist = "Mozart"
                # elif "Haydn" in objectFile:
                #     artist = "Haydn"
                # elif "Bach" in objectFile:
                #     artist = "Bach"
                # elif "Beet" in objectFile:
                #     artist = "Beethoven"
                # elif "Schumann" in objectFile:
                #     artist = "Schumann"
                # elif "Moz" in objectFile:
                #     artist = "Mozart"
                # else:
                #     artist = "Tchaikovsky"
                # infoFile.write("\t\t" + artist)
                # infoFile.write("\t\t"+"1800\n")
    # infoFile.close()
def generatePitchName4():
    startingNumstep = 1400
    global midiFolderLoc
    global pitchFolderLoc
    for i in range(12):
        midiFolderLoc = folderLoc + "/numSteps"+str(startingNumstep+(i*100))
        pitchFolderLoc = midiFolderLoc
        generatePitchName3()
def driver1():
    arguments = sys.argv
    if len(arguments) != 4:
        print "Incorrect Arguments"
        return
    global pitchNameGeneratorFileLoc
    pitchNameGeneratorFileLoc = arguments[1]
    global melodyFolderLoc
    melodyFolderLoc = arguments[2]
    global outputFolderLoc
    outputFolderLoc = arguments[3]
    generatePitchName1()
def driver2():
    arguments = sys.argv
    if len(arguments) != 3:
        print "Incorrect Arguments"
        return

    global pitchFolderLoc
    pitchFolderLoc = arguments[1]
    global midiFolderLoc
    midiFolderLoc = arguments[2]
    generatePitchName2()
def driver3():
    arguments = sys.argv
    if len(arguments) != 4:
        print "Incorrect Arguments"
        return
    global midiConverterLoc
    midiConverterLoc = arguments[1]
    global midiFolderLoc
    midiFolderLoc = arguments[2]
    global pitchFolderLoc
    pitchFolderLoc = arguments[3]
    # global infoFileLoc
    # infoFileLoc = arguments[4]
    generatePitchName3()
def driver4():
    arguments = sys.argv
    if len(arguments) != 3:
        print "Incorrect Arguments"
        return
    global midiConverterLoc
    midiConverterLoc = arguments[1]
    global folderLoc
    folderLoc = arguments[2]
    generatePitchName4()
if __name__ == "__main__":
    driver3()