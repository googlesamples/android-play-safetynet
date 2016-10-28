SafetyNet Samples
===================================

These samples demonstrate the end-to-end use of the SafetyNet API.
SafetyNet provides services for analyzing the configuration of a particular device to verify that it passes the Android compatibility test.

This repository consists of two components:

* [Android](android/SafetyNetSample): Android sample app, showing the use of Google Play Services for the SafetyNet API on a device.
* [Server](server/): Two samples, showing how to verify a SafetyNet API response on a server, including offline and online via the Android Device Verification API.

For more details, see the documenation for each and component and the guide at https://developer.android.com/training/safetynet/index.html .


Runing the Samples
------------------
* Build and run the [Android component](android/SafetyNetSample) of this sample.
* Retrieve a signed statement from the Android app and copy it to your machine. (You can use the "Share Result" option.)
* Build the [server component](server).
* Run the `OfflineVerify` or `OnlineVerify` checks and provide the signed statement from the app as input.


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

