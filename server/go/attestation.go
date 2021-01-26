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
	"encoding/base64"
	"errors"
	"github.com/dgrijalva/jwt-go"
)

type AttestationHeader struct {
	Algorithme       string   `json:"alg"`
	AlgorCertificats []string `json:"x5c"`
}

func GetAttestationHeader(header map[string]interface{}) (*AttestationHeader, error) {
	result := AttestationHeader{}
	if algo, ok := header["alg"].(string); ok {
		result.Algorithme = algo
	} else {
		return nil, errors.New("No alg header")
	}
	if certs, ok := header["x5c"]; ok {
		if certsList, ok := certs.([]interface{}); ok {
			for _, data := range certsList {
				result.AlgorCertificats = append(result.AlgorCertificats, data.(string))
			}
		} else {
			return nil, errors.New("Header x5c not a list")
		}
	} else {
		return nil, errors.New("No x5c header")
	}
	return &result, nil
}

type AttestationStatement struct {
	Nonce                      string   `json:"nonce"`
	TimestampMs                int64    `json:"timestampMs"`
	ApkPackageName             string   `json:"apkPackageName"`
	ApkDigestSha256            string   `json:"apkDigestSha256"`
	CtsProfileMatch            bool     `json:"ctsProfileMatch"`
	ApkCertificateDigestSha256 []string `json:"apkCertificateDigestSha256"`
	BasicIntegrity             bool     `json:"basicIntegrity"`
	EvaluationType             string   `json:"evaluationType"`
}

func (statement AttestationStatement) Valid() error {
	vErr := new(jwt.ValidationError)

	if vErr.Errors == 0 {
		return nil
	}

	return vErr
}

func (statement AttestationStatement) ReadNonce() string {
	decoded, _ := base64.StdEncoding.DecodeString(statement.Nonce)
	return string(decoded)
}
