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


## License

Licensend under the [MIT licence](https://opensource.org/license/mit/)

Copyright 2023 Rohde Fischer

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.