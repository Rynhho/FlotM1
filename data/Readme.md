# Data files

This is some kind of slow and not deployment ready database. You can add entries by moving instance or solution files into the directory, and you can remove them by deleting the files. Note that an instance ID is the file name withouth the file extension.

## Instances

Instances must have a ".txt" extension. Their internal format is the DIMACS minimum cost format, altough we don't use some of the parameters.

```txt
c [comment]
p [unused argument] [Number of vertices] [Number of edges]
n [Vertex ID] [Vertex demand]
a [Source vertex] [Destination vertex] [unused argument] [Capacity] [Cost]  
```

The reader demands that the problem be defined before any nodes or arcs.

## Solutions

Solutions must have a ".sol" extension.

```txt
[Network ID] [Number of vertices]
[For every edge] {    
[Source vertex] [Destination vertex] [Flow passing by the edge]
}
```
