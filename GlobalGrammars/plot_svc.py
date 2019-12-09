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
    out = ax.contourf(xx, yy, Z, **params)
    return out


# import some data to play with
# Take the first two features. We could avoid this by using a two-dim dataset
X = np.array([[0.24523961315187967,0.057586604533063766],[0.3212927879838592,-0.02554767199706979],[0.19081111463793068,0.023310744319871124],[0.21586317203681715,0.03493350191369294],[0.2683381476753309,0.04207369028182724],[0.35683143815452534,-0.0018353903741285182],[0.2908059213834449,-0.007354292205374791],[0.2049168590952193,0.0017699430496420404],[0.2422103431008233,0.07533641914146527],[0.27116511167710156,0.013296082428763926],[0.2745708272191646,-0.010902028746781327],[0.2646499595390252,-0.0341737895614293],[-0.17986854110181627,0.22984317857494296],[-0.21861930090256182,-0.00895322339251408],[-0.13057997711833544,0.16374348066301095],[-0.1613715342035953,0.10813193750633124],[-0.16237405159118545,0.08745706704280826],[-0.2272386299171746,0.18495665110908638],[-0.2304751363038704,0.0010212367030908514],[-0.2259212226201212,0.19449217096345536],[-0.2637624290464315,0.023814697714240055],[-0.21953873867204451,0.05539758608353559],[-0.1894606274918123,-0.27611677975976107],[-0.19884253308525357,-0.21331503837110355],[-0.1964498285668979,-0.3319223939203297],[-0.20885021023936096,0.2003188244556753],[-0.2269744441266521,0.031513188387706774],[-0.23531674962292498,-0.3030027510477824],[-0.011278488838065849,0.08053502803654732],[0.01260677831059059,0.052213333027895034],[-0.02862899997675972,0.12395760738351738],[0.1297485077548522,0.04663928173693188],[-0.12448335863347663,0.19963939900431937],[-0.08696219049544379,0.05939048365042492],[-0.03485882785107351,0.08165312635040424],[-0.06215137293971023,0.04066418933111365],[-0.02532182510019169,-0.11387908573741168]])
y = np.array([0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2])
print type(X)
print type(y)
print (X.shape)
print (y.shape)
print X
print y
# we create an instance of SVM and fit out data. We do not scale our
# data since we want to plot the support vectors
C = 50  # SVM regularization parameter
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
outputFolder = "/Users/Tiasa/Dropbox/UNIVERSITYofWATERLOO/Research/GlobalGrammars/tests"
X0, X1 = X[:, 0], X[:, 1]
xx, yy = make_meshgrid(X0, X1)
i = 0
for clf, plotFileName, title in zip(models, plotFileNames, titles):#, sub.flatten()):
    fig = plt.figure(i)
    i = i+1
    ax = fig.add_subplot(1, 1, 1)
    plot_contours(ax, clf, xx, yy,
                  cmap=plt.cm.coolwarm, alpha=0.8)
    ax.scatter(X0, X1, c=y, cmap=plt.cm.coolwarm, s=20, edgecolors='k')
    ax.set_xlim(xx.min(), xx.max())
    ax.set_ylim(yy.min(), yy.max())
    ax.set_xlabel('Feature 1')
    ax.set_ylabel('Feature 2')
    ax.set_xticks(())
    ax.set_yticks(())
    ax.set_title(title)
    fig.savefig(outputFolder + '/' + plotFileName + '.png', bbox_inches="tight")
    plt.close(fig)

