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
[Unreleased]: https://github.com/atlassian-labs/aws-resources/compare/release-1.18.4...master

## [1.18.4] - 2025-06-16
[1.18.4]: https://github.com/atlassian-labs/aws-resources/compare/release-1.18.3...release-1.18.4

### Fixed
- Bump Ubuntu Focal AMI to `ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-20250603`.
  The previous one was not that old, but still disappeared: `ubuntu-focal-20.04-amd64-server-20250508.1`.

## [1.18.3] - 2025-06-04
[1.18.3]: https://github.com/atlassian-labs/aws-resources/compare/release-1.18.2...release-1.18.3

- Empty release

## [1.18.2] - 2025-06-03
[1.18.2]: https://github.com/atlassian-labs/aws-resources/compare/release-1.18.1...release-1.18.2

### Fixed
- Bump Ubuntu Focal AMI to `ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-20250508.1`.
  The previous one was over a year old and disappeared: `ubuntu-focal-20.04-amd64-server-20240531`.

## [1.18.1] - 2024-12-03
[1.18.1]: https://github.com/atlassian-labs/aws-resources/compare/release-1.18.0...release-1.18.1

### Fixed
- Fix binary incompatibility of `Investment.copy` with the previous minor version.

## [1.18.0] - 2024-11-29
[1.18.0]: https://github.com/atlassian-labs/aws-resources/compare/release-1.17.0...release-1.18.0

### Added
- Add `Investment.Builder`
- Allow overriding the resource_owner label in `Investment.Builder`

### Changed
- Changed resource_owner default label to point to current Atlassian JPT maintainer

## [1.17.0] - 2024-06-14
[1.17.0]: https://github.com/atlassian-labs/aws-resources/compare/release-1.16.0...release-1.17.0

### Added
- Stop depending on `javax.activation:activation`.

## [1.16.0] - 2024-06-10
[1.16.0]: https://github.com/atlassian-labs/aws-resources/compare/release-1.15.0...release-1.16.0

### Added
- Add `CanonicalAmiProvider.Builder.avoidUnattendedUpgrades` for bumping `imageName` to Focal or higher.

### Fixed
- Bump Ubuntu Focal AMI to `ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-20240531`.
  The previous one was 2 years old and disappeared: `ubuntu-focal-20.04-amd64-server-20220610`.

## [1.15.0] - 2024-01-23
[1.15.0]: https://github.com/atlassian-labs/aws-resources/compare/release-1.14.0...release-1.15.0

### Added
- Add `Aws.Builder.housekeeping`

### Fixed
- Tag security groups atomically when created.
- Respect AWS request size limits in `TerminationBatchingEc2` and `TerminationPollingEc2`.
- Extend `ConcurrentHousekeeping` instance timeout.

## [1.14.0] - 2024-01-04
[1.14.0]: https://github.com/atlassian-labs/aws-resources/compare/release-1.13.0...release-1.14.0

### Added
- Add permissions for managing s3 bucket policies and lifecycle configurations.

### Fixed
- Add missing `iam:GetRole` permission. You have to update the policy manually. Fix [JPERF-1407].
- Reduce pressure on CloudFormation when cleaning long lists of expired stacks. Help [JPERF-1332].
- Clean up EC2 security groups before CloudFormation stacks. Fix [JPERF-1208].
- Fix housekeeping fail logging.

[JPERF-1407]: https://ecosystem.atlassian.net/browse/JPERF-1407
[JPERF-1332]: https://ecosystem.atlassian.net/browse/JPERF-1332
[JPERF-1208]: https://ecosystem.atlassian.net/browse/JPERF-1208

## [1.13.0] - 2023-08-14
[1.13.0]: https://github.com/atlassian-labs/aws-resources/compare/release-1.12.2...release-1.13.0

### Added
- Add `Storage.hasContent`.

## [1.12.2] - 2023-08-14
[1.12.2]: https://github.com/atlassian-labs/aws-resources/compare/release-1.12.1...release-1.12.2

Accidentally released `Storage.hasContent` without SemVer minor release.
Use 1.13.0 instead if you want to rely on the new API.

### Fixed
- Stop confusing `Storage`s with common prefix. Fix [JPERF-1272].

[JPERF-1272]: https://ecosystem.atlassian.net/browse/JPERF-1272

## [1.12.1] - 2023-07-24
[1.12.1]: https://github.com/atlassian-labs/aws-resources/compare/release-1.12.0...release-1.12.1

### Fixed
- Print EC2 instance ID in "failed to release itself" error message. 
- Increase cleanup timeout for `Ec2Instance`. Fix [JPERF-1216].

[JPERF-1216]: https://ecosystem.atlassian.net/browse/JPERF-1216

## [1.12.0] - 2023-06-06
[1.12.0]: https://github.com/atlassian-labs/aws-resources/compare/release-1.11.2...release-1.12.0

### Added
- Add `Aws.sts`.
- Add `Aws.callerIdentity`.
- Document required IAM policy in `resources/iam-policy.json`.

### Fixed
- Make our implementations of `AmiTiebreaker` predictable even when AMI creation dates are equal. Fix [JPERF-1153].

[JPERF-1153]: https://ecosystem.atlassian.net/browse/JPERF-1153

## [1.11.2] - 2023-03-07
[1.11.2]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.11.2%0Drelease-1.11.1

### Fixed
- Fix apt lock failures caused by unattended upgrades on Ubuntu Focal. Fix [JPERF-971].

[JPERF-971]: https://ecosystem.atlassian.net/browse/JPERF-971

## [1.11.1] - 2023-02-02
[1.11.1]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.11.1%0Drelease-1.11.0
### Fixed
- Improve `SshAmiMod` logging for clarity when there are multiple AMIs created in the same run.

## [1.11.0] - 2023-02-01
[1.11.0]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.11.0%0Drelease-1.10.2
### Added
- Add logging ssh access in `AwaitingEc2`. [JPERF-954].
- Grant default permissions to EC2 in `SshAmiMod`. Resolve [JPERF-932].
- Add ability to pass a list of managed policies arns to `Aws`. Progress on [JPERF-932].
- Add copying builder to `Aws`.
- Expose `AWSCredentialsProvider` from `Aws`.

[JPERF-954]: https://ecosystem.atlassian.net/browse/JPERF-954
[JPERF-932]: https://ecosystem.atlassian.net/browse/JPERF-932

## [1.10.2] - 2023-01-30
[1.10.2]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.10.2%0Drelease-1.10.1

### Fixed
- Remove incompatible dependency from POM. Fix [JPERF-944]. Progress on [JPERF-466].
- Restore Kotlin metadata compatibility. Fix [JPERF-945].

[JPERF-466]: https://ecosystem.atlassian.net/browse/JPERF-466

## [1.10.1] - 2023-01-27 🐛🐛
[1.10.1]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.10.1%0Drelease-1.9.1

### Added
- Inject `AmiProvider` into `Aws` to choose the default AMI.
- Expose `CanonicalAmiProvider` to find Ubuntu AMIs by name.
- Add `SshAmiMod` to modify existing AMIs.
- Add `AmiCache` to `SshAmiMod` to reuse AMIs instead of recreating them every time.
- Add `Housekeeping` interface, injectable via `Aws.Builder.housekeeping`.
- Extract `ConcurrentHousekeeping` from `Aws.cleanLeftovers`. 
- Add housekeeping for AMIs.

### Deprecated
- Deprecate `Aws.cleanLeftovers(Duration, Duration)` in favor of `ConcurrentHousekeeping.Builder`.

### INCOMPATIBILITY BUG
This version introduced two incompatibilities:
- Accidentally declare an incompatible dependency on `kotlin-stdlib-jdk8`: [JPERF-944].
- Bump Kotlin language to an incompatible language version 1.4 (from 1.1): [JPERF-945].

[JPERF-944]: https://ecosystem.atlassian.net/browse/JPERF-944
[JPERF-945]: https://ecosystem.atlassian.net/browse/JPERF-945

## 1.10.0
This version was not published due to a bug. Use [1.10.1] instead.

## [1.9.1] - 2023-01-24
[1.9.1]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.9.1%0Drelease-1.9.0

### Fixed
- Upgrade Ubuntu image to 20.04 LTS (Focal Fossa). Unblocks [JPERF-917].

[JPERF-917]: https://ecosystem.atlassian.net/browse/JPERF-917

### Added
- Extend `AWSCredentialsProviderChain` with `STSAssumeRoleWithWebIdentitySessionCredentialsProvider` to enable using OIDC token. Resolves [JPERF-916].

## [1.9.0] - 2022-08-22
[1.9.0]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.9.0%0Drelease-1.8.2

### Added
 - Extract default AMI ID resolution logic from `Aws` to `CanonicalImageIdByNameResolver`, so that it's easy to select different AMI by lib consumers.

### Security
 - Filter default AMI resolved by `Aws` (Ubuntu) by Canonical's owner ID. Resolves [JPERF-822].
 - Fail if more than one default AMI is resolved by `Aws`. 

### Fixed
 - Update default AMI resolved by `Aws` to one that is still available in all supported AWS regions. Resolves [JPERF-826].

[JPERF-822]: https://ecosystem.atlassian.net/browse/JPERF-822
[JPERF-826]: https://ecosystem.atlassian.net/browse/JPERF-826

## [1.8.2] - 2022-05-24
[1.8.2]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.8.2%0Drelease-1.8.1

### Security
- Fix a security issue of log4j. Resolves [JPERF-771].

[JPERF-771]: https://ecosystem.atlassian.net/browse/JPERF-771

### Fixed
- Capture failure reason of stack creation as part of `StackFormula`. Resolves [JPERF-494].

[JPERF-494]: https://ecosystem.atlassian.net/browse/JPERF-494

## [1.8.1] - 2022-02-16
[1.8.1]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.8.1%0Drelease-1.8.0

### Fixed
- Changed resource_owner default label to point to current Atlassian JPT maintainer

## [1.8.0] - 2021-10-27
[1.8.0]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.8.0%0Drelease-1.7.1

### Added
- `SshInstance` now contains AWS SDK `Instance` object, which includes i.a. information about private IP address. Unblocks [JPERF-730].
- `ProvisionedStack` now allows to find its security groups based on provided logical id. Unblocks [JPERF-730].

[JPERF-730]: https://ecosystem.atlassian.net/browse/JPERF-730

## [1.7.1] - 2021-09-28
[1.7.1]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.7.1%0Drelease-1.7.0

### Fixed
- Changed resource_owner default label to point to current Atlassian JPT maintainer

## [1.7.0] - 2021-04-06
[1.7.0]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.7.0%0Drelease-1.6.2

### Added
- Enable to choose specific AWS IAM permissionsBoundaryPolicy for storage access.

## [1.6.2] - 2021-03-26
[1.6.2]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.6.2%0Drelease-1.6.1

### Security
- Apply good practices for S3 permissions. Fix [JPERF-728].

### Fixed
- Pick a default instance type in `AwaitingEc2`.

[JPERF-728]: https://ecosystem.atlassian.net/browse/JPERF-728

## [1.6.1] - 2020-07-08
[1.6.1]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.6.1%0Drelease-1.6.0

### Fixed
- Upgrade AWS SDK to 1.11.817.

## [1.6.0] - 2020-03-25
[1.6.0]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.6.0%0Drelease-1.5.0

### Added
- Instance initiated shutdown on instances started with AwaitingEc2 will now terminate the EC2 instance instead of stopping it. Unblocks [JPERF-235].

[JPERF-235]: https://ecosystem.atlassian.net/browse/JPERF-235

## [1.5.0] - 2019-05-02
[1.5.0]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.5.0%0Drelease-1.4.0

### Added
- Allow customisation of resources' release timeout. Resolves [JPERF-419].

[JPERF-419]: https://ecosystem.atlassian.net/browse/JPERF-419

## [1.4.0] - 2019-03-22
[1.4.0]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.4.0%0Drelease-1.3.4

### Added
- A Builder for `Aws`.
- Allow avoiding problematic availability zones. Fix [JPERF-427].

### Deprecated
- Deprecate last `Aws` constructor in favour of `Aws.Builder`.

[JPERF-427]: https://ecosystem.atlassian.net/browse/JPERF-427

## [1.3.4] - 2019-02-07
[1.3.4]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.3.4%0Drelease-1.3.3

### Fixed
- Retry S3 object downloads. Fix [JPERF-382].

[JPERF-382]: https://ecosystem.atlassian.net/browse/JPERF-358

## [1.3.3] - 2019-01-16
[1.3.3]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.3.3%0Drelease-1.3.2

### Fixed
- Fix escape special characters when creating a URI for `Storage`. Resolve [JPERF-358].

[JPERF-358]: https://ecosystem.atlassian.net/browse/JPERF-358

## [1.3.2] - 2019-01-10
[1.3.2]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.3.2%0Drelease-1.3.1

### Fixed
- Stop detaching the policies from all stacks. Resolve [JPERF-317].
- Block until both `User` and `Dependency` are released when releasing `DependentResources`. Resolve [JPERF-337].

[JPERF-317]: https://ecosystem.atlassian.net/browse/JPERF-317
[JPERF-337]: https://ecosystem.atlassian.net/browse/JPERF-337

## [1.3.1] - 2018-12-14
[1.3.1]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.3.1%0Drelease-1.3.0

### Fixed
- Do not disable CloudFormation stack rollback. Resolve [JPERF-294].

[JPERF-294]: https://ecosystem.atlassian.net/browse/JPERF-294

## [1.3.0] - 2018-11-21
[1.3.0]: https://bitbucket.org/atlassian/aws-resources/branches/compare/release-1.3.0%0Drelease-1.2.0

### Added
- Support `ssh:2`

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
