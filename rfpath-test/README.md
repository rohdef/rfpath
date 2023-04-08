# Test

TBD

The testing library is not designed to be thread safe.
Due to the mutable nature of file systems there is no current motivation to
make the test structures immutable as would normally be the case.


## Usage

TBD


## Extended usage

Not all testing scenarios can be predicted.
To accommodate custom scenarios `TestFile` and `TestDirectory` are abstract classes.
Thus custom and complex behaviors can be implemented for usage in tests.


## Current state
This is pre-alpha - use at your own risk.

The project is attempted at a useable state at all times and used in my other projects.
That said it is far from the level of general purpose at the moment.

Major features in the TODO are
(please see [Issues](https://github.com/rohdef/rfpath/issues?q=is%3Aopen%20is%3Aissue%20project%3Arohdef%2F2) for a complete list):

- Testing of error scenarios
- Builder for complex structures