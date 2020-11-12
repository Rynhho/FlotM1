# Data files

## Instances

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

```txt
[Network ID] [Number of vertices]
[For every edge] {    
[Source vertex] [Destination vertex] [Flow passing by the edge]
}
```

It's important we store the number of vertices and edges to not go out of bounds.
