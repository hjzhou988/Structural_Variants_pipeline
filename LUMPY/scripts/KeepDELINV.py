#! /bin/python3

# Remove INV and DUP SVs from the SVTyper VCF.

infile='svtyper.combined.removeMissingGT.SVmasked.vdj_removed.sorted.annotated.filtered.passed.vcf'
outfile='svtyper.combined.removeMissingGT.SVmasked.vdj_removed.sorted.annotated.filtered.passed.DELINV.vcf'

f=open(infile)
o=open(outfile,'w')

for line in f:
	if line.startswith('#'):
		o.write(line)
		continue
	else:
		break
for line in f:
	if line.strip().split()[4]=='<DEL>' or line.strip().split()[4]=='<INV>':
		o.write(line)
