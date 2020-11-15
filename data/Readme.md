# Data files

This is some kind of slow and not deployment ready database. You can add entries by moving instance or solution files into the directory, and you can remove them by deleting the files.

## Instances

Instances must have a ".txt" extension.

```txt
[Number of vertices] [Number of edges] [Maximum capacity] [Maximum cost] [Maximum demand]
[For every vertex] {
[Demand]
}
[Number of edges]
[For every edge] {
[Source vertex] [Destination vertex] [Capacity] [Cost]
}
```

## Solutions

Solutions must have a ".sol" extension.

```txt
[Network ID] [Number of vertices]
[For every edge] {    
[Source vertex] [Destination vertex] [Flow passing by the edge]
}
```

It's important we store the number of vertices and edges to not go out of bounds.
