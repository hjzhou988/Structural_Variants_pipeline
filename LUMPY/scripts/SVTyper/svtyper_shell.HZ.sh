#!/bin/bash
#$ -pe shared 6
#$ -l h_rt=20:00:00
#$ -l h_data=2G
#$ -o /u/home/h/hjzhou/lumpy_batch/logs/4-25-18LUMPY_SVtyper.$JOB_ID.$TASK_ID.out
#$ -m a

while getopts f:l:s:v: option
do
 case "${option}"
 in
 f) BAMs_to_PROCESS=${OPTARG};;
 l) lumpy_folder=${OPTARG};;
 s) scratch=${OPTARG};;
 v) lumpy_vcf=$OPTARG;;
 esac
done

mkdir -p ${scratch}/${lumpy_folder}
cd ${scratch}/${lumpy_folder}
. /u/local/Modules/default/init/modules.sh
module load python/2.7.3
module load samtools/1.3.1
module load bedtools
module load R

#Get the path to the bam file
sample_path_bam=$(cat $BAMs_to_PROCESS | head -${SGE_TASK_ID} | tail -1 )

#Get the sample name
sample_name=`echo $sample_path_bam | sed 's/\/.*\///g' | sed 's/.dedup.realigned.recal.bam//g' `

echo $SGE_TASK_ID 
echo $BAMs_to_PROCESS
echo $sample_name
echo $lumpy_folder
echo $scratch
echo $sample_path_bam

~/.local/bin/svtyper -B $sample_path_bam -i ${scratch}/${lumpy_folder}/${lumpy_vcf} --max_reads 1000 | gzip -c > ${scratch}/${lumpy_folder}/${sample_name}.svtyper.vcf.gz
