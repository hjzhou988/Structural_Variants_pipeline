#!/bin/python3
from optparse import OptionParser
import sys
import gzip
import os


if __name__ == '__main__':

    parser = OptionParser()
    parser.add_option("-i", "--input", type="string", dest="in_fname",
                      help="Input mulitple-sample SVTyper VCF file.", metavar="FILE")

    parser.add_option("-o", "--output", type="string", dest="out_fname",
                      help="Output file name.", metavar="FILE")

    (options, args) = parser.parse_args()


    # if no options were given by the user, print help and exit
    if len(sys.argv) == 1:
        parser.print_help()
        exit(0)
    with open(options.in_fname) as f:

        o=open(options.out_fname,'w')
        for line in f: # skip all the headers
            if line.strip().startswith('#CHROM'):
                #samplelist=line.strip().split()[9:]
                o.write(line)
                break
            else:
                o.write(line)
        for line in f: # start now for each CNV
            if line.strip().split()[8]=='GT':
                continue
            else:
                o.write(line)

        o.close()
