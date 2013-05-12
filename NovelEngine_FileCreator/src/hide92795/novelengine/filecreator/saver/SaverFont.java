//
// NovelEngine Project
//
// Copyright (C) 2013 - hide92795
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
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

/**
 * フォントデータを保存するセーバーです。
 * 
 * @author hide92795
 */
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

	/**
	 * フォントデータを保存するセーバーを生成します。
	 * 
	 * @param outputDir
	 *            出力先のディレクトリ
	 * @param src
	 *            必要なファイルが保管されているディレクトリ、もしくはファイル
	 * @param crypt
	 *            暗号化に関する情報を保存するプロパティ
	 * @param encoding
	 *            読み込み時に使用する文字コード
	 */
	public SaverFont(File outputDir, File src, Properties crypt, String encoding) {
		super(outputDir, src, crypt, encoding);
	}

	@Override
	public void pack() throws Exception {
		CipherOutputStream cos = createCipherInputStream(new File(getOutputDir(), "font.neo"), getCryptProperties());

		MessagePack msgpack = new MessagePack();
		Packer p = msgpack.createPacker(cos);

		Yaml yaml = new Yaml();

		// フォント読み込み
		File file = new File(getSrc(), "font.yml");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), getEncoding()));
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
			File fontFile = new File(getSrc(), filename);

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

	/**
	 * フォントの縁の描画タイプを表すIDを文字列から判別し、返します。
	 * 
	 * @param decorationType_s
	 *            描画タイプを表す文字列
	 * @return 描画タイプを表すID
	 */
	private int getDecorationType(String decorationType_s) {
		switch (decorationType_s.toLowerCase()) {
		case "edged":
			return TEXT_EDGED;
		case "shadowed":
			return TEXT_SHADOWED;
		default:
			return TEXT_NONE;
		}
	}
}
