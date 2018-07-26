#! /bin/python3
# filter out the DUP calls in the cnvnator vcf.

infile='cnvnator.merge.vcf'
outfile='cnvnator.merge.DEL.vcf'
with open(infile) as f, open(outfile,'w') as o:
	for line in f:
		if line.startswith('#'):
			o.write(line)
		elif line.strip().split()[4]=='<DEL>':
			o.write(line)
		else:
			continue
