algorithmia-scala
=================

Scala client for accessing Algorithmia's algorithm marketplace and data APIs.

[![Latest Release](https://img.shields.io/maven-central/v/com.algorithmia/algorithmia-scala_2.13.svg)](http://repo1.maven.org/maven2/com/algorithmia/algorithmia-scala_2.13/)

## Getting started

The Algorithmia scala client is published to Maven central and can be added as a dependency via:

```scala
libraryDependencies += "com.algorithmia" %% "algorithmia-scala" % "0.9.6"
```

Instantiate a client using your API Key:

```scala
val client = Algorithmia.client(apiKey)
```

Notes:

- API key may be omitted only when making calls from algorithms running on the Algorithmia cluster

Now you are ready to call algorithms.

## Calling Algorithms

The following examples of calling algorithms are organized by type of input/output which vary between algorithms.

Note: a single algorithm may have different input and output types, or accept multiple types of input, so consult the algorithm's description for usage examples specific to that algorithm.

### Text input/output

Call an algorithm with text input by simply passing a string into its `pipe` method.
If the algorithm output is text, call the `asString` method on the response.

```scala
val algo = client.algo("algo://demo/Hello/0.1.1")
val result = algo.pipe("HAL 9000")
println(result.asString)
// -> Hello HAL 9000
```

### JSON input/output

Call an algorithm with JSON input by simply passing in a type that can be serialized to JSON,
including most plain old java objects, scala classes, and collection types.
If the algorithm output is JSON, call the `as` method on the response with the type that it should be deserialized into:

```scala
val algo = client.algo("algo://WebPredict/ListAnagrams/0.1.0")
val words = List("transformer", "terraforms", "retransform")
val result = algo.pipe(words)
// WebPredict/ListAnagrams returns an array of strings, so deserialize the result:
val anagrams = result.as[List[String]]
// -> List("transformer", "retransform")
```

Alternatively, you may work with raw JSON input by calling `pipeJson`,
and raw JSON output by calling `asJsonString` on the response:

```scala
val jsonWords = "[\"transformer\", \"terraforms\", \"retransform\"]"
val result2 = algo.pipeJson(jsonWords)
val anagrams = result2.asJsonString()
// -> "[\"transformer\", \"retransform\"]"

val durationInSeconds = response.getMetadata().duration
```


### Binary input/output

Call an algorithm with binary input by passing a byte array into the `pipe` method.
If the algorithm response is binary data, then call the `asBytes` method on the response to obtain the raw byte array.

```scala
val input = Files.readAllBytes(new File("/path/to/bender.jpg").toPath())
val result = client.algo("opencv/SmartThumbnail/0.1").pipe(input)
val buffer = result.asBytes
// -> [byte array]
```

### Error handling

API errors will result in the call to `pipe` throwing `APIException`.
Errors that occur durring algorithm execution will result in `AlgorithmException` when attempting to read the response.

```scala
val algo = client.algo("util/whoopsWrongAlgo")
try {
    val result = algo.pipe("Hello, world!")
    val output = result.asString
} catch {
  case e: Exception => println("API Exception: " e.getMessage)
}
```

### Request options

The client exposes options that can configure algorithm requests.
This includes support for changing the timeout or indicating that the API should include stdout in the response.:

```scala
val algo = client.algo("algo://demo/Hello/0.1.1")
                 .withTimeout(Duration(1, MINUTES))
                 .withStdout(true)
val result = algo.pipe("HAL 9000")
val stdout = response.metadata.stdout
```

Note: `withStdout(true)` is ignored if you do not have access to the algorithm source.

## Working with Data

The Algorithmia scala client also provides a way to manage both Algorithmia hosted data
and data from Dropbox or S3 accounts that you've connected to you Algorithmia account.

This client provides a `DataFile` type (generally created by `client.file(uri)`)
and a `DataDir` type (generally created by `client.dir(uri)`) that provide
methods for managing your data.

### Create directories

Create directories by instantiating a `DataDirectory` object and calling `create()`:

```scala
val robots = client.dir("data://.my/robots")
robots.create()

val dbxRobots = client.dir("dropbox://robots")
dbxRobots.create()
```

### Upload files to a directory

Upload files by calling `put` on a `DataFile` object, or by calling `putFile` on a `DataDirectory` object.

```scala
val robots = client.dir("data://.my/robots")

// Upload local file
robots.putFile(new File("/path/to/Optimus_Prime.png"))
// Write a text file
robots.file("Optimus_Prime.txt").put("Leader of the Autobots")
// Write a binary file
robots.file("Optimus_Prime.key").put("correct horse battery staple".getBytes)
```

### Download contents of file

Download files by calling `getString`, `getBytes`, or `getFile` on a DataFile object:

```scala
val robots = client.dir("data://.my/robots")

// Download file and get the file handle
val t800File = robots.file("T-800.png").getFile

// Get the file's contents as a string
val t800Text = robots.file("T-800.txt").getString

// Get the file's contents as a byte array
val t800Bytes = robots.file("T-800.png").getBytes
```

### Delete files and directories

Delete files and directories by calling `delete` on their respective `DataFile` or `DataDirectory` object.
`DataDirectories` take an optional `force` parameter that indicates whether the directory should be deleted
if it contains files or other directories.

```scala
client.file("data://.my/robots/C-3PO.txt").delete()
client.dir("data://.my/robots").delete(false)
```

### List directory contents

Iterate over the contents of a directory using the iterator returned by calling `files`, or `dirs` on a `DataDirectory` object:

```scala
// List top level directories
val myRoot = client.dir("data://.my");
for(dir <- myRoot.dirs) {
    println(s"Directory $dir at URL ${dir.url}")
}

// List files in the 'robots' directory
val robots = client.dir("data://.my/robots")
for(file <- robots.files) {
    println(s"File $file at URL: ${file.url}")
}
```

### Manage directory permissions

Directory permissions may be set when creating a directory, or may be updated on already existing directories.

```scala
val fooLimited = client.dir("data://.my/fooLimited")

// Create the directory as private
fooLimited.create(DataAcl(read = DataPrivate))

// Update a directory to be public
fooLimited.updatePermissions(DataAcl(read = DataPublic))

// Check a directory's permissions
if (fooLimited.getPermissions.read == DataPrivate) {
    println("fooLimited is private")
}
```
