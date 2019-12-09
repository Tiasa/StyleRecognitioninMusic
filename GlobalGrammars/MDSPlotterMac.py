import numpy as np
import matplotlib.pyplot as plt
from matplotlib.pyplot import cm
import subprocess
import sys
from mpl_toolkits.mplot3d import axes3d, Axes3D

def getDefName(definition):
    if definition == 1:
        return "NCD(x,y) = max {C(x|y*),C(y|x*)} / max{C(x), C(y)}"
    elif definition == 2:
        return "NCD(x,y) = (C(x|y*) + C(y|x*))/C(x,y)"
    else:
        return "NCD(x,y) = (C(x|y*) + C(y|x*))/C(x)+C(y)"

def TwoDimPlotter(output, plotFileName, labelAxes):
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
        plt.title('Clustering with '+ getDefName(int(output[fileNo].split(".")[0][-1])))
        box = ax.get_position()
        ax.set_position([box.x0, box.y0 + box.height * 0.1,
                         box.width, box.height * 0.9])
        ax.legend(loc='upper center', bbox_to_anchor=(0.5, -0.05),
                  fancybox=True, shadow=True, ncol=len(artists))
        if labelAxes:
            ax.set_xlabel('Vocal Distance')
            ax.set_ylabel('Melody Distance')
        fig.savefig(outputFolderLoc+'/'+plotFileName+str(fileNo)+'.png',bbox_inches = "tight")
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
        plt.title('Clustering with ' + getDefName(int(output[fileNo].split(".")[0][-1])))
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

    output = [pitchFolderLoc+'/2DScalingWithDef2.txt',pitchFolderLoc+'/2DScalingWithDef3.txt']
    TwoDimPlotter(output,'Clustering2D',False)




def create2DPlotRock(outputFolderLoc):
    outputE = [outputFolderLoc+'/2DEuclidean2ArtistsWithDef2.txt']
    TwoDimPlotter(outputE, 'Euclidean', False)
    outputM = [outputFolderLoc+'/2DManhattan2ArtistsWithDef2.txt']
    TwoDimPlotter(outputM, 'Manhattan', False)
    outputP = [outputFolderLoc+'/2DProjections2ArtistsWithDef2.txt']
    TwoDimPlotter(outputP, 'Projections', True)

if __name__=='__main__':
    arguments = sys.argv
    if len(arguments) == 3:
        phylipGeneratorLoc = arguments[1]
        pitchFolderLoc = arguments[2]
#        subprocess.check_output(['java', '-classpath',
#                                 phylipGeneratorLoc + '/mdsj.jar:' + phylipGeneratorLoc,
#                                 'Analyzer',
#                                 pitchFolderLoc])
        create2DPlot(pitchFolderLoc)

    if len(arguments) ==5:
        phylipGeneratorLoc = arguments[1]
        outputFolderLoc = arguments[2]
        vocalFolderLoc = arguments[3]
        melodyFolderLoc = arguments[4]
        # subprocess.check_output(['java', '-classpath',
        #                          phylipGeneratorLoc + '/mdsj.jar:' + phylipGeneratorLoc,
        #                          'Analyzer',
        #                          outputFolderLoc,
        #                          vocalFolderLoc,
        #                          melodyFolderLoc])
        create2DPlotRock(outputFolderLoc)
