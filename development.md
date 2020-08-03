Algorithm Development with Scala
====================

In this guide, we'll walk you through some of the changes regarding algorithm development 
in Scala that will hopefully make your job a little easier. If you have any questions or comments please feel free to file an issue :)

# Getting Started

## Previous system

If you've done Algorithm development work in Scala previously then you might be familiar with the following project structure:
```
- algorithmia.conf
- build.sbt
- deps.sbt
- README.md
- src
    - main
        - scala
            - algorithmia
                - algorithm_name
                    - algorithm_name.scala
```

and inside of `algorithm_name.scala` you would see the following:

```scala
package algorithmia.algorithm_name

import com.algorithmia._
import com.algorithmia.algo._
import com.algorithmia.data._
import com.google.gson._

class algorithm_name {
  def apply(input: String): String = {
    "Hello " + input
  }
}

```

This system is simple, however it hides things away from you. It makes it difficult to understand how the `apply` function gets interacted with, especially at the interface.
It's difficult to debug a project when you can't execute it, that's why we've changed the algorithm development experience.

## New System
Similar to before, the architecture remains relatively unchanged, except for the name of your package, and your primary class, those have changed to `Algorithm`. 
We've also added a project directory, in the file `build.properties` you can now specify the`sbt` version, cool right?

```
- algorithmia.conf
- build.sbt
- deps.sbt
- README.md
- project
    - build.properties
    - plugins.sbt 
- src
    - main
        - scala
            - com.algorithmia
                - Algorithm.scala
```

The structure looks similar, but lets explore that same hello world project but defined in our new system

```
package com.algorithmia
import com.algorithmia.handler.AbstractAlgorithm
import scala.util.{Success, Try}

class Algorithm extends AbstractAlgorithm[String, String] {
  var someVariable: Option[String] = "not loaded"

  override def apply(input: String): Try[String] = {
    Success(s"hello $input, we've $someVariable")
  }
}

object Algorithm {
  val handler = Algorithmia.handler(new Algorithm)
  def main(args: Array[String]): Unit = {
    handler.serve()
  }
}

```
That looks different, lets unpack this.
In our previous we would have just found your `apply` function and serialized/deserialized code internally, and tried to map that input to one of your applys.
This was relatively clean when things worked, but when your input or output structure was more complex, this broke down and made things really difficult to test.

We've since created a new structure that allows you to physically test the interface of your algorithm on your laptop. To do this, you'll need to create a class that extends an abstract class,
`AbastractAlgorithm`.
`class Algorithm extends AbstractAlgorithm[String, String]`
In the `[]` type box, you need to define the input and output type for your algorithm. Presently this system supports only a single input and output type, however that may change in the future.
Second, you'll notice that we have two defined functions, `apply` and `load`. `apply` is the entrypoint to your algorithm (like the previous system) however instead of just throwing an exception, we're catching failures and handling those within the `AlgorithmHandler` class, hence the `Try[]` type.

You can also define a `load` function:
``` 
  override def load = Try{
    someVariable = Some("loaded")
    Success()
  }
```
This function has no input arguments, and simply returns a Try[()] type (if there's a failure, wrap the exception in Failure(exception), if you're not expecting it; wrap the outer scope with Try)
`load` gets called when the algorithm's container first gets ready, unlike apply - which gets executed whenever an API request hits the container. This is great for when you need to download or load some model or binary file from an external source before your algorithm can function.
An example of this in scala could be a `spark` instance or a `ffmpeg` binary object video processing.


and finally the last component, the part that changes your algorithm from a simple library, into an executable:

``` 
object Algorithm {
  val handler = Algorithmia.handler(new Algorithm)
  def main(args: Array[String]): Unit = {
    handler.serve()
  }
}
```
This is a fixed section of your algorithm and should not be changed, but what it does is important. This process instances your class with all of your definitions, and passes it to an internal tool we call the `handler`, which 
intiates the conversation with Algorithmia. 

## Things to note

### Types
If you have a more complex Input/Output than a basic scala type, you should ensure that your Input types implement an [implicit reads](algorithm_examples/spark_mleap/src/main/scala/com/algorithmia/InputExample.scala), and your Output types implement an [implicit writes](algorithm_examples/spark_mleap/src/main/scala/com/algorithmia/OutputExample.scala). 
To see a full example check in our [examples](algorithm_examples) directory.

### Testing
We'll be building a better ergonomic experience around this, however for now if you want to run this on your laptop directly you'll need to create a fifo pipe called `algoout` in your system `/tmp` directory.
With that done, you can run either that block in your IDE, or compile and execute your jar, either way after you see an `PIPE_INIT_COMPLETE` message printed to stdout, you'll be able to provide a json request that contains your algorithm input, like the following:
```
{"content_type": "...", "data": .... }
```
Where `content_type` should be either `text` or `json`, depending on your input type. If you have a more complex input type than `String`, you should use `json`.
