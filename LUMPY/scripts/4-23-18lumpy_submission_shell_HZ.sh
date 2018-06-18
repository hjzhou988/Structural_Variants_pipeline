#!/bin/sh

# lumpy_variants.sh wrapper
# job array command, with taks 1-N
# When a single command in the array job is sent to a compute node,
# its task number is stored in the variable SGE_TASK_ID,
# so we can use the value of that variable to get the results we want:

# path to file with bam_id's
BAMs_to_PROCESS=/u/home/h/hjzhou/batch_all.list

# get the number of lines in txt file
number_bam=$(cat $BAMs_to_PROCESS | wc -l)

# set variables to pass to lumpy script
lumpy_folder=bipolar_lumpy_retry
scratch=$SCRATCH

# of jobs to process simultaneously 
JOBS=100


# submit jobs

qsub -t 1-1 -tc $JOBS lumpy_variants_HZ_4-23-18.sh -f $BAMs_to_PROCESS -l $lumpy_folder -s $scratch
