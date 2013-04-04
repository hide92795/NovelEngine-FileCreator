package hide92795.novelengine.filecreator.saver;

import hide92795.novelengine.filecreator.FileExtensionFilter;
import hide92795.novelengine.filecreator.VarNumManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import javax.crypto.CipherOutputStream;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.yaml.snakeyaml.Yaml;

public class SaverBox extends Saver {
	public static final int NAME_LEFT = 0;
	public static final int NAME_CENTER = 1;
	private File path;

	public SaverBox(File output, Properties crypt, File path, String encoding) {
		super(output, crypt, encoding);
		this.path = path;
	}

	@Override
	public void pack() throws Exception {
		CipherOutputStream cos = createCipherInputStream(new File(output, "box.neo"), crypt);

		Yaml yaml = new Yaml();
		MessagePack msgpack = new MessagePack();
		Packer p = msgpack.createPacker(cos);

		File[] buttons = path.listFiles(new FileExtensionFilter("yml"));

		int defaultId = 0;

		p.write(buttons.length);

		for (File box : buttons) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(box), encoding));
			Map<?, ?> map = (Map<?, ?>) yaml.load(reader);

			int id = (Integer) map.get("ID");

			if (map.containsKey("Default")) {
				defaultId = id;
			}

			String imageId_s = (String) map.get("Image");
			int imageId = VarNumManager.IMAGE.add(imageId_s);

			// 位置データ読み込み
			Map<?, ?> positionMap = (Map<?, ?>) map.get("Position");
			Map<?, ?> positionShow = (Map<?, ?>) positionMap.get("Show");
			Map<?, ?> positionHide = (Map<?, ?>) positionMap.get("Hide");

			int showX = (Integer) positionShow.get("x");
			int showY = (Integer) positionShow.get("y");
			float showAlpha = ((Double) positionShow.get("alpha")).floatValue();

			int hideX = (Integer) positionHide.get("x");
			int hideY = (Integer) positionHide.get("y");
			float hideAlpha = ((Double) positionHide.get("alpha")).floatValue();

			// 名前表示エリアデータ読み込み
			Map<?, ?> name = (Map<?, ?>) map.get("NameArea");
			String nameType_s = name.get("type").toString().toLowerCase();
			int nameType = getNameType(nameType_s);
			int nameX = (Integer) name.get("x");
			int nameY = (Integer) name.get("y");

			// 表示エリアデータ読み込み
			Map<?, ?> area = (Map<?, ?>) map.get("WordsArea");
			Map<?, ?> areaLeftUp = (Map<?, ?>) area.get("LeftUp");
			Map<?, ?> areaRightDown = (Map<?, ?>) area.get("RightDown");

			int areaLightUpX = (Integer) areaLeftUp.get("x");
			int areaLightUpY = (Integer) areaLeftUp.get("y");

			int areaRightDownX = (Integer) areaRightDown.get("x");
			int areaRightDownY = (Integer) areaRightDown.get("y");


			// 移動データ読み込み
			Map<?, ?> movementMap = (Map<?, ?>) map.get("Movement");
			Map<?, ?> movementShow = (Map<?, ?>) movementMap.get("Show");
			Map<?, ?> movementHide = (Map<?, ?>) movementMap.get("Hide");

			String showMoveRatio = movementShow.get("ratio").toString();
			String showMoveX = movementShow.get("x").toString();
			String showMoveY = movementShow.get("y").toString();
			String showMoveAlpha = movementShow.get("alpha").toString();

			String hideMoveRatio = movementHide.get("ratio").toString();
			String hideMoveX = movementHide.get("x").toString();
			String hideMoveY = movementHide.get("y").toString();
			String hideMoveAlpha = movementHide.get("alpha").toString();

			// 書き込み
			// 位置データ
			p.write(id);
			p.write(imageId);

			p.write(showX);
			p.write(showY);
			p.write(showAlpha);

			p.write(hideX);
			p.write(hideY);
			p.write(hideAlpha);

			// 名前表示エリアデータ
			p.write(nameType);
			p.write(nameX);
			p.write(nameY);

			// 表示エリアデータ
			p.write(areaLightUpX);
			p.write(areaLightUpY);

			p.write(areaRightDownX);
			p.write(areaRightDownY);

			// 移動データ
			p.write(showMoveRatio);
			p.write(showMoveX);
			p.write(showMoveY);
			p.write(showMoveAlpha);

			p.write(hideMoveRatio);
			p.write(hideMoveX);
			p.write(hideMoveY);
			p.write(hideMoveAlpha);

			reader.close();
		}

		p.write(defaultId);

		p.flush();
		p.close();
	}

	private int getNameType(String nameType_s) {
		if (nameType_s.equals("left")) {
			return NAME_LEFT;
		} else {
			return NAME_CENTER;
		}
	}
}
