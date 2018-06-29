#! /bin/python3

'''
Bundled with VCF_parser_for_lumpy_merged.py. 
Output each SV's "svtype, position, svlen, allele_frequency, allele_count" from VCF. Use merged LUMPY vcf or merged SYTyper vcf as input. 
Usage: python3 SV_statistics_for_lumpy_merged.py -i Input.vcf -o Output.stats.txt
'''

import VCF_parser_for_lumpy_merged as vp

from optparse import OptionParser
import sys
import os

if __name__ == '__main__':

	parser = OptionParser()
	parser.add_option("-i", "--input", type="string", dest="in_fname",
	                  help="Input mulitple-sample VCF file.", metavar="FILE")

	parser.add_option("-o", "--output", type="string", dest="out_fname",
	                  help="Output file name.", metavar="FILE")

	(options, args) = parser.parse_args()
	#print parser.parse_args()

	# if no options were given by the user, print help and exit
	if len(sys.argv) == 1:
	    parser.print_help()
	    exit(0)
	i=vp.vcfIterator(options.in_fname)


	s=vp.VariantStats(i)
	s.collect_variants()

	out=open(options.out_fname,'w')
	out.write('\t'.join(["#SVTYPE","POSITION","SVLEN","ALLELE_FREQUENCY","ALLELE_COUNT"]))
	out.write('\n')
	for i in s.stats:
		for j in i:
			#for item in j:
			#	out.write("%s\t" % item)
			#out.write("\n")
			out.write("%s\t%s\t%s\t%.7f\t%s\n"%(j[0],j[1],j[2],j[3],j[4]))
	out.close()






