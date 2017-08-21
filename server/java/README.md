Java Server SafetyNet Samples
===================================

This sample demonstrates how to verify the response received from the SafetyNet service.

It shows how to extract the compatibility check response from the JWS message, validate its SSL certificate chain, hostname and signature.

This check can be done completely offline (See `OfflineVerify.java`) or by using the _Android Verification API_ to verify the content and signature of the response (see `OnlineVerify`). This REST API requires you to register at the Google Developers console and register for an API key. Detailed steps are available [in the documentation] under _Validating the response with Google APIs_.


Note that this sample only provides a basic overview over the verification process and does not cover all possibilities. For example,it is reccomended to always verify the nonce in the request as well. This sample also does not show the app-to-server communication.

For more details, see the guide at https://developer.android.com/training/safetynet/index.html#verify-compat-check .

Next: Verify the response
-------------------------

This sample only demonstrates verification of the server response. The next step is to verify that the attestation response matches the request by verifying their contents, including the package name, digest, timestamp and nonce.

**This step is crucial. If you do not verify the response and request, you may be vulnerable to a [replay attack][replay-attack], in which an attacker can replay an old attestation response from a different device or app.**




Pre-requisites
--------------

- up-to-date Java JDK
The following dependencies are included in the
- [Google HTTP Client Library for Java (with Jackson 2 extension)](https://developers.google.https://developers.google.com/api-client-library/java/google-http-java-client/) for online verification (module `google-http-client-jackson2`)
- up-to-date Apache HttpClient Library for certificate verification

Getting Started
---------------

This sample uses the Gradle build system, but can also be easily build with any IDE or with any build tool.

To build this project using the provided gradle scripts, use the
"gradlew build" command and then run these special run tasks where you can specify the signed statement as a paramter:

* OfflineVerify: `gradlew runOfflineVerify -PsignedStatement=...`
* OnlineVerify: `gradlew runOnlineVerify -PsignedStatement=...`

Online verification requires an API key for the _Android Verification API_. Follow the steps in [the documentation under "_Validating the response with Google APIs_"][key] and add the API key into the `API_KEY` field at the top of `OnlineVerify.java`.


If you are not using Gradle, you can also build this sample with any other build system and run it directly from the commandline, adding the signed stament as a paramter. Just be aware that the `OnlineVerify.java` requires the Google HTTP Client Library for Java.

Runing the Samples
------------------
* Build and run the [Android component](../../android) of this sample.
* Retrieve a signed statement from the Android app and copy it to your machine. (You can use the "Share Result" option.)
* Build this server component and provide the signed statement as input.


Support
-------

If you've found an error in this sample, please file an issue:
https://github.com/googlesamples/android-SafetyNetSample

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub. Please see CONTRIBUTING.md for more details.

License
-------

Copyright 2016 Google Inc.

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
