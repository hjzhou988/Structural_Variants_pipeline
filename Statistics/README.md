# Statistics of VCF

## Preprocessing of VCF
Genome STRiP SVGenotyper has the "eval" output folder, where a bunch of statististics are already there. Of note, in AlleleFrequency.report.dat file, we get the alternative allele frequency, we also get the non-genotyped number of individuals for each site (missing due to the low-quality genotyping (marked as LQ in format GP column in VCF file)). Regardless, run: 
```
python3 SV_statistics_for_GS.py -i input.vcf -o output.stats
```
(bundled with VCF_parser_for_GS.py) to get each SV's length and alternative allele frequency, and aternative allele count information. 

For LUMPY output (genotyped by SVTyper already), run 
```
python3 SV_statistics_for_lumpy_merged.py -i input.vcf -o output.stats
```
 (bundled with VCF_parser_for_lumpy_merged.py) to get the SV type, length, alternative allele frequency, and aternative allele count information. 

## Use Jupyter Notebook to get more statistics

Then use the Statistic.ipynb to do the following statistics:
* Length Distribtuion of each SV
* Number of SVs per chromosome
* Number of SVs per chromosome per person
* Distribution of alternative allele frequency
* Distribtuion of missing rate

For determining the missing rate, use vcftools: (It does not work with Genome STRiP vcf)
```
vcftools --vcf Name.vcf --missing-indv --out outputfileName  # generates ".imiss" file that contains the missing rate for each individual
vcftools --vcf Name.vcf --missing-site --out outputfileName  # generates ".lmiss" file that contains the missing rate for each SV locus

```


## Mendel errors
For Mendel error, use pedcheck, and PLINK:

### Pedcheck
Jae-Hoon has written java scripts for preparing marker file, as well as dealing with [Pedcheck](https://watson.hgen.pitt.edu/register/docs/pedcheck.html) output.
After you downloaded the java files to your folder, you need to do
```
javac *.java
```
This compiles java source files into bytecode class files, so that they can be run by java. 
In the original Jae-Hoon's code, each chromosome's markers was analyzed seperately  ("set.me.missing.HZ.job"). However, since the total number of SV is small, I did it all in one job submission "set.me.missing.WG.HZ.job". 

Make sure that the input VCF is sorted (you can sort it using vcftools) and "gzipped" (ends with vcf.gz). The reason why the VCF needs to be sorted is that there might be multiple SVs that start at the same position (but ends at different positions). JaeHoon's script to generate the marker file does not take that into consideration, so there may be duplicated markers in the marker file. I wrote a python script to detect these duplicated markers, as long as the vcf was sorted. I already integrated this into the job submission. 

JaeHoon's script calls "level 1" and "level 2" Mendel errors by Pedcheck. In my test, most errors (if not all) are level 1 errors.

The final Mendel error statitistics is in the output "GIGI.genotype.ME.stat.chr..txt". It gives the Mendel error number per SV. 

### PLINK

In addition to Mendel error number per SV, [PLINK](http://zzz.bwh.harvard.edu/plink/) can give Mendel error number per individual and per family. 

Download the Linux (x86_64) binary from the PLINK download page (http://zzz.bwh.harvard.edu/plink/download.shtml#download). PLINK needs PED file and MAP file as inputs. 

* Use vcftools to generate ped file and map file.
```
vcftools --vcf Name.vcf --plink
```
It generates `out.map` and `out.ped` files. However, `out.ped` does not contain the family structure information. 
Script `ModifyVCFtoolsPED.py`  can integrate the family structure information into a new ped file:
```
python3 ModifyVCFtoolsPED.py out.ped PedStructSeqID.txt Name.ped
```
, in which `PedStructSeqID.txt` is the file that contains the family information for each individual, and was also used in the Pedcheck Mendel errors procedure. 
Then
```
mv out.map Name.map 
~/plink-1.07-x86_64/plink --file Name --noweb --mendel --out Name
```
It will generate a bunch of Mendel error statstics, including the .fmendel file. 

`PlotMendelError.R` reads the .fmendel file and plots the Mendel error per trio and Mendel error rate (need to provide the total SV number). 
