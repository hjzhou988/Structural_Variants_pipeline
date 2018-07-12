#! /bin/python3
import sys

infile=sys.argv[1]
with open(infile) as f:
	for line in f:
		if line.startswith('##'):
			continue
		else:
			break
	sitecount=0
	FalsePos=0
	NAcount=0
	for line in f:
		sitecount+=1
		temp=line.strip().split()[7].split(';')
		for i in temp:
			if i.split('=')[0]=='IRS_LOWERPVALUE':
				pval=i.split('=')[1]
				break
		if pval=='NA':
			NAcount+=1
		elif float(pval)>0.5:
			FalsePos+=1
	print(FalsePos/(sitecount-NAcount), FalsePos, sitecount,NAcount)
