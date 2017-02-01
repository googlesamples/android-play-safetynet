import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.api.client.util.Base64;
import com.google.api.client.util.Key;

import java.util.Arrays;

/**
 * Class for parsing the JSON data.
 */
public class AttestationStatement extends JsonWebSignature.Payload {
    @Key
    private String nonce;

    @Key
    private long timestampMs;

    @Key
    private String apkPackageName;

    @Key
    private String apkDigestSha256;

    @Key
    private boolean ctsProfileMatch;

    @Key
    private boolean basicIntegrity;

    @Key
    private String[] apkCertificateDigestSha256;


    public byte[] getNonce() {
        return Base64.decodeBase64(nonce);
    }

    public long getTimestampMs() {
        return timestampMs;
    }

    public String getApkPackageName() {
        return apkPackageName;
    }

    public byte[] getApkDigestSha256() {
        return Base64.decodeBase64(apkDigestSha256);
    }

    public boolean isCtsProfileMatch() {
        return ctsProfileMatch;
    }

    public byte[] getApkCertificateDigestSha256() {
        return Base64.decodeBase64(apkCertificateDigestSha256[0]);
    }

    public boolean hasBasicIntegrity() {
        return basicIntegrity;
    }
}
