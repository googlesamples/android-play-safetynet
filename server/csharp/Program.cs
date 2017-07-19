/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

using System;

namespace SafetyNetCheck
{
    /// <summary>
    /// Sample code to verify a SafetyNet attestation statement, with both online verification (for testing purposes
    /// only), and offline verification (for production environments).
    ///
    /// To use this sample, you need the following external libraries:
    /// * System.IdentityModel.Tokens.Jwt
    /// * Newtonsoft.Json
    ///
    /// Additionally, the offline verification logic requires the following external library:
    /// * Microsoft.IdentityModel.Logging
    /// 
    /// You can get the latest versions of these libraries via NuGet.
    /// </summary>
    static class Program
    {
        /// <summary>
        /// The attestation token string is returned by getJwsResult() on the client device. You can manually set a
        /// string here for testing purposes.
        /// </summary>
        private static string attestationStatementString = "";

        /// <summary>
        /// Entry point for the testing program.
        /// </summary>
        /// <param name="args">Unused</param>
        [STAThread]
        static void Main(string[] args)
        {
            AttestationStatement statement;

            // You can verify the statement online using Google's verification service, but this service should not be
            // used for production.
            statement = OnlineVerify.ParseAndVerify(attestationStatementString);

            // You can also verify the statement offline using your own verification logic. This is the preferred
            // way to do so for production environments.
            statement = OfflineVerify.ParseAndVerify(attestationStatementString);
        }
    }
}
