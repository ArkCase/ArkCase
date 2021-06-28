#!/bin/bash

#run this script from root of the app, or from individual module for detailed output

FILES=$(find . -name 'jacoco.csv')
for ELEMENT in ${FILES[@]}
  do
    awk -v elem="$ELEMENT" -F ',' '{
    inst += $4 + $5;
    inst_covered += $5;
    br += $6 + $7;
    br_covered += $7;
    line += $8 + $9;
    line_covered += $9;
    comp += $10 + $11;
    comp_covered += $11;
    meth += $12 + $13;
    meth_covered += $13; }
  END {
    print "Code coverage summary for module: " elem
    printf "  Instructions: %.2f% (%d/%d)\n", 100*inst_covered/inst, inst_covered, inst;
    printf "  Branches:     %.2f% (%d/%d)\n", 100*br_covered/br, br_covered, br;
    printf "  Lines:        %.2f% (%d/%d)\n", 100*line_covered/line, line_covered, line;
    printf "  Complexity:   %.2f% (%d/%d)\n", 100*comp_covered/comp, comp_covered, comp;
    printf "  Methods:      %.2f% (%d/%d)\n", 100*meth_covered/meth, meth_covered, meth; }
  ' $ELEMENT >> jacoco-output.txt
  done