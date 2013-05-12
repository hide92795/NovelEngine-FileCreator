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
package hide92795.novelengine.filecreator.saver.story;

import hide92795.novelengine.filecreator.CommandException;
import hide92795.novelengine.filecreator.saver.SaverStory;
import java.awt.Color;
import java.io.StreamTokenizer;
import java.lang.reflect.Field;
import java.util.LinkedList;

/**
 * 「背景色」コマンドを解析するローダーです。
 * 
 * @author hide92795
 */
public class CommandChangeBackGroundColor extends Command {
	@Override
	public void save(StreamTokenizer tokenizer, LinkedList<Object> commandLine) throws Exception {
		// 数値 対象, [文字列 色(HTML/定義済み/)] or [int 赤, int 緑, int 青], 数値 アルファ値
		commandLine.add(SaverStory.COMMAND_SET_BACKGROUND_COLOR);
		int next = tokenizer.nextToken();
		if (next != StreamTokenizer.TT_NUMBER) {
			// 非数値
			throw new CommandException(tokenizer.lineno(), "背景色", 1, "引数「対象」は数値でなければいけません。");
		} else {
			byte i = (byte) tokenizer.nval;
			commandLine.add(i);
		}
		next = nextArgument(tokenizer);
		Color color = null;
		boolean bool_str;
		if (next == SaverStory.DOUBLE_QUOTE) {
			// 文字列表現
			bool_str = true;
			String c = tokenizer.sval;
			if (c.startsWith("#")) {
				// HTML表記
				color = Color.decode(c);
			} else {
				// 定義済み
				Class<Color> c_class = Color.class;
				Field f = c_class.getField(c);
				color = (Color) f.get(null);
			}
		} else if (next == StreamTokenizer.TT_NUMBER) {
			// 3数字RGB表現
			bool_str = false;
			int r = (int) tokenizer.nval;
			next = nextArgument(tokenizer);
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非数値
				throw new CommandException(tokenizer.lineno(), "背景色", 3, "引数「緑」は数値でなければいけません。");
			}
			int g = (int) tokenizer.nval;

			next = nextArgument(tokenizer);
			if (next != StreamTokenizer.TT_NUMBER) {
				// 非数値
				throw new CommandException(tokenizer.lineno(), "背景色", 4, "引数「青」は数値でなければいけません。");
			}
			int b = (int) tokenizer.nval;
			color = new Color(r, g, b);
		} else {
			throw new CommandException(tokenizer.lineno(), "背景色", 2, "引数「色」が不正です。");
		}
		commandLine.add(color.getRGB());
		next = nextArgument(tokenizer);
		if (next != StreamTokenizer.TT_NUMBER) {
			// 非数値
			if (bool_str) {
				throw new CommandException(tokenizer.lineno(), "背景色", 3, "引数「アルファ値」は数値でなければいけません。");
			} else {
				throw new CommandException(tokenizer.lineno(), "背景色", 5, "引数「アルファ値」は数値でなければいけません。");
			}
		} else {
			byte i = (byte) tokenizer.nval;
			commandLine.add(i);
		}
		next = tokenizer.nextToken();
		if (next != SaverStory.ARGUMENT_END) {
			throw new CommandException(tokenizer.lineno(), "背景色", -1, "引数が閉じられていません。");
		}
	}
}
