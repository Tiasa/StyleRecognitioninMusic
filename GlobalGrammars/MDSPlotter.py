import numpy as np
import matplotlib.pyplot as plt
from matplotlib.pyplot import cm
import subprocess
import sys
# from mpl_toolkits.mplot3d import axes3d, Axes3D

def getDefName(definition):
    if definition == 1:
        return "d(x,y) = max {K(x|y*),K(y|x*)} / max{K(x), K(y)}"
    elif definition == 2:
        return "d(x,y) = (K(x|y*) + K(y|x*))/K(x,y)"
    elif definition == 4:
        return "d(x,y)=(K(xy)-min{K(x),K(y)})/ max{K(x),K(y)}"
    else:
        return "d(x,y) = (K(x|y*) + K(y|x*))/K(x)+K(y)"

def TwoDimPlotter(output, plotFileName, outputFolder ,labelAxes):
    for fileNo in range(len(output)):
        dataPoints = []
        artists = []
        with open(output[fileNo]) as MDSFile:
            for line in MDSFile:
                if len(line) > 0:
                    x = []
                    y = []
                    line = line.split()
                    artists.append(" ".join(line[0].split("_")))
                    i = 1
                    while i < len(line):
                        x.append(float(line[i]))
                        y.append(float(line[i+1]))
                        i+=2
                    dataPoints.append((np.asarray(x),np.asarray(y)))
        cmap = plt.get_cmap('gnuplot')
        colors =["red","blue","green","orange","black","violet"]#[cmap(i) for i in np.linspace(0, 1, len(artists))]#
        fig = plt.figure(fileNo)
        ax = fig.add_subplot(1, 1, 1, facecolor="1.0")
        for i in range(len(artists)):
            #if artists[i]=="The Beatles" or artists[i]=="Prince" or artists[i]=="Elvis Presley" or artists[i]=="Simon and Garfunkel":
            x, y = dataPoints[i]
            color = colors[i]
            artist = artists[i]
            ax.scatter(x, y, alpha=1, c=color,
                       edgecolors='none', s=30, label=artist)
            #ax.annotate(artist, (x[0], y[0]))
        plt.title(getDefName(int(output[fileNo].split(".")[0][-1])))
        box = ax.get_position()
        ax.set_position([box.x0, box.y0 + box.height * 0.1,
                         box.width, box.height * 0.9])
        ax.legend(loc='upper center', bbox_to_anchor=(0.5, -0.05),
                  fancybox=True, shadow=True, ncol=len(artists))
        if labelAxes:
            ax.set_xlabel('Vocal Distance')
            ax.set_ylabel('Melody Distance')
        fig.savefig(outputFolder+'/'+plotFileName+str(fileNo)+'.png',bbox_inches = "tight")
        plt.close(fig)

def create3DPlot(pitchFolderLoc):
    output = [pitchFolderLoc+'/3DScalingWithDef1.txt',pitchFolderLoc+'/3DScalingWithDef2.txt',pitchFolderLoc+'/3DScalingWithDef3.txt',pitchFolderLoc+'/3DScalingWithDef4.txt']
    for fileNo in range(len(output)):
        dataPoints = []
        artists = []
        with open(output[fileNo]) as MDSFile:
            for line in MDSFile:
                if len(line) > 0:
                    x = []
                    y = []
                    z = []
                    line = line.split()
                    artists.append(" ".join(line[0].split("_")))
                    i = 1
                    while i < len(line):
                        x.append(float(line[i]))
                        y.append(float(line[i + 1]))
                        z.append(float(line[i + 2]))
                        i += 3
                    dataPoints.append((np.asarray(x), np.asarray(y), np.asarray(z)))
        colors = cm.rainbow(np.linspace(0,1,len(artists))) #["red","blue","green","black"]#
        fig = plt.figure(fileNo)
        ax = fig.add_subplot(111, projection='3d')
        for i in range(len(artists)):
            x, y, z = dataPoints[i]
            color = colors[i]
            artist = artists[i]
            ax.scatter(x, y, z, c=color,
                       label=artist)
        plt.title(getDefName(int(output[fileNo].split(".")[0][-1])))
        box = ax.get_position()
        ax.set_position([box.x0, box.y0 + box.height * 0.1,
                         box.width, box.height * 0.9])
        ax.legend(loc='upper center', bbox_to_anchor=(0.5, -0.05),
                  fancybox=True, shadow=True, ncol=len(artists))
        fig.savefig(pitchFolderLoc + '/Clustering3D' + str(fileNo) + '.png')

        ax.set_xlabel('X Coordinate')
        ax.set_ylabel('Y Coordinate')
        ax.set_zlabel('Z Coordinate')

        # plt.show()
        plt.close(fig)



def create2DPlot(pitchFolderLoc):

    output = [pitchFolderLoc+'/2DScalingWithDef2.txt']
    TwoDimPlotter(output,'MultiDimScaling',pitchFolderLoc,False)




def create2DPlotRock(outputFolderLoc):
    # outputE = [outputFolderLoc+'/2DEuclidean4ArtistsWithDef2.txt']
    # TwoDimPlotter(outputE, 'Euclidean4',outputFolderLoc, False)
    # outputM = [outputFolderLoc+'/2DManhattan4ArtistsWithDef2.txt']
    # TwoDimPlotter(outputM, 'Manhattan4', outputFolderLoc, False)
    # outputP = [outputFolderLoc+'/2DProjections4ArtistsWithDef2.txt']
    # TwoDimPlotter(outputP, 'Projections4', outputFolderLoc, True)

    outputE = [outputFolderLoc + '/2DEuclidean2ArtistsWithDef2.txt']
    TwoDimPlotter(outputE, 'Euclidean2',outputFolderLoc, False)
    outputM = [outputFolderLoc + '/2DManhattan2ArtistsWithDef2.txt']
    TwoDimPlotter(outputM, 'Manhattan2',outputFolderLoc, False)
    outputP = [outputFolderLoc + '/2DProjections2ArtistsWithDef2.txt']
    TwoDimPlotter(outputP, 'Projections2',outputFolderLoc, True)

def testCombinedApproximationAccuracy(phylipGeneratorLoc, folderLoc):
    startingNumstep = 1400
    datapoints = []
    kxyDefinition = ["K(x,y) = K(x100000y)","K(x,y) = K(x)+K(y)"]
    for definition in range(2):
        x = []
        y = []
        for i in range(12):
            numSteps = startingNumstep + (i * 100)
            pitchFolderLoc = folderLoc + "/numSteps" + str(numSteps)
            file1 = pitchFolderLoc + "/1_dt.pitch"
            file2 = pitchFolderLoc + "/2_dt.pitch"
            output = subprocess.check_output(['java', '-classpath',
                                         phylipGeneratorLoc + '/mdsj.jar:' + phylipGeneratorLoc,
                                         'GrammarTest',
                                         str(definition+1),
                                         file1,
                                         file2])
            x.append(numSteps)
            y.append(float(output.strip()))
        datapoints.append((np.asarray(x), np.asarray(y)))
    cmap = plt.get_cmap('gnuplot')
    colors = ["red", "blue", "green", "orange", "black",
              "violet"]  # [cmap(i) for i in np.linspace(0, 1, len(artists))]#
    fig = plt.figure(1)
    ax = fig.add_subplot(1, 1, 1) #, facecolor="1.0")
    for i in range(2):
        # if artists[i]=="The Beatles" or artists[i]=="Prince" or artists[i]=="Elvis Presley" or artists[i]=="Simon and Garfunkel":
        x, y = datapoints[i]
        color = colors[i]
        kxyDef = kxyDefinition[i]
        ax.scatter(x, y, alpha=1, c=color,
                   edgecolors='none', s=30, label=kxyDef)
        # ax.annotate(artist, (x[0], y[0]))
    plt.title('Testing Accuracy of K(x,y) Definitions')
    box = ax.get_position()
    ax.set_position([box.x0, box.y0 + box.height * 0.1,
                     box.width, box.height * 0.9])
    ax.legend(loc='upper center', bbox_to_anchor=(0.5, -0.05),
              fancybox=True, shadow=True, ncol=2)

    ax.set_xlabel('Number of Steps')
    ax.set_ylabel('K complexity')
    plt.show()
    #fig.savefig(outputFolder + '/' + plotFileName + str(fileNo) + '.png', bbox_inches="tight")
    #plt.close(fig)
if __name__=='__main__':
    arguments = sys.argv
    # if len(arguments) == 3:
    #     phylipGeneratorLoc = arguments[1]
    #     folderLoc = arguments[2]
    #     testCombinedApproximationAccuracy(phylipGeneratorLoc,folderLoc)

    if len(arguments) == 3:
        phylipGeneratorLoc = arguments[1]
        pitchFolderLoc = arguments[2]
        subprocess.check_output(['java', '-classpath',
                                phylipGeneratorLoc + '/mdsj.jar:' + phylipGeneratorLoc,
                                'Analyzer',
                                pitchFolderLoc])
        #print output
        create2DPlot(pitchFolderLoc)


    # if len(arguments) ==5:
    #     phylipGeneratorLoc = arguments[1]
    #     outputFolderLoc = arguments[2]
    #     vocalFolderLoc = arguments[3]
    #     melodyFolderLoc = arguments[4]
    #     subprocess.check_output(['java', '-classpath',
    #                              phylipGeneratorLoc + '/mdsj.jar:' + phylipGeneratorLoc,
    #                              'Analyzer',
    #                              outputFolderLoc,
    #                              vocalFolderLoc,
    #                              melodyFolderLoc])
    #     create2DPlotRock(outputFolderLoc)


    # if len(arguments) == 4:
    #     phylipGeneratorLoc = arguments[1]
    #     referenceFileLoc = arguments[2]
    #     pitchFolderLoc = arguments[3]
    #     subprocess.check_output(['java', '-classpath',
    #                              phylipGeneratorLoc + '/mdsj.jar:' + phylipGeneratorLoc,
    #                              'Analyzer', referenceFileLoc,
    #                              pitchFolderLoc])
    #     create2DPlot(pitchFolderLoc)

