# Project in Scala about the Paper Solo: Lightweight Differential Privacy
## 1. Project Description:
Based on the paper ["Solo: A Lightweight Static Analysis for Differential Privacy"](https://arxiv.org/abs/2105.01632), we implement a dataset query scenario to compare normal queries without differential privacy, queries with differential privacy using Laplace/Gaussian noise and queries with differential privacy using SOLO typing system.
### 1.1 Scenario Description:
Given two neighboring [sales amounts datasets](https://raw.githubusercontent.com/OpenMined/PyDP/dev/examples/Tutorial_4-Launch_demo/data/01.csv) including names and sales amounts: `originalDataset` with 10 records and `modifiedDataset` with 9 records(missing the first record of the former one). We prepare three kinds of queries:

* Sum: "What is the sum of all sales amount? "
* Count: "How many records have sales amount of less than 100 Euro? "
* Average: "For people with less than 150 Euro sales amount, what is the average amount of those group?"

We implement three kinds of queries in 3 methods: normal queries without differential privacy, queries with Laplacian and Gaussian noise and queries with SOLO. Compared with common query mechanisms with differential privacy(adding noise), with SOLO we can not only add noise to hold differential privacy, but also encode sensitivity and privacy parameters in typing system to track and analyze. We reimplement SOLO using Scala 3 surrounding 2 aspects:
* Sensitivity analysis and test it with sum, count and average queries
* Privacy analysis and test privacy monads using sum queries with epsilon-differential privacy and (epsilon, delta)-differential privacy.
### 1.2 Development Environment:
* Scala 3.2.2
* VS Code(v1.77.1)
* Scala(Metals)(v1.22.0)

## 2. Result Analysis:
Since the core of differential privacy mechanisms is adding noise, which is a stochastic process, we only present one possible output of the program:
### 1. Sum queries:
1. Raw queries: 
```
Raw sum query on the original dataset is 733.87
Raw sum query on the modified dataset is 701.93
The expected difference between 2 queries is 31.94
The actual difference between 2 queries is 31.94
```
The difference between sum queries on original dataset and on modified dataset(actual value) is equal to the first record's sales amount in the original dataset(expected value), i.e., differential privacy doesn't hold.

2. Queries with Laplacian and Gaussian noise:

we set `sensitivity` as `upperbound = 200, epsilon = 1` for `addLaplaceNoise` function; `sensitivity` as `upperbound = 200, epsilon = 1, delta = 0.01` for `addGaussNoise` function. The reasons are as follows:
* Sensitivity of sum queries should be bounded by the maximum value of the dataset;
* Epsilon shouldn't be too large(>10) in order to control the loss of the usability;
* Delta should be negligible small(here take it as 1/n^2, where n is the size of the dataset).

Here is the output:
```
Sum query with Laplassian noise on the original dataset is 1205.86
Sum query with Laplassian noise on the modified dataset is 840.88
The expected difference between 2 queries is 31.94
The ectual difference between 2 queries is 364.98

Sum query with Gaussian noise on the original dataset is 758.86
Sum query with Gaussian noise on the modified dataset is 813.02
The expected difference between 2 queries is 31.94
The ectual difference between 2 queries is -54.16

```
We can see the differences between sum queries on 2 datasets with Laplacian and Gaussian noise are not relevant to the first record in the original dataset anymore. 

3. Queries with SOLO:

```
SOLO sum query on the original dataset is 1327.1
SOLO sum query on the modified dataset is 1336.33
The expected difference between 2 queries is 31.94
The actual difference between 2 queries is -9.23
The sensitivity of SOLO sum query on original dataset is: (originalDataset,200)
The sensitivity of SOLO sum query on modified dataset is: (modifiedDataset,200)
```
In addition to differential privacy still holds here, we can also track the sensitivity of the query as sensitive environment (source, sensitivity).
### 2. Count queries:
1. Raw queries: Still not differentially private.
```
Test for 3 kinds of count queries:
Raw count query on original dataset: the number of people whose sales amount is smaller than 100 is 7
Raw count query on modified dataset: the number of people whose sales amount is smaller than 100 is 6
the expected number in original dataset is 7
the expected number in modified dataset is 6
```
2. Queries with Laplacian and Gaussian noise:
we set `sensitivity = 1, epsilon = 1` for `addLaplaceNoise` function; `sensitivity = 1, epsilon = 1, delta = 0.01` for `addGaussNoise`, where the selection of epsilon and delta follow the same reasons, and count query always has sensitivity of 1.
```
Count query with Laplacian noise on original dataset: the number of people whose sales amount is smaller than 100 is 8
Count query with Laplacian noise on modified dataset: the number of people whose sales amount is smaller than 100 is 7

Count query with Gaussian noise on original dataset: the number of people whose sales amount is smaller than 100 is 8
Count query with Gaussian noise on modified dataset: the number of people whose sales amount is smaller than 100 is 7

```
3. Queries with SOLO:
```
Test for SOLO count queries:
SOLO count query on the original dataset is SDouble(Disc,SEnv(List((originalDataset,Succ(Zero)))),8.0)
SOLO count query on the modified dataset is SDouble(Disc,SEnv(List((modifiedDataset,Succ(Zero)))),7.0)
we can see actual query results are not relevant to the expected results, but could still keep partial usability.
```
The result matches our expectation, i.e., count queries should have Disc metric with the sensitivity of 1.
### 3. Average queries:
We select average queries in order to present sequential composition property of differential privacy, since average queries can be considered as sum/count queries. 
1. Raw queries:
```
Raw average query on original dataset: the average sales amount of people whose sales amount is smaller than 150 is 60.3
Raw average query on modified dataset: the average sales amount of people whose sales amount is smaller than 150 is 63.85
the expected average in original dataset is 60.3
the expected average in modified dataset is 63.85
```
2. Queries with Laplacian and Gaussian noise:
```
Average query with Laplacian noise on original dataset: the average sales amount of people whose sales amount is smaller than 150 is 94.66
Average query with Laplacian noise on modified dataset: the average sales amount of people whose sales amount is smaller than 150 is 90.85

Average query with Gaussian noise on original dataset: the average sales amount of people whose sales amount is smaller than 150 is 57.79
Average query with Gaussian noise on modified dataset: the average sales amount of people whose sales amount is smaller than 150 is 45.22
```
3. Queries with SOLO:
```
Test for SOLO average queries:
SOLO average query on the original dataset is 139.9087158891521
SOLO average query on the modified dataset is 147.4265436604751
The sensitivity of SOLO average query on original dataset is: (originalDataset,150)
The sensitivity of SOLO average query on modified dataset is: (modifiedDataset,150)
```
we can tell from the result of SOLO average queries that sensitivity of average queries equals to sensitivity of sum/sensitivity of count.

### 4. Test for EpsPrivacyMonad
```
Test for SOLO EpsPrivacyMonad:
The sum of embedding privacy environment and adding Laplacian Noise to the original dataset with epsilon-differential privacy using EpsPrivacyMonad is 836.712209385788 with EpsPrivEnv(List((originalDataset,EpsCost(1,1))))
The sum of embedding privacy environment and adding Laplacian Noise to the original dataset with epsilon-differential privacy using EpsPrivacyMonad is 748.8885317232591 with EpsPrivEnv(List((modifiedDataset,EpsCost(1,1))))
```
### 5. Test for EDPrivacyMonad
```
Test for SOLO EDPrivacyMonad:
The sum of embedding privacy environment and adding Gaussian Noise to the original dataset with (epsilon,delta)-differential privacy using EDPrivacyMonad is 1106.2397868003964 with EDEnv(List((originalDataset,Finite(1.0),Finite(0.01))))
The sum of embedding privacy environment and adding Gaussian Noise to the modified dataset with (epsilon,delta)-differential privacy using EDPrivacyMonad is 825.9160551248988 with EDEnv(List((modifiedDataset,Finite(1.0),Finite(0.01))))
```
