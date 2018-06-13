# Genome STRiP pipeline on hoffman2 cluster

This Github page provides detailed description of how to use Genome STRiP on hoffman2 cluster at UCLA and scripts that I used to call structural variants from 454 bipolar disorder samples. 

Genome STRiP is developed by Steven A McCarroll lab. It has its own website http://software.broadinstitute.org/software/genomestrip/, where you can download and look into the documentation of the software. The tutorial http://software.broadinstitute.org/software/genomestrip/workshop-presentations gives a general and easy-to-follow workflow of Genome STRiP, which gives you an overview of this software, but since it was somewhat old and new features have been added, it should be used carefully. For debugging and issue reporting, I always go to GenomeSTRiP topic in the GATK forum. 

I didn't include CNVDiscovery here (which is Genome STRiP 2.0). It takes longer time than Genome STRiP 1.0 (which is the SVDiscovery and SVGenotyper modules here). 

I also didn't include "split read" mapping. This can improve the accuracy of breakpoint calling. Please refer to the tutorial http://software.broadinstitute.org/software/genomestrip/workshop-presentations for details.

Genome STRiP uses the Queue workflow engine to implement its executable pipelines. Basically, you will see the major java program submitting the individual devided jobs to hoffman2 cluster, the status of which can be seen from "myjobs" command on your terminal.

The hoffman2 SGE might have issue for communicating with drmaa API https://gatkforums.broadinstitute.org/gatk/discussion/comment/49034#Comment_49034 . This issue need to be figured out in the future to improve the efficiency of the software working on hoffman2. 

Bash and job submmision scripts are included in each module's folder.

## Download the software. 
Software can be downloaded from Genome STRiP website after registration. Just directly unzip the downloaded file into your preferred location, and it does not need to be compiled. 

## Reference Genome bundle
I used the reference genome bundle HG19 in Alden's folder, where there is not only the reference genome, but also the gcmask files (required in SVPreprocess) and svmask files (required in the SVPreprocess, SVDiscovery, and SVGenotyper). 

## SVPreprocess
* The script was in general based on the script from Dr. Alden Huang and the test script from the software's folder svtoolkit/installtest
* As dealing with 454 samples altogether will take a long time, we divided them into 10 batches, and did SVPreprocess for each batch. Total list is in "batch_all.list". Each small batch is in "batch[1-2].[0-4].list"
* SVPreprocess_large.job (_2.job) and preprocess_HZ_large.sh are coupled to submit jobs.
* As suggested by the documentation of Genome STRiP, "dry run" is recommended to be done first. Just omit the "-run" flag in the command line script. 
* You also need to install "tabix", which is a generic indexer for TAB-delimited genome position files. This was used in the end stage of SVPreprocess.
* If "/u/systems/UGE8.0.1vm/lib/lx-amd64/" is not in your environmental variable LD_LIBRARY_PATH, then you need to include it:
```
export LD_LIBRARY_PATH=/u/systems/UGE8.0.1vm/lib/lx-amd64/:$LD_LIBRARY_PATH
```
Because required “libdrmaa.so” library is in this directory.
* As hoffam2 SGE might have issue for communicating with dramaa API, you may set h_rt=48:00:00, and resubmit the same job after that, so that it will work more efficiently. (Job submmission by the qscript becomes very slow after two days of running. After resubmission, Genome STRiP will pick up where the job left by recognizing the hidden ".done" ".failed" files in the running directory. )
* It's very normal to see "unable to determine job status" error. This kind of error will for sure to cause individual devided job to fail. This error is random (could be due to the drmaa API issue), and resubmitting the master java job will fix the problem.  
* It takes more than a week total to finish.

## SVDiscovery
* In the SVDiscovery module, it only detects Deletions call. 
* It is recommended to call 100-100k deletions and 100k-10m deletions separately in the SVDiscovery. Recommended window size and overlap settings were all from GenomeSTRiP website.
* You should be careful that the input bam file list "-I" need to be a total list. In the example here, it's the 454 bipolar disorder samples.
* "-md" are used multiple times here, specified with each batch’s meta data.
* Can detect invalid alignment in the bam file. (The way to ignore this error is to add “-P select.validateReadPairs:false”)
* Note that I already specified "-tempDir" in the command line script. This is important, else you will have "java.lang.NullPointerException" error. 
* Note that I loaded samtools/1.2 (not earlier default version on hoffman2) in the script. This avoids the "samtools" merge error in last few steps of SVDiscovery.
* Sometimes the Queue workflow mistakenly thinks some individual devided (partition) jobs were done (but actually they were not). This will cause error in the last few merging step of SVDiscovery, because the partition files were still missing. You can see the hidden ".done" file of the failed partition job in the running directory. Just delete these ".done" files, and resubmit the master java job. 
* Some partition job may take extremely long time to finish (in my case "SVDiscovery-178.out" takes 31 hours). Just need to be patient.

## Annotate and filter
In the SVDiscovery, several categories of filters were already applied by default: COVERAGE, COHERENCE, DEPTH, DEPTHPVAL, PAIRSPERSAMPLE. You can see them in the 7th column of the output vcf from SVDiscovery. So in this step, basically we want to annotate the alpha sattelite sites, as these sites were recommended to remove before genotyping. 
In my script, I also annotated other stuff other than alpha sattelite (MobileElements) (as Alden did). However, only alpha sattelite was filtered in the filtering step. 

## SVGenotyper
* Before this step, you may want to delete the sites with filter tag from the VCF file. The way to do this is to use vcftools. 
```
vcftools --vcf Filename.annotated.filtered.vcf --remove-filtered-all --recode --recode-INFO-all --stdout > Filename.annotated.filtered.passed.vcf
```
* Since we didn't use a third party tool to precisely map the breakpoint using "split read", we need to use "SVGenotyperWithoutSplitReads.q" in this step. 
* In this step, there is a bug associating with the name of the intermediate ".dat" files. So after the job was terminated due to this error, you should manually create soft links to these ".dat" files in the running directory.
Here is an example of the code to implement that:
```
for file in `ls Filename.annotated.filtered.passed.genotyped.genotypes.*.dat`
do 
 suffix=`echo $file | sed 's/Filename.annotated.filtered.passed.genotyped.genotypes//g'`
 echo $suffix
 ln -s $file Filename.annotated.filtered.passed.genotyped$suffix
done
```
Basically, "genotypes." in the file names should be deleted.
* SVGenotyper already included annotation and filters of ALIGNLENGTH, CLUSTERSEP, GTDEPTH, INBREEDINGCOEFF, DUPLICATE, NONVARIANT. No need to do it yourself. You may want to delete the sites with those filter tags using vcftools, just as before. 




























