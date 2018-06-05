# Genome STRiP pipeline on hoffman2 cluster

## detailed usage of Genome STRiP on hoffman2 cluster

This Github page provides detailed description of how to use Genome STRiP on hoffman2 cluster at UCLA and scripts that I used to call SVs from 454 bipolar disorders samples. 

Genome STRiP is developed by Steven A McCarroll lab. It has its own website http://software.broadinstitute.org/software/genomestrip/, where you can download and look into the documentation of the software. The tutorial http://software.broadinstitute.org/software/genomestrip/workshop-presentations gives a general and easy-to-follow workflow of Genome STRiP, but since it was somewhat old, new features have been added, and should be paid attention. For debugging and issue reporting, I always go to GenomeSTRiP topic on the GATK forum. 

The hoffman2 SGE might have issue for communicating with drmaa API https://gatkforums.broadinstitute.org/gatk/discussion/comment/49034#Comment_49034 . This issue may need to figured out in the future to improve the efficiency. 

## Download the software. 
Software can be downloaded from Genome STRiP website after registration. Just directly unzip the downloaded file, and it does not need to be compiled. 

## SVPreprocess
The script was in general based on the script from Dr. Alden Huang and the test script from the software's folder svtoolkit/installtest

As dealing with 454 samples altogether will take a long time, we divided them into 10 batches, and did SVPreprocess for each batch. 





