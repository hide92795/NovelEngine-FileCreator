package hide92795.novelengine.filecreator.saver;

import hide92795.novelengine.filecreator.VarNumManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.crypto.CipherOutputStream;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.yaml.snakeyaml.Yaml;

public class SaverFont extends Saver {
	private File path;

	public SaverFont(File output, Properties crypt, File path) {
		super(output, crypt);
		this.path = path;
	}

	@Override
	public void pack() throws Exception {
		CipherOutputStream cos = createCipherInputStream(new File(output, "font.neo"), crypt);

		MessagePack msgpack = new MessagePack();
		Packer p = msgpack.createPacker(cos);

		Yaml yaml = new Yaml();

		// フォント読み込み
		FileInputStream fis = new FileInputStream(new File(path, "font.yml"));
		Map<?, ?> map = (Map<?, ?>) yaml.load(fis);

		Set<?> key = map.keySet();

		p.write(key.size());

		int defaultId = 0;

		for (Object object : key) {
			String name = object.toString();

			Map<?, ?> fontData = (Map<?, ?>) map.get(object);

			int id;

			if (fontData.containsKey("ID")) {
				id = (Integer) fontData.get("ID");
				VarNumManager.FONT.getMap().put(name, id);
			} else {
				id = VarNumManager.FONT.add(name);
			}

			if ((Boolean) fontData.containsKey("Default")) {
				defaultId = id;
			}

			String filename = (String) fontData.get("File");
			int size = (Integer) fontData.get("Size");

			int fontId = VarNumManager.FONT_NAME.add(filename);

			p.write(id);
			p.write(fontId);
			p.write(size);
		}

		p.write(defaultId);

		p.flush();

		// フォントデータ読み込み
		ZipOutputStream zos = new ZipOutputStream(cos);
		zos.setMethod(ZipOutputStream.STORED);

		HashMap<String, Integer> fontsMap = VarNumManager.FONT_NAME.getMap();
		Set<String> fonts = fontsMap.keySet();

		for (String filename : fonts) {
			File file = new File(path, filename);

			FileInputStream fis1 = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			CRC32 crc = new CRC32();

			int c;
			while ((c = fis1.read()) != -1) {
				bos.write(c);
				crc.update(c);
			}

			ZipEntry ze = new ZipEntry(fontsMap.get(filename).toString());
			ze.setSize(file.length());
			ze.setCrc(crc.getValue());

			zos.putNextEntry(ze);
			zos.write(bos.toByteArray());

			fis1.close();
		}

		zos.flush();
		cos.close();
		fis.close();
	}
}
