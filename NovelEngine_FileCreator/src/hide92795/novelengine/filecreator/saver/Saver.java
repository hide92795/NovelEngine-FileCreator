package hide92795.novelengine.filecreator.saver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public abstract class Saver {
	public final File output;
	public final Properties crypt;

	public Saver(File output, Properties crypt) {
		this.output = output;
		this.crypt = crypt;
	}

	public abstract void pack() throws Exception;

	public static String removeFileExtension(String filename) {
		int lastDotPos = filename.lastIndexOf('.');

		if (lastDotPos == -1) {
			return filename;
		} else if (lastDotPos == 0) {
			return filename;
		} else {
			return filename.substring(0, lastDotPos);
		}
	}

	public static CipherOutputStream createCipherInputStream(File file, Properties crypt) throws IOException,
			NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		SecretKeySpec key = new SecretKeySpec(crypt.getProperty("key").getBytes(), "AES");
		FileOutputStream fos = new FileOutputStream(file);

		Cipher cipher = Cipher.getInstance("AES/PCBC/PKCS5Padding");

		String iv = crypt.getProperty("iv");
		if (iv == null || iv.length() == 0) {
			IvParameterSpec ivspec = new IvParameterSpec(crypt.getProperty("iv").getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, key, ivspec);
		} else {
			cipher.init(Cipher.ENCRYPT_MODE, key);
		}


		CipherOutputStream cos = new CipherOutputStream(fos, cipher);

		fos.write(cipher.getIV());

		return cos;
	}
}
