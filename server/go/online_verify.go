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
	"bytes"
	"encoding/json"
	"errors"
	"io/ioutil"
	"net/http"
)

const (
	ApiKey = "YOUR_API_KEY"
	URL    = "https://www.googleapis.com/androidcheck/v1/attestations/verify?key=" + ApiKey
)

type VerificationRequest struct {
	SignedAttestation string `json:"signedAttestation"`
}

type VerificationResponse struct {
	IsValidSignature bool                       `json:"isValidSignature"`
	ErrorMessage     *VerificationErrorResponse `json:"error"`
}

type VerificationErrorResponse struct {
	Code    int         `json:"code"`
	Message string      `json:"message"`
	Errors  interface{} `json:"errors"`
	Status  string      `json:"status"`
}

func ParseAndVerifyOnline(signedAttestationStatment string) (*AttestationStatement, error) {
	requestParam := VerificationRequest{
		SignedAttestation: signedAttestationStatment,
	}
	requestData, _ := json.Marshal(requestParam)
	req, _ := http.NewRequest("POST", URL, bytes.NewBuffer(requestData))
	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return nil, errors.New("Failure: Network error while connecting to the Google Service " + URL)
	}

	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return nil, errors.New("Failure: Read HTTP response error : " + err.Error())
	}

	var result VerificationResponse
	err = json.Unmarshal(body, &result)
	if err != nil {
		return nil, errors.New("Failure: Unmarshal HTTP response error : " + err.Error())
	}

	if result.ErrorMessage != nil {
		return nil, errors.New("Failure: The API encountered an error processing this request " + result.ErrorMessage.Message)
	}

	if !result.IsValidSignature {
		return nil, errors.New("Failure: The cryptographic signature of the attestation statement couldn't be verified.")
	}

	return ParseAndVerify(signedAttestationStatment)
}
