"""
==================================================
Plot different SVM classifiers in the iris dataset
==================================================

Comparison of different linear SVM classifiers on a 2D projection of the iris
dataset. We only consider the first 2 features of this dataset:

- Sepal length
- Sepal width

This example shows how to plot the decision surface for four SVM classifiers
with different kernels.

The linear models ``LinearSVC()`` and ``SVC(kernel='linear')`` yield slightly
different decision boundaries. This can be a consequence of the following
differences:

- ``LinearSVC`` minimizes the squared hinge loss while ``SVC`` minimizes the
  regular hinge loss.

- ``LinearSVC`` uses the One-vs-All (also known as One-vs-Rest) multiclass
  reduction while ``SVC`` uses the One-vs-One multiclass reduction.

Both linear models have linear decision boundaries (intersecting hyperplanes)
while the non-linear kernel models (polynomial or Gaussian RBF) have more
flexible non-linear decision boundaries with shapes that depend on the kind of
kernel and its parameters.

.. NOTE:: while plotting the decision function of classifiers for toy 2D
   datasets can help get an intuitive understanding of their respective
   expressive power, be aware that those intuitions don't always generalize to
   more realistic high-dimensional problems.

"""
print(__doc__)

import numpy as np
import matplotlib.pyplot as plt
from sklearn import svm, datasets


def make_meshgrid(x, y, h=.02):
    """Create a mesh of points to plot in

    Parameters
    ----------
    x: data to base x-axis meshgrid on
    y: data to base y-axis meshgrid on
    h: stepsize for meshgrid, optional

    Returns
    -------
    xx, yy : ndarray
    """
    x_min, x_max = x.min() - 1, x.max() + 1
    y_min, y_max = y.min() - 1, y.max() + 1
    xx, yy = np.meshgrid(np.arange(x_min, x_max, h),
                         np.arange(y_min, y_max, h))
    return xx, yy


def plot_contours(ax, clf, xx, yy, **params):
    """Plot the decision boundaries for a classifier.

    Parameters
    ----------
    ax: matplotlib axes object
    clf: a classifier
    xx: meshgrid ndarray
    yy: meshgrid ndarray
    params: dictionary of params to pass to contourf, optional
    """
    Z = clf.predict(np.c_[xx.ravel(), yy.ravel()])
    Z = Z.reshape(xx.shape)
    out = ax.contour(xx, yy, Z, **params)
    return out


# import some data to play with
# Take the first two features. We could avoid this by using a two-dim dataset
X = np.array([[-0.09247323389218268,0.2466294321752115],[-0.07027286949439165,-0.1937072370144891],[-0.08720732800317273,-0.029934856041615394],[-0.08074463879940522,0.11630926398102068],[-0.09637792224964818,0.10159370167427159],[-0.07521878051138581,0.08412608578635243],[-0.07955571304330854,-0.011660522772630192],[-0.06487598576345029,-0.0042649355819467695],[-0.05008535270242511,-0.1552554387432704],[-0.09642177837023595,0.16853317638513998],[-0.04270432865114445,0.013893490868870368],[-0.10347991690009879,0.15371940908002663],[-0.06076305572130878,-0.04357144366871341],[-0.08287456276153873,0.11636956699286774],[-0.06007840929087117,-0.15543620854157641],
[0.35514641532265456,-0.004697827961873162],[0.3842479938637321,0.04680905438491205],[0.3589487012070534,-0.0026906534527814004],[0.4114314599617854,0.0398872550616426],[0.3426255278985785,-0.02840620533573058],[0.3271808190495068,0.015722954320726897],[0.390851926621084,-0.009392487550451557],[0.3518286841915472,0.019937508631402263],[0.39133833717829253,0.042064375486458694],[0.14618814687559561,-0.135832691971789],[0.10005726519763576,-0.2367890054006197],[-0.01655369791293951,-0.26037178147915935],[0.0946032069277204,-0.257868103057796],[-0.03904932911580573,-0.29758271582378226],[0.054355647640420036,-0.1779406703726536]])
y = np.array([0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1])
y2 = np.array([0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1])
artists = ["Nocturnes", "Preludes"]
# we create an instance of SVM and fit out data. We do not scale our
# data since we want to plot the support vectors
C = 0.5# SVM regularization parameter
models = (svm.SVC(kernel='linear', C=C),
          svm.LinearSVC(C=C, max_iter=10000),
          svm.SVC(kernel='rbf', gamma=0.7, C=C))
models = (clf.fit(X, y) for clf in models)

# title for the plots
titles = ('SVC with linear kernel',
          'LinearSVC (linear kernel)',
          'SVC with RBF kernel')
plotFileNames = ('SVC_with_linear_kernel','LinearSVC','SVC_with_RBF_kernel')
# Set-up 2x2 grid for plotting.
# fig, sub = plt.subplots(3, 1)
# plt.subplots_adjust(wspace=0.4, hspace=0.4)
outputFolder = "/Users/Tiasa/Dropbox/UNIVERSITYofWATERLOO/Research/music/ISMIR Graphs"
X0, X1 = X[:, 0], X[:, 1]
xx, yy = make_meshgrid(X0, X1)
i = 0
for clf, plotFileName, title in zip(models, plotFileNames, titles):#, sub.flatten()):
    fig = plt.figure(i)
    i = i+1
    ax = fig.add_subplot(1, 1, 1)
    plot_contours(ax, clf, xx, yy,
                  cmap=plt.cm.coolwarm, alpha=0.8)

    currentArtist = y[0]
    X0 = []
    X1 = []
    colors = ["red", "blue", "green", "orange", "black", "violet"]
    for i in range(len(y2)):
        if (y2[i]==currentArtist):
            X0.append(X[i][0])
            X1.append(X[i][1])
        else:
            color = colors[currentArtist]
            ax.scatter(X0, X1, alpha=1, c=color,  s=30, edgecolors='none',label=artists[currentArtist])
            currentArtist = y2[i]
            X0 = []
            X1 = []
    color = colors[currentArtist]
    ax.scatter(X0, X1, alpha=1, c=color, s=30, edgecolors='none', label=artists[currentArtist])
    ax.set_xlim(xx.min(), xx.max())
    ax.set_ylim(yy.min(), yy.max())
    ax.set_xlabel('Feature 1')
    ax.set_ylabel('Feature 2')
    ax.set_xticks(())
    ax.set_yticks(())
    ax.set_title(title)
    box = ax.get_position()
    # ax.legend()
    ax.set_position([box.x0, box.y0 + box.height * 0.1,
                     box.width, box.height * 0.9])
    # ax.legend(loc='lower right', fancybox=True, shadow=True)
    ax.legend(loc='upper center', bbox_to_anchor=(0.5, -0.05),
              fancybox=True, shadow=True, ncol=len(artists))
    #plt.show()
    fig.savefig(outputFolder + '/' + plotFileName + '.png', bbox_inches="tight")
    plt.close(fig)

