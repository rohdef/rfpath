# rfpath - functional path operations

A path utility based on [monadic operations](https://arrow-kt.io/)
and with proper testing implementations.

Currently this supports:

- Traversing directories
- Reading and writing text to files
- Simple test implementations


## Current state
This is pre-alpha - use at your own risk.

The project is attempted at a useable state at all times and used in my other projects.
That said it is far from the level of general purpose at the moment.

Major features in the TODO are
(please see [Issues](https://github.com/rohdef/rfpath/issues?q=is%3Aopen%20is%3Aissue%20project%3Arohdef%2F3) for a complete list):

- Reading/writing binary content to files
- Streamed reading/writing of data from/to files
- Handling of directory/file ownership
- Testing implementations of error scenarios


## Usage
TBD


## Structure
* `core` - interfaces for the path operations
* `okio` - a mainly [Okio](https://square.github.io/okio/) based implementation of `core`
* `test` - [test](./rfpath-test) implementation of `core`


## Design principles

Error cases are considered as part of the domain, and are thus part of the method signatures.
This is done through the monad `Either`

To the point possible everything is strongly typed operations,
thys operations such as `newFile` will return `File` and similar for other.
Where strong typing is not possible the base type of `Path` is used,
and a cast might be needed

Testing should be possible to do without mocking frameworks (and other reflaction anti-patterns)