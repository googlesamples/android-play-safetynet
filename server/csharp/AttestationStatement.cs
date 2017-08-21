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
using System.Collections.Generic;
using System.Globalization;

namespace SafetyNetCheck
{
    /// <summary>
    /// Class for parsing the data inside the claims of an attestation JWT
    /// </summary>
    public sealed class AttestationStatement
    {
        /// <summary>
        /// The original claims dictionary.
        /// </summary>
        public Dictionary<string, string> Claims { get; private set; }

        /// <summary>
        /// The nonce provided when running the attestation on the device.
        /// </summary>
        public byte[] Nonce { get; private set; }

        /// <summary>
        /// The timestamp when the attestation was performed.
        /// </summary>
        public long TimestampMs { get; private set; }

        /// <summary>
        /// The package name of the calling APK.
        /// </summary>
        public string ApkPackageName { get; private set; }

        /// <summary>
        /// The SHA256 digest of the calling APK.
        /// </summary>
        public byte[] ApkDigestSha256 { get; private set; }

        /// <summary>
        /// The SHA256 digest of the signing certificate for the APK.
        /// </summary>
        public byte[] ApkCertificateDigestSha256 { get; private set; }

        /// <summary>
        /// Whether or not the device was determined to have a CTS profile
        /// match.
        /// </summary>
        public bool CtsProfileMatch { get; private set; }

        /// <summary>
        /// Whether or not the device was determined to have basic integrity.
        /// </summary>
        public bool BasicIntegrity { get; private set; }

        /// <summary>
        /// Constructs an Attestation statement from a dictionary of claims.
        /// </summary>
        /// <param name="claims">The claims retrieved from an attestation
        /// statement string.</param>
        public AttestationStatement(Dictionary<string, string> claims)
        {
            Claims = claims;

            if (claims.ContainsKey("nonce"))
            {
                Nonce = Convert.FromBase64String(claims["nonce"]);
            }

            if (claims.ContainsKey("timestampMs"))
            {
                long timestampMsLocal;
                long.TryParse(
                    claims["timestampMs"],
                    NumberStyles.Integer,
                    CultureInfo.InvariantCulture,
                    out timestampMsLocal);
                TimestampMs = timestampMsLocal;
            }

            if (claims.ContainsKey("apkPackageName"))
            {
                ApkPackageName = claims["apkPackageName"];
            }

            if (claims.ContainsKey("apkDigestSha256"))
            {
                ApkDigestSha256 = Convert.FromBase64String(
                    claims["apkDigestSha256"]);
            }

            if (claims.ContainsKey("apkCertificateDigestSha256"))
            {
                ApkCertificateDigestSha256 = Convert.FromBase64String(
                    claims["apkCertificateDigestSha256"]);
            }

            if (claims.ContainsKey("ctsProfileMatch"))
            {
                bool ctsProfileMatchLocal;
                bool.TryParse(
                    claims["ctsProfileMatch"],
                    out ctsProfileMatchLocal);
                CtsProfileMatch = ctsProfileMatchLocal;
            }

            if (claims.ContainsKey("basicIntegrity"))
            {
                bool basicIntegrityLocal;
                bool.TryParse(
                    claims["basicIntegrity"],
                    out basicIntegrityLocal);
                BasicIntegrity = basicIntegrityLocal;
            }
        }
    }
}
