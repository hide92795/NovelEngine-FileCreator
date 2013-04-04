package hide92795.novelengine.filecreator.saver;

import hide92795.novelengine.filecreator.VarNumManager;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
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
	/**
	 * 装飾なしの文字を表します。
	 */
	public static final int TEXT_NONE = 0;
	/**
	 * 影付き文字を表します。
	 */
	public static final int TEXT_SHADOWED = 1;
	/**
	 * 縁取り文字を表します。
	 */
	public static final int TEXT_EDGED = 2;
	private File path;

	public SaverFont(File output, Properties crypt, File path, String encoding) {
		super(output, crypt, encoding);
		this.path = path;
	}

	@Override
	public void pack() throws Exception {
		CipherOutputStream cos = createCipherInputStream(new File(output, "font.neo"), crypt);

		MessagePack msgpack = new MessagePack();
		Packer p = msgpack.createPacker(cos);

		Yaml yaml = new Yaml();

		// フォント読み込み
		File file = new File(path, "font.yml");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
		Map<?, ?> map = (Map<?, ?>) yaml.load(reader);

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

			// 装飾データ
			Map<?, ?> decoration = (Map<?, ?>) fontData.get("Decoration");
			Map<?, ?> color = (Map<?, ?>) decoration.get("Color");
			String decorationType_s = ((String) decoration.get("Type")).toLowerCase(Locale.ENGLISH);

			int decorationType = getDecorationType(decorationType_s);
			int fontId = VarNumManager.FONT_NAME.add(filename);

			p.write(id);
			p.write(fontId);
			p.write(size);
			p.write(decorationType);
			switch (decorationType) {
			case TEXT_NONE:
				Color c = Color.decode(color.get("Inner").toString());
				p.write(c.getRGB());
				break;
			case TEXT_SHADOWED:
			case TEXT_EDGED:
				Color inner = Color.decode(color.get("Inner").toString());
				Color edge = Color.decode(color.get("Edge").toString());
				p.write(inner.getRGB());
				p.write(edge.getRGB());
				break;
			default:
				break;
			}
		}

		p.write(defaultId);

		p.flush();

		// フォントデータ読み込み
		ZipOutputStream zos = new ZipOutputStream(cos);
		zos.setMethod(ZipOutputStream.STORED);

		HashMap<String, Integer> fontsMap = VarNumManager.FONT_NAME.getMap();
		Set<String> fonts = fontsMap.keySet();

		for (String filename : fonts) {
			File fontFile = new File(path, filename);

			FileInputStream fis1 = new FileInputStream(fontFile);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			CRC32 crc = new CRC32();

			int c;
			while ((c = fis1.read()) != -1) {
				bos.write(c);
				crc.update(c);
			}

			ZipEntry ze = new ZipEntry(fontsMap.get(filename).toString());
			ze.setSize(fontFile.length());
			ze.setCrc(crc.getValue());

			zos.putNextEntry(ze);
			zos.write(bos.toByteArray());

			fis1.close();
		}

		zos.flush();
		cos.close();
		reader.close();
	}

	private int getDecorationType(String decorationType_s) {
		if (decorationType_s.equals("edged")) {
			return TEXT_EDGED;
		} else if (decorationType_s.equals("shadowed")) {
			return TEXT_SHADOWED;
		} else {
			return TEXT_NONE;
		}
	}
}
