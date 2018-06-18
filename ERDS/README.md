# ERDS
## overview
ERDS (estimation by read depth with single-nucleotide variants) combines read depth, paired-end information, and polymorphism data primarily by using a paired Hidden Markov Model (HMM). Via different procedures, ERDS can identify CNVs that are in uniquely mappable regions of the genome as well as those that are not [1]. The flow chart of ERDS is shown below.
[ERDS pipe chart](https://github.com/hjzhou988/Genome-STRiP-pipeline/blob/master/ERDS/Screen%20Shot%202018-06-18%20at%209.30.38%20AM.png)
Of note, ERDS uses "split read" to accurately call the breakpoint of deletions. 
In addition to the bam file, ERDS also needs SNP VCF file as input. If you have a multi-sample VCF, you had better split it into single sample VCF, as it will significantly improves ERDS efficiency. ERDS uses about 20G memory for one sample. (h_data=10G or 12G might work, but if it gets killed or there is error message in the log file, then increase the memory)
It takes about a day to finish.

## Split multi-sample SNV VCF into single-sample VCF.
ExtractSingleSNPvcf.job

## Run ERDS
ERDS.job


[1] Zhu, Mingfu, et al. "Using ERDS to infer copy-number variants in high-coverage genomes." The American Journal of Human Genetics 91.3 (2012): 408-421.
