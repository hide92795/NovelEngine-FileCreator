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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.crypto.CipherOutputStream;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.yaml.snakeyaml.Yaml;

/**
 * キャラクターデータを保存するセーバーです。
 * 
 * @author hide92795
 */
public class SaverCharacter extends Saver {
	/**
	 * キャラクターデータを保存するセーバーを生成します。
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
	public SaverCharacter(File outputDir, File src, Properties crypt, String encoding) {
		super(outputDir, src, crypt, encoding);
	}

	@Override
	public void pack() throws Exception {
		// 位置データ
		CipherOutputStream cos = createCipherInputStream(new File(getOutputDir(), "character.neo"),
				getCryptProperties());

		MessagePack msgpack = new MessagePack();
		Packer p = msgpack.createPacker(cos);

		File position = new File(getSrc(), "position");
		position = new File(position, "position.yml");

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(position), getEncoding()));
		Yaml yaml = new Yaml();
		Map<?, ?> map = (Map<?, ?>) yaml.load(reader);
		Set<?> set = map.keySet();

		p.write(set.size());

		for (Object position_id_s : set) {
			Map<?, ?> pos = (Map<?, ?>) map.get(position_id_s);

			p.write(VarNumManager.CHARACTER_POSITION.add(position_id_s.toString()));
			p.write(pos.get("x"));
			p.write(pos.get("y"));
		}

		reader.close();

		// キャラクターデータ
		HashMap<String, Integer> characters = VarNumManager.CHARACTER.getMap();
		Set<String> characters_ks = characters.keySet();

		p.write(characters_ks.size());

		for (String character_s : characters_ks) {
			p.write(characters.get(character_s));

			File character = new File(getSrc(), character_s + ".yml");
			BufferedReader reader_c = new BufferedReader(new InputStreamReader(new FileInputStream(character),
					getEncoding()));
			Map<?, ?> map_c = (Map<?, ?>) yaml.load(reader_c);

			p.write(map_c.get("Name"));
			p.write(VarNumManager.FONT.add(map_c.get("Font").toString()));
			LinkedHashMap<?, ?> faces = (LinkedHashMap<?, ?>) map_c.get("Face");
			Set<?> faces_ks = faces.keySet();
			p.write(faces.size());
			for (Object face_name : faces_ks) {
				LinkedHashMap<?, ?> faceInfos = (LinkedHashMap<?, ?>) faces.get(face_name);
				int interval = (Integer) faceInfos.get("Interval");
				ArrayList<?> faceImages = (ArrayList<?>) faceInfos.get("Image");
				p.write(VarNumManager.FACE_TYPE.add(face_name.toString()));
				p.write(interval);
				p.write(faceImages.size());
				for (Object image : faceImages) {
					p.write(VarNumManager.IMAGE.add(image.toString()));
				}
			}
			reader_c.close();
		}
		p.flush();
		p.close();
	}
}
