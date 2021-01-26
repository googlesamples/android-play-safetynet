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
	"github.com/stretchr/testify/assert"
	"strings"
	"testing"
)

func TestOnlineParseAndVerify(t *testing.T) {
	attestation, err := ParseAndVerifyOnline(mobileToken)
	if assert.NoError(t, err) {
		assert.Equal(t, attestation.TimestampMs, int64(1611644438128))
		assert.Equal(t, attestation.Nonce, "jlFGG2IS+XG3BAXq2jAOGIO6nkYiAAlFU2FmZXR5IE5ldCBTYW1wbGU6IDE2MTE2NDQ0MzY0NDM=")
		assert.Equal(t, attestation.ApkPackageName, "com.example.android.safetynetsample")
		assert.Equal(t, attestation.EvaluationType, "BASIC")
	}
}

func TestOnlineParseAndVerifyFormatError(t *testing.T) {
	tokenPart := strings.Split(mobileToken, ".")
	_, err := ParseAndVerifyOnline(tokenPart[0])
	assert.EqualError(t, err, "Failure: The cryptographic signature of the attestation statement couldn't be verified.")
}

func TestOnlineParseAndVerifyBadSignature(t *testing.T) {
	_, err := ParseAndVerifyOnline(mobileTokenBadSignature)
	assert.EqualError(t, err, "Failure: The cryptographic signature of the attestation statement couldn't be verified.")
}

func TestOnlineParseAndVerifyBadCertificate(t *testing.T) {
	_, err := ParseAndVerifyOnline(mobileTokenBadCertificate)
	assert.EqualError(t, err, "Failure: The cryptographic signature of the attestation statement couldn't be verified.")
}
