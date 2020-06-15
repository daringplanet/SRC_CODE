/************************************************************************
Graph.h

Purpose:
    Struct definitions for the Adjacency Matrix representation of a Graph.
    Define function prototypes used by graphs.
************************************************************************/

//Including Queue.h so that the a queue can be used in the shortest path algorithm.
#include "Queue.h"


//typedef for a directed edge that begins at fromVertex and ends at toVertex.
typedef struct
{
	Vertex fromVertex;
	Vertex toVertex;

} Edge;


//Typedef for a Graph implementation.
//Contains an adjacency matrix and the number of vertices in the graph (n).
typedef struct
{
    int n;
	int **adjacencyM;
} GraphImp;

typedef GraphImp *Graph;


/*****Prototypes*******/

//Given the number of vertices of the graph, malloc the nxn adjacency matrix and initialize every edge to 0 (i.e. the edges aren't there initially).
//Return the address of the graph.
Graph newGraph(int n);

//Free the adjacency matrix and then the graph itself.
void freeGraph(Graph g);

//Add the edge e to the graph g.
void addEdge(Graph g, Edge e);

//Given graph g and vertex v, scan the adjacency matrix and return the first edge in g such that v is the "fromVertex" of the edge.
//Return NULL if there is no such edge.
Edge firstAdjacent(Graph g, Vertex v);

//Given graph g and vertex e, scan the adjacency matrix and return the next edge after e in g such that e.fromVertex is the "fromVertex" of the edge.
//Return NULL if there is no such edge.
Edge nextAdjacent(Graph g, Edge e);

//Print the sequence of vertices on a shortest path in g starting from start and ending at destination.
//A shortest path should be computed using the Breadth First Search (BFS) algorithm that maintains the parents of each vertex in the shortest path tree as defined in class.
//BFS can be implemented directly in this function, or you may create a new function for BFS.
void shortestPath(Graph g, Vertex start, Vertex destination);


/**************************************
Creates a new grapgh and adjacency matrix and retuns the new graph struct
***************************************/
Graph newGraph(int n)
{
	Graph g = (Graph)malloc(sizeof(GraphImp)); //creates a new graph
	g->n = n; //sets the number of vertices
	g->adjacencyM = (int **)malloc(n*sizeof(int*)); //creates a new matrix, first malloc is an array of pointers
	int i, j;
	for(i=0; i<n; i++)
		g->adjacencyM[i] = (int *)malloc(n *sizeof(int)); //is is a array from the array of pointers to store actuall vaules
	for(i=0; i<n; i++)
		for(j=0; j<n; j++)
			g->adjacencyM[i][j] = 0; //set all the values to 0 for no connections

	return g;
}


/***************************************
Free the Graph and free the adjacency matrix
**************************************/
void freeGraph(Graph g)
{
	int i;
	for(i=0; i<g->n; i++) //for the array of pointers
		free(g->adjacencyM[i]); //free the actuall matrix values
	free(g->adjacencyM); //frees the array of pointers
	free(g); //frees the graph
	return;
}

/*********************
Adds a edge to a given graph using the given edge
*********************/
void addEdge(Graph g, Edge e)
{
	g->adjacencyM[e.fromVertex][e.toVertex] = 1; //sets the adjaceny matrix at that location to 1 indicating there is a edge
	return;
}

/******************************************************
This function retuns the first adacent edge from a vertex, if any
******************************************************/
Edge firstAdjacent(Graph g, Vertex v)
{
	Edge e;
	if(v > g->n)
	{
		e.fromVertex = -1;
		e.toVertex = -1;
		return e;
	}
	int i;
	for(i=0; i<g->n; i++)
		if(g->adjacencyM[v][i] == 1)
		{
			e.fromVertex = v;
			e.toVertex = i;
			return e;
		}

	e.fromVertex = -1;
	e.toVertex = -1;
	return e;

}

/****************************************************************
This function will return the next adjacent edge from a vertex, if any/
****************************************************************/

Edge nextAdjacent(Graph g, Edge e)
{
	int i;
	for(i=e.toVertex+1; i<g->n; i++)
		if(g->adjacencyM[e.fromVertex][i] == 1)
		{
			e.toVertex = i;
			return e;
		}

	e.fromVertex = -1;
	e.toVertex = -1;
	return e;
}


/***************************************************
This function will recurse down the parent array and print the vaules in revers
***************************************************/
void recPrint(int parent[], Vertex current, Vertex stop)
{
	if(current == stop)
	{
		printf("%d->", current);
		return;
	}

	recPrint(parent, parent[current], stop);
	printf("%d->", current);
	return;
}
/******************************************************
This functon calls recPrint then prints the last or the finish value after all the other parent values are printed
******************************************************/
void reversePrint(int parent[], Vertex temp, int n, Vertex start, Vertex finish)
{

	recPrint(parent, temp, start);
	printf("%d\n", finish);
	return;
}
/*****************************************************
This function finds the shortest path from one point of the graph to anthoner
Will detect if the vertex given is not in graph and if there is no path from one point to another
Graph is passed in
Vertex start and Vertex destination is passed in

******************************************************/
void shortestPath(Graph g, Vertex start, Vertex destination)
{
	if(start > g->n || destination > g->n)
	{ //checks to see if vertexs are in graph
		printf("A vertex is not in the graph\n");
		return;
	}

	if(start == destination)
	{ //checks to see if the start and destination vertex are the same, if so, no need to move
		printf("%d->%d\n", start, destination);
		return;
	}

	Vertex visted[g->n]; //create a visited array
	Vertex parent[g->n]; //create a parent array
	int i;
	for(i=0; i<g->n; i++)
	{ //sets all values to -1
		visted[i]=-1;
		parent[i] = -1;
	}

	Edge e; //create some tempory variables
	Vertex temp, current;
	Queue q = newQueue();  //creates a new queue
	insertQ(q, start); //inserts the starting vertex in the que as number 1
	while(removeQ(q, &current))
	{ //we remove it than look at all the adjacent verticies and que them up

		for(e=firstAdjacent(g, current); e.toVertex!=-1; e=nextAdjacent(g, e))
		{
			if(visted[e.toVertex]!=1)
			{ //if the current new vertex has not been visted before
				visted[e.toVertex] = 1; //mark as visited
				parent[e.toVertex] = current; //set its parent
				insertQ(q, e.toVertex); //insert it into the que
				if(e.toVertex == destination)
				{ //if the the current vertex is the destination
					reversePrint(parent, parent[e.toVertex], g->n, start, destination); //then print in reverse
					freeQueue(q);//frees the memory for the que
					return; //returns

				}
			}
		}

	}

	freeQueue(q); //frees the que
	printf("No path from %d to %d\n", start, destination); //there is no path
	return;
}
