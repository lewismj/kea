# KEA

This library provides a type-safe (_validated_),  way to query Typelevel configuration.

Configuration values are returned as a `ValidatedNel[T]`. So, any errors in your
configuration may be accumulated.

## Example

