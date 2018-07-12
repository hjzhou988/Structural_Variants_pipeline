#!/bin/python3
infile='GIGI.genotype.ME.stat.chr..txt'
f=open(infile)
lineCount=0
count=0 # ME count
for line in f:
	lineCount+=1
	if line.strip().split()[1] != '0':
		count+=1
print(count/lineCount)
f.close()
