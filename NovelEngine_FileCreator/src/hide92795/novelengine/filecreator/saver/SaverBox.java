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
 * メッセージボックスに関するデータを保存するセーバーです。
 * 
 * @author hide92795
 */
public class SaverBox extends Saver {
	/**
	 * 名前表示を左寄りにすることを表します。
	 */
	public static final int NAME_LEFT = 0;
	/**
	 * 名前表示を中央寄りにすることを表します。
	 */
	public static final int NAME_CENTER = 1;

	/**
	 * メッセージボックスに関するデータを保存するセーバーを生成します。
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
	public SaverBox(File outputDir, File src, Properties crypt, String encoding) {
		super(outputDir, src, crypt, encoding);
	}

	@Override
	public void pack() throws Exception {
		CipherOutputStream cos = createCipherInputStream(new File(getOutputDir(), "box.neo"), getCryptProperties());

		Yaml yaml = new Yaml();
		MessagePack msgpack = new MessagePack();
		Packer p = msgpack.createPacker(cos);

		File[] buttons = getSrc().listFiles(new FileExtensionFilter("yml"));

		int defaultId = 0;

		p.write(buttons.length);

		for (File box : buttons) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(box), getEncoding()));
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

	/**
	 * 文字の配置を表すIDを文字列から判別し、返します。
	 * 
	 * @param nameType
	 *            文字の配置を表す文字列
	 * @return 文字の配置を表すID
	 */
	private int getNameType(String nameType) {
		if (nameType.toLowerCase().equals("left")) {
			return NAME_LEFT;
		} else {
			return NAME_CENTER;
		}
	}
}
