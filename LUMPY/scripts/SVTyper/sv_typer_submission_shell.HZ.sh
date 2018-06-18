#!/bin/sh

# lumpy_variants.sh wrapper
# job array command, with takes 1-N
# When a single command in the array job is sent to a compute node,
# its task number is stored in the variable SGE_TASK_ID,
# so we can use the value of that variable to get the results we want:

# path to file with bam_id's
BAMs_to_PROCESS=/u/home/h/hjzhou/batch_all.list

# get the number of lines in txt file
number_bam=$(cat $BAMs_to_PROCESS | wc -l)

# Specify the input merged lumpy vcf file.
lumpy_vcf=Lumpy_master_merged_bnd_rm.vcf

# set variables to pass to lumpy script
lumpy_folder=bipolar_lumpy
scratch=$SCRATCH

# of jobs to process simultaneously 
JOBS=100


# submit jobs
qsub -t 1-$number_bam -tc $JOBS svtyper_shell.HZ.sh -f $BAMs_to_PROCESS -l $lumpy_folder -s $scratch -v $lumpy_vcf
