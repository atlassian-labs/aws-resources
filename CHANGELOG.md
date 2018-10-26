# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## API
The API consists of all public Kotlin types from `com.atlassian.performance.tools.aws.api` and its subpackages:

  * [source compatibility]
  * [binary compatibility]
  * [behavioral compatibility] with behavioral contracts expressed via Javadoc

[source compatibility]: http://cr.openjdk.java.net/~darcy/OpenJdkDevGuide/OpenJdkDevelopersGuide.v0.777.html#source_compatibility
[binary compatibility]: http://cr.openjdk.java.net/~darcy/OpenJdkDevGuide/OpenJdkDevelopersGuide.v0.777.html#binary_compatibility
[behavioral compatibility]: http://cr.openjdk.java.net/~darcy/OpenJdkDevGuide/OpenJdkDevelopersGuide.v0.777.html#behavioral_compatibility

### POM
Changing the license is breaking a contract.
Adding a requirement of a major version of a dependency is breaking a contract.
Dropping a requirement of a major version of a dependency is a new contract.

## [Unreleased]
[Unreleased]: https://bitbucket.org/atlassian/aws-resources/branches/compare/master%0Drelease-1.2.0

## [1.2.0] - 2018-10-26
[1.2.0]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.2.0%0Drelease-1.1.1

### Added
- Fail or warn users if they didn't explicitly declare they took care of AWS housekeeping. Mitigate [JPERF-235].

### Deprecated
- Deprecate the compatibility `Aws` constructor, which only warns users instead of failing fast.

[JPERF-235]: https://ecosystem.atlassian.net/browse/JPERF-235

## [1.1.1] - 2018-10-09
[1.1.1]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.1.1%0Drelease-1.1.0

## Fixed
- Have more patience for stack cleanup. Fix [JPERF-86].
- Add `InstanceType.C5d9xlarge`. Unblock [JPERF-186].

[JPERF-86]: https://ecosystem.atlassian.net/browse/JPERF-86
[JPERF-186]: https://ecosystem.atlassian.net/browse/JPERF-186

## [1.1.0] - 2018-09-18
[1.1.0]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.1.0%0Drelease-1.0.0

### Added
- Add `TextCapacityMediator(region: Regions)`.
- Inject `detectionTimeout` and `pollingTimeout` into `StackFormula`.
- Expose the behavior of `StackFormula`.

### Fixed
- Bump AMI image version and improve error message. Fix [JPERF-103].

[JPERF-103]: https://ecosystem.atlassian.net/browse/JPERF-103

### Deprecated
- Deprecate `TextCapacityMediator()`.
- Deprecate `BatchingCloudformation` as public API.

### Fixed
- Time out when detecting existing stacks matching a `StackFormula` instead of hanging indefinitely. Fix [JPERF-60].

[JPERF-60]: https://ecosystem.atlassian.net/browse/JPERF-60

## [1.0.0] - 2018-09-04
[1.0.0]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.0.0%0Drelease-0.0.3

### Changed
- Define public API for the module.
- Use stable APT APIs.
- Opt in for the Nanny.

### Fixed
- Add this changelog.

## [0.0.3] - 2018-08-29
[0.0.3]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-0.0.3%0Drelease-0.0.2

### Fixed
- Remove only stacks which "lifespan" tag.
- Don't send requests to AWS while creating Aws object.

## [0.0.2] - 2018-08-29
[0.0.2]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-0.0.2%0Drelease-0.0.1

### Added
- License.

## [0.0.1] - 2018-08-22
[0.0.1]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-0.0.1%0Dinitial-commit

### Added
- Migrate aws-resources from [JPT submodule].
- Add [README.md](README.md).
- Configure Bitbucket Pipelines.

[JPT submodule]: https://stash.atlassian.com/projects/JIRASERVER/repos/jira-performance-tests/browse/aws-resources?at=cb909508d9c504d7126d68af9c72087f5822ff2b
