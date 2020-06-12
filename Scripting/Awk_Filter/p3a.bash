#!/bin/bash


awk -f p3a.awk unsortedNames.txt | sort | sed -E 's/.*> //' > p3a.out



