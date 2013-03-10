package hide92795.novelengine.filecreator.saver;

import hide92795.novelengine.filecreator.FileExtensionFilter;
import hide92795.novelengine.filecreator.VarNumManager;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;
import javax.crypto.CipherOutputStream;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.yaml.snakeyaml.Yaml;

public class SaverBox extends Saver {
	private File path;

	public SaverBox(File output, Properties crypt, File path) {
		super(output, crypt);
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
			FileInputStream fis = new FileInputStream(box);
			Map<?, ?> map = (Map<?, ?>) yaml.load(fis);

			int id = (Integer) map.get("ID");

			if ((Boolean) map.get("Default")) {
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

			// 移動データ読み込み
			Map<?, ?> movementMap = (Map<?, ?>) map.get("Movement");
			Map<?, ?> movementShow = (Map<?, ?>) movementMap.get("Show");
			Map<?, ?> movementHide = (Map<?, ?>) movementMap.get("Hide");

			String showMoveRatio =  movementShow.get("ratio").toString();
			String showMoveX =  movementShow.get("x").toString();
			String showMoveY =  movementShow.get("y").toString();
			String showMoveAlpha =  movementShow.get("alpha").toString();

			String hideMoveRatio =  movementHide.get("ratio").toString();
			String hideMoveX =  movementHide.get("x").toString();
			String hideMoveY =  movementHide.get("y").toString();
			String hideMoveAlpha =  movementHide.get("alpha").toString();

			// 書き込み
			p.write(id);
			p.write(imageId);

			p.write(showX);
			p.write(showY);
			p.write(showAlpha);

			p.write(hideX);
			p.write(hideY);
			p.write(hideAlpha);

			p.write(showMoveRatio);
			p.write(showMoveX);
			p.write(showMoveY);
			p.write(showMoveAlpha);

			p.write(hideMoveRatio);
			p.write(hideMoveX);
			p.write(hideMoveY);
			p.write(hideMoveAlpha);

			fis.close();
		}

		p.write(defaultId);

		p.flush();
		p.close();
	}
}
