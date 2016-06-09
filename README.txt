Group 11:
Uzman
Yuce
Xiao
Liuang

The program consists of 3 classes.
KMeans.java: the main Class file to run the algorithm; reads from FILE and prints
Iris.java: Runs the KMeans algorithm on Iris data set for both distance methods. Needs iris.data.txt in the project folder.
MultiRunner.java: Runs the KMeans algorithm 10 times on distributed data with both euclidean and custom distance method.
            Needs the input files called myfile_0.txt to myfile_9.txt in the folder called data

Compile & Run
Extract ALL the files and folder(s) in the zip file to a folder.
Set the console working directory  to the directory with all java files.
Run:
    javac KMeans.java Iris.java MultiRunner.java
    #This will run test on data Iris
    java Iris
    #This will run test on 10 files
    java MultiRunner

