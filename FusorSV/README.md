# FusorSV
## Overview
FusorSV [1] is algorithm that trains a "fusion" model with 8 SV detection algorithms' outputs and a truth set. The fusion model is then used for detecting SVs in new samples. Since we do not have a truth set, we are going to use their fusion model for SV detection. 
FusorSV is part of Structural Variants Engine, which installs all the algorithms and aligners and does the aligning and SV detection for you. Due to the fact that hoffman2 is not able to install Docker, which SVE highly depends on, I focused on FusorSV only.  We might need to install each algorithm by ourselves. 

I was testing FusorSV using the author's own git hub page https://github.com/timothyjamesbecker/FusorSV , which is different from the official FusorSV https://github.com/TheJacksonLaboratory/SVE . The official one seems harder to install, and the author already corrected some bugs and made it usable for many samples. However, he has not solved the memory issue if we want to run on hundreds of samples yet. 

## Install
* Go to https://github.com/timothyjamesbecker/FusorSV and install according to the "pip" install instructions. 
The easiest way is to load Hoffman2's python/2.7 (2.7.2) and install pysam:
```
module load python/2.7
pip install -Iv 'pysam>=0.9.0,<0.9.2' --user  # "--user" means it will be installed in your local home directory
```
That's because hoffman2's python 2.7.2 already installed most of the required packages.

Follow their "pip" install instruction for installing FusorSV. Add "--user" option so that it's installed in your local directory. The FusorSV.py file will be in the ~/.local/bin/ directory. bx-python was hard to install, but it was already installed on the hoffman2 python/2.7 (but not on the other python versions). bx-python 0.7.3  version is fine (even though it's not the required <0.7.3). 

* I also installed python 2.7.10 in my home directory, as the required version of python (2.7.6 < python < 2.7.12) is not available on hoffman2, but 2.7.2 from hoffman2 seem's fine. 
* FusorSV requires you to provide the same reference genome as you used for alignment and the algorithms. For their provided samples, HG37 decoy version of reference genome should be used. 

## Prepare inputs from different algorithms
Currently, we have the outputs from CNVnator, Genome STRiP, and LUMPY. The author provided script for converting multi-sample vcf from Genome STRiP to single-sample vcf with the VCFs from both SVGenotyper and CNVDiscovery. I tweaked the script to accept the VCF from SVGenotyper only. 
```
python gs_split_merge.HZ.py -d GenomeStrip.vcf -o GSsplit/
```
CNVnator also has its own perl code to convert their format of output (.call.txt) to VCF format. 
```
# in the folder of CNVnator output
for file in `ls *.calls.txt`
do
name=`echo $file | sed 's/.calls.txt//g'`
perl ~/CNVnator/cnvnator2VCF.pl $file > $SCRATCH/mysamples/${name}/${name}_S10.vcf
done
```
Both scripts are included in the scripts folder

## Run FusorSV
* For the samples that was installed with the software (without --merge or --no_merge), the peak memory is 46G (whether or not whole genome or just chromosome 22). 
* FusorSV appears to be able to resume failed job. Because if I specify the same output folder, the job uses less time and peak memory for finishing the job. 
* If bx-python was not installed, it will have this error:
```
Traceback (most recent call last):
  File "/u/home/h/hjzhou/.local/bin/FusorSV.py", line 532, in <module>
    su.fusorSV_vcf_liftover_samples(out_dir+'/vcf/all_samples_genotypes*.vcf*',ref_path,lift_over) #default is on
  File "/u/home/h/hjzhou/.local/lib/python2.7/site-packages/fusorsv/svu_utils.py", line 902, in fusorSV_vcf_liftover_samples
    fusorSV_vcf_liftover(vcf,ref_path,chain_path)
  File "/u/home/h/hjzhou/.local/lib/python2.7/site-packages/fusorsv/svu_utils.py", line 894, in fusorSV_vcf_liftover
    import crossmap as cs
  File "/u/home/h/hjzhou/.local/lib/python2.7/site-packages/fusorsv/crossmap.py", line 16, in <module>
    from bx.bbi.bigwig_file import BigWigFile
ImportError: No module named bx.bbi.bigwig_file

```

## Large Sample Size

From the communication with the author, it seems that the reason why it consumes a large memory is that FusorSV is either training my own data or using the break-point smoothing functionality. We don't have truth set, so we are not training our own data. For now, we also don't use break-point smoothing functionality. The newer version (0.1.2) integrates "--merge" and "--no_merge" flags. This allows us to run samples in batches with low memory request and finally merge the single vcfs into a multi-sample vcf. 

FusorSV is not fully compatible with HG19 reference yet. We have to manually change the chromosome notation in the VCF files and reference files (chr1 changing to 1, chrX changing to X, chrM changing to MT). Here is the bash script I used that makes this step a little bit easier:
```
for file in `ls`
do
 sed -i 's/chr\([0-9]\{1,2\}\)/\1/g; s/chr\([X-Y]\)/\1/g; s/chrM/MT/g' $file &
done
```
Basically "sed" editor finds and replaces autosomes, sex chromosomes, and mitochondrial chromosome notation sequentially. "&" parallelizes the "for loop" jobs.



[1] Becker, Timothy, et al. "FusorSV: an algorithm for optimally combining data from multiple structural variation detection methods." Genome biology 19.1 (2018): 38.
