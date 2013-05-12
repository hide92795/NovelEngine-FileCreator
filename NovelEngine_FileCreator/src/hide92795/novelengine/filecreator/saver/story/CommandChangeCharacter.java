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
import hide92795.novelengine.filecreator.VarNumManager;
import hide92795.novelengine.filecreator.saver.SaverStory;
import java.io.StreamTokenizer;
import java.util.LinkedList;

/**
 * 「キャラ変更」コマンドを解析するローダーです。
 * 
 * @author hide92795
 */
public class CommandChangeCharacter extends Command {
	@Override
	public void save(StreamTokenizer tokenizer, LinkedList<Object> commandLine) throws Exception {
		// byte 対象, int 遅延, String キャラID, String 位置ID, String 表情ID
		commandLine.add(SaverStory.COMMAND_SET_CHARACTER);
		int next = tokenizer.nextToken();
		if (next != StreamTokenizer.TT_NUMBER) {
			// 非数値
			throw new CommandException(tokenizer.lineno(), "キャラ変更", 1, "引数「対象」は数値でなければいけません。");
		} else {
			byte i = (byte) tokenizer.nval;
			commandLine.add(i);
		}
		next = nextArgument(tokenizer);
		if (next != StreamTokenizer.TT_NUMBER) {
			// 非数値
			throw new CommandException(tokenizer.lineno(), "キャラ変更", 2, "引数「遅延」は数値でなければいけません。");
		} else {
			int i = (int) tokenizer.nval;
			commandLine.add(i);
		}
		next = nextArgument(tokenizer);
		if (next != SaverStory.DOUBLE_QUOTE) {
			// 非文字
			throw new CommandException(tokenizer.lineno(), "キャラ変更", 3, "引数「キャラID」は文字列でなければいけません。");
		} else {
			commandLine.add(VarNumManager.CHARACTER.add(tokenizer.sval));
		}
		next = nextArgument(tokenizer);
		if (next != SaverStory.DOUBLE_QUOTE) {
			// 非文字
			throw new CommandException(tokenizer.lineno(), "キャラ変更", 4, "引数「位置ID」は文字列でなければいけません。");
		} else {
			commandLine.add(VarNumManager.CHARACTER_POSITION.add(tokenizer.sval));
		}
		next = nextArgument(tokenizer);
		if (next != SaverStory.DOUBLE_QUOTE) {
			// 非文字
			throw new CommandException(tokenizer.lineno(), "キャラ変更", 5, "引数「表情ID」は文字列でなければいけません。");
		} else {
			commandLine.add(VarNumManager.FACE_TYPE.add(tokenizer.sval));
		}
		next = tokenizer.nextToken();
		if (next != SaverStory.ARGUMENT_END) {
			throw new CommandException(tokenizer.lineno(), "キャラ変更", -1, "引数が閉じられていません。");
		}
	}
}
