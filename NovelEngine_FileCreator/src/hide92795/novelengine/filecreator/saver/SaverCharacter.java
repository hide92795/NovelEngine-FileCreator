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

public class SaverCharacter extends Saver {

	private File path;

	public SaverCharacter(File output, Properties crypt, File path, String encoding) {
		super(output, crypt, encoding);
		this.path = path;
	}

	@Override
	public void pack() throws Exception {
		// 位置データ
		CipherOutputStream cos = createCipherInputStream(new File(output, "character.neo"), crypt);

		MessagePack msgpack = new MessagePack();
		Packer p = msgpack.createPacker(cos);

		File position = new File(path, "position");
		position = new File(position, "position.yml");

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(position), encoding));
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

			File character = new File(path, character_s + ".yml");
			BufferedReader reader_c = new BufferedReader(
					new InputStreamReader(new FileInputStream(character), encoding));
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
