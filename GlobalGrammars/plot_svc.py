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
X = np.array([[-0.0240631753197078,-0.2990462866965766],[0.31338337701716545,-0.003062631066558111],[0.23734230715551424,0.12464314863499591],[0.05987546399905694,-0.21635964595991694],[-0.05339560472437975,0.15286026715229967],[0.3262249081728731,0.04316127294372796],[0.2158393246095433,-0.027583797035016504],[-0.06019512413984161,-0.30670377432779333],[-0.02901008889601607,0.2055256419269452],[0.21442245712064334,-0.03415466718842385],[-0.08817092658641001,0.1826517125906389],[0.2897540308975429,0.009783569299663984],
[-0.1722406598035309,0.14115049149472778],[-0.14158417762960407,0.13979235089051034],[-0.14999825612979537,-0.06467796011148931],[-0.07128572146274145,0.24684109749584757],[-0.13731594183725712,-0.15426098386644513],[-0.11953540320962494,-0.22274100314343806],[-0.1369708105288605,0.06468883280045155]])
y = np.array([0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1])

# we create an instance of SVM and fit out data. We do not scale our
# data since we want to plot the support vectors
C = 1 # SVM regularization parameter
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

