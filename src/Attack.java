import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Random;

public class Attack {

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//
//	}
	public static void main(String arg[]) throws Exception {
//		String input = "Try to attack me!";
		int bits = 22;
		
		Attack attack = new Attack();
//		byte[] hash = attack.getHash(input, 2);
////		System.out.println(hash.length);
//		for (byte b : hash) {
//			System.out.println(attack.byteToHex(b));
//		}
//		
//		System.out.println(Integer.toHexString(attack.maskBytes(hash, bits)));
		
		
		System.out.printf("Collision\t%d\n", bits);
		for (int i = 0; i < 50; i++) {
			attack.collisionAttack(bits);
		}
		
		System.out.printf("\nPre-image\t%d\n", bits);
		int numBytes = bits/4 + 1;
		if (bits%4 == 0) {
			numBytes--;
		}
		
		for (int i = 0; i < 50; i++) {
			byte[] hash = new byte[numBytes]; // length is bounded by 7
			new Random().nextBytes(hash);
			attack.preimageAttack(hash, bits);
		}
	}
	
	public void collisionAttack(int bits) throws Exception {
		int numBytes = bits/4 + 1;
		if (bits%4 == 0) {
			numBytes--;
		}
		
		int x1 = new Random().nextInt();
		int x2 = new Random().nextInt();
		byte[] hash1 = getHash(x1, numBytes);
		byte[] hash2 = getHash(x2, numBytes);
		int attempts = 1;
		
		while (maskBytes(hash1, bits) != maskBytes(hash2, bits)) {
			x1 = new Random().nextInt();
			x2 = new Random().nextInt();
			hash1 = getHash(x1, numBytes);
			hash2 = getHash(x2, numBytes);
			attempts++;
		}
		
		System.out.printf("\t%d\n", attempts);
	}
	
	public void preimageAttack(byte[] originalHash, int bits) throws Exception {
		int numBytes = bits/4 + 1;
		if (bits%4 == 0) {
			numBytes--;
		}
		
		int dummy = 0x00;
		byte[] dummyHash = getHash(dummy, numBytes);
		int attempts = 1;
		
		while (maskBytes(originalHash, bits) != maskBytes(dummyHash, bits)) {
			dummy++;
			dummyHash = getHash(dummy, numBytes);
			attempts++;
		}
		
		System.out.printf("\t%d\n", attempts);
	}
	
	private byte[] getHash(int x, int numBytes) throws Exception {
		byte[] bytes = encrypt(x);
		byte[] masked = new byte[numBytes];
		for (int i = bytes.length - numBytes; i < bytes.length; i++) {
			masked[i - (bytes.length - numBytes)] = bytes[i];
		}
		return masked;
	}
	
	private byte[] getHash(String x, int numBytes) throws Exception {
		byte[] bytes = encrypt(x);
		byte[] masked = new byte[numBytes];
		for (int i = bytes.length - numBytes; i < bytes.length; i++) {
			masked[i - (bytes.length - numBytes)] = bytes[i];
		}
		return masked;
	}
	
	private byte[] encrypt(int x) throws Exception {
		java.security.MessageDigest d = null;
		d = java.security.MessageDigest.getInstance("SHA-1");
		d.reset();
		byte[] bytes = ByteBuffer.allocate(4).putInt(x).array();
		d.update(bytes);
		return d.digest();
	}
	
	private byte[] encrypt(String x) throws Exception {
		java.security.MessageDigest d = null;
		d = java.security.MessageDigest.getInstance("SHA-1");
		d.reset();
		d.update(x.getBytes());
		return d.digest();
	}
	
	public int maskBytes(byte[] bytes, int bits) {
		int mask = 0x00;
		for (int i = 0; i < bits; i++) {
			mask <<= 1;
			mask++;
		}
		
		int result = bytes[0];
		for (int i = 1; i < bytes.length; i++) {
			result <<= 8;
			result += (int) bytes[i] & 0xFF;
		}
		
		return result & mask;
	}
	
	public String byteToHex(byte b) {
		int i = b & 0xFF;
		return Integer.toHexString(i);
	}
}
