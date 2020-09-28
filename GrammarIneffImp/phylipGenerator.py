import subprocess
import sys
import numpy as np
import pickle
import os
import multiprocessing as mp
from parse_midi import parse_midi

#gets the Kolmogorov complexity from a file
def getKolmogorovComplexity(fileName):
    result = 0.0
    with open(fileName) as grammarFile:
        for line in grammarFile:
            if len(line) > 0 and line.split(":")[0].strip()=="Complexity":
                result = float(line.split(":", 1)[1].strip())
    return result


# Combines the contents of two files.
def combine(file1, file2, combinedFolderLoc):
    combinedFileLoc = combinedFolderLoc+"/"+file1.split("/")[-1].split(".")[0] +file2.split("/")[-1]
    if not os.path.exists(combinedFileLoc):
        combinedFile = open(combinedFileLoc, 'w')
        with open(file1) as refFile:
            for line in refFile:
                if len(line) > 0:
                    combinedFile.write(line)
        with open(file2) as refFile:
            for line in refFile:
                if len(line) > 0:
                    combinedFile.write(line)
    return combinedFileLoc


def generateSmallestGrammar(fileName):
    subprocess.call(['java',
                     '-cp', javaPackageLoc,'GenerateCFG',
                     '-File', fileName])
    return 0

def ncd(ka, kb, kab):
    return (kab - min(ka, kb)) / max(ka, kb)

def generatePhylip():
    # Generate individual Grammar
    objectFiles = []
    numObjects = 0
    for objectFile in os.listdir(objectsFolderLoc):
        if ".pitch" in objectFile:
            numObjects += 1
            objectFiles.append(os.path.join(objectsFolderLoc, objectFile))
    pool = mp.Pool(processes=4)
    pool.map(generateSmallestGrammar, objectFiles)
    pool.close()
    pool.join()

    #Generate Combined Grammar
    combinedObjectFiles = []
    combinedObjectsDirectory = objectsFolderLoc + "/combined"
    if not os.path.exists(combinedObjectsDirectory):
        os.makedirs(combinedObjectsDirectory)
    for refFile1 in objectFiles:
        for refFile2 in objectFiles:
            combinedObjectFiles.append(combine(refFile1,refFile2,combinedObjectsDirectory))

    pool = mp.Pool(processes=4)
    pool.map(generateSmallestGrammar, combinedObjectFiles)
    pool.close()
    pool.join()

    # Time for the Phylip format
    # Create a file
    extension = ".Grammar"
    phylipFileLoc = matrixLocation + "/phylipDistanceMatrix.txt"
    if os.path.exists(phylipFileLoc):
        os.remove(phylipFileLoc)
    phylipFile = open(phylipFileLoc,'w')
    # Write the number of objects
    phylipFile.write(str(numObjects)+"\n")
    for idx1, refFile1 in enumerate(objectFiles):
        phylipFile.write(refFile1.split("/")[-1].split(".")[0])
        ka = getKolmogorovComplexity(refFile1.split(".")[0] + extension)
        for idx2, refFile2 in enumerate(objectFiles):
            kb = getKolmogorovComplexity(refFile2.split(".")[0]+extension)
            combinedFileLoc = combinedObjectsDirectory + "/" + refFile1.split("/")[-1].split(".")[0] + refFile2.split("/")[-1]
            kab = getKolmogorovComplexity(combinedFileLoc.split(".")[0]+extension)
            distance = ncd(ka,kb,kab)
            if distance < 0:
                distance = 0
            phylipFile.write(" "+str("{0:.2f}".format(distance)))
        phylipFile.write("\n")


def driver():
    arguments = sys.argv
    if len(arguments) != 3:
        print "Incorrect Arguments"
        return
    global matrixLocation
    matrixLocation = arguments[0].rsplit("/", 1)[0]
    global javaPackageLoc
    javaPackageLoc = arguments[1]
    global objectsFolderLoc
    objectsFolderLoc = arguments[2]
    generatePhylip()

# PHYLIP generator outputs a distance matrix in the PHYLIP format
# That can be used to make a phylogenetic tree
if __name__ == "__main__":
    driver()