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

/**
 * GUIデータを保存するセーバーです。
 * 
 * @author hide92795
 */
public class SaverGui extends Saver {

	/**
	 * GUIデータを保存するセーバーを生成します。
	 * 
	 * @param outputDir
	 *            出力先のディレクトリ
	 * @param crypt
	 *            暗号化に関する情報を保存するプロパティ
	 * @param encoding
	 *            読み込み時に使用する文字コード
	 */
	public SaverGui(File outputDir, Properties crypt, String encoding) {
		super(outputDir, null, crypt, encoding);
	}

	@Override
	public void pack() throws Exception {
		CipherOutputStream cos = createCipherInputStream(new File(getOutputDir(), "gui.neo"), getCryptProperties());

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
