/******************************************
Project 6, Written by William Lippard


This is the Main function


******************************************/

#include "Graph.h"

int main(int argc, char *argv[])
{
	FILE *fp = stdin; //creates a file pointer from the input file
	int n, edgeN, i; //a few int for the # of vertices, edges, and paths.
	fscanf(fp, "%d", &n); //gets the number of nodes
	fscanf(fp, "%d", &edgeN); //gets the # of edges
	Graph g = newGraph(n); //creates a new graph
	Edge e; //temp edge
	for(i=0; i<edgeN; i++){ //loop through all the deges and create new edges according to the from and to vertex
		fscanf(fp, "%d", &e.fromVertex);
		fscanf(fp, "%d", &e.toVertex);
		addEdge(g, e); 
		}	
	int searches; //the total number of searches 
	char c[56]; //get the string infront of the number
	Vertex start, finish;
	fscanf(fp,"%d", &searches);
	

	for(i=0; i<searches; i++){ //for the number searches
		fscanf(fp, "%s", &c); //scan shortespath, so you could check to see if it was a different command if there where to be more commands
		fscanf(fp, "%d", &start); //store the start
		fscanf(fp, "%d", &finish); //store the finish
		printf("The shortest path from %d to %d is:\n", start, finish);
		shortestPath(g, start, finish); //run the shortes path function
	}
	freeGraph(g); //free the graph
	return 0;
}


