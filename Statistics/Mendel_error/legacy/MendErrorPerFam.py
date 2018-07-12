#! /bin/python3

import glob
COUNT=[]
for f in glob.glob('*.fmendel'):
	with open(f) as fmendel:
		count=0
		trioCount=0
		fmendel.readline() # skip the header
		for line in fmendel:
			count+=int(line.strip().split()[4])
			trioCount+=1
		average=count/trioCount
		COUNT.append(average)
print(COUNT)
import numpy as np
C=np.array(COUNT)
print(C.mean())
