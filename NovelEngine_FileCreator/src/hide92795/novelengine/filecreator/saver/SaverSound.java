package hide92795.novelengine.filecreator.saver;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javax.crypto.CipherOutputStream;

public class SaverSound extends Saver {
	private final File src;
	private final int id;

	public SaverSound(File output, Properties crypt, File src, int id, String encoding) {
		super(output, crypt, encoding);
		this.src = src;
		this.id = id;
	}

	@Override
	public void pack() throws Exception {
		System.out.println(src.getName());
		FileInputStream fis = new FileInputStream(src);

		CipherOutputStream cos = createCipherInputStream(new File(output, id + ".nea"), crypt);

		// System.out.print("IV: ");
		// byte[] prme = cipher.getIV();
		// for (int i = 0; i < prme.length; i++) {
		// System.out.print(Integer.toHexString(prme[i] & 0xff) + " ");
		// }
		// System.out.println();

		byte[] a = new byte[8];
		int i = fis.read(a);

		while (i != -1) {
			cos.write(a, 0, i);
			i = fis.read(a);
		}

		cos.flush();
		cos.close();
		fis.close();
	}

}
