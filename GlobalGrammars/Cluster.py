# -*- coding: utf-8 -*-
"""
===================================
Demo of DBSCAN clustering algorithm
===================================

Finds core samples of high density and expands clusters from them.

"""
print(__doc__)

import numpy as np

from sklearn.cluster import DBSCAN
from sklearn import metrics
from sklearn.datasets.samples_generator import make_blobs
from sklearn.preprocessing import StandardScaler

from sklearn import svm
# #############################################################################
# Generate sample data
# centers = [[0, 0], [-0.3, -0.3], [0, -0.3]]
# X =
# labels_true = [0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2]
# X, labels_true = make_blobs(n_samples=750, centers=centers, cluster_std=0.4,
#                             random_state=0)
# X = [[0.24524728701995696,0.06463235084405204],[0.32123494347456377,-0.0378753439107393],[0.19089810916790356,0.034930677222124236],[-0.062103922371273504,0.05076063210602448],[-0.025210581190727673,-0.1054606632201176]]
X = np.array([[0.24523961315187967,0.057586604533063766],[0.3212927879838592,-0.02554767199706979],[0.19081111463793068,0.023310744319871124],[0.21586317203681715,0.03493350191369294],[0.2683381476753309,0.04207369028182724],[0.35683143815452534,-0.0018353903741285182],[0.2908059213834449,-0.007354292205374791],[0.2049168590952193,0.0017699430496420404],[0.2422103431008233,0.07533641914146527],[0.27116511167710156,0.013296082428763926],[0.2745708272191646,-0.010902028746781327],[0.2646499595390252,-0.0341737895614293],[-0.17986854110181627,0.22984317857494296],[-0.21861930090256182,-0.00895322339251408],[-0.13057997711833544,0.16374348066301095],[-0.1613715342035953,0.10813193750633124],[-0.16237405159118545,0.08745706704280826],[-0.2272386299171746,0.18495665110908638],[-0.2304751363038704,0.0010212367030908514],[-0.2259212226201212,0.19449217096345536],[-0.2637624290464315,0.023814697714240055],[-0.21953873867204451,0.05539758608353559],[-0.1894606274918123,-0.27611677975976107],[-0.19884253308525357,-0.21331503837110355],[-0.1964498285668979,-0.3319223939203297],[-0.20885021023936096,0.2003188244556753],[-0.2269744441266521,0.031513188387706774],[-0.23531674962292498,-0.3030027510477824],[-0.011278488838065849,0.08053502803654732],[0.01260677831059059,0.052213333027895034],[-0.02862899997675972,0.12395760738351738],[0.1297485077548522,0.04663928173693188],[-0.12448335863347663,0.19963939900431937],[-0.08696219049544379,0.05939048365042492],[-0.03485882785107351,0.08165312635040424],[-0.06215137293971023,0.04066418933111365],[-0.02532182510019169,-0.11387908573741168]])
labels_true = np.array([0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2])
print(type(X))
print(type(labels_true))
print(X.shape)
print(labels_true.shape)
X = StandardScaler().fit_transform(X)

# #############################################################################
# Compute DBSCAN
db = DBSCAN(eps=0.6, min_samples=4).fit(X)
#db.labels_ = labels_true
core_samples_mask = np.zeros_like(db.labels_, dtype=bool)
core_samples_mask[db.core_sample_indices_] = True
labels = labels_true
print(labels)
# Number of clusters in labels, ignoring noise if present.
n_clusters_ = len(set(labels)) - (1 if -1 in labels else 0)
n_noise_ = list(labels).count(-1)

print('Estimated number of clusters: %d' % n_clusters_)
print('Estimated number of noise points: %d' % n_noise_)
print("Homogeneity: %0.3f" % metrics.homogeneity_score(labels_true, labels))
print("Completeness: %0.3f" % metrics.completeness_score(labels_true, labels))
print("V-measure: %0.3f" % metrics.v_measure_score(labels_true, labels))
print("Adjusted Rand Index: %0.3f"
      % metrics.adjusted_rand_score(labels_true, labels))
print("Adjusted Mutual Information: %0.3f"
      % metrics.adjusted_mutual_info_score(labels_true, labels))
print("Silhouette Coefficient: %0.3f"
      % metrics.silhouette_score(X, labels))

# #############################################################################
# Plot result
import matplotlib.pyplot as plt

# Black removed and is used for noise instead.
unique_labels = set(labels)
colors = [plt.cm.Spectral(each)
          for each in np.linspace(0, 1, len(unique_labels))]
for k, col in zip(unique_labels, colors):
    if k == -1:
        # Black used for noise.
        col = [0, 0, 0, 1]

    class_member_mask = (labels == k)

    xy = X[class_member_mask & core_samples_mask]
    plt.plot(xy[:, 0], xy[:, 1], 'o', markerfacecolor=tuple(col),
             markeredgecolor='k', markersize=14)

    xy = X[class_member_mask & ~core_samples_mask]
    plt.plot(xy[:, 0], xy[:, 1], 'o', markerfacecolor=tuple(col),
             markeredgecolor='k', markersize=6)

plt.title('Estimated number of clusters: %d' % n_clusters_)
plt.show()
