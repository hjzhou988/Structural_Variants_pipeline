# CNVnator

CNVnator is an algorithm based on read depth (RD), and utilizes mean-shift technique to do partitioning [1].

The script is simple, all in CNVnator_large.job (CNVnator does not determine sex, and assumes a male sample). 

CNVnator generates `calls.txt` files. They provided `cnvnator2VCF.pl` script to convert those kind of files to vcf files. The vcf files contain "genotype" and "copy number", which are infered from "natorRD" value. 

I also provided the code to keep DEL records in the vcf file only (`FilterDup.py`).


[1] Abyzov, Alexej, et al. "CNVnator: an approach to discover, genotype, and characterize typical and atypical CNVs from family and population genome sequencing." Genome research 21.6 (2011): 974-984.
