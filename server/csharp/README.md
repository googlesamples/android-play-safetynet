Important Notice
================

SafetyNet Attestation is now deprecated. New and existing developers should use the [Play
Integrity API](https://developer.android.com/google/play/integrity) instead.

This repository will remain for reference, but will not receive any updates in the future.

C# Server SafetyNet Samples
===================================

This sample demonstrates how to verify the response received from the SafetyNet service in C#.

It shows how to extract the compatibility check response from the JWS message, validate its SSL
certificate chain, hostname and signature.

This check can be done completely offline (See `OfflineVerify.cs`) or by using the
_Android Verification API_ to verify the content and signature of the response (see
`OnlineVerifycs`). This REST API requires you to register at the Google Developers console and
register for an API key. Detailed steps are available [in the documentation] under _Validating the
response with Google APIs_.


Note that this sample only provides a basic overview over the verification process and does not
cover all possibilities. For example,it is reccomended to always verify the nonce in the request
as well. This sample also does not show the app-to-server communication.

For more details, see the guide at
https://developer.android.com/training/safetynet/index.html#verify-compat-check .

Next: Verify the response
-------------------------

This sample only demonstrates verification of the server response. The next step is to verify that
the attestation response matches the request by verifying their contents, including the package
name, digest, timestamp and nonce.

**This step is crucial. If you do not verify the response and request, you may be vulnerable to
a [replay attack][replay-attack], in which an attacker can replay an old attestation response from
a different device or app.**

Pre-requisites
--------------

- A C# compiler.

Getting Started
---------------

This sample was tested with Visual Studio 2015 and 2017, but should work with .NET core or mono.

To build this project using Visual Studio 2015 or 2017, simply open SafetyNetVerify.csproj and
external dependencies should resolve automatically from NuGet.

You can modify `Program.cs` and set `attestationStatementString` to the value returned by
getJwsResult() on the client device to attest a hardcoded value.

Online verification is available for test purposes only. To use it, define a compilation symbol
called `ONLINE_VERIFICATION`, and online verification will be used. Note that online verification
should not be used on production environments.

Additionally, online verification requires an API key for the _Android Verification API_. Follow
the steps in [the documentation under "_Validating the response with Google APIs_"][key] and add
the API key into the `ApiKey` field at the top of `OnlineVerify.cs`.

For different versions of Visual Studio, .NET core or mono, you may need to manually build the
source. The required dependencies are stated in project.json.

Running the Samples
------------------
* Build and run the [Android component](../../android) of this sample.
* Retrieve a signed statement from the Android app and copy it to your machine. (You can use the
"Share Result" option.)
* Build this server component and provide the signed statement as input, either as a command line
argument, or by hardcoding it into `attestationStatementString` in `Program.cs`.

Support
-------

If you've found an error in this sample, please file an issue:
https://github.com/googlesamples/android-SafetyNetSample

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub. Please see CONTRIBUTING.md for more details.

License
-------

Copyright 2017 Google Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.

[key]: https://developer.android.com/training/safetynet/index.html#verify-compat-check "See Validating the response with Google APIs"
[replay-attack]:https://en.wikipedia.org/wiki/Replay_attack
