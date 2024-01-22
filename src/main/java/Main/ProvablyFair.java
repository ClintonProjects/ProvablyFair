package Main;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProvablyFair {

	private final String SHA = "SHA-256";
	private final String HMAC = "HmacSHA512";

	private String randomString = "";
	private int nonce; // roll number
	public String clientSeed;

	public void resetSeed() {
		final int N = 10000;
		final String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

		StringBuilder sb = new StringBuilder(N);
		for (int i = 0; i < N; i++) {
			sb.append(AB.charAt(new SecureRandom().nextInt(AB.length())));
		}
		randomString = sb.toString();
	}

	public String getServerSeed() throws NoSuchAlgorithmException {
		byte[] input = (randomString).getBytes();
		MessageDigest SMA256 = MessageDigest.getInstance(SHA);
		SMA256.update(input);
		byte[] digest = SMA256.digest();
		StringBuffer hexDigest = new StringBuffer();
		for (int i = 0; i < digest.length; i++)
			hexDigest.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
		return String.valueOf(hexDigest);
	}

	public String getHashedSeed(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance(SHA);
		md.reset();
		byte[] buffer = input.getBytes("UTF-8");
		md.update(buffer);
		byte[] digest = md.digest();

		String hexStr = "";
		for (int i = 0; i < digest.length; i++) {
			hexStr += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
		}
		return hexStr;
	}

	public double getRoll() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
		String ClientSeed = clientSeed + "-" + nonce;
		String f = calculateHMAC(ClientSeed, getServerSeed());
		String upToNCharacters = f.substring(0, 5);
		double foo = Integer.parseInt(String.valueOf(upToNCharacters), 16);
		if (foo > 999999.0) {
			upToNCharacters = f.substring(5, 10);
			foo = Integer.parseInt(String.valueOf(upToNCharacters), 16);
		}
		return foo % (10000) / 100;
	}

	private String toHexString(byte[] bytes) {
		Formatter formatter = new Formatter();
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}
		String s = formatter.toString();
		formatter.close();
		return s;
	}

	private String calculateHMAC(String data, String key)
			throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), HMAC);
		Mac mac = Mac.getInstance(HMAC);
		mac.init(secretKeySpec);
		return toHexString(mac.doFinal(data.getBytes()));
	}
}
