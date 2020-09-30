# Network flow solving algorithms

## How to run

The applications is to be launched via a command line interface as following.

```bash
java NWF [arguments]
```

Where arguments is one of the following:
* **-c (input instance file) (input solution file)**: Check if the solution file is a realisable solution for the instance file problem.
* **-r (algorithm) (input instance file) (output solution file)**: Runs an algorithm with the specified instance file and outputs the solution file.
* **-b (input instance file)**: Benchmarks ALL implemented algorithms.

Information about some arguments:
* **algorithm** is a string on the following list:
  * Todo

Adding to that, we have some optional arguments:
* **-mt (integer, default=3600))**: Optional arguments in -r mode. The specified integer is the maximum number of seconds to run the algorithm.

## Implemented algorithms

## Roadmap

### First phase

Luis:
* Prepare packages, source directories, interfaces
* Readme

Others:
* Find book and start studying it.

### Second phase

Luis:
* Start report (LaTeX on overleaf)
* Help others

Lucas:
* Checker

Sylvain:
* Test script testing both generator and checker

Lin:
* Generator

### Third phase

Luis:
* Nothing lol

Everyone else:
* Implements algorithms
* Write respective algorithm into report

Lucas:

Sylvain:

Lin:

### Fourth phase

Luis:
* Benchmarks

Everyone else:
* Nothing lol
