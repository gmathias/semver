# Semantic Versioning 2.0.0

Implementation of [Semantic Versioning 2.0.0](http://semver.org)

This is an first draft trying to parse and compare easily version numbers.

## Format

Example : "1.2.0-beta.2+build.456"

 Examples:
- "1"
- "1.0"
- "1.0.0"
 *
Pre-release :
- "1.2.3-alpha"
- "1.2.3-beta"
- "1.2.3-beta.2"
- "1.2.3-rc.1"
- "1.2.3-rc.2"
 *
Build metadata (no impact on precedence) :
- "1.0+build.12"
- "1.0+r24682"
- "1.0-beta+r24682"

## Comparison

SemanticVersion implements Comparable so you can easily sort