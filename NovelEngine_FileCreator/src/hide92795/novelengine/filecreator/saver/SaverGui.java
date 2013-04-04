package hide92795.novelengine.filecreator.saver;

import hide92795.novelengine.filecreator.VarNumManager;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import javax.crypto.CipherOutputStream;
import javax.imageio.ImageIO;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

public class SaverGui extends Saver {

	public SaverGui(File output, Properties crypt, String encoding) {
		super(output, crypt, encoding);
	}

	@Override
	public void pack() throws Exception {
		CipherOutputStream cos = createCipherInputStream(new File(output, "gui.neo"), crypt);

		MessagePack msgpack = new MessagePack();
		Packer p = msgpack.createPacker(cos);


		HashMap<String, Integer> clickables = VarNumManager.CLICKABLE.getMap();
		Set<String> clickableName = clickables.keySet();

		p.write(clickableName.size());

		for (String name : clickableName) {
			int id = clickables.get(name);
			File clickable = new File(name);

			System.out.println(clickable.getName());

			FileInputStream fis = new FileInputStream(clickable);
			BufferedImage image = ImageIO.read(fis);
			fis.close();

			p.write(id);

			int width = image.getWidth();
			int height = image.getHeight();
			int size = width * height;

			p.write(size);

			for (int i = 0; i < size; i++) {
				int x = i % width;
				int y = i / width;
				int color = image.getRGB(x, y);
				if (color == Color.black.getRGB()) {
					p.write(true);
				} else {
					p.write(false);
				}
			}
		}

		p.flush();
		p.close();
	}
}
