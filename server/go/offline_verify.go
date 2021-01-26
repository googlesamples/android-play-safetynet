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
	"crypto/x509"
	"encoding/pem"
	"errors"
	"github.com/dgrijalva/jwt-go"
)

func ParseAndVerify(signedAttestationStatment string) (*AttestationStatement, error) {

	// Parse JSON Web Signature format.
	token, _, err := new(jwt.Parser).ParseUnverified(signedAttestationStatment, &AttestationStatement{})
	if err != nil {
		return nil, errors.New("Failure: Parse token error : " + err.Error())
	}

	// Verify the signature of the JWS and retrieve the signature certificate.
	header, err := GetAttestationHeader(token.Header)
	if err != nil {
		return nil, errors.New("Bad header format : " + err.Error())
	}
	cert := "-----BEGIN CERTIFICATE-----\n" + header.AlgorCertificats[0] + "\n-----END CERTIFICATE-----"
	googlePublicKey, _ := jwt.ParseRSAPublicKeyFromPEM([]byte(cert))
	_, err = jwt.ParseWithClaims(signedAttestationStatment, &AttestationStatement{}, func(token *jwt.Token) (interface{}, error) {
		return googlePublicKey, nil
	})
	if err != nil {
		return nil, errors.New("Failure: Signature verification failed : " + err.Error())
	}

	// Verify the hostname of the certificate.
	if !verifyHostname("attest.android.com", cert) {
		return nil, errors.New("Failure: Certificate isn't issued for the hostname attest.android.com")
	}

	return token.Claims.(*AttestationStatement), nil
}

func verifyHostname(hostname, pemCertificat string) bool {
	block, _ := pem.Decode([]byte(pemCertificat))
	cert, err := x509.ParseCertificate(block.Bytes)
	if err != nil {
		return false
	}
	return cert.Subject.CommonName == hostname
}
