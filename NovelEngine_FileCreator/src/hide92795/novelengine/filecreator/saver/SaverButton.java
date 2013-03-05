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

public class SaverButton extends Saver {
	private File path;

	public SaverButton(File output, Properties crypt, File path) {
		super(output, crypt);
		this.path = path;
	}

	public void pack() throws Exception {
		CipherOutputStream cos = createCipherInputStream(new File(output, "button.neo"), crypt);

		MessagePack msgpack = new MessagePack();
		Packer p = msgpack.createPacker(cos);

		// ボタンデータ

		File[] buttons = path.listFiles(new FileExtensionFilter("txt"));

		p.write(buttons.length);

		for (File button : buttons) {
			FileInputStream fis = new FileInputStream(button);
			Properties prop = new Properties();
			prop.load(fis);
			String imageNormal = prop.getProperty("ImageNormal");
			String imageOnMouse = prop.getProperty("ImageOnMouse");
			String imageDisabled = prop.getProperty("ImageDisabled");
			String imageClicked = prop.getProperty("ImageClicked");
			String clickable_s = prop.getProperty("Clickable");
			File clickable = new File(path, clickable_s);
			String textXPos_s = prop.getProperty("TextXPosition");
			String textYPos_s = prop.getProperty("TextYPosition");
			String width_s = prop.getProperty("Width");
			String height_s = prop.getProperty("Height");

			int imageNormalID = VarNumManager.IMAGE.add(imageNormal);
			int imageOnMouseID = VarNumManager.IMAGE.add(imageOnMouse);
			int imageDisabledID = VarNumManager.IMAGE.add(imageDisabled);
			int imageClickedID = VarNumManager.IMAGE.add(imageClicked);
			int cliclableID = VarNumManager.CLICKABLE.add(clickable.getCanonicalPath());
			int textXPos = Integer.valueOf(textXPos_s);
			int textYPos = Integer.valueOf(textYPos_s);
			int width = Integer.valueOf(width_s);
			int height = Integer.valueOf(height_s);

			fis.close();

			p.write(VarNumManager.BUTTON.add(removeFileExtension(button.getName())));
			p.write(imageNormalID);
			p.write(imageOnMouseID);
			p.write(imageDisabledID);
			p.write(imageClickedID);
			p.write(cliclableID);
			p.write(textXPos);
			p.write(textYPos);
			p.write(width);
			p.write(height);
		}

		// 位置データ

		File position_path = new File(path, "position");

		File[] positions = position_path.listFiles(new FileExtensionFilter("yml"));

		p.write(positions.length);

		for (File position : positions) {
			FileInputStream fis = new FileInputStream(position);
			Yaml yaml = new Yaml();
			Map<?, ?> map = (Map<?, ?>) yaml.load(fis);

			int num = map.size();
			p.write(VarNumManager.BUTTON_POSITION.add(removeFileExtension(position.getName())));
			p.write(num);

			for (int i = 0; i < num; i++) {
				int order = i + 1;
				Map<?, ?> pos = (Map<?, ?>) map.get(order);

				p.write(pos.get("x"));
				p.write(pos.get("y"));
			}

			fis.close();
		}

		p.flush();
		p.close();
	}
}