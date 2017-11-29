# Tensia
A tool for a parallelized tensor network contractions based on Actor Model

## Usage
* Clone repository
* `cd tensia/native`
* Make sure `JAVA_HOME` is set to a valid path
  * on Linux it will be something like `/usr/lib/jvm/default`, `which -a java` might help
  * on MacOS you can use `export JAVA_HOME=$(shell /usr/libexec/java_home)`
* `make` (or `JAVA_HOME=/usr/lib/jvm/default make`)
* `cd ..`
* `sbt "run /path/to/input.graphml /path/to/output.graphml"`

## Data input
Example of GraphML file descibing Tensor Network serving as input
```xml
<graphml xmlns="http://graphml.graphdrawing.org/xmlns"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd">
  <graph id="0" edgedefault="undirected">
    <node id="0">
      <data key="shape">2,3</data>
      <data key="dataPath">/home/user/tensia/src/main/resources/a.bin</data>
      <data key="locked" />
    </node>
    <node id="1">
      <data key="shape">3,4</data>
      <data key="dataPath">/home/user/tensia/src/main/resources/b.bin</data>
    </node>
    <edge id="e1" source="0" target="1">
      <data key="srcDim">1</data>
      <data key="destDim">0</data>
    </edge>
  </graph>
</graphml>
```
General rules are:
* Each node represents a tensor
  * Needs an unique `id`
  * Requires `shape` as comma-separated list of sizes
  * `dataPath` needs to be a path to ND4J binary file
  * Can have another data element with key `locked` if that tensor should be present in final results
* Every edge describes one pair of indices to be contracted
  * `source` and `target` describe ids of nodes
  * `srcDim` contains number (starting from 0) of index to contract from `source` tensor
  * `destDim` number of index to contract from `target` tensor

## Requirements
* Java 8
* sbt
* gcc, make