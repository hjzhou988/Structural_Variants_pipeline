# SV_DIR is the installation directory for SVToolkit - it must be an exported environment variable.
# runDir is the directory for all the output files.
# SV_TMPDIR is a directory for writing temp files, which may be large if you have a large data set.
# usage: sh preprocess.sh bamfiles.list

file_list=$1
out_root=`echo $1 | sed 's/\.list//1'`

. /u/local/Modules/default/init/modules.sh
module load java
module load R
module load samtools

# SV_DIR is the installation directory for SVToolkit - it must be an exported environment variable.
# runDir is where all the output files go
export SV_DIR="/u/home/p/paulinal/svtoolkit"
runDir=/u/flashscratch/h/hjzhou/bipolar_sv_large/$out_root

# SV_TMPDIR is a directory for writing temp files, which may be large if you have a large data set. Here the SV_TMPDIR is set in the run directory
SV_TMPDIR=/u/flashscratch/h/hjzhou/bipolar_sv_large/$out_root/tmpdir

# These executables must be on your path.
which java > /dev/null || exit 1
which Rscript > /dev/null || exit 1
which samtools > /dev/null || exit 1

# For SVAltAlign, you must use the version of bwa compatible with Genome STRiP.
export PATH=${SV_DIR}/bwa:${PATH}
export LD_LIBRARY_PATH=${SV_DIR}/bwa:${LD_LIBRARY_PATH}
export LD_LIBRARY_PATH=/u/systems/UGE8.0.1vm/lib/lx-amd64/:$LD_LIBRARY_PATH

mx="-Xmx4g"
classpath="${SV_DIR}/lib/SVToolkit.jar:${SV_DIR}/lib/gatk/GenomeAnalysisTK.jar:${SV_DIR}/lib/gatk/Queue.jar"

mkdir -p ${runDir}/logs.$1 || exit 1
mkdir -p ${runDir}/metadata.$1 || exit 1

# Display version information.
java -cp ${classpath} ${mx} -jar ${SV_DIR}/lib/SVToolkit.jar

# Run preprocessing.
# For large scale use, you should use -reduceInsertSizeDistributions, but this is too slow for the installation test.
# The method employed by -computeGCProfiles requires a GC mask and is currently only supported for human genomes.

java -cp ${classpath} ${mx} \
    org.broadinstitute.gatk.queue.QCommandLine \
    -S ${SV_DIR}/qscript/SVPreprocess.q \
    -S ${SV_DIR}/qscript/SVQScript.q \
    -gatk ${SV_DIR}/lib/gatk/GenomeAnalysisTK.jar \
    --disableJobReport \
    -cp ${classpath} \
    -configFile ${SV_DIR}/conf/genstrip_parameters.txt \
    -tempDir ${SV_TMPDIR} \
    -R /u/home/a/alden/eeskin2/bipolar_sv/svtoolkit/reference/Homo_sapiens_assembly19.fasta \
    -copyNumberMaskFile /u/home/a/alden/eeskin2/bipolar_sv/svtoolkit/reference/Homo_sapiens_assembly19.gcmask.fasta \
    -genomeMaskFile /u/home/a/alden/eeskin2/bipolar_sv/svtoolkit/reference/Homo_sapiens_assembly19.svmask.fasta \
    -runDirectory ${runDir} \
    -md ${runDir}/metadata \
    -disableGATKTraversal \
    -useMultiStep \
    -reduceInsertSizeDistributions true \
    -computeGCProfiles true \
    -computeReadCounts true \
    -jobLogDir ${runDir}/logs \
    -bamFilesAreDisjoint true \
    -I $file_list \
    -jobRunner Drmaa \
    -jobNative "-V -l h_data=5G,h_rt=48:00:00,highp" \
    -debug true \
    -run \
    || exit 1
