# Data files

## Instances

```txt
[Number of vertices]
[For every vertex] {
[Demand]
}
[Number of edges]
[For every edge] {
[Source vertex] [Destination vertex] [Capacity] [Costs]
}
```

## Solutions

```txt
[Number of vertices]
[For every edge] {    
[Source vertex] [Destination vertex] [Flow passing by the edge]
}
```

It's important we store the number of vertices and edges to not go out of bounds.
