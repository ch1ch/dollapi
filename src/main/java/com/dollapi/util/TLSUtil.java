package com.dollapi.util;

import java.security.PublicKey;
import java.security.Security;

import com.dollapi.exception.DollException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;

import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.util.Arrays;

import java.security.Signature;
import java.security.PrivateKey;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


/**
 * <p>Copyright: All Rights Reserved</p>
 * <p>Company: 指点无限(北京)科技有限公司   http://www.zhidianwuxian.cn</p>
 * <p>Description:  </p>
 * <p>Author:hexu/方和煦, 2017/9/25</p>
 */
public class TLSUtil {

    public static class GenTLSSignatureResult {
        public String errMessage;
        public String urlSig;
        public int expireTime;
        public int initTime;

        public GenTLSSignatureResult() {
            errMessage = "";
            urlSig = "";
        }
    }

    public static class CheckTLSSignatureResult {
        public String errMessage;
        public boolean verifyResult;
        public int expireTime;
        public int initTime;

        public CheckTLSSignatureResult() {
            errMessage = "";
            verifyResult = false;
        }
    }

    public static GenTLSSignatureResult GenTLSSignatureEx(
            long skdAppid,
            String identifier,
            String privStr) throws IOException {
        return GenTLSSignatureEx(skdAppid, identifier, privStr, 3600 * 24 * 180);
    }

    public static GenTLSSignatureResult GenTLSSignatureEx(
            long skdAppid,
            String identifier,
            String privStr,
            long expire) throws IOException {

        GenTLSSignatureResult result = new GenTLSSignatureResult();

        Security.addProvider(new BouncyCastleProvider());
        Reader reader = new CharArrayReader(privStr.toCharArray());
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PEMParser parser = new PEMParser(reader);
        Object obj = parser.readObject();
        parser.close();
        PrivateKey privKeyStruct = converter.getPrivateKey((PrivateKeyInfo) obj);

        String jsonString = "{"
                + "\"TLS.account_type\":\"" + 0 + "\","
                + "\"TLS.identifier\":\"" + identifier + "\","
                + "\"TLS.appid_at_3rd\":\"" + 0 + "\","
                + "\"TLS.sdk_appid\":\"" + skdAppid + "\","
                + "\"TLS.expire_after\":\"" + expire + "\","
                + "\"TLS.version\": \"201512300000\""
                + "}";

        String time = String.valueOf(System.currentTimeMillis() / 1000);
        String SerialString =
                "TLS.appid_at_3rd:" + 0 + "\n" +
                        "TLS.account_type:" + 0 + "\n" +
                        "TLS.identifier:" + identifier + "\n" +
                        "TLS.sdk_appid:" + skdAppid + "\n" +
                        "TLS.time:" + time + "\n" +
                        "TLS.expire_after:" + expire + "\n";

        try {
            //Create Signature by SerialString
            Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
            signature.initSign(privKeyStruct);
            signature.update(SerialString.getBytes(Charset.forName("UTF-8")));
            byte[] signatureBytes = signature.sign();

            String sigTLS = Base64.encodeBase64String(signatureBytes);

            //Add TlsSig to jsonString
            JSONObject jsonObject = new JSONObject(jsonString);
            jsonObject.put("TLS.sig", (Object) sigTLS);
            jsonObject.put("TLS.time", (Object) time);
            jsonString = jsonObject.toString();

            //compression
            Deflater compresser = new Deflater();
            compresser.setInput(jsonString.getBytes(Charset.forName("UTF-8")));

            compresser.finish();
            byte[] compressBytes = new byte[512];
            int compressBytesLength = compresser.deflate(compressBytes);
            compresser.end();
            String userSig = new String(base64_url.base64EncodeUrl(Arrays.copyOfRange(compressBytes, 0, compressBytesLength)));

            result.urlSig = userSig;
        } catch (Exception e) {
            e.printStackTrace();
            result.errMessage = "generate usersig failed";
        }

        return result;
    }

    @Deprecated
    public static CheckTLSSignatureResult CheckTLSSignature(String urlSig,
                                                            String strAppid3rd, long skdAppid,
                                                            String identifier, long accountType,
                                                            String publicKey) throws DataFormatException {
        CheckTLSSignatureResult result = new CheckTLSSignatureResult();
        Security.addProvider(new BouncyCastleProvider());

        //DeBaseUrl64 urlSig to json
        Base64 decoder = new Base64();

        //byte [] compressBytes = decoder.decode(urlSig.getBytes());
        byte[] compressBytes = base64_url.base64DecodeUrl(urlSig.getBytes(Charset.forName("UTF-8")));

        //System.out.println("#compressBytes Passing in[" + compressBytes.length + "] " + Hex.encodeHexString(compressBytes));

        //Decompression
        Inflater decompression = new Inflater();
        decompression.setInput(compressBytes, 0, compressBytes.length);
        byte[] decompressBytes = new byte[1024];
        int decompressLength = decompression.inflate(decompressBytes);
        decompression.end();

        String jsonString = new String(Arrays.copyOfRange(decompressBytes, 0, decompressLength));

        //System.out.println("#Json String passing in : \n" + jsonString);

        //Get TLS.Sig from json
        JSONObject jsonObject = new JSONObject(jsonString);
        String sigTLS = jsonObject.getString("TLS.sig");

        //debase64 TLS.Sig to get serailString
        byte[] signatureBytes = decoder.decode(sigTLS.getBytes(Charset.forName("UTF-8")));

        try {

            String sigTime = jsonObject.getString("TLS.time");
            String sigExpire = jsonObject.getString("TLS.expire_after");

            //checkTime
            //System.out.println("#time check: "+ System.currentTimeMillis()/1000 + "-"
            //+ Long.parseLong(sigTime) + "-" + Long.parseLong(sigExpire));
            if (System.currentTimeMillis() / 1000 - Long.parseLong(sigTime) > Long.parseLong(sigExpire)) {
                result.errMessage = new String("TLS sig is out of date ");
                System.out.println("Timeout");
                return result;
            }

            //Get Serial String from json
            String SerialString =
                    "TLS.appid_at_3rd:" + strAppid3rd + "\n" +
                            "TLS.account_type:" + accountType + "\n" +
                            "TLS.identifier:" + identifier + "\n" +
                            "TLS.sdk_appid:" + skdAppid + "\n" +
                            "TLS.time:" + sigTime + "\n" +
                            "TLS.expire_after:" + sigExpire + "\n";

            //System.out.println("#SerialString : \n" + SerialString);

            Reader reader = new CharArrayReader(publicKey.toCharArray());
            PEMParser parser = new PEMParser(reader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            Object obj = parser.readObject();
            parser.close();
            PublicKey pubKeyStruct = converter.getPublicKey((SubjectPublicKeyInfo) obj);

            Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
            signature.initVerify(pubKeyStruct);
            signature.update(SerialString.getBytes(Charset.forName("UTF-8")));
            boolean bool = signature.verify(signatureBytes);
            //System.out.println("#jdk ecdsa verify : " + bool);
            result.verifyResult = bool;
        } catch (Exception e) {
            e.printStackTrace();
            result.errMessage = "Failed in checking sig";
        }

        return result;
    }


    public static CheckTLSSignatureResult CheckTLSSignatureEx(
            String urlSig,
            long sdkAppid,
            String identifier,
            String publicKey) throws DataFormatException {

        CheckTLSSignatureResult result = new CheckTLSSignatureResult();
        Security.addProvider(new BouncyCastleProvider());

        //DeBaseUrl64 urlSig to json
        Base64 decoder = new Base64();

        byte[] compressBytes = base64_url.base64DecodeUrl(urlSig.getBytes(Charset.forName("UTF-8")));

        //Decompression
        Inflater decompression = new Inflater();
        decompression.setInput(compressBytes, 0, compressBytes.length);
        byte[] decompressBytes = new byte[1024];
        int decompressLength = decompression.inflate(decompressBytes);
        decompression.end();

        String jsonString = new String(Arrays.copyOfRange(decompressBytes, 0, decompressLength));

        //Get TLS.Sig from json
        JSONObject jsonObject = new JSONObject(jsonString);
        String sigTLS = jsonObject.getString("TLS.sig");

        //debase64 TLS.Sig to get serailString
        byte[] signatureBytes = decoder.decode(sigTLS.getBytes(Charset.forName("UTF-8")));

        try {
            String strSdkAppid = jsonObject.getString("TLS.sdk_appid");
            String sigTime = jsonObject.getString("TLS.time");
            String sigExpire = jsonObject.getString("TLS.expire_after");

            if (Integer.parseInt(strSdkAppid) != sdkAppid) {
                result.errMessage = new String("sdkappid "
                        + strSdkAppid
                        + " in tls sig not equal sdkappid "
                        + sdkAppid
                        + " in request");
                return result;
            }

            if (System.currentTimeMillis() / 1000 - Long.parseLong(sigTime) > Long.parseLong(sigExpire)) {
                result.errMessage = new String("TLS sig is out of date");
                return result;
            }

            //Get Serial String from json
            String SerialString =
                    "TLS.appid_at_3rd:" + 0 + "\n" +
                            "TLS.account_type:" + 0 + "\n" +
                            "TLS.identifier:" + identifier + "\n" +
                            "TLS.sdk_appid:" + sdkAppid + "\n" +
                            "TLS.time:" + sigTime + "\n" +
                            "TLS.expire_after:" + sigExpire + "\n";

            Reader reader = new CharArrayReader(publicKey.toCharArray());
            PEMParser parser = new PEMParser(reader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            Object obj = parser.readObject();
            parser.close();
            PublicKey pubKeyStruct = converter.getPublicKey((SubjectPublicKeyInfo) obj);

            Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
            signature.initVerify(pubKeyStruct);
            signature.update(SerialString.getBytes(Charset.forName("UTF-8")));
            boolean bool = signature.verify(signatureBytes);
            result.expireTime = Integer.parseInt(sigExpire);
            result.initTime = Integer.parseInt(sigTime);
            result.verifyResult = bool;
        } catch (Exception e) {
            e.printStackTrace();
            result.errMessage = "Failed in checking sig";
        }

        return result;
    }


    public static String getSig(String identifier, String privStr, String pubStr, Long sdkAppId) {

        privStr = "-----BEGIN PRIVATE KEY-----\n" +
                "MIGEAgEAMBAGByqGSM49AgEGBSuBBAAKBG0wawIBAQQgUXvcxF68hZW6HEUYGgmE\n" +
                "h/LXRCRAtWwrIijiPus34BOhRANCAATiRpBvJzUsnq7dyhTwfUr8qtBNj72Y0iN9\n" +
                "HZTZY+3j7VOsc/ANKKw2YseqGddvHcjpFAu+TD3S2UM5uz+NrnV9\n" +
                "-----END PRIVATE KEY-----\n";

        //change public pem string to public string
        pubStr = "-----BEGIN PUBLIC KEY-----\n" +
                "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAE4kaQbyc1LJ6u3coU8H1K/KrQTY+9mNIj\n" +
                "fR2U2WPt4+1TrHPwDSisNmLHqhnXbx3I6RQLvkw90tlDObs/ja51fQ==\n" +
                "-----END PUBLIC KEY-----\n";

        try {
            GenTLSSignatureResult result = GenTLSSignatureEx(sdkAppId, identifier, privStr);
            if (0 == result.urlSig.length()) {
                System.out.println("GenTLSSignatureEx failed: " + result.errMessage);
                throw new DollException(ApiContents.LTS_ERROR.value(), ApiContents.LTS_ERROR.desc() + result.errMessage);
            }

            CheckTLSSignatureResult checkResult = CheckTLSSignatureEx(result.urlSig, sdkAppId, identifier, pubStr);
            if (checkResult.verifyResult == false) {
                System.out.println("CheckTLSSignature failed: " + result.errMessage);
                throw new DollException(ApiContents.LTS_ERROR.value(), ApiContents.LTS_ERROR.desc() + result.errMessage);
            }
            System.out.println("---\ngenerate sig:\n" + result.urlSig + "\n---\n");
            return result.urlSig;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DollException(ApiContents.LTS_ERROR.value(), ApiContents.LTS_ERROR.desc());
        }
    }


    public static void main(String[] args) {
        try {
            //Use pemfile keys to test
//            String privStr = "-----BEGIN PRIVATE KEY-----\n" +
//                    "MIGEAgEAMBAGByqGSM49AgEGBSuBBAAKBG0wawIBAQQgiBPYMVTjspLfqoq46oZd\n" +
//                    "j9A0C8p7aK3Fi6/4zLugCkehRANCAATU49QhsAEVfIVJUmB6SpUC6BPaku1g/dzn\n" +
//                    "0Nl7iIY7W7g2FoANWnoF51eEUb6lcZ3gzfgg8VFGTpJriwHQWf5T\n" +
//                    "-----END PRIVATE KEY-----";
//
//            //change public pem string to public string
//            String pubStr = "-----BEGIN PUBLIC KEY-----\n"+
//                    "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAE1OPUIbABFXyFSVJgekqVAugT2pLtYP3c\n"+
//                    "59DZe4iGO1u4NhaADVp6BedXhFG+pXGd4M34IPFRRk6Sa4sB0Fn+Uw==\n"+
//                    "-----END PUBLIC KEY-----";

            String privStr = "-----BEGIN PRIVATE KEY-----\n" +
                    "MIGEAgEAMBAGByqGSM49AgEGBSuBBAAKBG0wawIBAQQgUXvcxF68hZW6HEUYGgmE\n" +
                    "h/LXRCRAtWwrIijiPus34BOhRANCAATiRpBvJzUsnq7dyhTwfUr8qtBNj72Y0iN9\n" +
                    "HZTZY+3j7VOsc/ANKKw2YseqGddvHcjpFAu+TD3S2UM5uz+NrnV9\n" +
                    "-----END PRIVATE KEY-----\n";

            //change public pem string to public string
            String pubStr = "-----BEGIN PUBLIC KEY-----\n" +
                    "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAE4kaQbyc1LJ6u3coU8H1K/KrQTY+9mNIj\n" +
                    "fR2U2WPt4+1TrHPwDSisNmLHqhnXbx3I6RQLvkw90tlDObs/ja51fQ==\n" +
                    "-----END PUBLIC KEY-----\n";

            // generate signature
//            GenTLSSignatureResult result = GenTLSSignatureEx(1400000955, "xiaojun", privStr);
            GenTLSSignatureResult result = GenTLSSignatureEx(1400043428, "admin", privStr);
            if (0 == result.urlSig.length()) {
                System.out.println("GenTLSSignatureEx failed: " + result.errMessage);
                return;
            }

            System.out.println("---\ngenerate sig:\n" + result.urlSig + "\n---\n");

            // check signature
            CheckTLSSignatureResult checkResult = CheckTLSSignatureEx(result.urlSig, 1400043428, "admin", pubStr);
            if (checkResult.verifyResult == false) {
                System.out.println("CheckTLSSignature failed: " + result.errMessage);
                return;
            }

            System.out.println("\n---\ncheck sig ok -- expire time " + checkResult.expireTime + " -- init time " + checkResult.initTime + "\n---\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
