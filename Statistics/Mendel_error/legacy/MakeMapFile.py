#! /bin/python3 

# This script converts .marker file to .map file that is required by PLINK. 
# Take the first argument as the input file. 
# The output file is by default use the same file name but use .map suffix. 
import sys



infile=sys.argv[1]
outfile=infile.strip('.marker-1').strip('.marker')+'.map' 


o=open(outfile,'w')
with open(infile) as f:
	f.readline() # skip the first line
	for line in f:
		name=line.strip()
		temp=name.split(':')
		chro=temp[0][3:]
		pos=temp[1].split('-')[0] # in case for the duplicated markers that ends with "-1""-2" etc. 
		o.write(chro+'\t'+name+'\t'+'0'+'\t'+pos+'\n')
o.close()

