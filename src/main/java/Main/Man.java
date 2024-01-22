package Main;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public class Man {

	// example usage
	public static void main(String... args) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, SignatureException {
		ProvablyFair fair = new ProvablyFair();
		fair.setRandomString("test123");
		String seed = fair.getServerSeed();
		String hashServerSeed = fair.getHashedSeed(seed);
		fair.setClientSeed("test12ggg3");
		fair.setNonce(552);
		
		System.out.println("Server seed: " + seed);
		System.out.println("Hashed server seed: " + hashServerSeed);
		System.out.println("the roll is " + fair.getRoll());
	}

}
