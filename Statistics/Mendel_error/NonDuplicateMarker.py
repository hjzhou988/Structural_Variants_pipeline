#! /bin/python3

# Check whether the .marker file generated by "ConvertVCFToLinkage" from sorted vcf has duplicated ones. If there are, then number the duplicated ones as -1, -2 ...
# Usage:
# python3 NonDuplicateMarker.py makerfile.marker

import sys

markerfile=sys.argv[1]
outputfile=markerfile+'-1'
f=open(markerfile,'r')
o=open(outputfile,'w')
m=''
counter=0
for line in f:
	if m == line.strip():
		counter+=1
		o.write(line.strip()+'-'+str(counter)+'\n')
		print (m)
	else:
		m=line.strip()
		counter=0
		o.write(line)
f.close()
o.close()
		
		
 
