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

/**
 * ボタンデータを保存するセーバーです。
 * 
 * @author hide92795
 */
public class SaverButton extends Saver {

	/**
	 * ボタンデータを保存するセーバーを生成します。
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
	public SaverButton(File outputDir, File src, Properties crypt, String encoding) {
		super(outputDir, src, crypt, encoding);
	}

	@Override
	public void pack() throws Exception {
		CipherOutputStream cos = createCipherInputStream(new File(getOutputDir(), "button.neo"), getCryptProperties());

		MessagePack msgpack = new MessagePack();
		Packer p = msgpack.createPacker(cos);

		// ボタンデータ

		File[] buttons = getSrc().listFiles(new FileExtensionFilter("txt"));

		p.write(buttons.length);

		for (File button : buttons) {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(button), getEncoding()));
			Properties prop = new Properties();
			prop.load(reader);
			String imageNormal = prop.getProperty("ImageNormal");
			String imageOnMouse = prop.getProperty("ImageOnMouse");
			String imageDisabled = prop.getProperty("ImageDisabled");
			String imageClicked = prop.getProperty("ImageClicked");
			String clickable_s = prop.getProperty("Clickable");
			File clickable = new File(getSrc(), clickable_s);
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

			reader.close();

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

		File position_path = new File(getSrc(), "position");

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