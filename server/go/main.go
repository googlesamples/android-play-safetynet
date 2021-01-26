// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package main

import (
	"fmt"
	"os"
	"strconv"
)

func main() {
	allParams := os.Args
	if len(allParams) != 2 {
		fmt.Println("Usage: android-play-safetynet <signed attestation statement>")
		return
	}

	stmt, err := ParseAndVerify(allParams[1])
	if err != nil {
		fmt.Println("Failure: Failed to parse and verify the attestation statement.")
		fmt.Println("Failure detail: " + err.Error())
		return
	}

	fmt.Println("The content of the attestation statement is:")

	// Nonce that was submitted as part of this request.
	fmt.Println("Nonce: " + stmt.ReadNonce())
	// Timestamp of the request.
	fmt.Println("Timestamp: " + strconv.FormatInt(stmt.TimestampMs, 10) + " ms")

	if len(stmt.ApkPackageName) > 0 && len(stmt.ApkDigestSha256) > 0 {
		// Package name and digest of APK that submitted this request. Note that these details
		// may be omitted if the API cannot reliably determine the package information.
		fmt.Println("APK package name: " + stmt.ApkPackageName)
		fmt.Println("APK digest SHA256: " + stmt.ApkDigestSha256)
	}
	// Has the device a matching CTS profile?
	ctsProfile := "FALSE"
	if stmt.CtsProfileMatch {
		ctsProfile = "TRUE"
	}
	fmt.Println("CTS profile match: " + ctsProfile)
	// Has the device passed CTS (but the profile could not be verified on the server)?
	hasBasicIntegrity := "FALSE"
	if stmt.BasicIntegrity {
		hasBasicIntegrity = "TRUE"
	}
	fmt.Println("Basic integrity match: " + hasBasicIntegrity)

	fmt.Println("\n** This sample only shows how to verify the authenticity of an " +
		"attestation response. Next, you must check that the server response matches the " +
		"request by comparing the nonce, package name, timestamp and digest.")

}
